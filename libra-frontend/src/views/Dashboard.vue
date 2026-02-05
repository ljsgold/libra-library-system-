<template>
  <div class="dashboard-container page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Admin dashboard</span>
        <h1 class="page-title">运营概览</h1>
        <p class="page-subtitle">关键指标、借阅趋势与待办事项一屏掌握。</p>
      </div>
      <div class="header-actions">
        <el-button type="primary">导出报表</el-button>
        <el-button>新建公告</el-button>
      </div>
    </header>

    <kpi-cards @navigate="handleKpiNavigate" />

    <el-row :gutter="16" class="mt-16">
      <el-col :xs="24" :md="16">
        <borrow-trend-chart />
      </el-col>
      <el-col :xs="24" :md="8" class="mt-md-0 mt-16">
        <hot-books-list />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt-16">
      <el-col :xs="24" :md="16">
        <overdue-reminder @goBorrowManage="handleBorrowManage" />
      </el-col>
      <el-col :xs="24" :md="8" class="mt-md-0 mt-16">
        <quick-actions @quick="handleQuickAction" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import KpiCards from './dashboard/components/KpiCards.vue'
import BorrowTrendChart from './dashboard/components/BorrowTrendChart.vue'
import HotBooksList from './dashboard/components/HotBooksList.vue'
import OverdueReminder from './dashboard/components/OverdueReminder.vue'
import QuickActions from './dashboard/components/QuickActions.vue'

const router = useRouter()

const handleKpiNavigate = (payload: { type: 'books' | 'borrow' | 'overdue' }) => {
  if (payload.type === 'books') {
    router.push('/admin/books')
  }
  if (payload.type === 'borrow' || payload.type === 'overdue') {
    router.push('/admin/books')
  }
}

const handleBorrowManage = () => {
  router.push('/admin/books')
}

const handleQuickAction = (payload: { key: string }) => {
  switch (payload.key) {
    case 'addBook':
      router.push('/admin/books')
      break
    case 'borrowManage':
      router.push('/admin/books')
      break
    case 'categoryManage':
      router.push('/admin/books')
      break
    case 'userManage':
      router.push('/admin/books')
      break
    default:
      break
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 4px 0 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
}

.page-title {
  margin: 8px 0 6px;
  font-size: clamp(28px, 3vw, 40px);
}

.page-subtitle {
  margin: 0;
  color: var(--color-muted);
  max-width: 520px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.mt-16 {
  margin-top: 16px;
}

.mt-md-0 {
  margin-top: 0;
}

@media (max-width: 991px) {
  .mt-md-0 {
    margin-top: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
