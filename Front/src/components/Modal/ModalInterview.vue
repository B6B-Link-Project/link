<script setup lang="ts">
import {onMounted, ref, watch} from "vue";
import DatePicker from 'vue3-datepicker';
import {TeamService} from "@/api/TeamService.ts";
import {ReservationService} from "@/api/ReservationService.ts";
import {Builder} from "builder-pattern";
import {ReservationRequest, ReservationResponse, ScheduleResponse} from "@/dto/tmpDTOs/reservationDTO.ts";
import {ScheduleService} from "@/api/ScheduleService.ts";

const props = defineProps({
  userId: {
    type: Number,
    default: 0
  }
})

class ReservationManager {

  reservationService: ReservationService

  constructor() {
    this.reservationService = new ReservationService();
  }

  async getReservationsByUserId(userId: number): Promise<Map<String, ReservationResponse[]>> {
    const myReservations = await this.reservationService.getReservationsByUserId(userId);
    const map = new Map<String, ReservationResponse[]>();
    myReservations.forEach((r) => {
      const key = this.extractDate(r);
      if (!map.has(key))
        map.set(key, []);
      map.get(key)?.push(r);
    });
    return map;
  }

  extractDate(reservationResponse: ReservationResponse) {
    const date = new Date(reservationResponse.reservationDatetime);
    const year = date.getFullYear();
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const day = `0${date.getDate()}`.slice(-2);

    return `${year}.${month}.${day}`;
  }

  formatDate(date: Date) {
    const year = date.getFullYear();
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const day = `0${date.getDate()}`.slice(-2);

    return `${year}.${month}.${day}`;
  }

  extractHours(reservationResponse: ReservationResponse): number {
    const date = new Date(reservationResponse.reservationDatetime);
    return date.getHours();
  }
}


const as = ref<Boolean>(false);
const clicked = (bool: boolean) => {
  as.value = bool;
}

const selectedDate = ref(new Date());
const selectedTime = ref(-1);
const selectableTimes = ref(new Set<number>());
const reservations = ref<Map<String, ReservationResponse[]>>();
const reservationManager = new ReservationManager();
const scheduleClasses = ref<string[]>([]);
for (let i = 0; i < 24; i++) {
  scheduleClasses.value.push('unselectable-time');
}

const teamService = new TeamService();
const scheduleService = new ScheduleService();
const reservationService = new ReservationService();

async function suggest() {
  const teamId = (await teamService.getActiveTeamId()).id;
  await teamService.postSuggestionByTeam(teamId, props.userId);
  location.href = "/myPage?mode=4";
}

const selectTime = (num: number) => {
  if (scheduleClasses.value[num] === 'unselectable-time') return;
  if (selectedTime.value === num) {
    selectedTime.value = -1;
  } else {
    selectedTime.value = num;
  }
}

watch(() => selectedTime.value, (to, from) => {
  scheduleClasses.value[to] = 'select-time';
  if (from === -1) return;
  scheduleClasses.value[from] = 'non-select-time';
});

const suggestWithInterview = async () => {

  if (selectedTime.value < 0) {
    alert("시간을 선택해주세요!");
    return;
  }

  const toIsoString = (date: Date, hour: number) => {
    // let tzo = -date.getTimezoneOffset(), dif = tzo >= 0 ? '+' : '-';
    const pad = (num: number) => {
      return (num < 10 ? '0' : '') + num;
    };

    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate()) +
        'T' + pad(hour) +
        ':' + pad(0) +
        ':' + pad(0);
    // dif + pad(Math.floor(Math.abs(tzo) / 60)) +
    // ':' + pad(Math.abs(tzo) % 60);
  }

  const makeReservation = async () => {
    await reservationService.registerReservation(Builder<ReservationRequest>()
        .userId(props.userId)
        .reservationDateTime(toIsoString(selectedDate.value, selectedTime.value))
        .build());
  }

  await suggest();
  await makeReservation();
}

const initScheduleSet = async (userId: number, set: Set<Number>) => {
  const scheduleResponseToHours = (sr: ScheduleResponse): number[] => {
    return sr.times.map<number>((t) => parseInt(t.slice(0, 2)));
  }

  const mySchedule = await scheduleService.getUserSchedule(userId);
  scheduleResponseToHours(mySchedule).forEach((n) => {
    scheduleClasses.value[n] = 'non-select-time';
  });

  reservations.value?.get(reservationManager.formatDate(selectedDate.value))?.forEach((reservationResponse) => {
    set.delete(reservationManager.extractHours(reservationResponse));
    scheduleClasses.value[reservationManager.extractHours(reservationResponse)] = 'unselectable-time';
  });

}

const handleDate = () => {
  initScheduleSet(props.userId, selectableTimes.value);
}

onMounted(() => {
  reservationManager.getReservationsByUserId(props.userId).then((m) => {
    reservations.value = m;
  }).then(() => {
    initScheduleSet(props.userId, selectableTimes.value);
  });
});

</script>

<template>
  <div class="interview-container">
    <h1>면접예약</h1>
    <h2>날짜 선택</h2>
    <DatePicker v-model="selectedDate"
                style="width: 100%; min-height: 40px; height: max-content; border: 1px solid #6F7070;
                ;font-size: 14px; padding-left: 10px; margin-bottom: 16px; font-family: Pretendard-Regular,serif"
                @update:model-value="handleDate"/>
    <h2>홍길동님 미팅 가능 시간</h2>
    <div class="schedule-menu">
      <div class="as">
        <div :class="!as?'select':'non-select'" @click="clicked(false)">오전</div>
        <div :class="as?'select':'non-select'" @click="clicked(true)">오후</div>
      </div>
    </div>
    <div class="time-container" v-if="!as">
      <div class="time-button" v-for="i in 12" @click="selectTime(i-1)" :class="scheduleClasses[i-1]">{{ i - 1 }}:00
      </div>
    </div>
    <div class="time-container" v-if="as">
      <div class="time-button" v-for="i in 12" @click="selectTime(i+11)" :class="scheduleClasses[i+11]">{{
          i + 11
        }}:00
      </div>
    </div>
    <div class="button-container">
      <div class="button w" @click="suggest">
        면접 없이 권유하기
      </div>
      <div class="button p" @click="suggestWithInterview">
        면접 예약
      </div>
    </div>

  </div>
</template>


<style scoped>

.unselectable-time {
  background: #c5c5c5;
  color: #303030;
}

.select-time {
  background: #7d3bff;
  color: white;
}

.select-time :hover {
  transform: scale(1.2);
  -webkit-transform: scale(1.2); /* 크롬, 사파리 */
  -moz-transform: scale(1.2); /* 파이어폭스 */
  -ms-transform: scale(1.2); /* IE */
  -o-transform: scale(1.2); /* 오페라 */
}

.non-select-time {
  color: #303030;
}

.non-select-time :hover {
  transform: scale(1.2);
  -webkit-transform: scale(1.2); /* 크롬, 사파리 */
  -moz-transform: scale(1.2); /* 파이어폭스 */
  -ms-transform: scale(1.2); /* IE */
  -o-transform: scale(1.2); /* 오페라 */
}

.button-container {
  margin-top: 30px;
  margin-bottom: 30px;
  display: flex;
  gap: 16px;
}

.button {
  padding: 9px 14px;
  border-radius: 5px;
  border: solid 1px #7D3BFF;
  transition: color 0.3s;
  min-width: max-content;
  text-align: center;
  flex: 1;
}

.w:hover {
  background: #7d3bff;
  color: white;
}

.p {
  background: #7d3bff;
  color: white;
}

.p:hover {
  color: #7d3bff;
  background: white;
}

* {
  text-align: left;
}

h1 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

h2 {
  font-size: 16px;
  margin-bottom: 12px;
}

.interview-container {
  padding: 20px;
  height: 80vh;
  max-height: 430px;
}

.schedule-menu {
  width: 100%;
  display: flex;
}

.time-container {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  max-width: 480px;
  width: 100vw;
  margin-top: 12px;
  margin-bottom: 20px;
}

.time-button {
  width: 70px;
  height: 40px;
  border-radius: 10px;
  border: 1px solid #DEDEDE;
  transition: color 0.3s ease;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: 600;
  line-height: 39px;
}

.select, .non-select {
  width: 76px;
  padding-bottom: 8px;
  text-align: center;
}

.select {
  border-bottom: #7D3BFF solid 2px;
  color: #303030;
}

.non-select {
  border-bottom: #DEDEDE solid 2px;
  color: #606060;
}

.as {
  flex: 1;
  display: flex;
}
</style>
