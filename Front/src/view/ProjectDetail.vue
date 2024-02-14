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
import {IssueCountResponseDTO} from "@/dto/tmpDTOs/IssueCountResponseDTO.ts";


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
const issueCount: Ref<IssueCountResponseDTO | undefined> = ref();
const isMyProject: Ref<Boolean | undefined> = ref();

onMounted(async () => {
  issueCount.value = await projectService.getIssueCount(parseInt(<string>route.params.id));
  isMyProject.value = await projectService.getIsMyProject(parseInt(<string>route.params.id));
})

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
  projectService.updateProject(projectDetail.value.projectId, projectRequestDto, newImage.value.files[0]);
}

const changeProjectImage = () => {
  projectDetail.value.imgSrc = URL.createObjectURL(newImage.value.files[0]);
  projectService.updateProject(projectDetail.value.projectId, projectRequestDto, newImage.value.files[0]);
}

const init = async () => {
  try {
    projectDetail.value = await projectService.getProjectDetail(parseInt(route.params.id as string));
  } catch (error) {
    await router.push('/myPage?mode=3');
  }
  projectRequestDto.projectName = projectDetail.value.projectName;
  projectRequestDto.teamId = projectDetail.value.teamId;
  projectRequestDto.projectDesc = projectDetail.value.projectDesc;
  projectRequestDto.projectUrl = projectDetail.value.projectUrl;
  projectRequestDto.deployUrl = projectDetail.value.deployUrl
  await initTeamRefs(projectDetail.value.teamId);
  isLeader.value = await projectService.checkLeader(projectDetail.value.projectId);
}

onMounted(() => init());
watch(() => route.path, () => init());

// TODO: 해당 팀을 소유한 리더인지 확인 필요
const isLeader = ref(false);

const newImage = ref();

</script>


<template>
  <div class="detail-container">
    <div class="side-container">
      <img class="project-image"
           :src="projectDetail.imgSrc"
           ref="projectImg" alt="">

      <template v-if="isLeader">
        <h1 style="margin: 10px 0">이미지 변경</h1>
        <div class="link-content-container">
          <input ref="newImage" id="input"
                 type="file" name="image" accept="image/*" :multiple="false" @change="changeProjectImage">
          <!--      <input v-model="edit.url"  :placeholder="edit.text + ' 입력'">-->
        </div>
      </template>
      <ProjectLink :project-detail="projectDetail" :update-project="updateProject" :editable="isLeader"/>
      <template v-if="isMyProject">
        <h1 class="redmineTitle">레드마인</h1>
        <div class="table">
          <ul>
            <li>할일</li>
            <li>진행중</li>
            <li>완료</li>
          </ul>
          <ul>
            <li>
              <template v-if="issueCount?.issueCount['할 일']">
                {{ issueCount?.issueCount['할 일'] }}
              </template>
              <template v-else>
                0
              </template>
            </li>
            <li>
              <template v-if="issueCount?.issueCount['진행중']">
                {{ issueCount?.issueCount['진행중'] }}
              </template>
              <template v-else>
                0
              </template>
            </li>
            <li>
              <template v-if="issueCount?.issueCount['완료']">
                {{ issueCount?.issueCount['완료'] }}
              </template>
              <template v-else>
                0
              </template>
            </li>
          </ul>
        </div>
<!--        <a :href="`http://localhost:9090/projects/redmine${route.params.id}`" target="_blank" class="issueButton">이슈-->
<!--          관리하기</a>-->
        <a :href="`http://i10a602.p.ssafy.io:3000/projects/redmine${route.params.id}`" target="_blank" class="issueButton">이슈
          관리하기</a>
      </template>
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
                  .build()" height="28px" radius="3px" width="28px"/>
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
  max-width: 300px;
  flex: 1;
  display: flex;
  gap: 8px;
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

input {
  padding-left: 12px;
  height: 36px;
  flex: 1;
  color: #606060;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.link-content-container {
  display: flex;
  gap: 12px;
}

.table {
  border: 1px solid #b2b2b2;
  border-radius: 10px;
}

.table ul {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #b2b2b2;
}

.table ul:first-child li {
  font-weight: 700;
}

.table ul:last-child {
  border-bottom: none;
}

.table ul li {
  list-style: none;
  width: calc(100% / 3);
  text-align: center;
  padding: 10px;
  border-right: 1px solid #b2b2b2;
}

.table ul li:last-child {
  border-right: none;
}

h1.redmineTitle {
  margin-top: 16px;
  margin-bottom: 12px;
}

a.issueButton {
  margin-top: 12px;
  display: block;
  height: 34px;
  border-radius: 5px;
  background: #7d3bff;
  border: #7d3bff solid 1px;
  line-height: 33px;
  color: #FFF;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: lighter;
  transition: 0.3s ease color;
  text-decoration: none;
}
</style>

