<script lang="ts" setup>
import {PropType} from "vue";
import {Builder} from "builder-pattern";

const props = defineProps({
  skill: {
    type: Object as PropType<SkillDTO>,
    default: Builder<SkillDTO>().build()
  },
  width: String,
  height: String,
  radius: String,
  fontSize: String,
});

</script>
<template>
  <div>
    <div v-if="props.skill?.skillYear"
         :style="{ width: props.width, height: props.height, borderRadius: props.radius, fontSize: props.fontSize }"
         class="normal-container hover-container">
      <div class="year-text">
        {{ props.skill?.skillYear + "Y" }}
      </div>
      <img :src="props.skill?.skillImgUrl" :style="{ width: props.width, height: props.height, borderRadius: props.radius }" alt="">
    </div>
    <div v-else
         :style="{ width: props.width, height: props.height, borderRadius: props.radius, fontSize: props.fontSize }"
         class="normal-container">
      <img :src="props.skill?.skillImgUrl" :style="{ width: props.width, height: props.height, borderRadius: props.radius }" alt="">
    </div>
  </div>
</template>

<!--<template>-->
<!--  <div v-if="props.skill?.skillLevel"-->
<!--       :style="{ width: width, height: height,borderRadius: radius, fontSize:fontSize }"-->
<!--       class="normal-container hover-container">-->
<!--    <div v-if="skill?.skillLevel" class="year-text">-->
<!--      {{ props.skill?.skillLevel + "Y" }}-->
<!--    </div>-->
<!--    <img :src="props.skill.skillImageUrl" :style="{ width: width, height: height,borderRadius: radius }" alt="">-->
<!--  </div>-->
<!--  <div v-else :style="{ width: width, height: height,borderRadius: radius, fontSize:fontSize }"-->
<!--       class="normal-container">-->
<!--    <img :src="props.skill?.skillImageUrl" :style="{ width: width, height: height,borderRadius: radius }" alt="">-->
<!--  </div>-->

<!--</template>-->

<style scoped>
.normal-container {
  position: relative;
}

img {
  object-fit: cover;
  position: absolute;
  z-index: 1;
  width: 100%;
  height: 100%;
  border: 0.5px solid #606060;
}

.hover-container:hover img {
  background: rgba(30,30,30, 0.7);
  filter: brightness(0.7);
}

.year-text {
  font-family: "Jockey One", sans-serif;
  position: absolute;
  z-index: 3;
  color: #ECEEF0;
  opacity: 0;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.hover-container:hover .year-text {
  opacity: 1;
}
</style>