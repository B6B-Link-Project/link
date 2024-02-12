<script lang="ts" setup>
import {UserService} from "@/api/UserService.ts";
import FitDropDown from "@/components/FitContent/FitDropDown.vue";
import SkillSelectV from "@/components/SkillSelector.vue";
import {computed, ref} from "vue";
import Store from "@/store";
import {Builder} from "builder-pattern";
import {UserImageDTO} from "@/dto/UserImageDTO.ts";
import {AddUserInfoDTO} from "@/dto/AddUserInfoDTO.ts";

const userService = new UserService();

const email = computed(() => String(Store.state.email));
const skills = computed(() => Store.state.skillSelectList);
const referenceUrl = ref<string>('');
const field = computed(() => String(Store.state.field));
const career = ref<number>(0);
const registered = ref<boolean>(false);
const introduce = ref<string>('');
const image = Builder<UserImageDTO>()
    .userImageId(111)
    .userImageName('user_image1.jpg')
    .userImageUrl('http://example.com/images/user_image1.jpg')
    .userOriginImageName('user_image_origin1.jpg')
    .build();

const addUserInfo = function () {
  const data = Builder<AddUserInfoDTO>()
      .email(email.value)
      .userSkills(skills.value)
      .field(field.value)
      .career(career.value)
      .referenceUrl(referenceUrl.value)
      .introduce(introduce.value)
      .registered(registered.value)
      .userImage(image)
      .build();

  userService.addUserInfo(data);

}

</script>

<template>
  <h1>추가정보 입력</h1>
  <div class="content-container">
    <div>
      <h2>기술</h2>
      <SkillSelectV/>
    </div>
    <div>
      <h2>깃허브 링크</h2>
      <input v-model = "referenceUrl" class="text-input" type="text">
    </div>
    <div>
      <h2>분야</h2>
      <FitDropDown/>
    </div>
    <div>
      <h2>프로필 이미지</h2>
      <div class="detail-content-container">
        <input class="text-input" type="text">
        <div class="button pp">파일 추가</div>
      </div>
    </div>
    <div>
      <div class="detail-content-container" style="margin-bottom: 7px">
        <h2>팀 구하기 등록</h2>
        <h3>등록하신 정보가 팀원 구하기 페이지에 등록됩니다.</h3>
      </div>
      <div class="detail-content-container" style="margin-bottom: 7px">
        <input v-model="registered" name = "registered" type="radio" value="true">네 등록할래요
        <input v-model="registered" name = "registered" type="radio" value="false">다음에 등록할게요.
      </div>
    </div>
    <div class="detail-content-container" style="margin-bottom: 7px">
      <h2>자기소개</h2>
    </div>
    <div class="detail-content-container" style="margin-bottom: 7px">
      <input v-model="introduce" class="introduce-container" type="text">
    </div>

    <div class="detail-content-container">
      <div @click = "addUserInfo" class="button pp">등록하기</div>
    </div>
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

.year {
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.pp {
  background: #7D3BFF;
  color: #F2F2F2;
}

.pp:hover {
  background: white;
  color: #303030;
}

.wt {
  background: white;
  color: #303030;
}

.wt:hover {
  background: #7D3BFF;
  color: #F2F2F2;
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

.content-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
.introduce-container {
  width: 100%;
  height: 120px; /* 더 큰 높이로 조정합니다. */
  border-radius: 5px;
  border: 1px solid #303030;
  padding: 10px;
  color: #2b2b2b;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  overflow-wrap: break-word;
}
</style>