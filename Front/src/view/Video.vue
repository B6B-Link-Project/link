<script setup lang="ts">

import VideoPlayer from "@/components/Video/VideoPlayer.vue";
import VideoInfo from "@/components/Video/VideoInfo.vue";
import ModalEffect from "@/components/Modal/ModalEffect.vue";
import {onMounted, PropType, Ref, ref} from "vue";
import {MemberDetailResponseDto} from "@/dto/tmpDTOs/memberDTO.ts";
import {TeamBuildingService} from "@/api/TeamBuildingService.ts";
import router from "@/router";
import {useRoute} from "vue-router";

const route = useRoute();

const reservationId = route.params.rid;
const guestId = route.params.uid;

const teamBuildingService = new TeamBuildingService();

const memberDetailResponseDto: Ref<MemberDetailResponseDto | undefined> = ref();

onMounted(async () => {
  console.log(route.params.reservationId, route.params.guestId);
  try{
    if (guestId)
      memberDetailResponseDto.value = await teamBuildingService.getMemberDetailByUserId(parseInt(guestId as string));
  } catch (e) {
    alert('유효하지 않은 사용자 정보입니다.');
    await router.push('/');
  }
});

</script>

<template>
  <div style="overflow: hidden; border-radius: 30px">
    <ModalEffect text="홍길동님과의 화상 채팅"/>
    <div class="video-container">
      <div class="video-sector">
        <VideoPlayer v-if="reservationId" :reservation-id="reservationId"/>
      </div>
      <div class="introduce-container" v-if="memberDetailResponseDto">
        <VideoInfo :member-info="memberDetailResponseDto"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
@media screen and (max-width: 1240px) {
  .video-container {
    flex-direction: column;
  }

  .introduce-container {
    margin-top: 30px;
    margin-bottom: 20px;
    min-width: 0;
  }
}

@media screen and (max-width: 573px) {
  .introduce-container {
    margin-top: 160px;
  }
}

.video-container {
  padding: 20px;
  display: flex;
  border-radius: 30px;
  border: 2px solid #7D3BFF;
  overflow: hidden;
}

.video-sector {
  flex: 6;
}

.introduce-container {
  padding-right: 20px;
  flex: 1;
}
</style>