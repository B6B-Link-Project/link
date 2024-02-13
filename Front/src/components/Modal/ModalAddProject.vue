<script lang="ts" setup>

import {ref} from "vue";
import {ProjectService} from "@/api/ProjectService.ts";
import {ProjectRequestDto} from "@/dto/projectDTO.ts";
import {Builder} from "builder-pattern";

const props = defineProps({
  teamId: {
    type: Number,
    required: true
  }

})

const projectRequestDto: ProjectRequestDto = Builder<ProjectRequestDto>()
    .build();
const projectService = new ProjectService();
const newImage = ref();
const projectName = ref('');
const projectDesc = ref('');
const projectUrl = ref('');

const handleRegistration = () => {
  projectRequestDto.projectName = projectName.value;
  projectRequestDto.projectDesc = projectDesc.value;
  projectRequestDto.projectUrl = projectUrl.value;
  projectRequestDto.teamId = props.teamId;

  projectService.createProject(projectRequestDto, newImage.value.files[0]);
  location.href = "/myPage";
}

</script>

<template>
  <div class="container">
    <h1>프로젝트 생성</h1>
    <h2>프로젝트 이름</h2>
    <input class="input" type="text" v-model="projectName">
    <h2>프로젝트 설명</h2>
    <textarea class="input" placeholder="프로젝트 설명 및 추구하는 방향에 대해 자유롭게 적어주세요" style="height: 177px"
              v-model="projectDesc"></textarea>
    <h2>GitHub Url</h2>
    <input class="input" type="text" v-model="projectUrl">
    <h2>프로필 이미지</h2>
      <input type="file" ref="newImage"
             id="profile">
    <div class="button" @click="handleRegistration">등록하기</div>
  </div>
</template>

<style scoped>
.button {
  color: #F2F2F2;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: 700;
  height: 40px;
  line-height: 39px;
  border: #7D3BFF solid 1px;
  border-radius: 5px;
  background: #7D3BFF;
  transition: 0.3s color ease;
}

.button:hover {
  color: #7D3CFF;
  background: white;
}

textarea {
  resize: none;
}

h1 {
  color: #303030;
  text-align: center;
  font-size: 28px;
  font-style: normal;
  font-weight: 600;
  line-height: normal;
  margin-bottom: 30px;
}

h2 {
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.input {
  width: 476px;
  height: 40px;
  border-radius: 5px;
  border: 1px solid #000;
  font-family: Pretendard-Regular, serif;
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding: 10px;
}

.container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 20px;
}

@media screen and (max-width: 698px) {
  .input {
    width: 100%;
  }
}
</style>