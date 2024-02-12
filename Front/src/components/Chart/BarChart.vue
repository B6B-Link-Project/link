<script setup lang="ts">
//@ts-nocheck
import {onMounted, PropType, ref} from 'vue';
import {BarController, BarElement, CategoryScale, Chart, Legend, LinearScale, Title, Tooltip} from 'chart.js';

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Title, Tooltip, Legend);

const barChartCanvas = ref<HTMLCanvasElement>();
const props = defineProps({
  data: {
    type: Object as PropType<number[]>,
    required: true
  }
})

onMounted(() => {
  if (barChartCanvas.value) {
    const ctx = barChartCanvas.value.getContext('2d');
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['추가', '삭제', '생성', '완료', '수정', '병합', '리팩토링', '이동', '대치', '이외'],
        datasets: [{
          data: props.data,
          backgroundColor: [
            'rgba(255, 99, 132, 0.5)',
            'rgba(54, 162, 235, 0.5)',
            'rgba(255, 206, 86, 0.5)',
            'rgba(75, 192, 192, 0.5)',
            'rgba(153, 102, 255, 0.5)',
            'rgba(255, 159, 64, 0.5)',
            'rgba(201, 203, 207, 0.5)',
            'rgba(255, 99, 132, 0.5)',
            'rgba(54, 162, 235, 0.5)',
            'rgba(255, 206, 86, 0.5)'
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)',
            'rgba(201, 203, 207, 1)',
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)'
          ],
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    });
  }
});
</script>

<template>
  <div class="container">
    <canvas ref="barChartCanvas"></canvas>
  </div>
</template>

<style scoped>
.container {
  padding-top: 25px;
  padding-bottom: 20px;
}
</style>
