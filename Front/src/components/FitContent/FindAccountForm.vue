<script lang="ts" setup>
import {ref} from "vue";
import {UserService} from "@/api/UserService.ts";
import {Builder} from "builder-pattern";
import {FindEmailDTO} from "@/dto/FindEmailDTO.ts"
import {SendEmailRequestDTO} from "@/dto/SendEmailRequestDTO.ts";
import {VerificationRequestDTO} from "@/dto/VerificationRequestDTO.ts";
import Store from "@/store/index.ts";
import router from "@/router";

const userService = new UserService();

//아이디 찾기
const birth = ref<string>('');
const name = ref<string>('');
const phoneNumber = ref<string>('');
const currentYear = new Date().getFullYear();

//비밀번호 찾기
const email = ref<string>('');
const verificationCode = ref<string>('');
const isValid = ref<boolean>(false);
const isChecked = ref<boolean>(false);

const findEmail = function () {

  if(!birth.value || birth.value.length != 8){
    alert("생년월일을 8자리로 정확히 입력해주세요")
    return;
  }

  if(!name.value){
    alert("이름을 입력해주세요")
    return;
  }

  if(!phoneNumber.value || phoneNumber.value.length != 11){
    alert("핸드폰 번호를 제대로 입력해주세요")
    return;

  }

  const year = parseInt(birth.value.substring(0, 4), 10)
  const month = parseInt(birth.value.substring(4, 6), 10)
  const day = parseInt(birth.value.substring(6, 8), 10) + 1

  const firstNumber = phoneNumber.value.substring(0, 3)
  const secondNumber = phoneNumber.value.substring(3, 7)
  const lastNumber = phoneNumber.value.substring(7, 11)

  if (!year|| year < 1900 || year > currentYear) {
    alert("생일(연도)을 정확이 입력해주세요")
    return;
  }
  //월은 1 이상 12 이하
  if (!month || month < 1 || month > 12) {
    alert("생일(월)을 정확이 입력해주세요")
    return;
  }
  //일 제한
  if (!day || day < 1 || day > 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //4, 6, 9, 11월은 30일임
  if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
    alert("생일(일)을 정확이 입력해주세요")
    return;
  }
  //2월일 때는 연도에 따라서 29일
  if (month == 2) {
    const isLeap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));

    if (day > 29 || (day == 29 && !isLeap)) {
      alert("생일(일)을 정확이 입력해주세요")
      return;
    }
  }
  //핸드폰 번호
  if(firstNumber !== "010" || secondNumber.length != 4 || lastNumber.length != 4){
    alert("전화번호가 제대로 입력되었는지 확인해주세요")
    return
  }
  else {
    if(!(/\d/.test(firstNumber)) || !(/\d/.test(secondNumber)) || !(/\d/.test(lastNumber)) ) {
      alert("전화번호에는 숫자만 입력해주세요")
      return
    }
  }



  console.log(new Date(year.toString() + "-" + month.toString() + "-" + day.toString()))

  const data = Builder<FindEmailDTO>()
      .birth(new Date(year.toString() + "-" + month.toString() + "-" + day.toString()))
      .name(name.value)
      .phoneNumber(phoneNumber.value)
      .build();

  userService.findEmail(data);

  alert()

}
const sendVerificationCode = async function () {

  const data = Builder<SendEmailRequestDTO>()
      .email(email.value)
      .build();

  isValid.value = await userService.sendVerificationCode(data);

}

const comparedVerificationCode = async function () {

  if(!isValid) {
    alert("인증번호를 발급받으세요.")
  }

  if(!verificationCode.value) {
    alert("인증번호를 입력하세요.")
  }

  const data = Builder<VerificationRequestDTO>()
      .email(email.value)
      .verificationKey(verificationCode.value)
      .build();

  isChecked.value = await userService.comparedVerification(data);

}

const setEmail = function () {
  Store.commit('setEmail', email.value);
  router.push('/passwordChange');
}


const mode = ref<Boolean>(true)
const modeUpdate = (bool: boolean) => {
  mode.value = bool;
}
</script>

<template>
  <h1>아이디 / 비밀번호 찾기</h1>
  <div class="option-select-container">
    <div :class="mode?'selected':'non-selected'" class="select" @click="modeUpdate(true)">
      아이디 찾기
    </div>
    <div :class="!mode?'selected':'non-selected'" class="select" @click="modeUpdate(false)">
      비밀번호 찾기
    </div>
  </div>
  <div v-if="mode" class="input-container">
    <input v-model = "birth" class="input-text" placeholder="생년월일 입력(8자리)" type="text">
    <input v-model = "name" class="input-text" placeholder="이름 입력" type="text">
    <input v-model = "phoneNumber" class="input-text" placeholder="핸드폰 번호 입력(숫자만)" type="text">
    <input @click = "findEmail" class="register-button" type="button" value="아이디 찾기">
  </div>
  <div v-if="!mode" class="input-container">
    <div class="content-detail">
      <input v-model = "email" class="input-text" placeholder="이메일 입력" type="text">
      <input @click = "sendVerificationCode" class="register-button" style="width: 114px;" type="button" value="인증번호 발송">
    </div>
    <div class="content-detail">
      <input v-model="verificationCode" class="input-text" placeholder="인증번호 입력" type="text">
      <input @click = "comparedVerificationCode" class="register-button" style="width: 94px" type="button" value="인증">
    </div>
    <input @click = "setEmail" v-if="isChecked" class="register-button" type="button" value="비밀번호 찾기">
  </div>
</template>

<style scoped>
input:focus {
  outline: #7D3CFF 1px solid;
}
.content-detail {
  display: flex;
  gap: 20px;
}

.input-container {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-text {
  border-radius: 5px;
  border: 1px solid #303030;
  height: 40px;
  width: 100%;
  padding: 10px;
  color: #606060;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
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
  line-height: 39px;
  transition: color 0.3s ease;
}

.register-button:hover {
  background: white;
  color: #2b2b2b;
}

.select {
  flex: 1;
  text-align: center;
  font-size: 20px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  padding-bottom: 12px;
}

.selected {
  color: #303030;
  border-bottom: #7D3CFF 2px solid;
}

.non-selected {
  color: #909090;
  border-bottom: #909090 2px solid;
}

h1 {
  color: #303030;
  text-align: center;
  font-size: 28px;
  font-style: normal;
  font-weight: 600;
  line-height: normal;
  margin-bottom: 41px;
}

.option-select-container {
  display: flex;
  justify-content: center;
}
</style>