<script setup lang="ts">

import Tier from "@/components/Tier.vue";
import ModalButton from "@/components/Modal/ModalButton.vue";
import ModealGithubButton from "@/components/Modal/ModealGithubButton.vue";
import ModalSkill from "@/components/Modal/ModalSkill.vue";
</script>

<template>
  <div class="member-modal-container">
    <img src="https://thumb.mtstarnews.com/06/2023/06/2023060322064338985_2.jpg/dims/optimize" alt="">
    <div class="member-info-container">
      <div class="member-info">
        <div class="member-name">카리나</div>
        <div class="member-workflow">3년차 백엔드 개발자</div>
        <Tier width="48px" height="28px" font-size="24px" rating="200" radius="10px"/>
      </div>
      <div class="member-introduce">
        안녕하세요. 백엔드 개발자입니다. 동해번쩍 서해번쩍 잘합니다. 동해번쩍 서해번쩍 잘합니다. 동해번쩍 서해번쩍 잘합니다. 동해번쩍 서해번쩍 잘합니다. 동해번쩍 서해번쩍 잘합니다.
      </div>
      <div class="button-wrapper">
        <div class="button-container">
          <div class="git-button">
            <ModealGithubButton/>
          </div>
          <div class="button-right-container">
            <ModalButton button-text="합류 요청"/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="member-skill-container">
    <h2>기술 스택</h2>
    <div class="skill-list-container">
      <ModalSkill title="백엔드" font-size="16px" color="#303030" font-weight="400" margin-bottom="12px"/>
      <ModalSkill title="프론트엔드" font-size="16px" color="#303030" font-weight="400" margin-bottom="12px" style="justify-content: end"/>
    </div>
    <div class="skill-list-container">
      <ModalSkill title="데이터베이스" font-size="16px" color="#303030" font-weight="400" margin-bottom="12px"/>
      <ModalSkill title="인프라" font-size="16px" color="#303030" font-weight="400" margin-bottom="12px" style="justify-content: end"/>
    </div>
  </div>
</template>

<style scoped>
.skill-list-container {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

h2 {
  color: #303030;
  font-size: 21px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 16px;
}

.member-skill-container {
  padding: 0 24px 24px;
}

* {
  -ms-overflow-style: none;
}

::-webkit-scrollbar {
  display: none;
}

@media screen and (max-width: 458px) {
  .git-button {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .member-workflow {
    display: none;
  }
}

.button-right-container {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.member-modal-container {
  padding: 24px;
  display: flex;
}

img {
  width: 196px;
  height: 196px;
  border-radius: 5px;
}

.member-info-container {
  margin-left: 18px;
  margin-top: 26px;
  display: flex;
  flex-direction: column;
}

.member-info {
  gap: 12px;
  display: flex;
}

.member-name {
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  line-height: 28px;
}

.member-workflow {
  padding-top: 13px;
  color: #303030;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
}

.member-introduce {
  margin-top: 14px;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  max-height: 80px;
  overflow: scroll;
}

.button-wrapper {
  display: flex;
  flex: 1;
  justify-content: flex-end;
}

.button-container {
  display: flex;
  align-items: flex-end;
  width: 100%;
}
</style>