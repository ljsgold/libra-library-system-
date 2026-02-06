<template>
  <el-card class="list-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <span class="card-title">热门图书排行</span>
          <span class="card-subtitle">按借阅次数排名</span>
        </div>
        <el-radio-group v-model="limit" size="small" @change="handleLimitChange">
          <el-radio-button :label="5">Top 5</el-radio-button>
          <el-radio-button :label="10">Top 10</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <div v-if="loading" class="list-loading">
      <el-skeleton :rows="6" animated />
    </div>
    <div v-else-if="error" class="list-empty">
      <el-empty description="热门图书数据加载失败" />
    </div>
    <div v-else-if="!books.length" class="list-empty">
      <el-empty description="暂无热门图书数据" />
    </div>
    <el-table
      v-else
      :data="books"
      size="small"
      border
      header-row-class-name="table-header-row"
      class="list-table"
    >
      <el-table-column type="index" label="#" width="50" align="center" />
      <el-table-column prop="title" label="书名" min-width="140" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" min-width="100" show-overflow-tooltip />
      <el-table-column
        prop="borrowCount"
        label="借阅次数"
        width="90"
        align="right"
      />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { HotBookItem } from '@/api/dashboard'
import { getHotBooks } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const error = ref(false)
const limit = ref<5 | 10>(5)
const books = ref<HotBookItem[]>([])

const fetchData = async () => {
  loading.value = true
  error.value = false
  try {
    books.value = await getHotBooks({ limit: limit.value })
  } catch (e) {
    error.value = true
    ElMessage.error('加载热门图书数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleLimitChange = () => {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.list-card {
  height: 100%;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border-light);
  transition: all 300ms ease;
}

.list-card:hover {
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

.list-table {
  width: 100%;
}

.table-header-row {
  background-color: var(--color-background-secondary) !important;
}

.list-loading,
.list-empty {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
