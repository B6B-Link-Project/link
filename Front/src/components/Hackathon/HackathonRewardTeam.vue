<script setup lang="ts">
//@ts-nocheck
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import {PropType, watchEffect} from "vue";
import {WinnerProjectResponseDto} from "@/dto/tmpDTOs/hackathonDTO.ts";
import {Builder} from "builder-pattern";

const props = defineProps({
  data: {
    type: Object as PropType<WinnerProjectResponseDto>,
    required: true
  },
  id: {
    type: Number,
    required: true
  }
});
watchEffect(() => {
  if (props.data?.winnerProjectInfoDto == undefined && props.data.length == 0) {
    return;
  }
});
</script>

<template v-if="props.data?.winnerProjectInfoDto">
  <div class="container">
    <img :src="props.data?.winnerProjectInfoDto?.imgSrc" alt=""/>
    <h1>프로젝트 설명</h1>
    <section>
      {{ props.data.winnerProjectInfoDto?.projectDesc }}
    </section>
    <h1>팀원 목록</h1>
    <div class="member-img-container">
      <img :src=member.userImageUrl alt=""
           class="member-img" v-for="member in props.data.teamResponseDto?.members">
    </div>
    <h1>기술 스택</h1>
    <div class="skill-container">
      <div v-for="skill in props.data?.teamResponseDto?.teamSkills">
        <SkillIcon :skill="Builder<SkillDTO>().skillName(skill.skillName).skillImgUrl(skill.skillImageUrl).build()"
                   width="52px"
                   height="52px" radius="10px"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skill-container {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: left;
}

.member-img-container {
  display: flex;
  gap: 22px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.member-img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  border: 1px solid #303030;
}

section {
  color: #303030;
  text-align: justify;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 24px; /* 150% */
  margin-top: 8px;
}

h1 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  line-height: 22.4px; /* 112% */
  margin-top: 20px;
}

.container {
  margin-top: 27px;
  border-radius: 10px;
  border: 2px solid #7D3BFF;
  background: #FFF;
  padding: 24px;
  margin-bottom: 60px;
  flex: 1;
}

img {
  object-fit: cover;
  height: 250px;
  width: 100%;
  max-width: 379px;
  border-radius: 5px;
}
</style>