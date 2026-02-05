<template>
  <el-card class="list-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <span class="card-title">逾期提醒</span>
          <span class="card-subtitle">仅显示前 5 条逾期记录</span>
        </div>
        <el-button size="small" text type="primary" @click="handleViewMore">
          前往借阅管理
        </el-button>
      </div>
    </template>

    <div v-if="loading" class="list-loading">
      <el-skeleton :rows="4" animated />
    </div>
    <div v-else-if="error" class="list-empty">
      <el-empty description="逾期数据加载失败" />
    </div>
    <div v-else-if="!records.length" class="list-empty">
      <el-empty description="暂无逾期记录" />
    </div>
    <el-table
      v-else
      :data="records"
      size="small"
      border
      header-row-class-name="table-header-row"
      class="list-table"
    >
      <el-table-column prop="userName" label="用户" min-width="100" show-overflow-tooltip />
      <el-table-column prop="bookTitle" label="图书" min-width="160" show-overflow-tooltip />
      <el-table-column label="逾期天数" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getOverdueTagType(scope.row.overdueDays)" effect="plain" size="small">
            {{ scope.row.overdueDays }} 天
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { OverdueRecordItem } from '@/api/dashboard'
import { getOverdueRecords } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const emit = defineEmits<{
  (e: 'goBorrowManage'): void
}>()

const loading = ref(false)
const error = ref(false)
const records = ref<OverdueRecordItem[]>([])

const fetchData = async () => {
  loading.value = true
  error.value = false
  try {
    records.value = await getOverdueRecords({ limit: 5 })
  } catch (e) {
    error.value = true
    ElMessage.error('加载逾期提醒数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const getOverdueTagType = (days: number) => {
  if (days <= 3) return 'warning'
  if (days <= 7) return 'danger'
  return 'danger'
}

const handleViewMore = () => {
  emit('goBorrowManage')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.list-card {
  height: 100%;
  border-radius: 18px;
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
  color: var(--color-muted);
}

.list-table {
  width: 100%;
}

.table-header-row {
  background-color: rgba(107, 63, 42, 0.04) !important;
}

.list-loading,
.list-empty {
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
