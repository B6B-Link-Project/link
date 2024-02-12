<script lang="ts" setup>
import minus from "@/assets/minus.png";
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import {inject, PropType} from "vue";
import {TeamSkillAddDto} from "@/dto/tmpDTOs/TeamSkillAddDto.ts";

defineProps({
  skills: Object as PropType<TeamSkillAddDto[]>
})

const skillSelect: skillSelect = inject('skillSelect')!;
const skillSelectRef: skillSelectRef = inject('skillSelectRef')!;
const index: index = inject('index')!;
const selectedSkillId: selectedSkillId = inject('selectedSkillId')!;
const skillSelectList: skillSelectList = inject('skillSelectList')!;
const handleSkillSelect: Function = <Function>inject('handleSkillSelect');
const removeSkill: Function = <Function>inject('removeSkill');
const addSkill: Function = <Function>inject('addSkill');

</script>

<template>
  <div class="detail-content-container">
    <div style="width: 100%">
      <div class="text-input" style="display: flex;">
        <select class="" v-model="selectedSkillId" style="border: none; font-size: 16px; width: 100%" @change="handleSkillSelect">
          <option v-for="(data, i) in skills" :value="i">{{ data.skillName }}</option>
        </select>
      </div>
      <template v-for="i in skillSelectList">
        <div class="my-skill-container">
          <SkillIcon :skill="skills[i]" height="24px" radius="5px" style="margin: auto" width="24px"/>
          <div style="flex: 5; margin: auto">{{ skills?.[i].skillName }}</div>
          
          <img :src="minus" style="width: 16px; height: 16px; margin: auto auto auto 10px;"
               @click="removeSkill(i)" alt="">
        </div>
      </template>
    </div>
    <div @click = "addSkill(index)" class="button pp">추가</div>
  </div>
</template>

<style scoped>
h3 {
  margin-top: 5px;
  color: #606060;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

input {
  outline: none;
}

.pp {
  background: #7D3BFF;
  color: #F2F2F2;
}

.pp:hover {
  background: white;
  color: #303030;
}

.button {
  transition: color 0.3s ease;
  border-radius: 5px;
  border: 1px solid #7D3BFF;
  height: 40px;
  line-height: 39px;
  flex: 1;
  padding-left: 20px;
  padding-right: 20px;
  min-width: max-content;
}

h1 {
  color: #303030;
  text-align: center;
  font-size: 28px;
  font-style: normal;
  font-weight: 600;
  line-height: normal;
  margin-bottom: 40px;
}

h2 {
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  text-align: left;
  margin-bottom: 9px;
}

.detail-content-container {
  display: flex;
  gap: 20px;
}

.text-input {
  width: 100%;
  height: 40px;
  border-radius: 5px;
  border: 1px solid #303030;
  padding: 10px;
  color: #2b2b2b;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.year-text {
  color: #606060;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.my-skill-container {
  text-align: left;
  display: flex;
  color: #303030;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: 24px;
  gap: 6px;
  margin-top: 12px;
}
</style>