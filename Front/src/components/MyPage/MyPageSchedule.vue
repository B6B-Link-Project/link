<script setup lang="ts">
import {onMounted, Ref, ref, watch} from "vue";
import UserCard from "@/components/User/UserCard.vue";
import {ScheduleService} from "@/api/ScheduleService.ts";
import {useRoute} from "vue-router";
import {ReservationResponse, ScheduleRequest, ScheduleResponse} from "@/dto/tmpDTOs/reservationDTO.ts";
import {Builder} from "builder-pattern";
import {ReservationService} from "@/api/ReservationService.ts";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";
import {TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";
import router from "@/router";

const route = useRoute();

const isPm = ref<Boolean>(false);
const selectedTime = ref(new Set<Number>());
const reservationMapRef: Ref<Map<String, ReservationResponse[]> | undefined> = ref();

const clicked = (bool: boolean) => {
  isPm.value = bool;
}
const selectTime = (num: number) => {
  if (selectedTime.value.has(num)) {
    selectedTime.value.delete(num)
  } else {
    selectedTime.value.add(num)
  }
};

class ScheduleManager {

  scheduleService: ScheduleService;

  constructor() {
    this.scheduleService = new ScheduleService();
  }

  scheduleResponseToHours(sr: ScheduleResponse): number[] {
    return sr.times.map<number>((t) => parseInt(t.slice(0, 2)));
  }

  async initScheduleSet(set: Set<Number>) {
    const mySchedule = await this.scheduleService.getMySchedule();
    this.scheduleResponseToHours(mySchedule).forEach((n) => {
      set.add(n);
    });
  }

  async updateMySchedule(set: Set<Number>) {
    const times: String[] = [];
    set.forEach((n) => {
      const d = new Date("0");
      d.setHours(n.valueOf());
      times.push(`${n.valueOf() < 10 ? '0' + n.toString() : n}:00:00`);
    });
    await this.scheduleService.updateMySchedule(Builder<ScheduleRequest>()
        .times(times)
        .build());
  }
}

class ReservationManager {

  reservationService: ReservationService
  teamBuildingService: TeamBuildingService

  constructor() {
    this.reservationService = new ReservationService();
    this.teamBuildingService = new TeamBuildingService();
  }

  async getMyReservations(): Promise<Map<String, ReservationResponse[]>> {
    const myReservations = await this.reservationService.getMyReservations();
    const map = new Map<String, ReservationResponse[]>();
    myReservations.forEach((r) => {
      const date = r.reservationDatetime;
      const key = this.formatDate(new Date(date));
      if (!map.has(key))
        map.set(key, []);
      map.get(key)?.push(r);
    });
    return map;
  }

  getMemberDetail(userId: number): Ref<TeamMemberFindUserDTO> {
    const memberRef = ref<TeamMemberFindUserDTO>(Builder<TeamMemberFindUserDTO>().build());
    this.teamBuildingService.getMemberDetailByUserId(userId).then((memberDetailByUserId) => {
      memberRef.value = Builder<TeamMemberFindUserDTO>()
          .career(memberDetailByUserId.career)
          .field(memberDetailByUserId.field)
          .name(memberDetailByUserId.name)
          .introduce(memberDetailByUserId.introduce)
          .rating(memberDetailByUserId.rating)
          .profileImageURL(memberDetailByUserId.profileImageURL)
          .skillSets(memberDetailByUserId.skillSets)
          .build();
    });
    return memberRef;
  }

  moveToVideo(reservationId: number) {
    router.push(`/video/${reservationId}`);
  }

  formatDate(date: Date) {
    const year = date.getFullYear();
    const month = `0${date.getMonth() + 1}`.slice(-2);
    const day = `0${date.getDate()}`.slice(-2);

    return `${year}.${month}.${day}`;
  }
}

const reservationManager = new ReservationManager();
const scheduleManager = new ScheduleManager();

onMounted(() => {
  scheduleManager.initScheduleSet(selectedTime.value);
  reservationManager.getMyReservations().then(map => {
    reservationMapRef.value = map
  });
});

watch(() => route.path, () => {
  scheduleManager.initScheduleSet(selectedTime.value);
  reservationManager.getMyReservations().then(map => {
    reservationMapRef.value = map
  });
})

</script>

<template>
  <h1>스케줄 관리</h1>
  <h2>미팅 가능 일정</h2>
  <div class="schedule-menu">
    <div class="as">
      <div :class="!isPm?'select':'non-select'" @click="clicked(false)">오전</div>
      <div :class="isPm?'select':'non-select'" @click="clicked(true)">오후</div>
    </div>
    <div class="edit" @click="scheduleManager.updateMySchedule(selectedTime)">수정하기</div>
  </div>
  <div v-if="!isPm" class="time-container">
    <div class="time-button" v-for="i in 12" @click="selectTime(i-1)" :class="selectedTime.has(i-1)?
        'select-time':'non-select-time'">{{ i - 1 }}:00
    </div>
  </div>
  <div v-if="isPm" class="time-container">
    <div class="time-button" v-for="i in 12" @click="selectTime(i+11)" :class="selectedTime.has(i+11)?
        'select-time':'non-select-time'">{{ i + 11 }}:00
    </div>
  </div>
  <h2>미팅 스케줄</h2>
  <template v-for="[key, arr] in reservationMapRef">
    <h3>{{ key }}</h3>
    <div class="user-card-container">
      <div style="flex: 1;">
        <UserCard v-for="val in arr" :user-info="reservationManager.getMemberDetail(val.userId)"
                  @click="reservationManager.moveToVideo(val.reservationId)"/>
      </div>
    </div>
  </template>
</template>

<style scoped>
.edit {
  width: 70px;
  height: 30px;
  flex-shrink: 0;
  border-radius: 10px;
  background: #7D3BFF;
  color: #FFF;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  line-height: 29px;
  border: #7D3CFF solid 1px;
  transition: color 0.3s ease;
}

.edit:hover {
  background: white;
  color: #7D3BFF;
}


.schedule-menu {
  max-width: 480px;
  display: flex;

}

.time-container {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  max-width: 500px;
  margin-top: 12px;
  margin-bottom: 20px;
}

.select-time {
  background: #7d3bff;
  color: white;
}

.non-select-time {
  color: #303030;
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

h1 {
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 28px;
}

h2 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 16px;
}

h3 {
  color: #303030;
  font-size: 14px;
  font-style: normal;
  font-weight: 500;
  line-height: 22.4px; /* 160% */
  border-bottom: 1px #7D3BFF solid;
  padding-bottom: 2px;
  width: max-content;
  margin-bottom: 12px;
}

.user-card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
</style>
