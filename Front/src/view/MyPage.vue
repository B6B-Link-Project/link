<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref, Ref, watch} from "vue";
import MyPageRecruitingTeam from "@/components/MyPage/MyPageRecruitingTeam.vue";
import MyInfo from "@/components/MyPage/MyInfo.vue";
import MyPageRecruitTeamInfo from "@/components/MyPage/MyPageRecruitTeamInfo.vue";
import MyPageMyProject from "@/components/MyPage/MyPageMyProject.vue";
import MyPageSchedule from "@/components/MyPage/MyPageSchedule.vue";
import {MyPageService} from "@/api/MyPageService.ts";
import {MypageConditionDTO} from "@/dto/tmpDTOs/MypageConditionDTO.ts";
import {TeamService} from "@/api/TeamService.ts";
import MyPageRecruitTeamInfo1 from "@/components/MyPage/MyPageRecruitTeamInfo1.vue";

const teamService = new TeamService();
const myPageService = new MyPageService();
const refMyPageCond: Ref<MypageConditionDTO | undefined> = ref();
const route = useRoute();
const mode = ref<number>(0);
const refTeamIds = ref<number[]>();
const refTeamNames = ref<string[]>();
const refTeamId = ref<number>(0);

const initTeamRefs = async () => {
  const response = await getTeams();
  refTeamIds.value = response.ids;
  refTeamNames.value = response.names;
  refTeamId.value = refTeamIds.value[0];
}

onMounted(async () => {
  refMyPageCond.value = await myPageService.getMyPageCond();
})

const updatePageFromQuery = async () => {
  const queryParam = route.query.mode;
  mode.value = parseInt(queryParam as string);
  if (isNaN(mode.value)) mode.value = 0;
  await initTeamRefs();
};

watch([() => route.fullPath], updatePageFromQuery, {immediate: true});

async function getTeams() {
  return await teamService.getBuildingTeamIds();
}

async function acceptSuggestion(teamId: number) {
  await teamService.postSuggestionByUser(teamId);
}

async function declineSuggestion(teamId: number) {
  await teamService.deleteSuggestionByUser(teamId);
}

function updateId(teamId: number) {
  refTeamId.value = teamId;
}

</script>

<template>
  <div class="myPage-container">
    <div class="content">
      <MyPageRecruitTeamInfo v-if="mode===1">
<!--        <div class="title-container">-->
<!--          <h1>신청한 팀</h1>-->
<!--          <div class="remove-button">신청 취소</div>-->
<!--        </div>-->
      </MyPageRecruitTeamInfo>
      <MyPageRecruitTeamInfo1 v-else-if="mode===2" :team-id="refTeamId">
        <div class="title-container">
          <h1>권유받은 팀</h1>
          <div class="accept-button" @click="acceptSuggestion(refTeamId)">수락</div>
          <div class="remove-button" @click="declineSuggestion(refTeamId)">거절</div>
        </div>
      </MyPageRecruitTeamInfo1>
      <MyPageMyProject v-else-if="mode===3"/>
      <MyPageRecruitingTeam v-else-if="mode===4"/>
      <MyPageSchedule v-else-if="mode===5"/>
      <MyInfo v-else/>
    </div>
    <table class="menu">
      <tr>
        <td :class="{'select':mode==0}">
          <router-link to="?mode=0">내 정보</router-link>
        </td>
      </tr>
      <template v-if="refMyPageCond?.applied">
        <tr>
          <td :class="{'select':mode==1}">
            <router-link to="?mode=1">신청한 팀</router-link>
          </td>
        </tr>
      </template>
      <template v-if="refMyPageCond?.suggested">
        <tr>
          <td :class="{'select':mode==2}">
            <router-link to="?mode=2">권유받은 팀</router-link>
          </td>
        </tr>
      </template>
      <template v-if="refMyPageCond?.project">
        <tr>
          <td :class="{'select':mode==3}">
            <router-link to="?mode=3">내 프로젝트</router-link>
          </td>
        </tr>
      </template>
      <template v-if="refMyPageCond?.team">
        <tr>
          <td :class="{'select':mode==4}">
            <router-link to="?mode=4">모집중인 팀</router-link>
          </td>
        </tr>
      </template>
      <tr>
        <td :class="{'select':mode==5}">
          <router-link to="?mode=5">스케쥴 관리</router-link>
        </td>
      </tr>
      <tr>
        <td :class="{'select':mode==6}">
          <router-link to="/exit"><span>회원 탈퇴</span></router-link>
        </td>
      </tr>
    </table>
  </div>
  <div v-if="mode==2">
    <ul>
      <li v-for="(teamId, i) in refTeamIds">
        <router-link to="/myPage?mode=2" :class="{'select':teamId==refTeamId}" @click="updateId(teamId)">{{ teamId }}</router-link>
      </li>
    </ul>
  </div>
</template>

<style scoped>

.accept-button {
  background: #7D3BFF;
  border: #7D3BFF solid 1px;
}

.accept-button:hover {
  color: #7D3BFF;
  background: white;
}

h1 {
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  flex: 1;
  margin-bottom: 20px;
}

.title-container {
  display: flex;
  gap: 10px;
}

.accept-button, .remove-button {
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

.select {
  background: #7D3BFF;
  color: #FFF;
  border: #7D3BFF solid 1px;
}

.select a {
  color: #FFF;
}

.myPage-container {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  margin-bottom: 50px;
}

.content {
  flex: 1;
}

.menu {
  width: 100%;
  border-collapse: separate;
  border-radius: 10px;
  overflow: hidden;
  border: #DEDEDE solid 1px;
  border-bottom: none;
  height: max-content;
}

td {
  padding: 13px 13px 13px 18px;
  border-bottom: #DEDEDE solid 1px;
}

a {
  color: #303030;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 22.4px;
  width: 250px;
}
</style>