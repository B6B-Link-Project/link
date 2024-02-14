<script lang="ts" setup>
import {ref} from "vue";
import {UserService} from "@/api/UserService.ts";
import {Builder} from "builder-pattern";
import {SendEmailRequestDTO} from "@/dto/SendEmailRequestDTO.ts";
import {EditValidCareerDTO} from "@/dto/EditValidCareerDTO.ts";
import router from "@/router";

const userService = new UserService();

const queryEmail = router.currentRoute.value.query.email;
const queryName = router.currentRoute.value.query.name;
const email = ref<string>(queryEmail ? queryEmail.toString() : '');
const pw1 = ref<string>('');
const pw2 = ref<string>('');
const name = ref<string>(queryName ? queryName.toString() : '');
const firstNumber = ref<string>('');
const secondNumber = ref<string>('');
const lastNumber = ref<string>('');
const year = ref<number>();
const month = ref<number>();
const day = ref<number>();
const career = ref<number>(0);
const gender = ref<boolean>();
const telecom = ref<number>(0);

const dropdownOpen = ref<boolean>(false);
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
const verification = ref<boolean>(false);
const currentYear = new Date().getFullYear();// 올해 연도

const register = function () {


  // if (!verification.value) {
  //   //이메일 인증하라고 해야함
  //   alert("이메일 인증을 완료하세요")
  //   return;
  // }

  //유효성 검증
  //이메일
  const emailPattern: RegExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
  if (!emailPattern.test(email.value)) {
    alert("이메일의 형식이 올바르지 않습니다.")
    return
  }

  //비밀번호
  //8자리 이상
  //영어 + 숫자 + 특수문자 각각 하나 이상 조합
  if (pw1.value.length < 8 && pw1.value.length > 13) {
    alert("비밀번호는 8자리 이상, 13자리 미만으로 설정해주세요")
    return false;
  }

  // 영어, 숫자, 특수문자 각각 하나 이상 포함
  if(!(/[a-zA-Z]/.test(pw1.value))){
    alert("비밀번호에 영어를 하나 이상 포함시켜야 합니다.")
    return
  }
  //숫자를 하나 이상 포합
  if(!(/\d/.test(pw1.value))){
    alert("비밀번호에 숫자를 하나 이상 포함시켜야 합니다.")
    return
  }
  if(!(/[!@#$%^&*()_+{}\[\]:;<>,.?~\\-]/.test(pw1.value))){
    alert("비밀번호에 특수문자를 하나 이상 포함시켜야 합니다.")
    return
  }

  if (pw1.value !== pw2.value) {
    alert("비밀번호가 일치하지 않습니다.")
    return
  }

  if(!name.value){
    alert("이름을 입력해주세요")
    return
  }

  //생일
  //연도값 확인
  //연도는 크게 제약 X
  if (!year || year.value === undefined || year.value < 1900 || year.value > currentYear) {
    alert("생일(연도)을 정확이 입력해주세요")
    return;
  }
  //월은 1 이상 12 이하
  if (!month || month.value === undefined || month.value < 1 || month.value > 12) {
    alert("생일(월)을 정확이 입력해주세요")
    return;
  }
  //일 제한
  if (!day || day.value === undefined || day.value < 1 || day.value > 31) {
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
  //핸드폰 번호
  if(firstNumber.value !== "010" || secondNumber.value.length != 4 || lastNumber.value.length != 4){
    alert("전화번호가 제대로 입력되었는지 확인해주세요")
    return
  }
  else {
    if(!(/\d/.test(firstNumber.value)) || !(/\d/.test(secondNumber.value)) || !(/\d/.test(lastNumber.value)) ) {
      alert("전화번호에는 숫자만 입력해주세요")
      return
    }
  }

  if (!gender || gender.value === undefined){
    alert("성별을 선택해주세요")
    return
  }

  const user = Builder<UserSignUpDto>()
      .email(email.value)
      .password(pw1.value)
      .name(name.value)
      .birth(new Date(year.value.toString() + "-" + month.value.toString() + "-" + day.value.toString()))
      .phoneNumber(firstNumber.value + secondNumber.value + lastNumber.value)
      .gender(gender.value)
      .build();

  userService.sign(user);

}

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
}

const validEmail = async function () {

  if (!email.value) return;

  const data = Builder<SendEmailRequestDTO>()
      .email(email.value)
      .build();

  verification.value = await userService.verificationEmail(data);

}
</script>

<template>
  <h1>회원 정보 입력</h1>
  <div class="register-container">
    <div class="register-content-block">
      <h2>이메일</h2>
      <div class="register-content-detail">
        <input v-model="email" class="input-text" type="email">
<!--        <input class="register-button" style="width: 94px;" type="button" value="이메일 인증" @click="validEmail">-->
      </div>
    </div>
    <div class="register-content-block">
      <h2>비밀번호</h2>
      <input v-model="pw1" class="input-text" type="password">
    </div>
    <div class="register-content-block">
      <h2>비밀번호 확인</h2>
      <input v-model="pw2" class="input-text" type="password">
    </div>
    <div class="register-content-block">
      <h2>이름</h2>
      <input v-model="name" class="input-text" type="text">
    </div>
    <div class="register-content-block">
      <h2>생년월일</h2>
      <div class="register-content-detail">
        <input v-model="year" class="input-text" placeholder="년도(4자)" type="text">
        <input v-model="month" class="input-text" placeholder="월" type="text">
        <input v-model="day" class="input-text" placeholder="일" type="text">
      </div>
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

    <h2>관련 경력</h2>
    <div class="detail-content-container">
      <div class="text-input" style="width: 100%; display: flex;">
        <div class="year" >{{career}} 년차</div>
      </div>
      <div @click = "validCareer" class="button">경력인증</div>
    </div>
    <div class="register-content-block">
      <h2>성별</h2>
      <div class="register-content-detail">
        <div>
          <input v-model="gender" name="gender" type="radio" value="true"/> 남자
        </div>
        <div>
          <input v-model="gender" name="gender" type="radio" value="false"/> 여자
        </div>
      </div>
    </div>
    <div class="register-content-block">
      <div class="register-button" @click="register">가입하기</div>
    </div>
  </div>
</template>

<style scoped>
input:focus {
  outline: #7D3CFF 1px solid;
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

.button {
  transition: color 0.3s ease;
  border-radius: 5px;
  background: #7D3BFF;
  border: #7D3CFF 1px solid;
  color: #F2F2F2;
  height: 40px;
  line-height: 39px;
  flex: 1;
  padding-left: 20px;
  padding-right: 20px;
  min-width: max-content;
}

.register-button {
  height: 40px;
  border-radius: 5px;
  background: #7D3BFF;
  border: #7D3CFF 1px solid;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  transition: color 0.3s ease;
  line-height: 39px;
}

.register-button:hover {
  background: white;
  color: #2b2b2b;
}

.register-content-detail {
  display: flex;
  gap: 20px;
}

h1 {
  color: #303030;
  text-align: center;
  font-size: 28px;
  font-style: normal;
  font-weight: 600;
  line-height: normal;
  margin-bottom: 45px;
}

h2 {
  text-align: left;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.register-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.register-content-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-text {
  border-radius: 5px;
  border: 1px solid #303030;
  height: 40px;
  width: 100%;
  padding: 10px;
  font-size: 16px;
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
</style>