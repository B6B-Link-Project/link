<script setup lang="ts">
//@ts-nocheck
import UserCard from "@/components/User/UserCard.vue";
import HackathonCard from "@/components/Hackathon/HackathonCard.vue";
import {onMounted, watch, Ref, ref, markRaw} from "vue";
import {AppliedTeamDTO} from "@/dto/tmpDTOs/AppliedTeamDTO.ts";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";
import {useRoute, useRouter} from "vue-router";

const teamBuildingService = new TeamBuildingService();
const refTeam: Ref<AppliedTeamDTO | undefined> = ref();
const route = useRoute();
const router =  useRouter();
const team = ref<number>(0);
const refTeamId = ref<number>(0);

const cancelApply = (teamId : number) => {
  teamBuildingService.deleteAppliedTeam(teamId);
  location.href = "/myPage"

}
// function getTeamInfo() {
//   const teamService = new TeamService();
// }
const getSelectTeam = (teamId : number, num : number) => {
  router.push("/myPage?mode=1&team=" + num);
  refTeamId.value = teamId;
}
const initTeamRefs = async () => {
  refTeam.value = await teamBuildingService.getAppliedTeam(refTeamId.value);
}
const updatePageFromQuery = async () => {
  const queryParam = route.query.team;
  console.log(queryParam)
  team.value = parseInt(queryParam as string);
  if (isNaN(team.value)) team.value = 0;
  await initTeamRefs();
};

watch([() => route.fullPath], updatePageFromQuery, {immediate: true});

onMounted(async () => {
  refTeam.value = await teamBuildingService.getAppliedTeam();
  await initTeamRefs();
})

</script>

<template>
  <h1 class="btn">신청한 팀 리스트</h1>
  <div class="teamListBox">
    <template v-if="refTeam?.teams">
      <div v-for="(data, i) in refTeam.teams" class="teamBtn" @click="getSelectTeam(data.teamId, i+1)" :class="team===i+1?'nonClick':'click'">
        {{data.teamName}}
      </div>
    </template>
  </div>
  <hr>
  <div class="title-container">
    <h1 class="btn">{{ refTeam?.teamName }}</h1>
    <div class="remove-button" @click="cancelApply(refTeam?.teamId)">신청 취소</div>
  </div>
  <h1 class="teamDesc">{{ refTeam?.teamDesc }}</h1>
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

.btn {
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  flex: 1;
  margin-bottom: 20px;
  margin-top: 0;
}


.title-container {
  display: flex;
  gap: 10px;
}

.remove-button {
  padding-left: 16px;
  padding-right: 16px;
  height: 34px;
  border-radius: 5px;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 33px;
  transition: color 0.3s ease;
}

.remove-button {
  background: #FF6161;
  border: #FF6161 solid 1px;
}

.remove-button:hover {
  color: #FF6161;
  background: white;
}

.teamListBox {
  display: flex;
  align-items: center;
  width: 400px;
  justify-content: flex-start;
}

.teamBtn {
  width: 80px;
  margin-right: 16px;
  margin-bottom: 8px;
}

.teamDesc {
  margin-top: 0;
  font-size: 16px;
  margin-bottom: 20px;
}

.click {
  color: #303030;
}

.nonClick {
  color: #7d3bff;
}

@media screen and (max-width: 697px) {
  .myPage-container {
    flex-direction: column-reverse;
  }
}

@media screen and (min-width: 698px) {
  .menu {
    max-width: 255px;
  }
}
</style>
