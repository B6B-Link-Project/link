<script setup lang="ts">

import SkillIcon from "@/components/Skill/SkillIcon.vue";
import Tier from "@/components/Tier.vue";
import {UserService} from "@/api/UserService.ts";
import {computed, onMounted, ref} from "vue";
import {GetUserDataDTO} from "@/dto/GetUserDataDTO.ts";
import {Builder} from "builder-pattern";

const userService = new UserService();

const data = ref<GetUserDataDTO>();

onMounted(async () => {
  data.value = await userService.getUserData()
});

const name = computed(() => data.value?.name);
const field = computed(() => {
  const fieldValue = data.value?.field;
  if(fieldValue != null){
    return changeToKorean(fieldValue);
  }
  return "초보 개발자";
});

const introduce = computed(() => data.value?.introduce);
const referenceUrl = computed(() => data.value?.referenceUrl);
const Image = computed(() => data.value?.image);
const career = computed(() => data.value?.career);
//const registered = computed(() => data.value?.registered);

function changeToKorean(field: string): string {
  if (field === "FRONTEND") {
    return "프론트엔드 개발자";
  } else if (field === "BACKEND") {
    return "백엔드 개발자";
  } else if (field === "FULLSTACK") {
    return "풀스택 개발자";
  } else if (field === "DESIGN") {
    return "디자이너";
  } else {
    return "프로젝트 매니저";
  }
}

const groupedSkills = computed(() => {
  const grouped: Record<string, SkillDTO[]> = {};
  const skills = data.value?.userSkills; // userSkills를 사용하여 스킬 데이터를 가져옵니다.
  if (!skills) return []; // 유저 스킬 데이터가 없을 경우 빈 배열을 반환합니다.

  // 각 스킬을 해당하는 타입으로 그룹화합니다.
  skills.forEach(skill => {
    const t = skill.skill;
    if (!grouped[t.skillType]) {
      grouped[t.skillType] = [];
    }
    grouped[t.skillType].push(
        Builder<SkillDTO>()
            .skillName(t.skillName)
            .skillType(t.skillType)
            .skillId(t.skillId)
            .skillImgUrl(t.skillImageUrl)
            .build());
  });

  // 그룹화된 스킬을 배열로 변환하여 반환합니다.
  return Object.keys(grouped).map(type => ({
    type,
    skills: grouped[type]
  }));
});

</script>

<template>
  <div class="title-container">
    <h1>내정보</h1>
    <RouterLink to="/editInfo" class="remove-button">수정</RouterLink>
  </div>
  <div class="profile">
    <img alt="" :src="Image?.userImageUrl" class="img">
    <div class="text-container">
      <div class="info-text">
        {{name}}
        <Tier :rating="300" font-size="24px" height="28px" style="margin-left: 12px" width="48px" radius="10px"></Tier>
      </div>
      <div class="introduce-text">
        <a :href="referenceUrl" target="_blank">{{referenceUrl}}</a>
      </div>
      <div class="introduce-text">
        안녕하세요. {{ career }}년차 {{ field }} 입니다.
        {{introduce}}
      </div>
    </div>
  </div>
  <div class="my-skill-container">
    <h1>기술 스택</h1>
    <div v-for="skillGroup in groupedSkills" :key="skillGroup.type">
      <h2>{{ skillGroup.type }}</h2>
      <div class="skill-container">
        <div class="skill-box" v-for="skill in skillGroup.skills" :key="skill.skillName">
          <SkillIcon :skill="skill" height="56px" radius="10px" width="56px"/>
          <h3>{{ skill.skillName }}</h3>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@media screen and (max-width: 530px) {
  .img {
    display: none;
  }
}
.my-skill-container {
  margin-bottom: 60px;
}
.skill-container {
  display: flex;
  gap: 26px;
  flex-wrap: wrap;
  justify-content: left;
  max-width: 700px;
  margin-bottom: 20px;
}

.skill-box {
  display: flex;
  gap: 12px;
}

h3 {
  line-height: 55px;
}

h2 {
  color: #303030;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  margin-bottom: 16px;
}

.text-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.introduce-text {
  max-width: 440px;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: 22px;
}

.profile {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.img {
  border-radius: 10px;
  width: 200px;
  height: 200px;
}

.info-text {
  display: flex;
  color: #303030;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  line-height: 28px;
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
}

.remove-button {
  width: 80px;
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
</style>