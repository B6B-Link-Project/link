<script setup lang="ts">
import SimpleUser from "@/components/User/SimpleUser.vue";
import {onMounted, PropType, Ref, ref, watch} from "vue";
import {TeamMemberResponseDto} from "@/dto/tmpDTOs/teamDTO.ts";
import {Builder} from "builder-pattern";

const prop = defineProps({
  teamMemberDtos: {
    required: true,
    type: Object as PropType<TeamMemberResponseDto[]>
  }
});

// const leader: SimpleUserDTO = Builder<SimpleUserDTO>()
//     .userId(1)
//     .userImgUrl("https://i.namu.wiki/i/yVEttFa1u45vMA2qpwpP3y4lrMMq1nTRoiA1tgw14rK_5LHezLYrZcrOd3NzsN7FT5weKoF3cba5y9ygP81JKQ.webp")
//     .introduce("안녕하세요. 고죠입니다.")
//     .userName("고죠 사토루").build()

const leader: Ref<SimpleUserDTO> = ref({} as SimpleUserDTO);
const members: Ref<SimpleUserDTO[]> = ref([]);

onMounted(() => {
  watch(() => prop.teamMemberDtos, () => {
    prop.teamMemberDtos.map((m) => {
      console.log(m);
      if (m.role === "LEADER") {
        leader.value = Builder<SimpleUserDTO>()
            .userId(m.userId)
            .userName(m.name)
            .introduce(m.introduce)
            .userImgUrl(m.userImageUrl)
            .build();
      } else if (m.role === "MEMBER") {
        members.value.push(Builder<SimpleUserDTO>()
            .userId(m.userId)
            .userName(m.name)
            .introduce(m.introduce)
            .userImgUrl(m.userImageUrl)
            .build());
      }
    })
  })
})

</script>

<template>
  <h1>팀원 {{members.length + 1}}</h1>
  <div class="team-container">
    <h1>팀장</h1>
    <div class="member-container">
      <SimpleUser :data="leader"/>
    </div>
    <h1>팀원</h1>
    <div v-for="m in members">
      <div style="border-bottom: #ccc 1px solid">
        <SimpleUser :data="m"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 {
  color: #303030;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  line-height: 22.4px;
}

.team-container {
  margin-top: 12px;
  border-radius: 10px;
  border: 1px solid #7D3CFF;
  width: 100%;
  padding: 13px;
}
</style>