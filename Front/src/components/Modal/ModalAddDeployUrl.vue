<script lang="ts" setup>

import {onMounted, PropType, ref} from "vue";

const emit = defineEmits(['registerDeployUrl']);

const props = defineProps({
  mainDeployUrlProp: {
    type: String,
    default: ""
  },
  deployUrlsProp: {
    type: Object as PropType<string[]>,
    default: []
  },
});

const mainDeployUrl = ref<string>("");
const deployUrls = ref<string[]>([]);
const newUrl = ref("/");

const addDeployUrl = () => {
  if (deployUrls.value.indexOf(newUrl.value) >= 0) {
    alert("중복된 URL 입니다.");
    return;
  }
  deployUrls.value.push(newUrl.value);
  newUrl.value = "/";
}

const deleteDeployUrl = (i: number) => {
  deployUrls.value.splice(i, 1);
}

const handleRegistration = () => {
  emit('registerDeployUrl', mainDeployUrl.value, deployUrls.value);
}

onMounted(() => {
  mainDeployUrl.value = props.mainDeployUrlProp;
  deployUrls.value = props.deployUrlsProp;
})

</script>

<template>
  <div class="container">
    <h1>프로젝트 링크 등록</h1>
    <h2>프로젝트 도메인 링크 ("http://...")</h2>
    <input class="input" type="text" v-model="mainDeployUrl">
    <h2>분석용 추가 경로 ("/...")</h2>
    <div class="link-container">
      <input class="input" type="text" v-model="newUrl">
      <div class="save-button" @click="addDeployUrl">추가</div>
    </div>
    <template v-for="(_, i) in deployUrls" :id="i">
      <input class="input" type="text" v-model="deployUrls[i]" >
      <div class="delete-button" @click="deleteDeployUrl(i)">삭제</div>
    </template>

    <div class="button" @click="handleRegistration">등록하기</div>
  </div>
</template>

<style scoped>

.link-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.save-button {
  width: 53px;
  height: 36px;
  border-radius: 5px;
  background: #7D3BFF;
  border: #7D3BFF solid 1px;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 35px;
  transition: color 0.3s ease;
}

.save-button:hover {
  background: white;
  color: #7D3BFF;
}

.delete-button {
  width: 53px;
  height: 36px;
  border-radius: 5px;
  background: #ff3b3b;
  border: #ff3b3b solid 1px;
  color: #F2F2F2;
  text-align: center;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 35px;
  transition: color 0.3s ease;
}

.delete-button:hover {
  background: white;
  color: #ff3b3b;
}

.input-sm {
  padding-left: 12px;
  border-radius: 5px;
  border: 1px solid #303030;
  height: 36px;
  flex: 1;
  color: #606060;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

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