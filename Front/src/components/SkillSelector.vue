<script lang="ts" setup>
import {computed, onMounted, ref} from "vue";
import minus from "@/assets/minus.png";
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import {Builder} from "builder-pattern";
import store from "@/store";
import {UserSkillDTO} from "@/dto/tmpDTOs/UserSkillDTO.ts";
import {SkillRequestDto} from "@/dto/tmpDTOs/skillDTO.ts";
import {UserService} from "@/api/UserService.ts";

const userService = new UserService();

const getSkills = computed(async () => {
  const response = await userService.getSkills();
  return response;
});

const skills = ref<SkillRequestDto[]>([]);
const skillNames = ref<string[]>([]);

onMounted(async () => {
  skills.value = await getSkills.value;
  skillNames.value = skills.value.map(skill => skill.skillName);
});

const year = ref<number>(0);
const skillSelectList = ref<UserSkillDTO[]>([]);
const select = ref(0);

const dropdownOpen = ref(false);
const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};

const clickDropdownMenu = (item: number) => {
  select.value = item;
};

const removeSkill = (skillId: number) => {
  const index = skillSelectList.value.findIndex((data) => data.skill.skillId === skillId)
  if (index !== -1) {
    skillSelectList.value.splice(index, 1);
  }
  saveSkill();
}

const addSkill = function (i: number) {

  if (year.value <= 0) {
    alert("연차를 제대로 설정해주세요");
    return;
  }

  const targetSkill = skills.value[i];
  const data = Builder<UserSkillDTO>()
      .skill(Builder<SkillRequestDto>()
          .skillId(targetSkill.skillId)
          .skillName(targetSkill.skillName)
          .skillImageUrl(targetSkill.skillImageUrl)
          .skillType(targetSkill.skillType)
          .build()
      )
      .skillLevel(year.value)
      .build();

  removeSkill(i+1);
  skillSelectList.value.push(data);
  saveSkill();
}

const saveSkill = function () {
  store.commit('setSkillSelectList', skillSelectList.value);
}

</script>

<template>
  <div style="display: flex; gap: 16px;">
    <div class="dropdown-container" @click="toggleDropdown">
      <div class="dropdown-box">
        <div style="flex: 1; text-align: left">{{ skills[select]?.skillName }}</div>
        <svg fill="none" height="6" style="margin:auto" viewBox="0 0 12 6" width="12"
             xmlns="http://www.w3.org/2000/svg">
          <path d="M1 1L6 5L11 1" stroke="#303030" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/>
        </svg>
      </div>
      <div v-if="dropdownOpen" class="dropdown-content">
        <div v-for="(item, index) in skills" :key="index" @click="clickDropdownMenu(index)">{{ item.skillName }}</div>
      </div>
    </div>
    <div class="text-input">
      <input class="year-input" v-model="year" type="text"><span>년차</span>
    </div>
    <div @click="addSkill(select)" class="button pp" style="width: 10px;">추가</div>
  </div>
  <template v-for="skill in skillSelectList">
    <div class="my-skill-container">
      <SkillIcon :skill="
          Builder<SkillDTO>()
          .skillId(skill.skill.skillId)
          .skillName(skill.skill.skillName)
          .skillType(skill.skill.skillType)
          .skillImgUrl(skill.skill.skillImageUrl)
          .build()" height="24px" radius="5px" style="margin: auto" width="24px"/>
      <div style="flex: 5; margin: auto">{{ skill.skill.skillName }}</div>
      <div style="display: flex; border-radius: 5px; border: 1px solid #303030; padding: 6px; width: 65px">
        <div class="year-text" style="white-space: nowrap">{{ skill.skillLevel }} 년차</div>
      </div>
      <img :src="minus" style="width: 16px; height: 16px; margin: auto auto auto 10px;"
           @click="removeSkill(skill.skill.skillId)">
    </div>
  </template>

</template>

<style scoped>
.year-input {
  font-size: 16px;
  color: #2A2A2A;
  width: 20px;
  border: none;
  text-align: right;
  margin-right: 2px
}

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

.dropdown-container {
  flex: 5;
  display: inline-block;
  position: relative;
}

.dropdown-box {
  flex: 5;
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding: 9px 15px 10px;
  border-radius: 5px;
  border: 1px solid #303030;
  width: 100%;
  display: flex;
  height: 40px;
}

.dropdown-content {
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding: 9px 18px 10px;
  border-radius: 5px;
  border: 1px solid #303030;
  position: absolute;
  min-width: max-content;
  z-index: 2;
  background: white;
  top: 0;
  width: 100%;
  max-height: 30vh;
  overflow: scroll;
  overflow-x: hidden;
}

.dropdown-content div {
  text-align: center;
  padding: 8px;
  color: black;
  text-decoration: none;
  display: block;
}

.dropdown-content div:hover {
  background-color: #f1f1f1;
  border-radius: 4px;
}
</style>