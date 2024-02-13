<script lang="ts" setup>
import {PropType} from "vue";
import {ProjectInfoDTO} from "@/dto/projectDTO.ts";
import router from "@/router";
import {ProjectService} from "@/api/ProjectService.ts";
import ProjectStore from "@/store/projectStorage.ts";

const projectService: ProjectService = ProjectStore.getters.getProjectService;

const props = defineProps({
  dataList: {
    type: Object as PropType<ProjectInfoDTO[]>,
    required: true
  }
});

const starClick = async (data: ProjectInfoDTO, projectId: number) => {
  if (data.starred) {
    await projectService.unlikeProject(projectId);
    data.starCount--;
    data.starred = false;
  } else {
    await projectService.likeProject(projectId);
    data.starCount++;
    data.starred = true;
  }
};

const toProjectDetail = (id: number) => {
  router.push({name: 'projectDetailById', params: {id: id}})
}

const shortEncoding = (text: string, cut: number): string => {
  if (text.length > cut)
    return text.substring(0, cut) + "...";
  else
    return text
}
</script>

<template>
  <div v-for="(data) in props.dataList" class="project-card">
    <div>
      <img :src="data?.imgSrc" alt="" @click="toProjectDetail(data.projectId)">
    </div>
    <div class="content-container">
      <div class="name-container">
        <div class="project-name" @click="toProjectDetail(data.projectId)">{{ data?.projectName }}</div>
        <div class="right-box-container">
          <div class="star-container" @click="starClick(data, data?.projectId)">
            <svg v-show="!data.starred" fill="none" height="15" viewBox="0 0 16 15" width="16"
                 xmlns="http://www.w3.org/2000/svg">
              <path
                  d="M6.20653 1.63397C6.94016 0.14748 9.05984 0.14748 9.79347 1.63397L10.4736 3.01203C10.7649 3.60232 11.328 4.01146 11.9795 4.10611L13.5002 4.3271C15.1407 4.56547 15.7957 6.58141 14.6087 7.73848L13.5082 8.81115C13.0368 9.27063 12.8218 9.93263 12.933 10.5814L13.1928 12.096C13.473 13.7299 11.7582 14.9758 10.2909 14.2044L8.93068 13.4893C8.34803 13.183 7.65197 13.183 7.06932 13.4893L5.70909 14.2044C4.24184 14.9758 2.52697 13.7299 2.80719 12.096L3.06697 10.5814C3.17825 9.93263 2.96315 9.27063 2.49178 8.81115L1.39133 7.73848C0.2043 6.58141 0.85932 4.56547 2.49976 4.3271L4.02054 4.10611C4.67196 4.01146 5.23509 3.60232 5.52641 3.01203L6.20653 1.63397Z"
                  fill="#D9D9D9"/>
            </svg>
            <svg v-show="data.starred" fill="none" height="15" viewBox="0 0 16 15" width="16"
                 xmlns="http://www.w3.org/2000/svg">
              <path
                  d="M6.20653 1.63397C6.94016 0.14748 9.05984 0.14748 9.79347 1.63397L10.4736 3.01203C10.7649 3.60232 11.328 4.01146 11.9795 4.10611L13.5002 4.3271C15.1407 4.56547 15.7957 6.58141 14.6087 7.73848L13.5082 8.81115C13.0368 9.27063 12.8218 9.93263 12.933 10.5814L13.1928 12.096C13.473 13.7299 11.7582 14.9758 10.2909 14.2044L8.93068 13.4893C8.34803 13.183 7.65197 13.183 7.06932 13.4893L5.70909 14.2044C4.24184 14.9758 2.52697 13.7299 2.80719 12.096L3.06697 10.5814C3.17825 9.93263 2.96315 9.27063 2.49178 8.81115L1.39133 7.73848C0.2043 6.58141 0.85932 4.56547 2.49976 4.3271L4.02054 4.10611C4.67196 4.01146 5.23509 3.60232 5.52641 3.01203L6.20653 1.63397Z"
                  fill="#7D3CFF"/>
            </svg>
            <span class="star-text">{{ data?.starCount }}</span>
          </div>
        </div>
      </div>
      <div class="project-content" @click="toProjectDetail(data.projectId)">{{ shortEncoding(data?.projectDesc, 70) }}</div>
    </div>

  </div>
</template>

<style scoped>
.content-container {
  flex: 0 1 auto;
  max-width: calc(100% - 120px);
  position: relative;
}
.name-container {
  display: flex;
}

.project-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  color: #303030;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  width: 100%;
}

.project-content {
  line-height: 18px;
  margin-top: 9px;
  color: #303030;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
}

.star-text {
  color: #303030;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  margin-left: 6px;
  line-height: 15px;
}

.star-container {
  display: flex;
  justify-content: right;
}

.right-box-container {
  flex: 1;
  margin-left: 8px;
}

img {
  width: 100px;
  height: 100px;
  border-radius: 5px;
  object-fit: cover;
}

.project-card {
  padding: 12px;
  flex: 1;
  min-width: 310px;
  height: 124px;
  border-radius: 10px;
  border: 1px solid #DEDEDE;
  display: flex;
  transition: transform 0.3s ease;
  gap: 16px;
  position: relative;
}

.project-card:hover {
  transform: scale(1.005);
  border: #7d3bff 1px solid;
  box-shadow: 4px 4px 6px 0 rgba(0, 0, 0, 0.25);
}
</style>