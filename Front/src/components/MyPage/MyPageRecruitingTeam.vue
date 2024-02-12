<script setup lang="ts">

import UserCard from "@/components/User/UserCard.vue";
import {onMounted, Ref, ref} from "vue";
import {RecruitTeamDTO} from "@/dto/tmpDTOs/RecruitTeamDTO.ts";
import ModalMember from "@/components/Modal/ModalMember.vue";
import ModalButton from "@/components/Modal/ModalButton.vue";
import Modal from "@/components/Modal/Modal.vue";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";

const clickedModal = ref<Number>();

const handleModalClose = (num: number) => {
  clickedModal.value = num;
}
const clickedModal1 = ref<Number>();

const handleModalClose1 = (num: number) => {
  clickedModal1.value = num;
}
const clickedModal2 = ref<Number>();

const handleModalClose2 = (num: number) => {
  clickedModal2.value = num;
}
const teamBuildingService = new TeamBuildingService();
const refTeam: Ref<RecruitTeamDTO | undefined> = ref();

onMounted(async () => {
  refTeam.value = await teamBuildingService.getRecruitTeam();
  isLeader.value = await teamBuildingService.getIsLeader();
})

const acceptApply = (userId: number) => {
  teamBuildingService.postAcceptApply(userId, refTeam.value?.teamId)
  location.href = "/myPage?mode=4"
}

const refuseApply = (userId: number) => {
  teamBuildingService.deleteRefuseApply(userId, refTeam.value?.teamId)
  location.href = "/myPage?mode=4"
}

const isLeader = ref<Boolean>();


</script>

<template>
  <div class="recruiting-container">
    <h1>모집중인 팀</h1>
    <div class="remove-button">프로젝트 삭제</div>
  </div>
  <h2>{{ refTeam?.teamName }}</h2>
  <p class="desc">{{ refTeam?.teamDesc }}</p>
  <div class="content">
    <div class="fx1">
      <h2>현재 팀원</h2>
      <div class="list">
        <template v-if="refTeam?.members['JOINED']">
          <template v-for="(data, i) in refTeam?.members['JOINED']">
            <Modal v-if="clickedModal===i+1" @closeModal="handleModalClose">
              <ModalMember :userInfo="data">
                <template v-if="isLeader">
                  <ModalButton button-text="추방하기"/>
                </template>
              </ModalMember>
            </Modal>
            <UserCard @click="handleModalClose(i+1)" :userInfo="data" :t="true"/>
          </template>
        </template>
      </div>
    </div>
    <div class="fx1">
      <h2>신청한 사용자</h2>

      <div class="list">
        <template v-if="refTeam?.members['APPLIED']">
          <template v-for="(data, i) in refTeam?.members['APPLIED']">
            <Modal v-if="clickedModal1===i+1" @closeModal="handleModalClose1">
              <ModalMember :userInfo="data">
                <template v-if="isLeader">
                  <ModalButton button-text="면접 예약"/>
                  <ModalButton button-text="수락" @click="acceptApply(data.userId)"/>
                  <ModalButton button-text="거절" @click="refuseApply(data.userId)"/>
                </template>
              </ModalMember>
            </Modal>
            <UserCard @click="handleModalClose1(i+1)" :userInfo="data" :t="true"/>
          </template>
        </template>
      </div>
    </div>
    <div class="fx1">
      <h2>권유한 사용자</h2>
      <div class="list">
        <template v-if="refTeam?.members['SUGGESTED']">
          <template v-for="(data, i) in refTeam?.members['SUGGESTED']">
            <Modal v-if="clickedModal2===i+1" @closeModal="handleModalClose2">
              <ModalMember :userInfo="data">
                <template v-if="isLeader">
                  <ModalButton button-text="면접 예약"/>
                  <ModalButton button-text="권유 취소"/>
                </template>
              </ModalMember>
            </Modal>
            <UserCard @click="handleModalClose2(i+1)" :userInfo="data" :t="true"/>
          </template>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
@media screen and (max-width: 1307px) {
  .content {
    flex-direction: column;
  }
}

::-webkit-scrollbar {
  width: 4px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: #A679FF;
}

::-webkit-scrollbar-thumb:hover {
  background: #7D3CFF;
}


.fx1 {
  flex: 1;
}

.list {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: scroll;
  overflow-x: hidden;
  max-height: 460px;
  padding-right: 10px;
}

.content {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 60px;
}

.remove-button {
  width: 110px;
  height: 34px;
  border-radius: 5px;
  background: #FF6161;
  border: #FF6161 solid 1px;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 33px;
  transition: color 0.3s ease;
}

.remove-button:hover {
  color: #FF6161;
  background: white;
}

.recruiting-container {
  display: flex;
}

h1 {
  flex: 1;
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
}

h2 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  margin-top: 17px;
}

h3 {
  color: #303030;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 20px; /* 125% */
  max-width: 600px;
  margin-top: 12px;
}

.desc {
  margin-top: 16px;
}
</style>
