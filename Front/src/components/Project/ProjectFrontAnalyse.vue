<script setup lang="ts">
import {LighthouseService} from "@/api/LighthouseService.ts";
import {onMounted, Ref, ref} from "vue";
import ThinDonutChart from "@/components/Chart/ThinDonutChart.vue";
import {Report} from "@/Interface/frontReport.ts";
import {Builder} from "builder-pattern";
import Modal from "@/components/Modal/Modal.vue";
import RadarChart from "@/components/Chart/RadarChart.vue";
import {round} from "@kurkle/color";

defineProps({
  editable: {
    type: Boolean,
    default: true
  },
});
const calculateAverageScores = (reports: Report[]) => {
  const scoreSums = new Map<string, number>();
  const scoreCounts = new Map<string, number>();

  reports.forEach((report) => {
    Object.keys(report.report).forEach((key) => {
      const currentScore = report.report[key].score

      if (scoreSums.has(key)) {
        scoreSums.set(key, scoreSums.get(key) || currentScore);
        scoreCounts.set(key, scoreCounts.get(key) || 1);
      } else {
        scoreSums.set(key, currentScore);
        scoreCounts.set(key, 1);
      }
    });
  });

  const averageScores = new Map<string, number>();
  scoreSums.forEach((sum, key) => {
    averageScores.set(key, Math.round(sum / (scoreCounts.get(key) || 0) * 100));
  });

  return Array.from(averageScores);
};

const lighthouseService = new LighthouseService();
let refReport: Ref<Report[]> = ref([]);

onMounted(async () => {
  await getFrontendReport();
})

const getFrontendReport = async () => {
  refReport.value = await lighthouseService.getLighthouseReport(1);
}

const updateFrontendReport = () => {
  alert("요청이 완료되었습니다. 대기열에 따라 처리 시간이 변동되며 평균적으로 페이지 1개당 20~30초가 소모됩니다.")
  lighthouseService.updateLighthouseReport(1);
}
const buildObject = (score: number) => {
  return Builder<PerformanceChartDTO>()
      .centerText((score).toString())
      .actualValue((score))
      .build()
}

const modalController = ref<boolean>(false);

function modalSwitch() {
  modalController.value = !modalController.value
  detail.value = -1;
}

const modalListSelect = ref<number>(0);

const move = (num: number) => {
  if (modalListSelect.value + num >= 0 && modalListSelect.value + num <= refReport.value.length - 1) modalListSelect.value += num;
  detail.value = -1;
}
const detail = ref<number>(-1);
const detailOpen = (num: number) => {
  detail.value = num;
}
</script>

<template>
  <Modal v-if="modalController" @closeModal="modalSwitch">
    <div class="modal-container">
      <template v-for="(report,i) in refReport">
        <template v-if="i===modalListSelect">
          <RadarChart :report="report.report" :url="report.url"/>
          <h1>[{{ report.url }}] 레포트 </h1>

          <template v-for="(key, keyNumber) in Object.keys(report.report)">
            <div v-if="detail===-1" @click="detailOpen(keyNumber)" class="grid">
              <div style="display: flex; gap:7px; flex: 1">
                <h1 style="text-align: left; margin-bottom: 3px; margin-top: 3px; font-family: Pretendard-Regular,serif;">
                  {{ key }}
                </h1>
                <h2 style="text-align: left; margin-top: 5px">점수:{{ round(report.report[key].score * 100) }}</h2>

              </div>
              <svg fill="none" height="20" style="margin-top: 5px;" viewBox="0 0 12 20" width="20"
                   xmlns="http://www.w3.org/2000/svg">
                <g transform="rotate(90, 6, 10)">
                  <path d="M2 18L10 10L2 2" stroke="#A9A9A9" stroke-linecap="round" stroke-linejoin="round"
                        stroke-width="3"/>
                </g>
              </svg>
            </div>
            <template v-if="detail===keyNumber">
              <div class="grid" @click="detailOpen(-1)">
                <h1 style="text-align: left; margin-bottom: 3px; margin-top: 3px; font-family: Pretendard-Regular,serif">
                  {{ key }}
                </h1>
                <h2 style="text-align: left; margin-top: 5px;flex: 1;">점수:{{
                    round(report.report[key].score * 100)
                  }}</h2>
                <svg fill="none" height="20" style="margin-top: 5px;" viewBox="0 0 12 20" width="20"
                     xmlns="http://www.w3.org/2000/svg">
                  <g transform="rotate(270, 6, 10)">
                    <path d="M2 18L10 10L2 2" stroke="#A9A9A9" stroke-linecap="round" stroke-linejoin="round"
                          stroke-width="3"/>
                  </g>
                </svg>
              </div>
              <div class="content">
                <template v-if="report.report[key].description">
                  <h3>• 설명</h3>
                  <div class="modal-text">- {{ report.report[key].description }}</div>
                </template>
                <h3>• 가이드 라인</h3>
                <div class="modal-text" v-if="report.report[key].audits.length === 0">잘 하셨습니다! 부족한 부분이 없게 진행되었습니다.</div>
                <template v-else>
                  <div class="modal-text" v-for="(audit,i) in report.report[key].audits">
                    <div class="modal-text">
                      {{ i + 1 }}. {{ audit.title }} [{{ round(audit.score * 100) }}/100]
                      <div class="modal-text">- {{ audit.description }}</div>
                    </div>
                  </div>
                </template>
              </div>
            </template>
          </template>
        </template>
      </template>
      <div class="page-box">
        <div>
          <svg fill="none" height="20" viewBox="0 0 12 20"
               width="12" xmlns="http://www.w3.org/2000/svg" @click="move(-1)">
            <g transform="scale(-1, 1) translate(-12, 0)">
              <path d="M2 18L10 10L2 2" stroke="#A9A9A9" stroke-linecap="round" stroke-linejoin="round"
                    stroke-width="3"/>
            </g>
          </svg>
          <span class="list"><span style="color: #7d3bff">{{ modalListSelect + 1 }}</span>/{{ refReport.length }}</span>
          <svg fill="none" height="20"
               viewBox="0 0 12 20" width="12" xmlns="http://www.w3.org/2000/svg" @click="move(1)">
            <path d="M2 18L10 10L2 2" stroke="#A9A9A9" stroke-linecap="round" stroke-linejoin="round" stroke-width="3"/>
          </svg>
        </div>

      </div>
    </div>
  </Modal>
  <h1>프론트 엔드</h1>
  <section>
    <h1 v-if="refReport.length===0" style="margin-bottom: 0;">분석된 데이터가 없습니다!</h1>
    <div class="chart-container">
      <template v-for="data in calculateAverageScores(refReport)">
        <div class="chart">
          <ThinDonutChart :pc="buildObject(data[1])"/>
          <h2>{{ data[0] }}</h2>
        </div>
      </template>
    </div>
    <h2>* 웹 성능 최적화를 위한 분석 결과입니다.</h2>
  </section>

  <div class="button-container">
    <div class="button" @click="updateFrontendReport" v-if="editable">프론트 분석 요청</div>
    <div class="button" @click="modalSwitch()" v-if="refReport.length!==0">분석 상세정보</div>
  </div>

</template>

<style scoped>
.page-box {
  text-align: center;
  padding-top: 16px;
  font-family: "Jockey One", sans-serif;
  font-size: 30px;
  color: #2A2A2A;
}
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
  margin-bottom: 16px;
  width: 92px;
  height: 92px;
  position: relative;
}

.chart-container {
  margin-bottom: 16px;
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  justify-content: center;
}
</style>