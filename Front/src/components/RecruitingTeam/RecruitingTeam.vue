<script lang="ts" setup>

import IndexSubMenu from "@/components/Index/IndexSubMenu.vue";
import {onMounted, ref, Ref} from "vue";
import {HackathonTeamInfo1DTO} from "@/dto/tmpDTOs/HackathonTeamDTO.ts";
import {TeamService} from "@/api/TeamService.ts";
import RecruitingTeamCard1 from "@/components/RecruitingTeam/RecruitingTeamCard1.vue";

const refTeam: Ref<HackathonTeamInfo1DTO | undefined> = ref();
const teamService = new TeamService();
onMounted(async () => {
  refTeam.value = await teamService.getRecruitTeam();
})

</script>

<template>
  <IndexSubMenu class="recruiting-container" title="모집중인 팀"/>
  <div class="recruiting-content-container">
    <template v-for="data in refTeam">
      <RecruitingTeamCard1 :data="data"/>
    </template>
  </div>


</template>

<style scoped>
.recruiting-container {
  margin-top: 48px;
  margin-bottom: 23px;
}

.recruiting-content-container {
  height: max-content;
  flex-wrap: wrap;
  gap: 21px;
  display: flex;
  flex-direction: row;
}
</style>