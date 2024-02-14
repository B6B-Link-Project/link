<script lang="ts" setup>

import DropDownBox from "@/components/DropDownBox.vue";
import DropDownBox1 from "@/components/DropDownBox1.vue";
import DropDownBox2 from "@/components/DropDownBox2.vue";
import DropDownBox3 from "@/components/DropDownBox3.vue";

const tierSelect = (
  ["티어", "브론즈", "실버", "골드", "플래티넘", "다이아"]
);

const careerSelect = (
  ["경력", "0년차", "1년차", "2년차", "3년차", "4년차", "5년차", "6년차", "7년차", "8년차", "9년차", "10년차 이상"]
);

const genderSelect = (
    ["성별", "남자", "여자"]
);

const fieldSelect = (
  ["분야", "프론트엔드", "백엔드", "풀스택", "디자인", "기획"]
);

</script>

<template>
  <div class="user-info-category-container">
    <div>
      <div class="user-info-category-text">티어</div>
      <div class="dropdown-container">
        <DropDownBox :menuList="tierSelect"/>
      </div>
    </div>
    <div>
      <div class="user-info-category-text">기타</div>
      <div class="dropdown-container">
        <DropDownBox1 :menuList="careerSelect"/>
        <DropDownBox2 :menuList="genderSelect"/>
        <DropDownBox3 :menuList="fieldSelect"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dropdown-container {
  display: flex;
  gap: 8px;
}

.user-info-category-container {
  flex: 1;
  display: flex;
  gap: 14px;
}

.user-info-category-text {
  margin-bottom: 20px;
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  padding-left: 6px;
  border-left: #7D3BFF 2px solid;
}


</style>