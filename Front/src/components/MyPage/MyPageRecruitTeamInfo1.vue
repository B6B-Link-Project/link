<script setup lang="ts">
//@ts-nocheck
import UserCard from "@/components/User/UserCard.vue";
import HackathonCard from "@/components/Hackathon/HackathonCard.vue";
import {onMounted, watch, Ref, ref} from "vue";
import {AppliedTeamDTO} from "@/dto/tmpDTOs/AppliedTeamDTO.ts";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";

const teamBuildingService = new TeamBuildingService();
const refTeam: Ref<AppliedTeamDTO | undefined> = ref();

const props = defineProps({
  teamId: {
    type:Number,
    default:0
  }
})
async function getTeamInfo() {
  // refTeam.value = await teamBuildingService.getTeam(props.teamId);
  refTeam.value = await teamBuildingService.getSuggestedTeam(props.teamId);
}

onMounted(async () => {
  await getTeamInfo();
  console.log(refTeam.value);
})
watch(() => props.teamId, getTeamInfo, { immediate: true });
</script>

<template>
  <slot/>
  <h1>{{ refTeam?.teamName }}</h1>
  <h2>{{ refTeam?.teamDesc }}</h2>
  <h1>해커톤 정보</h1>
  <HackathonCard :data="refTeam?.hackathonInfoResponseDto"
                 :name="refTeam?.hackathonInfoResponseDto.hackathonName" />
  <h1>팀장</h1>
  <div class="user-card-container">
    <div class="user-card">
      <template v-if="refTeam?.leader">
        <UserCard :userInfo="refTeam?.leader"/>
      </template>
    </div>
  </div>
  <h1>현재 팀원</h1>
  <div class="user-card-container">
    <div v-for="(data, i) in refTeam?.members" class="user-card">
        <UserCard :userInfo="data"/>
    </div>
  </div>
</template>

<style scoped>
.user-card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  flex-direction: column;
}

.user-card {
  flex: 1;
}

h1 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  margin-top: 17px;
  margin-bottom: 20px;
}

h2 {
  max-width: 600px;
  margin-top: 12px;
}
</style>
