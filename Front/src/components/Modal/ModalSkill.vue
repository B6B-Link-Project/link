<script lang="ts" setup>
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import {PropType} from "vue";
import {TeamFindSkillDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";
import {Builder} from "builder-pattern";

defineProps({
  skillInfo: {
    type: Object as PropType<TeamFindSkillDTO[]>,
    default: Builder<TeamFindSkillDTO[]>().build()
  },
  title: Object as PropType<string>
})
</script>

<template>
  <div class="modal-skill-container">
    <div :style="{
      color: '#303030',
      fontSize: '16px',
      fontWeight: 400,
      marginBottom: '12px'
    }">
      {{ title }}
    </div>
    <div class="skill-container">
      <SkillIcon v-for="skill in skillInfo"
                 :skill="Builder<SkillDTO>().skillImgUrl(skill.skillImageUrl)
                 .skillYear(skill.skillLevel)
                 .skillName(skill.skillName)
                 .build()" font-size="28px" height="48px" radius="10px" width="48px">
      </SkillIcon>
    </div>
  </div>
</template>

<style scoped>
.modal-skill-container {
  max-width: 290px;
  width: 100%;
}

.skill-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>