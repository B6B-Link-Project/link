<script lang="ts" setup>
import {computed, onMounted, ref, Ref} from "vue";
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

onMounted(async () => {
  skills.value = await getSkills.value;
});

const skillSelect: Set<Number> = new Set<Number>();
const skillSelectRef: Ref<Set<Number>> = ref(skillSelect);
const year = ref<number>(0);
const index = ref<number>(0);
const selectedSkillId = ref<number>(0);
const skillSelectList = ref<UserSkillDTO[]>([]);

const handleSkillSelect = () => {
  // 공통 db 전용
  // index.value = selectedSkillId.value - 5;

  //내 db 전용
  index.value = selectedSkillId.value;

}

const removeSkill = (skillId:number) => {
  const index = skillSelectList.value.findIndex((data)=> data.skill.skillId === skillId)
  if (index !== -1) {
    skillSelectList.value.splice(index, 1);
  }
}

const addSkill = function (i: number){

  if (year.value <= 0){
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

  removeSkill(i);

  const tmp1 = skillSelectRef.value.size;
  skillSelectRef.value.add(i);
  const tmp2 = skillSelectRef.value.size;

  // 둘이 같으면 중복 스킬임
  if(tmp1 === tmp2){
    return;
  }
  skillSelectList.value.push(data);
}

const saveSkill = function (){
  store.commit('setSkillSelectList', skillSelectList.value);
}

</script>

<template>
  <div class="detail-content-container">
    <div style="width: 100%">
      <div class="text-input" style="display: flex;">
        <select class="" v-model="selectedSkillId" style="border: none; font-size: 16px; width: 100%" @change="handleSkillSelect">
          <option v-for="skill in skills" :value="skill.skillId">{{ skill.skillName }}</option>
        </select>
        <input v-model="year" style="border: none; font-size: 16px; width: 20%" type="number">
      </div>
      <template v-for="skill in skillSelectList">
        <div class="my-skill-container">
          <SkillIcon :skill="
          Builder<SkillRequestDto>()
          .skillId(skill.skill.skillId)
          .skillName(skill.skill.skillName)
          .skillType(skill.skill.skillType)
          .build()" height="24px" radius="5px" style="margin: auto" width="24px"/>
          <div style="flex: 5; margin: auto">{{ skill.skill.skillName }}</div>
          <div style="display: flex; border-radius: 5px; border: 1px solid #303030; padding: 6px; width: 65px">
            <div class="year-text" style="white-space: nowrap">{{skill.skillLevel}} 년차</div>
          </div>
          <img :src="minus" style="width: 16px; height: 16px; margin: auto auto auto 10px;"
               @click="removeSkill(skill.skill.skillId)">
        </div>
      </template>
    </div>
    <div @click = "addSkill(index)" class="button pp">추가</div>
    <div @click = "saveSkill" class="button pp">선택</div>
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