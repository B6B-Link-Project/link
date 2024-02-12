<script lang="ts" setup>

import SkillCategory from "@/components/Skill/SkillCategory.vue";
import SkillCategoryList from "@/components/Skill/SkillCategoryList.vue";
import {Builder} from "builder-pattern";
import {onMounted, provide, Ref, ref, watch} from "vue";
import Modal from "@/components/Modal/Modal.vue";
import ModalAddTeam from "@/components/Modal/ModalAddteam.vue";
import {useRoute} from "vue-router";
import {HackathonService} from "@/api/HackathonService.ts";
import {HackathonTeamDTO} from "@/dto/tmpDTOs/HackathonTeamDTO.ts";
import {SkillCategoryResponseDto} from "@/dto/tmpDTOs/SkillTypeDTO.ts";
import RecruitingTeamCard1 from "@/components/RecruitingTeam/RecruitingTeamCard1.vue";
import PaginationV2 from "@/components/PaginationV2.vue";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";

const route = useRoute();
const teamBuildingService = new TeamBuildingService();
const hackathonService: HackathonService = new HackathonService();
const refTeamList: Ref<HackathonTeamDTO | undefined> = ref();
const refSkillCategory: Ref<SkillCategoryResponseDto | undefined> = ref();
const skillCategoryList: Ref<SkillCategory[]> = ref([]);

defineProps({
  hackathonId: {
    type: Number,
    required: true,
  }
})

onMounted(async () => {
  refSkillCategory.value = await teamBuildingService.getAllSkillType();
  const queryId = route.query.id;
  refTeamList.value = await hackathonService.getAllTeamByHackathon({
    page: isNaN(parseInt(<string>route.query.page)) ? 1 : parseInt(<string>route.query.page),
    size: 9,
    hackathonId: parseInt(<string>queryId)
  })
  if (refSkillCategory.value) {
    const skillTypeResponseDtoMap = refSkillCategory.value;

    for (const category in skillTypeResponseDtoMap) {
      if (Object.prototype.hasOwnProperty.call(skillTypeResponseDtoMap, category)) {
        const skillList: SkillDTO[] = skillTypeResponseDtoMap[category].map((data) => {
          return Builder<SkillDTO>()
              .skillId(data.skillId)
              .skillYear(data.skillYear)
              .skillName(data.skillName)
              .skillImgUrl(data.skillImageUrl)
              .build()
        });
        skillCategoryList.value.push(
            Builder<SkillCategory>()
                .categoryName(category)
                .skillList(skillList)
                .build()
        );
      }
    }
  }
  console.log(refTeamList.value)
})
const memberRecruit = Builder<MemberRecruit>()
    .selectedCategory(-1)
    .skillCategoryList(skillCategoryList.value)
    .selectSkillId(new Set<number>())
    .build()
const refMemberRecruit = ref<MemberRecruit>(memberRecruit);

const updateSelectedCategory = (newCategory: number) => {
  refMemberRecruit.value.selectedCategory = newCategory;
};

const updateSelectSkillId = async (skillId: number) => {
  if (refMemberRecruit.value.selectSkillId.has(skillId))
    refMemberRecruit.value.selectSkillId.delete(skillId)
  else
    refMemberRecruit.value.selectSkillId.add(skillId)

  refTeamList.value = await hackathonService.getAllTeamByHackathon({

    skillIds: Array.from(refMemberRecruit.value.selectSkillId),
    page: isNaN(parseInt(<string>route.query.page)) ? 1 : parseInt(<string>route.query.page),
    size: 9,
    hackathonId: parseInt(<string>route.query.id)
  });
};

watch(
    () => route.query.page,
    async (newPage, oldPage) => {
      if (newPage !== oldPage) {
        refTeamList.value = await hackathonService.getAllTeamByHackathon({
          skillIds: Array.from(refMemberRecruit.value.selectSkillId),
          page: isNaN(parseInt(<string>route.query.page)) ? 1 : parseInt(<string>route.query.page),
          size: 9,
          hackathonId: parseInt(<string>route.query.id)
        })
      }
    }
)

const pageableDto = ref<PageableDto>({
  pageNumber: undefined,
  totalPages: undefined,
});

watch(refTeamList, (newVal) => {
  pageableDto.value.pageNumber = newVal?.number != null ? newVal?.number + 1 : undefined;
  pageableDto.value.totalPages = newVal?.totalPages != null ? newVal?.totalPages : undefined;
});

// const refDummy = ref<MemberRecruit>(dummy);
provide('skillCategorySelect', refMemberRecruit);
provide('updateSelectedCategory', updateSelectedCategory);
provide('updateSelectSkillId', updateSelectSkillId);

const modalRef = ref<Boolean>(false);
const modalController = () => {
  modalRef.value = !modalRef.value
}

</script>

<template>
  <Modal v-if="modalRef" @closeModal="modalController">
    <ModalAddTeam :hackathonId="hackathonId"/>
  </Modal>
  <h1>모집중인 팀</h1>
  <div class="category-button-container">
    <SkillCategory style="flex: 1"/>
    <div class="team-build-button" @click="modalController">팀 생성</div>
  </div>

  <SkillCategoryList/>
  <hr>
  <div class="team-container">
    <template v-for="(data) in refTeamList?.content">
      <RecruitingTeamCard1 :data="data"/>
    </template>
  </div>
  <PaginationV2 style="margin-bottom: 80px" :pageableDTO="pageableDto"/>
</template>

<style scoped>
h1 {
  margin-top: 56px;
  margin-bottom: 20px;
  color: #000;
  font-size: 24px;
  font-style: normal;
  font-weight: 600;
  line-height: 22.4px;
}

hr {
  margin-top: 20px;
  border-bottom: solid 1px #7D3BFF;
  margin-bottom: 20px;
}

.category-button-container {
  display: flex;
}

.team-build-button:hover {
  background: white;
  color: #7D3BFF;
}

.team-build-button {
  transition: color 0.3s ease;
  border-radius: 5px;
  background: #7D3BFF;
  border: 1px #7D3BFF solid;
  width: 80px;
  height: 40px;
  color: #FFF;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: 600;
  line-height: 39px;
}

.team-container {
  display: flex;
  flex-wrap: wrap;
  gap: 21px;
}


</style>