<script lang="ts" setup>

import ProjectCenter from "@/components/Project/ProjectCenter.vue";
import {Builder} from "builder-pattern";
import SkillIcon from "@/components/Skill/SkillIcon.vue";
import ProjectTeam from "@/components/Project/ProjectTeam.vue";
import ProjectLink from "@/components/Project/ProjectLink.vue";
import {onMounted, Ref, ref, watch} from "vue";
import {ProjectDetailDto, ProjectRequestDto} from "@/dto/projectDTO.ts";
import ProjectStore from "@/store/projectStorage.ts";
import {ProjectService} from "@/api/ProjectService.ts";
import {useRoute} from "vue-router";
import router from "@/router";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";
import {TeamMemberResponseDto, TeamSkillDto} from "@/dto/tmpDTOs/teamDTO.ts";
import {SkillType} from "@/dto/tmpDTOs/commonDTO.ts";


interface TeamRefs {
  skillsRef: Ref<TeamSkillDto[]>
  membersRef: Ref<TeamMemberResponseDto[]>
  skillsMapRef: Ref<Map<SkillType, TeamSkillDto[]>>
}

const route = useRoute();

const teamBuildingService: TeamBuildingService = new TeamBuildingService();
const teamRefs: TeamRefs = {
  skillsRef: ref([]),
  membersRef: ref([]),
  skillsMapRef: ref(new Map<SkillType, TeamSkillDto[]>()),
}

const initTeamRefs = async (teamId: number) => {
  const teamResponseDto = await teamBuildingService.getTeamDetailByTeamId(teamId);
  teamRefs.skillsRef.value = teamResponseDto?.teamSkills;
  teamRefs.membersRef.value = teamResponseDto.members;
  const m = teamRefs.skillsMapRef.value;
  teamRefs.skillsRef.value.map((teamSkillDto) => {
    const t = teamSkillDto.skillType;
    if (!m.has(t))
      m.set(t, []);
    m.get(t)?.push(teamSkillDto);
  });
}

const projectService: ProjectService = ProjectStore.getters.getProjectService;
const projectDetail: Ref<ProjectDetailDto> = ref({} as ProjectDetailDto);

const projectRequestDto: ProjectRequestDto = Builder<ProjectRequestDto>()
    .build();

const projectImg = ref();

const updateProject = (key: string, url: string) => {
  if (key === 'projectUrl')
    projectRequestDto.projectUrl = projectDetail.value.projectUrl = url;
  else if (key === 'deployUrl') {
    projectRequestDto.deployUrl = projectDetail.value.deployUrl = url;
  }
  projectService.updateProject(projectDetail.value.projectId, projectRequestDto, null);
}

const init = async () => {
  try {
    projectDetail.value = await projectService.getProjectDetail(parseInt(route.params.id as string));
  } catch (error) {
    alert("잘못된 링크입니다!!");
    await router.push('/');
  }
  projectRequestDto.projectName = projectDetail.value.projectName;
  projectRequestDto.teamId = projectDetail.value.teamId;
  projectRequestDto.projectDesc = projectDetail.value.projectDesc;
  projectRequestDto.projectUrl = projectDetail.value.projectUrl;
  projectRequestDto.deployUrl = projectDetail.value.deployUrl
  await initTeamRefs(projectDetail.value.teamId);
}

// FIXME: 내 프로젝트가 하나도 없을 경우 처리 필요

onMounted(() => init());
watch(() => route.path, () => init());

// TODO: 해당 팀을 소유한 리더인지 확인 필요
const isLeader = ref(false);

</script>


<template>
  <div class="detail-container">
    <div class="side-container">
      <img class="project-image"
           :src="projectDetail.imgSrc"
           ref="projectImg" alt="">
      <ProjectLink :project-detail="projectDetail" :update-project="updateProject" :editable="isLeader"/>
    </div>
    <project-center :project-detail="projectDetail" :editable="isLeader"/>
    <div class="side-container">
      <div>
        <h1>기술스택</h1>
        <div v-for="[key, value] in teamRefs.skillsMapRef?.value" class="stack-container">
          <h2>{{ key }}</h2>
          <div class="skill-list-container">
            <div v-for="skill in value">
              <SkillIcon
                  :skill="Builder<SkillDTO>()
                  .skillName(skill.skillName)
                  .skillType(skill.skillType)
                  .skillImgUrl(skill.skillImageUrl)
                  .build()" height="28px" radius="3px" style="border-bottom: 1px solid" width="28px"/>
            </div>
          </div>
        </div>
      </div>
      <!-- TODO: 프로젝트 팀 아이디 넘겨서 팀 목록 호출하기 -->
      <ProjectTeam :team-member-dtos="teamRefs.membersRef.value"/>
    </div>
  </div>
</template>


<style scoped>


.stack-container {
  margin-top: 10px;
  display: flex;
  gap: 20px;
  max-width: 250px;
  width: 100%;
}

h1 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 28px;
}

h2 {
  margin-left: 8px;
  color: #303030;
  font-size: 16px;
  font-style: normal;
  font-weight: 500;
  line-height: 28px;
  white-space: nowrap;
  width: 80px;
}

.skill-list-container {
  margin-bottom: 20px;
  max-width: 250px;
  flex: 1;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

img {
  width: 250px;
  height: 250px;
  object-fit: cover;
  border-radius: 10px;
}

.side-container {
  min-width: 250px;
  height: max-content;
}

@media screen and (max-width: 943px) {
  .detail-container {
    flex-direction: column;
  }

  .project-image {
    width: 100%;
  }
}

.detail-container {
  display: flex;
  gap: 20px;
}
</style>

