<script lang="ts" setup>
import Tier from "@/components/Tier.vue";
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import {Builder} from "builder-pattern";
import {PropType} from "vue";
import {TeamMemberFindUserDTO} from "@/dto/tmpDTOs/teamBuildingDTO.ts";

const props = defineProps({
  t: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object as PropType<TeamMemberFindUserDTO>,
    default: Builder<TeamMemberFindUserDTO>().build()
  }
});
const shortEncoding = (text: string, cut: number): string => {
    if (text.length >= cut)
      return text.substring(0, cut) + "...";
    else
      return text
}

const userInfo = props.userInfo;

</script>
<template>
  <div :class="{'t':!props.t}" class="user-card">
    <img alt="" v-bind:src="userInfo.profileImageURL">
    <div class="text-container">
      <div class="info-text">

      {{ userInfo.name }}
        <Tier :rating="userInfo.rating" font-size="14px" height="16px" style="margin-left: 8px" width="28px"></Tier>
      </div>
      <div class="introduce-text">
        <template v-if="userInfo.introduce">
        {{ shortEncoding(userInfo.introduce, 40) }}
        </template>
      </div>
      <div style="display: flex; gap:4px; flex: 1">
        <template v-for="data in userInfo.skillSets?.slice(0,8)">
          <SkillIcon :skill="
          Builder<SkillDTO>().skillType(data.skillType)
          .skillYear(data.skillLevel)
          .skillName(data.skillName)
          .skillImgUrl(data.skillImageUrl).build()" height="20px" radius="4px" width="20px"/>
        </template>
        <!--        <SkillIcon :skill="userInfo.skillSets" height="20px" radius="4px" width="20px"/>-->
        <!--        <SkillIcon :skill="dummySkill" height="20px" radius="4px" width="20px"/>-->
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-container {
  display: flex;
  flex-direction: column;
  flex: 1;
}

img {
  object-fit: cover;
  width: 76px;
  height: 76px;
  border-radius: 5px;
  margin-right: 8px;
}

.introduce-text {
  flex: 1;
  color: #303030;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: 16px;
  margin-top: 4px;
  margin-bottom: 4px;
  width: 100%;
}

.info-text {
  flex: 1;
  display: flex;
  color: #303030;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
}

.user-card {
  display: flex;
  padding: 12px;
  min-width: 310px;
  width: 100%;
  flex: 1;
  height: 100px;
  border-radius: 10px;
  border: 1px solid #DEDEDE;
  transition: transform 0.3s ease;
}

.t:hover {
  transform: scale(1.01);
  border: #7d3bff 1px solid;
  box-shadow: 4px 4px 6px 0 rgba(0, 0, 0, 0.25);
}
</style>