<script setup lang="ts">
import {UserService} from "@/api/UserService.ts";
import ContentFit from "@/components/Util/ContentFit.vue";
import SkillSelector from "@/components/SkillSelector.vue";
import FitDropDown from "@/components/FitContent/FitDropDown.vue";
import {computed, onMounted, ref} from "vue";
import {Builder} from "builder-pattern";
import {UserImageDTO} from "@/dto/UserImageDTO.ts";
import {EditValidCareerDTO} from "@/dto/EditValidCareerDTO.ts";
import Store from "@/store/index.ts";
import {UpdateUserDTO} from "@/dto/UpdateUserDTO.ts";
import router from "@/router";
import store from "@/store";
import {ImageDTO} from "@/dto/ImageDTO.ts";
// import {ImageDTO} from "@/dto/ImageDTO.ts";

const userService = new UserService();

// const pw1 = ref<string>('');
// const pw2 = ref<string>('');
const name = ref<string>('');
const year = ref<number>();
const month = ref<number>();
const day = ref<number>();
const firstNumber = ref<string>('');
const secondNumber = ref<string>('');
const lastNumber = ref<string>('');
const gender = ref<boolean>(false);
const skills = computed(() => Store.state.skillSelectList);
const referenceUrl = ref<string>('');
const field = computed(() => String(Store.state.field));
const career = ref<number>(0);
const registered = ref<boolean>(false);
const introduce = ref<string>('');
const currentYear = new Date().getFullYear();// 올해 연도

const dropdownOpen = ref<boolean>(false);
const telecom = ref<number>(0);
const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value;
};

const setTelecom = (index: number) => {
  telecom.value = index;
};

const telecomOptions = ref([
  'SKT' ,
   'KT' ,
  'LG U+'
]);
const getBeforeEditInfo = computed(async () => {
  const response = await userService.getBeforeEditInfo();
  return response;
});

const beforeInfo = ref<UpdateUserDTO>();

onMounted(async () => {
  beforeInfo.value = await getBeforeEditInfo.value;

  const birth = beforeInfo.value?.birth.toString();
  const parts = birth?.split("-");

  name.value = beforeInfo.value?.name;
  year.value = parseInt(parts[0]);
  month.value = parseInt(parts[1]);
  day.value = parseInt(parts[2]);
  gender.value = beforeInfo.value?.gender;
  referenceUrl.value = beforeInfo.value?.referenceUrl;
  career.value = beforeInfo.value?.career;
  registered.value = beforeInfo.value?.registered;
  introduce.value = beforeInfo.value?.introduce;
  firstNumber.value = beforeInfo.value?.phoneNumber.substring(0, 3);
  secondNumber.value = beforeInfo.value?.phoneNumber.substring(3, 7);
  lastNumber.value = beforeInfo.value?.phoneNumber.substring(7, 11);
  store.commit('setSkillSelectList', beforeInfo.value?.userSkills);
  store.commit('setField', beforeInfo.value?.field);
});

const newImage = ref();
const imageName = ref<string>('');
const imageUrl = ref<string>('');
const image = ref<UserImageDTO>();
const s3Img = ref<ImageDTO>();

//경력인증 했는지 확인
const isValid = ref<boolean>(false);
const validCareer = async function() {

  if(!name.value){
    alert("이름을 입력해주세요")
    return;
  }
  if (!year.value || year.value < 1900 || year.value > currentYear) {
    alert("생일(연도)을 정확이 입력해주세요")
    return;
  }
  //월은 1 이상 12 이하
  if (!month.value || month.value < 1 || month.value > 12) {
    alert("생일(월)을 정확이 입력해주세요")
    return;
  }
  //일 제한
  if (!day.value || day.value < 1 || day.value > 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //4, 6, 9, 11월은 30일임
  if ((month.value == 4 || month.value == 6 || month.value == 9 || month.value == 11) && day.value == 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //2월일 때는 연도에 따라서 29일
  if (month.value == 2) {
    const isLeap = (year.value % 4 == 0 && (year.value % 100 != 0 || year.value % 400 == 0));

    if (day.value > 29 || (day.value == 29 && !isLeap)) {
      alert("생일(일)을 정확이 입력해주세요")
      return;
    }
  }
  if(!telecom){
    alert("통신사를 골라주세요")
  }

  if(firstNumber.value !== "010" || secondNumber.value.length != 4 || lastNumber.value.length != 4){
    alert("전화번호가 제대로 입력되었는지 확인해주세요")
    return;
  }
  else {
    if(!(/\d/.test(firstNumber.value)) || !(/\d/.test(secondNumber.value)) || !(/\d/.test(lastNumber.value)) ) {
      alert("전화번호에는 숫자만 입력해주세요")
      return;
    }
  }

  const data = Builder<EditValidCareerDTO>()
      .userName(name.value)
      .identity(year.value.toString() + month.value.toString() + day.value.toString())
      .telecom(telecom.value.toString())
      .phoneNo(firstNumber.value + secondNumber.value + lastNumber.value)
      .build();

  career.value = await userService.validCareer(data);

  // -1이면 실패하는 느낌으로 ㄱㄱ
  if(career.value === -1){
    career.value = 0;
    alert("다시 인증해주세요");
    return;
  }
  // 제대로 된 숫자면
  isValid.value = true;
}

//부분만 눌러도 수정 할 수 있도록
const updateUser = function () {
  if(!name.value){
    alert("이름을 입력해주세요")
    return;
  }
  if (!year.value || year.value < 1900 || year.value > currentYear) {
    alert("생일(연도)을 정확이 입력해주세요")
    return;
  }
  //월은 1 이상 12 이하
  if (!month.value || month.value < 1 || month.value > 12) {
    alert("생일(월)을 정확이 입력해주세요")
    return;
  }
  //일 제한
  if (!day.value || day.value < 1 || day.value > 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //4, 6, 9, 11월은 30일임
  if ((month.value == 4 || month.value == 6 || month.value == 9 || month.value == 11) && day.value == 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //2월일 때는 연도에 따라서 29일
  if (month.value == 2) {
    const isLeap = (year.value % 4 == 0 && (year.value % 100 != 0 || year.value % 400 == 0));

    if (day.value > 29 || (day.value == 29 && !isLeap)) {
      alert("생일(일)을 정확이 입력해주세요")
      return;
    }
  }
  if(!telecom){
    alert("통신사를 골라주세요")
  }

  if(firstNumber.value !== "010" || secondNumber.value.length != 4 || lastNumber.value.length != 4){
    alert("전화번호가 제대로 입력되었는지 확인해주세요")
    return;
  }
  else {
    if(!(/\d/.test(firstNumber.value)) || !(/\d/.test(secondNumber.value)) || !(/\d/.test(lastNumber.value)) ) {
      alert("전화번호에는 숫자만 입력해주세요")
      return;
    }
  }

  if(!field.value) {
    alert("분야를 설정해주세요");
    return;
  }
  if(skills.value.length < 1) {
    alert("사용 기술을 하나라도 등록해주세요");
    return;
  }

  if(introduce.value.length > 200) {
    alert("자기소개는 공백포함 200자 미만입니다.")
  }

  image.value = Builder<UserImageDTO>()
      .userImageName(imageName.value)
      .userImageUrl(imageUrl.value)
      .userOriginImageName(imageName.value)
      .build();

  if(image.value == undefined) {
    alert("이미지를 첨부해주세요")
    console.log("No Image")
    return;
  }

  //경력인증 확인
  const data = Builder<UpdateUserDTO>()
      // .password(pw1.value)
      .name(name.value)
      .birth(new Date(year.value.toString() + "-" + month.value.toString() + "-" + day.value.toString()))
      .phoneNumber(firstNumber.value + secondNumber.value + lastNumber.value)
      .gender(gender.value)
      .userSkills(skills.value)
      .referenceUrl(referenceUrl.value)
      .field(field.value)
      .career(career.value)
      .userImage(image.value)
      .registered(registered.value)
      .introduce(introduce.value)
      .build()

  userService.updateUser(data);

  //메인으로 이동
  router.push("/")
}

const changeUserImage = async () => {
  imageName.value = newImage.value.files[0].name;
  s3Img.value = await userService.updateImage(newImage.value.files[0]);
  imageUrl.value = s3Img.value?.imageUrl;
};

</script>

<template>
  <ContentFit>
    <h1>회원 정보 수정</h1>
    <div class="holder">
      <h3>이름</h3>
      <input v-model="name" class="text-input" type="text">
      <h3>생년월일</h3>
      <div class="detail-content-container">
        <input v-model="year" class="text-input" placeholder="년도(4자)" type="text">
        <input v-model="month" class="text-input" placeholder="월" type="text">
        <input v-model="day" class="text-input" placeholder="일" type="text">
      </div>

      <div class="register-content-block">
        <h2>핸드폰 번호</h2>
        <div class="register-content-detail" style="gap: 6px; line-height: 39px">
          <div class="dropdown-container" @click="toggleDropdown">
            <div class="dropdown-box">
              <div style="flex: 9; text-align: left">{{ telecomOptions[telecom] }}</div>
              <svg fill="none" height="6" style="margin:auto" viewBox="0 0 12 6" width="12"
                   xmlns="http://www.w3.org/2000/svg">
                <path d="M1 1L6 5L11 1" stroke="#303030" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/>
              </svg>
            </div>

            <div v-if="dropdownOpen" class="dropdown-content">
              <div v-for="(item, index) in telecomOptions" :key="index" @click="setTelecom(index)">{{ item }}</div>
            </div>
          </div>
          <input v-model="firstNumber" class="input-text" type="text">-
          <input v-model="secondNumber" class="input-text" type="text">-
          <input v-model="lastNumber" class="input-text" type="text">
        </div>
      </div>

      <h3>성별</h3>
      <div class="detail-content-container">
        <div>
          <input v-model="gender" name="gender" type="radio" value="true"/> 남자
        </div>
        <div>
          <input v-model="gender" name="gender" type="radio" value="false"/> 여자
        </div>
      </div>

      <h2>기술</h2>
      <SkillSelector/>
      <h3>깃허브 링크</h3>
      <input v-model="referenceUrl" class="text-input" type="text">

      <h3>분야</h3>
      <FitDropDown :prop = "beforeInfo"/>

      <h3>관련 경력</h3>
      <div class="detail-content-container">
        <div class="text-input" style="width: 100%; display: flex;">
          <div class="year" >{{career}} 년차</div>
        </div>
        <div @click = "validCareer" class="button pp">경력인증</div>
      </div>

      <h3>프로필 이미지</h3>
      <div class="detail-content-container">
        <input style="display: none" id="input-file" type="file" ref="newImage" accept="image/*" :multiple="false" @change="changeUserImage">
        <input v-model="imageName" class="text-input" >
        <label for="input-file" class="button pp">파일 추가</label>
      </div>
      <div style="text-align: left">
        <div class="detail-content-container" style="margin-bottom: 7px">
          <h2>팀 구하기 등록</h2>
          <h4>등록하신 정보가 팀원 구하기 페이지에 등록됩니다.</h4>
        </div>
        <div class="detail-content-container" style="margin-bottom: 7px">
          <input v-model="registered" name="registered"  type="radio" value="true">네 등록할래요
          <input v-model="registered" name="registered"  type="radio" value="false">다음에 등록할게요.
        </div>
      </div>
      <h3>자기소개</h3>
      <input v-model="introduce" class="introduce-container" type="text">

    </div>
    <div @click = 'updateUser' class="button pp" style="margin-top: 20px">수정하기</div>
  </ContentFit>
</template>

<style scoped>
.holder {
  display: flex;
  flex-direction: column;
  gap: 10px;
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
  font-weight: 400;
  line-height: normal;
}

h2 {
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  text-align: left;
}

h3 {
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  text-align: left;
}

h4 {
  margin-top: 5px;
  color: #606060;
  font-size: 12px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
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

.detail-content-container {
  display: flex;
  gap: 20px;
}

input {
  outline: none;
}
.input-text {
  border-radius: 5px;
  border: 1px solid #303030;
  height: 40px;
  width: 100%;
  padding: 10px;
  font-size: 16px;
}
.register-content-detail {
  display: flex;
  gap: 20px;
}
.register-content-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.dropdown-box {
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding: 9px 15px 10px;
  border-radius: 5px;
  border: 1px solid #303030;
  width: 100%;
  display: flex;
  height: 40px;
}
.dropdown-container {
  display: inline-block;
  position: relative;
  width: 100%;
}
.dropdown-content {
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding: 9px 18px 10px;
  border-radius: 5px;
  border: 1px solid #303030;
  width: 100%;
  position: absolute;
  min-width: max-content;
  z-index: 2;
  background: white;
  top: 0;
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