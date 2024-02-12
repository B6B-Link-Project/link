<script setup lang="ts">

import HackathonRewardTeam from "@/components/Hackathon/HackathonRewardTeam.vue";
import {onMounted, ref, Ref} from "vue";
import {WinnerProjectResponseDto} from "@/dto/tmpDTOs/hackathonDTO.ts";
import {HackathonService} from "@/api/HackathonService.ts";

const props = defineProps({
  id : {
    type : Number,
    required : true
  }
});

const winnersRef: Ref<WinnerProjectResponseDto[]> = ref([]);
const firstWinner: Ref<WinnerProjectResponseDto> = ref({} as WinnerProjectResponseDto);
const secondWinner: Ref<WinnerProjectResponseDto> = ref({} as WinnerProjectResponseDto);
const thirdWinner: Ref<WinnerProjectResponseDto> = ref({} as WinnerProjectResponseDto);
const hackathonService = new HackathonService();
onMounted(async () => {
  winnersRef.value = await hackathonService.getWinnerProjects(props.id);
  firstWinner.value = winnersRef.value[0];
  secondWinner.value = winnersRef.value[1];
  thirdWinner.value = winnersRef.value[2];
  console.log("a",firstWinner.value.winnerProjectInfoDto.imgSrc,secondWinner)
})

</script>

<template>
  <h1>수상자</h1>
  <div class="reward-container">
    <div class="reward">
      <h2 class="second">2</h2>
      <HackathonRewardTeam :id="props.id" :data="secondWinner"/>
    </div>
    <div class="reward">
      <h2 class="first">1</h2>
      <HackathonRewardTeam :id="props.id" :data="firstWinner"/>
    </div>
    <div class="reward">
      <h2 class="third">3</h2>
      <HackathonRewardTeam :id="props.id" :data="thirdWinner"/>
    </div>
  </div>
</template>

<style scoped>
.reward-container {
  display: flex;
  width: 100%;
  gap: 20px;
}

.reward {
  flex: 1;
}

.first {
  color: #D5A11E;
}

.second {
  color: #A3A3A3;
}

.third {
  color: #CD7F32;
}

h2 {
  text-align: center;
  font-size: 60px;
  font-style: normal;
  font-weight: 900;
}

h1 {
  margin-top: 56px;
  color: #000;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  line-height: 22.4px; /* 93.333% */
}
</style>