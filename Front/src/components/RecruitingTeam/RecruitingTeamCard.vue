<script lang="ts" setup>
//@ts-nocheck
import {PropType, ref} from "vue";
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import Modal from "@/components/Modal/Modal.vue";
import ModalTeam from "@/components/Modal/ModalTeam.vue";
import {HackathonTeamInfoDTO} from "@/dto/hackathonTeamInfoDTO.ts";

const props = defineProps({
  data: Object as PropType<HackathonTeamInfoDTO>
});

const refModal = ref<Boolean>(false);
const controlModal = () => {
  refModal.value = !refModal.value;
}
</script>

<template>
  <Modal v-if="refModal" @closeModal="controlModal">
    <ModalTeam :data="null" :total-point="null"/>
  </Modal>
  <div class="card-container" @click="controlModal">
    <div class="upper-box">
      <div class="img-container">
        <img :src="props.data?.imgUrl" alt="팀" class="img-container">
      </div>
      <div class="right-box-container">
        <div class="hackathon-title">
          <div class="hackathon-title-text">{{ props.data?.hackathonName }}</div>
          <div class="year-container">
            ~ {{ props.data?.registerDate.getFullYear() }}.
            {{ props.data?.registerDate.getMonth() }}.
            {{ props.data?.registerDate.getDay() }}
          </div>
        </div>
        <div class="team-name">
          {{ props.data?.teamName }}
        </div>
        <div class="project-content">
          {{ props.data?.projectContent }}
        </div>
        <div class="skill-container">
          <div v-for="skill in (props.data?.skillList || [])" style="margin-right: 9px">
            <SkillIcon :skill="skill" height="26px" radius="5px" width="25px"/>
          </div>
        </div>
      </div>
    </div>
    <div class="recruiting-content">
      <div class="recruiting-text">
        <div>
          <span class="normal">모집인원 :</span>
          <span class="high-right">{{ props.data?.nowMember }}</span>
          <span class="normal">/ </span>
          <span class="normal">{{ props.data?.maxMember }}</span>
        </div>
        <div style="margin-left: 12px">
          <span class="normal">남은점수 :</span>
          <span class="high-right">{{ props.data?.nowMember }}</span>
          <span class="normal">/ </span>
          <span class="normal">{{ props.data?.maxMember }}</span>
        </div>
      </div>
      <div class="recruiting-button">
        상세보기
      </div>
    </div>

  </div>
</template>

<style scoped>
@media screen and (max-width: 573px) {
  .year-container {
    display: none;
  }
}

.recruiting-button {
  text-align: center;
  width: 79px;
  height: 28px;
  border-radius: 3px;
  background: #7D3CFF;
  color: #EDEDED;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  line-height: 28px;
  transition: background-color 0.3s ease;
}

.recruiting-button:hover {
  border: 1px solid #7d3bff;
  color: #7d3bff;
  background: white;
}

.recruiting-content {
  margin-top: 12px;
  display: flex;
}

.high-right {
  color: #7D3CFF;
  font-size: 24px;
  font-style: normal;
  font-weight: 700;
  margin-left: 3px;
  margin-right: 2px;
}

.normal {
  color: #404040;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
}

.recruiting-text {
  flex: 1;
  display: flex;
}

.skill-container {
  flex: 1;
  height: max-content;
  margin-top: 11px;
  display: flex;
  align-items: flex-end;
}

.project-content {
  color: #303030;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: 18px;
  margin-top: 8px;
  max-height: 55px;
}

.right-box-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-left: 10px;
}

.hackathon-title {
  padding-top: 4px;
  color: #303030;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  display: flex;
  width: 100%;
}

.year-container {
  width: max-content;
  color: #8A8A8A;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: 18px;
  letter-spacing: -0.36px;
}

.hackathon-title-text {
  height: max-content;
  width: 100%;
  flex: 1;
}

.team-name {
  margin-top: 9px;
  color: #303030;
  font-size: 12px;
  font-style: normal;
  font-weight: 600;
}


.hackathon-title-text, .team-name {
  border-left: #7D3CFF solid 2px;
  padding-left: 6px;
}

.upper-box {
  display: flex;
  width: 100%;
  height: max-content;
  justify-content: center;
}

.card-container {
  flex: 1;
  width: 100%;
  min-width: 400px;
  height: 204px;
  border-radius: 10px;
  border: 1px solid #DEDEDE;
  padding: 12px;
  transition: transform 0.3s ease;
}

@media screen and (max-width: 573px) {
  .card-container {
    min-width: 300px;
  }
}
.card-container:hover {
  transform: scale(1.005);
  border: #7d3bff 1px solid;
  box-shadow: 4px 4px 6px 0 rgba(0, 0, 0, 0.25);
}

.img-container {
  width: 140px;
  height: 140px;
  border-radius: 5px;
}
</style>