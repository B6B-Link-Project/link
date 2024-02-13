<script setup lang="ts">
import {ref} from "vue";
import DatePicker from 'vue3-datepicker';
import {TeamService} from "@/api/TeamService.ts";

const props = defineProps({
  userId: {
    type: Number,
    default: 0
  }
})

const as = ref<Boolean>(false);
const clicked = (bool: boolean) => {
  as.value = bool;
}

const selectedDate = ref(new Date());
const selectedTime = ref(-1);

const teamService = new TeamService();

async function suggest() {
  const teamId = (await teamService.getActiveTeamId()).id;
  await teamService.postSuggestionByTeam(teamId, props.userId);
  location.href = "/myPage?mode=4";
}

const selectTime = (num: number) => {
  if (selectedTime.value === num) {
    selectedTime.value = -1;
  }
  else {
    selectedTime.value = num;
  }
}
</script>

<template>
  <div class="interview-container">
    <h1>면접예약</h1>
    <h2>날짜 선택</h2>
    <DatePicker v-model="selectedDate"
                style="width: 100%; min-height: 40px; height: max-content; border: 1px solid #6F7070;
                ;font-size: 14px; padding-left: 10px; margin-bottom: 16px; font-family: Pretendard-Regular,serif"/>
    <h2>홍길동님 미팅 가능 시간</h2>
    <div class="schedule-menu">
      <div class="as">
        <div :class="!as?'select':'non-select'" @click="clicked(false)">오전</div>
        <div :class="as?'select':'non-select'" @click="clicked(true)">오후</div>
      </div>
    </div>
    <div class="time-container" v-if="!as">
      <div class="time-button" v-for="i in 12" @click="selectTime(i-1)" :class="selectedTime===i-1?
        'select-time':'non-select-time'">{{ i - 1 }}:00
      </div>
    </div>
    <div class="time-container" v-if="as">
      <div class="time-button" v-for="i in 12" @click="selectTime(i+11)" :class="selectedTime===i+11?
        'select-time':'non-select-time'">{{ i + 11 }}:00
      </div>
    </div>
    <div class="button-container">
      <div class="button w" @click="suggest">
        면접 없이 권유하기
      </div>
      <div class="button p">
        면접 예약
      </div>
    </div>

  </div>
</template>


<style scoped>
.select-time {
  background: #7d3bff;
  color: white;
}

.non-select-time {
  color: #303030;
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
