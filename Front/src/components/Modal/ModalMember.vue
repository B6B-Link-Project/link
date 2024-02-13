<script lang="ts" setup>

import Tier from "@/components/Tier.vue";
import ModalGithubButton from "@/components/Modal/ModalGithubButton.vue";
import ModalSkill from "@/components/Modal/ModalSkill.vue";
import {PropType} from "vue";
import {TeamFindSkillDTO, TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";
import {Builder} from "builder-pattern";

const props = defineProps({
  userInfo: {
    type: Object as PropType<TeamMemberFindUserDTO>,
    default: Builder<TeamMemberFindUserDTO>().build()
  }
});
import {computed} from 'vue';

// Computed property to group skills by skillType
const groupedSkills = computed(() => {
  const groupedSkills: Record<string, { skillType: string; skills: TeamFindSkillDTO[] }> = {};
  props.userInfo.skillSets.forEach((skill) => {
    if (!groupedSkills[skill.skillType]) {
      groupedSkills[skill.skillType] = {skillType: skill.skillType, skills: []};
    }
    groupedSkills[skill.skillType].skills.push(skill);

  });

  return Object.values(groupedSkills);
});
</script>

<template>
  <div class="member-modal-container">
    <img class="profile" alt="" :src="userInfo.userImageUrl">
    <div class="member-info-container">
      <div class="member-info">
        <div class="member-name">{{ userInfo.name }}</div>
        <div class="member-workflow">{{ userInfo.career }}년차 {{ userInfo.field }}</div>
        <Tier :rating="userInfo.rating" font-size="24px" height="28px" radius="10px" width="48px"/>
      </div>
      <div class="member-introduce">
        {{ userInfo.introduce }}
      </div>
      <div class="button-wrapper">
        <div class="button-container">
          <a :href="userInfo.referenceUrl" target="_blank" class="git-button">
            <ModalGithubButton/>
          </a>
          <div class="button-right-container">
            <slot/>
          </div>
        </div>
      </div>
    </div>
  </div>
  <h2 style="padding-left: 23px">기술 스택</h2>
  <div class="member-skill-container">
    <template v-for="skillGroup in groupedSkills" :key="skillGroup.skillType">
      <div class="skill-list-container">
        <ModalSkill :skillInfo="skillGroup.skills" :title="skillGroup.skillType"/>
      </div>
    </template>
  </div>
</template>

<style scoped>
.skill-list-container {
  display: flex;
  gap: 26px;
  margin-bottom: 16px;
  min-width: max-content;
  flex: 1;
}

h2 {
  color: #303030;
  font-size: 21px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 16px;
}

.member-skill-container {
  max-height: 185px;
  overflow: scroll;
  margin-bottom: 23px;
  padding: 0 24px 0px;
  display: flex;
  max-width: 100%;
  flex-wrap: wrap;
  overflow-x: hidden;
  gap: 16px;
}

@media screen and (max-width: 458px) {
  .git-button {
    display: none;
  }

  .profile {
    object-fit: cover;
    width: 150px;
    height: 150px;
  }

  .button-container {
    margin-top: 15px;
  }
}

@media screen and (max-width: 698px) {
  .member-skill-container {
    height: max-content;
    max-height: max-content;
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
  object-fit: cover;
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
  max-width: 382px;
  margin-top: 14px;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  max-height: 80px;
  height: max-content;
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
