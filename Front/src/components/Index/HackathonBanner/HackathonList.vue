<script lang="ts" setup>
import {PageableHackathonList} from "@/dto/hackathonInfoDTO.ts";
import {PropType} from "vue";
import {useRouter} from "vue-router";

const props = defineProps({
  ht : {
    type : Object as PropType<PageableHackathonList>,
    require : true
  }
})

// const goToHackathonDetail = (hackathonId : number) => {
//   const router = useRouter();
//   router.push(`/hackathonDetail?id=${hackathonId}`);
// }
console.log(props.ht?.hackathons)
</script>

<template>
  <div class="hackathon-banner-list">
    <div v-for="data in props.ht?.hackathons" class="hackathon-banner-list-container">
      <div class="list-title-container">
        <div  class="list-title">
          {{ data.hackathonName }}
        </div>
      </div>
      <div class="img-container">
        <RouterLink :to="`/hackathonDetail?id=${data.hackathonId}`">
          <img :src="data.hackathonImageUrl" alt="">
        </RouterLink>
      </div>

      <div class="title-container">
        <div class="title">
          {{ data.hackathonTopic }}
        </div>
        <div class="list-button">
          <div class="list-button-text">참여하기</div>
        </div>
      </div>
    </div>


  </div>
</template>

<style scoped>
img:hover {
  transform: scale(1.1);
}

.title {
  flex: 1;
  width: 100%;
  line-height: 31px;
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 600;
}

.title-container {
  margin-top: 13px;
  display: flex;
  width: 100%;
}

.list-button-text {
  flex: 1;

  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: 700;
  line-height: normal;
  vertical-align: middle;
  margin-top: 5px;
}

.list-button:hover {
  color: #ECEEF0;
  background: #7d3bff;
}

.list-button {
  transition: background-color 0.3s ease;
  color: #7D3CFF;
  width: 89px;
  height: 31px;
  border-radius: 4px;
  border: 1px solid #7D3CFF;
}

.list-title-container {
  z-index: 2;
  width: 300px;
  height: max-content;
  margin-top: 13px;
  margin-left: 13px;
  position: absolute;
  line-height: 23px;
}

img {
  transition: transform 0.2s ease;
  width: 100%;
  height: 200px;
  object-fit: cover;
  filter: brightness(0.6);

}

.img-container {
  width: 100%;
  height: 200px;
  border-radius: 10px;
  overflow: hidden;
}

.hackathon-banner-list {
  min-width: 300px;
  margin-top: 21px;
  display: flex;
  flex-direction: row;
  gap: 20px;
  flex-wrap: wrap;
}

.hackathon-banner-list-container {
  flex: 1;
  min-width: 300px;
  height: max-content;
  overflow: hidden;
}

.list-title {
  max-width: 100%;
  color: #F0F0F0;
  font-size: 20px;
  font-style: normal;
  font-weight: 800;
}
</style>