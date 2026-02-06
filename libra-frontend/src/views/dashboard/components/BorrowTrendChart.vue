<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <span class="card-title">借阅量趋势分析</span>
          <span class="card-subtitle">近期借阅数量变化趋势，单位：册</span>
        </div>
        <el-radio-group v-model="days" size="small" @change="handleRangeChange">
          <el-radio-button :label="7">近7天</el-radio-button>
          <el-radio-button :label="30">近30天</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <div v-if="loading" class="chart-loading">
      <el-skeleton :rows="4" animated />
    </div>
    <div v-else-if="error" class="chart-empty">
      <el-empty description="借阅趋势数据加载失败" />
    </div>
    <div v-else-if="!trendData.length" class="chart-empty">
      <el-empty description="暂无借阅趋势数据" />
    </div>
    <div v-else ref="chartRef" class="chart-body"></div>
  </el-card>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import type { BorrowTrendItem } from '@/api/dashboard'
import { getBorrowTrend } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const chartRef = ref<HTMLDivElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const loading = ref(false)
const error = ref(false)
const days = ref<7 | 30>(7)
const trendData = ref<BorrowTrendItem[]>([])

const primary = '#007AFF'
const secondary = '#1D1D1F'

const initChart = () => {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const xData = trendData.value.map((item) => item.date)
  const yData = trendData.value.map((item) => item.borrowCount)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const p = params[0]
        return `${p.axisValue}<br/>借阅量：${p.data} 册`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.1)' } },
      axisLabel: { color: '#71717A' }
    },
    yAxis: {
      type: 'value',
      name: '借阅量（册）',
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.1)' } },
      axisLabel: { color: '#71717A' },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } }
    },
    series: [
      {
        name: '借阅量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: yData,
        itemStyle: {
          color: primary
        },
        lineStyle: {
          width: 2,
          color: secondary
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(0, 122, 255, 0.2)' },
            { offset: 1, color: 'rgba(0, 122, 255, 0.02)' }
          ])
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const fetchData = async () => {
  loading.value = true
  error.value = false
  try {
    trendData.value = await getBorrowTrend({ days: days.value })
    if (trendData.value.length) {
      initChart()
    }
  } catch (e) {
    error.value = true
    ElMessage.error('加载借阅趋势数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleRangeChange = () => {
  fetchData()
}

const handleResize = () => {
  chartInstance && chartInstance.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

watch(
  () => trendData.value,
  () => {
    if (trendData.value.length && chartInstance) {
      initChart()
    }
  }
)
</script>

<style scoped>
.chart-card {
  height: 100%;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border-light);
  transition: all 300ms ease;
}

.chart-card:hover {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
}

.card-subtitle {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.chart-body {
  width: 100%;
  height: 340px;
}

.chart-loading,
.chart-empty {
  height: 340px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
