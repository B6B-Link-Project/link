<script setup lang="ts">

import {onMounted, Ref, ref} from "vue";
import Modal from "@/components/Modal/Modal.vue";
import BarChart from "@/components/Chart/BarChart.vue";
import {ProjectService} from "@/api/ProjectService.ts";
import {PageableBackPerformance} from "@/dto/projectDTO.ts";
import ThinDonutChart from "@/components/Chart/ThinDonutChart.vue";
import {Builder} from "builder-pattern";
import {BackPerformanceMessageResponseDto} from "@/dto/BackPerformanceMessageResponseDto.ts";

defineProps({
  editable: {
    type: Boolean,
    default: true
  },
});

const projectService = new ProjectService();
let refReport: Ref<PageableBackPerformance> = ref({} as PageableBackPerformance);
const refMessageCount: Ref<BackPerformanceMessageResponseDto> = ref({} as BackPerformanceMessageResponseDto);

onMounted(async () => {
  await getBackendReport();
})

const getBackendReport = async () => {
  refReport.value = await projectService.getBackMetrics(1);
  refMessageCount.value = await projectService.getBackMetricsMessageCounts(1);
}

const updateBackendReport = () => {
  alert("요청이 완료되었습니다. 대기열에 따라 처리 시간이 변동되며 평균적으로 1분이 소모됩니다.")
  projectService.postBackMetrics(1);
}

const modalController = ref<boolean>(false);

function modalSwitch() {
  modalController.value = !modalController.value
  detail.value = -1;
}

const detail = ref<number>(-1);

function calculateGrade(score: number): string {
  if (score >= 90) {
    return 'A';
  } else if (score >= 80) {
    return 'B';
  } else if (score >= 70) {
    return 'C';
  } else if (score >= 60) {
    return 'D';
  } else {
    return 'E';
  }
}

</script>

<template>
  <Modal v-if="modalController" @closeModal="modalSwitch">
    <div class="modal-container">
      <BarChart :data="[refMessageCount.addCount, refMessageCount.removeCount,refMessageCount.changeCount, refMessageCount.completeCount
      ,refMessageCount.changeCount, refMessageCount.mergeCount, refMessageCount.refactorCount, refMessageCount.moveCount, refMessageCount.replaceCount, refMessageCount.etcCount]"/>
      <h1 style="">개선 사항</h1>
      <div class="grid">
        <h1 style="text-align: left; margin-bottom: 3px; margin-top: 3px; font-family: Pretendard-Regular,serif">
          수정 사항 설명
        </h1>
        <h2 style="text-align: left; margin-top: 5px;flex: 1;"></h2>
      </div>
      <div class="content">
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 삭제</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
        <h3>• 추가</h3>
        <div class="modal-text">어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다. 어쩌구 저쩌구해야합니다.</div>
      </div>
    </div>
  </Modal>
  <h1>백엔드</h1>
  <section v-if="refReport">
    <h1 style="margin-bottom: 0;" v-if="refReport.pageable?.totalPages===0">분석된 데이터가 없습니다!</h1>
    <div class="chart-container">
      <template v-for="data in refReport.backMetrics">
        <div class="chart">
          <ThinDonutChart
              :pc="Builder<PerformanceChartDTO>().actualValue(100-data.bugs).centerText(calculateGrade(100-data.bugs)).build()"/>
          <h2>버그</h2>
        </div>
        <div class="chart">
          <ThinDonutChart
              :pc="Builder<PerformanceChartDTO>().actualValue(data.coverage).centerText(data.coverage*100+'%').build()"/>
          <h2>테스트 자동화</h2>
        </div>
        <div class="chart">
          <ThinDonutChart
              :pc="Builder<PerformanceChartDTO>().actualValue((500-data.codeSmells)/5).centerText(calculateGrade((500-data.codeSmells)/5)).build()"/>
          <h2>코드 악취</h2>
        </div>
        <div class="chart">
          <ThinDonutChart
              :pc="Builder<PerformanceChartDTO>().actualValue(100-data.duplications).centerText(calculateGrade(100-data.duplications)).build()"/>
          <h2>코드 중복</h2>
        </div>
        <div class="chart">
          <ThinDonutChart
              :pc="Builder<PerformanceChartDTO>().actualValue(data.securityRating * 100).centerText((data.securityRating * 100).toString()).build()"/>
          <h2>보안 점수</h2>
        </div>
      </template>
    </div>
    <h2>* 코드 품질과 보안을 개선하기 위한 분석 결과입니다.</h2>
  </section>
  <div class="button-container">
    <div class="button" @click="updateBackendReport" v-if="editable">백엔드 분석 요청</div>
    <div class="button" @click="modalSwitch()" v-if="refReport.pageable?.totalPages!==0">분석 상세정보</div>
  </div>

</template>

<style scoped>
.grid {
  border-bottom: #6f7070 solid 2px;
  padding: 10px;
  width: 100vw;
  max-width: 100%;
  display: flex;
  gap: 7px;
}

.content {
  margin-top: 20px;
  height: calc(30vh - 80px);
  overflow: scroll;
  overflow-x: hidden;
  padding: 10px;

}

@media screen and (max-width: 698px) {
  .content {
    height: calc(50vh - 80px);
  }
}

section {
  padding: 18px;
  border-radius: 10px;
  border: 1px solid #7D3BFF;
  background: #FFF;
  justify-content: center;
  min-height: calc(100% - 96px);
  max-height: max-content;
  display: flex;
  flex-direction: column;
}

.modal-text {
  margin-left: 13px;
  margin-top: 8px;
  margin-bottom: 8px;
  line-height: 27px;
}

.modal-container {
  padding: 20px;
  max-width: 680px;
}

.button-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 14px;
  margin-bottom: 10px;
  gap: 14px;
}

.button {
  flex: 1;
  height: 34px;
  border-radius: 5px;
  background: #7d3bff;
  border: #7d3bff solid 1px;
  line-height: 33px;
  color: #FFF;
  text-align: center;
  font-size: 16px;
  font-style: normal;
  font-weight: lighter;
  transition: 0.3s ease color;
}

.button:hover {
  color: #7d3bff;
  background: white;
}

h1 {
  color: #303030;
  text-align: center;
  font-size: 20px;
  font-style: normal;
  font-weight: 600;
  margin-bottom: 18px;
}

h2 {
  color: #303030;
  text-align: center;
  font-family: Pretendard-Regular, serif;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  line-height: 22px;
}

h3 {
  color: #303030;
  font-size: 18px;
  font-style: normal;
  font-weight: 400;
}

@media screen and (max-width: 768px) {
  .chart-container {
    max-width: 100vw;
    width: 100%;
  }
}

.chart {
  width: 92px;
  height: 92px;
  position: relative;
  margin-bottom: 16px;
}

.chart-container {
  margin-bottom: 16px;
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  justify-content: center;
}
</style>