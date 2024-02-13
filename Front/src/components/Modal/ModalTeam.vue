<script lang="ts" setup>
import UserCard from "@/components/User/UserCard.vue";
import ModalEffect from "@/components/Modal/ModalEffect.vue";
import {PropType, ref} from "vue";
import {HackathonTeamInfo1DTO} from "@/dto/tmpDTOs/HackathonTeamDTO.ts";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";

const teamBuildingService = new TeamBuildingService();

defineProps({
  data: {
    type: Object as PropType<HackathonTeamInfo1DTO>,
    required: true
  },
  totalPoint: {
    type: Number,
    required: true
  }
})

const clickedModal = ref<Number>();
const handleModalClose = (num: number) => {
  clickedModal.value = num;
}

const applyTeam = (num: number | undefined) => {
  teamBuildingService.postAppliedTeam(num);
  // router.push("/myPage")
  location.href = "/myPage?mode=1"
}

</script>

<template>
  <ModalEffect v-if="(data?.teamMaxPoint-totalPoint) > 0 && data?.teamMaxMember > data?.teamMember"
               :text="'남은 점수 '+ (data?.teamMaxPoint-totalPoint) + '점'"/>
  <div style="padding: 24px;">
    <div class="container">
      <img :src="data?.members?.[0].userImageUrl" alt="" class="img">
      <div class="team-container">
        <h1>{{ data?.teamName }}</h1>
        <section>
          {{ data?.teamDesc }}
        </section>
        <div style="display: flex; justify-content: right">
          <div @click="applyTeam(data?.teamId)"
              :class="!((data?.teamMaxPoint-totalPoint) > 0 && data?.teamMaxMember > data?.teamMember)?'button':'button-clickable'">
            참가 신청
          </div>
        </div>
      </div>
    </div>
    <h2>현재팀원</h2>
    <div class="user-card-container">
      <template v-for="(d, i) in data?.members">
        <UserCard @click="handleModalClose(i+1)" :userInfo="d" :t="true"/>
      </template>
    </div>
  </div>
</template>

<style scoped>
.button-clickable {
  width: 86px;
  height: 34px;
  border-radius: 5px;
  background: #7D3CFF;
  border: 1px #7D3CFF solid;
  color: #F2F2F2;
  text-align: center;
  line-height: 33px;
  transition: 0.3s color ease;
}

.button-clickable:hover {
  color: #7D3CFF;
  background: white;
}

.user-card-container {
  max-width: 658px;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  max-height: 225px;
  overflow: scroll;
  overflow-x: hidden;
  padding-right: 13px;
}

h2 {
  margin-top: 10px;
  margin-bottom: 11px;
  color: #000;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
}

.button {
  width: 86px;
  height: 34px;
  border-radius: 5px;
  background: #BEBEBE;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 33px;
}

.team-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

h1 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 400;
  line-height: 22.4px;
}

section {
  max-width: 350px;
  color: #303030;
  text-align: justify;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 24px;
  flex: 1;
}

.container {
  display: flex;
  gap: 20px;
}

.img {
  width: 280px;
  border-radius: 6px;
  height: 280px;
  object-fit: cover;
}

@media screen and (max-width: 698px) {
  .img {
    width: calc(100% - 43px);
  }

  .user-card-container {
    max-height: none;
  }

  .container {
    flex-direction: column;
  }

  section {
    max-width: none;
  }
}
</style>