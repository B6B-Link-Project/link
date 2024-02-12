<script setup lang="ts">

import ModalGithubButton from "@/components/Modal/ModalGithubButton.vue";
import Tier from "@/components/Tier.vue";
import ModalSkill from "@/components/Modal/ModalSkill.vue";
import {MemberDetailResponseDto} from "@/dto/tmpDTOs/memberDTO.ts";
import {PropType, ref, watch} from "vue";
import {TeamFindSkillDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";
import {Builder} from "builder-pattern";

const props = defineProps({
  memberInfo: {
    required: true,
    type: Object as PropType<MemberDetailResponseDto>,
  }
});

const skillMap = ref(new Map<String, TeamFindSkillDTO[]>());

watch(() => props.memberInfo, () => {
  props?.memberInfo?.skillSets.map((s) => {
    if (!skillMap.value.has(s.skillType))
      skillMap.value.set(s.skillType, []);
    skillMap.value.get(s.skillType)?.push(Builder<TeamFindSkillDTO>()
        .skillName(s.skillName)
        .skillImageUrl(s.skillImageUrl)
        .skillLevel(s.skillLevel)
        .skillType(s.skillType)
        .build());
  })
});

const changeToKorean = (field: string): string => {
  if (field === "FRONTEND") {
    return "프론트엔드 개발자";
  } else if (field === "BACKEND") {
    return "백엔드 개발자";
  } else if (field === "FULLSTACK") {
    return "풀스택 개발자";
  } else if (field === "DESIGN") {
    return "디자이너";
  } else if (field === "MANAGE") {
    return "프로젝트 매니저";
  } else
    return "";
}

</script>

<template>
  <div class="member-info-container">
    <div class="member-info">
      <div class="member-name">{{ props?.memberInfo?.name }}</div>
      <Tier :rating="props?.memberInfo?.rating" font-size="24px" height="28px" radius="10px" width="48px"/>
      <ModalGithubButton/>
    </div>
    <div class="member-workflow">{{ props?.memberInfo?.career }}년차 {{ changeToKorean(props?.memberInfo?.field) }}</div>
    <div class="member-introduce">
      {{ props?.memberInfo?.introduce }}
    </div>
    <div class="skill-list-container">
      <ModalSkill v-for="[key, value] in skillMap" :skill-info="value" :title="key" font-size="18px"
                  font-weight="500" margin-bottom="10px"/>
    </div>
    <div class="button">
      합류 요청
    </div>
  </div>
</template>

<style scoped>
@media screen and (max-width: 1240px) {
  .skill-list-container {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 20px;
  }
}

@media screen and (min-width: 1241px) {
  .skill-list-container {
    flex-direction: row;
    flex-wrap: wrap;
    gap:12px;
  }
}

.skill-list-container {
  margin-top: 21px;
  margin-bottom: 22px;
  display: flex;
  overflow: auto;
  height: auto;
  max-height: 350px;
}

.button {
  height: 40px;
  width: 100%;
  border-radius: 10px;
  background: #7D3BFF;
  border: #7D3BFF solid 1px;
  color: #F2F2F2;
  font-size: 16px;
  font-style: normal;
  font-weight: 700;
  text-align: center;
  line-height: 39px;
  transition: color 0.3s ease;
}

.button:hover {
  background: white;
  color: #2b2b2b;
}

h2 {
  color: #303030;
  font-size: 21px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 16px;
}

* {
  -ms-overflow-style: none;
}

::-webkit-scrollbar {
  display: none;
}


.member-info-container {
  min-width: 288px;
  width: 100%;
  margin-left: 18px;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  height: 100%;
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
  min-height: 100px;
  line-height: 19px;
  margin-top: 14px;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  max-height: max-content;
}
</style>