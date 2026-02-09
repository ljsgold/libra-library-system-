<template>
  <div class="borrow-manage-page page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Records</span>
        <h1 class="page-title">借阅管理</h1>
        <p class="page-subtitle">查看全量借阅记录并跟踪逾期状态。</p>
      </div>
      <div class="header-actions">
        <el-button>导出记录</el-button>
      </div>
    </header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">筛选条件</span>
            <span class="card-subtitle">按状态快速定位借阅记录</span>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="statusFilter" placeholder="状态" clearable class="filter-item">
          <el-option label="借阅中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="已逾期" :value="3" />
          <el-option label="已赔付" :value="4" />
        </el-select>
        <el-button type="primary" @click="fetchRecords">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>

      <div v-if="loading" class="table-loading">
        <el-skeleton :rows="6" animated />
      </div>
      <div v-else-if="!records.length" class="table-empty">
        <el-empty description="暂无借阅记录" />
      </div>
      <div v-else>
        <el-table :data="records" border size="small">
          <el-table-column prop="recordId" label="记录ID" width="110" />
          <el-table-column prop="userName" label="读者" min-width="120" />
          <el-table-column prop="bookTitle" label="图书" min-width="180" show-overflow-tooltip />
          <el-table-column label="借出日期" width="170">
            <template #default="scope">
              {{ formatDate(scope.row.borrowTime) }}
            </template>
          </el-table-column>
          <el-table-column label="应还日期" width="170">
            <template #default="scope">
              {{ formatDate(scope.row.dueTime) }}
            </template>
          </el-table-column>
          <el-table-column label="归还日期" width="170">
            <template #default="scope">
              {{ formatDate(scope.row.returnTime) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="statusTag(scope.row.status)" size="small">
                {{ statusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="逾期天数" width="100" align="center">
            <template #default="scope">
              {{ scope.row.overdueDays || 0 }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getBorrowRecords, type AdminBorrowRecord } from '@/api/admin'

const records = ref<AdminBorrowRecord[]>([])
const loading = ref(false)
const statusFilter = ref<number | undefined>()

const fetchRecords = async () => {
  loading.value = true
  try {
    records.value = await getBorrowRecords({
      status: statusFilter.value,
      limit: 100
    })
  } catch (e) {
    ElMessage.error('获取借阅记录失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  statusFilter.value = undefined
  fetchRecords()
}

const formatDate = (value?: string) => {
  if (!value) return '--'
  // 保留完整时间，格式：2026-02-06 14:30:45
  return value.slice(0, 19).replace('T', ' ')
}

const statusText = (status?: number) => {
  switch (status) {
    case 1:
      return '借阅中'
    case 2:
      return '已归还'
    case 3:
      return '已逾期'
    case 4:
      return '已赔付'
    default:
      return '未知'
  }
}

const statusTag = (status?: number) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'info'
    case 3:
      return 'danger'
    case 4:
      return 'warning'
    default:
      return 'info'
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.borrow-manage-page {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.borrow-manage-page :deep(.el-card) {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 200ms ease, transform 200ms ease;
}

.borrow-manage-page :deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding: 0 12px;
}

.eyebrow {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-primary);
  margin-bottom: 8px;
  display: inline-block;
}

.page-title {
  margin: 0;
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-subtitle {
  margin: 0;
  font-size: 17px;
  color: var(--color-text-secondary);
  max-width: 600px;
  line-height: 1.5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 18px;
  background: var(--color-primary);
  border-radius: 2px;
}

.card-subtitle {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 24px;
}

.filter-item {
  min-width: 160px;
}

.table-loading,
.table-empty {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.65);
  border-radius: 16px;
}

.borrow-manage-page :deep(.el-table) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  --el-table-border-color: rgba(0, 0, 0, 0.05);
  --el-table-header-bg-color: rgba(249, 250, 251, 0.8);
}

.borrow-manage-page :deep(th.el-table__cell) {
  background-color: rgba(249, 250, 251, 0.8);
  font-weight: 600;
  color: var(--color-text-secondary);
  height: 48px;
}

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
