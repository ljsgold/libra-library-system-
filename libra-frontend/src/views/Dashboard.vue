<template>
  <div class="dashboard">
    <section class="stats-section">
      <div class="stats-grid">
        <div v-for="item in kpiItems" :key="item.key" class="stat-card" :class="item.type" @click="handleKpiClick(item)">
          <div class="stat-icon">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
          <div class="stat-trend" v-if="item.trend">
            {{ item.trend }}
          </div>
        </div>
      </div>
    </section>

    <section class="charts-section">
      <el-row :gutter="24">
        <el-col :xs="24" :lg="16">
          <borrow-trend-chart />
        </el-col>
        <el-col :xs="24" :lg="8">
          <hot-books-list />
        </el-col>
      </el-row>
    </section>

    <section class="bottom-section">
      <el-row :gutter="24">
        <el-col :xs="24" :lg="16">
          <overdue-reminder @goBorrowManage="handleBorrowManage" />
        </el-col>
        <el-col :xs="24" :lg="8">
          <quick-actions @quick="handleQuickAction" />
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { DashboardOverview } from '@/api/dashboard'
import { getDashboardOverview } from '@/api/dashboard'
import { Collection, Document, Download, Tickets, Upload, WarningFilled } from '@element-plus/icons-vue'
import BorrowTrendChart from './dashboard/components/BorrowTrendChart.vue'
import HotBooksList from './dashboard/components/HotBooksList.vue'
import OverdueReminder from './dashboard/components/OverdueReminder.vue'
import QuickActions from './dashboard/components/QuickActions.vue'

const router = useRouter()
const overview = ref<DashboardOverview | null>(null)

const kpiItems = ref([
  { key: 'totalBookTypes', icon: Collection, label: '图书种类', value: 0, type: 'primary', actionType: 'books' },
  { key: 'totalInventory', icon: Document, label: '馆藏总量', value: 0, type: 'default', actionType: 'books' },
  { key: 'borrowingCount', icon: Tickets, label: '当前借出', value: 0, type: 'warning', actionType: 'borrow' },
  { key: 'overdueCount', icon: WarningFilled, label: '逾期未还', value: 0, type: 'danger', actionType: 'overdue' },
  { key: 'todayBorrowCount', icon: Download, label: '今日借出', value: 0, type: 'success', trend: '+12%', actionType: 'borrow' },
  { key: 'todayReturnCount', icon: Upload, label: '今日归还', value: 0, type: 'success', trend: '+8%', actionType: 'borrow' }
])

const fetchOverview = async () => {
  try {
    overview.value = await getDashboardOverview()
    if (overview.value) {
      kpiItems.value[0].value = overview.value.totalBookTypes
      kpiItems.value[1].value = overview.value.totalInventory
      kpiItems.value[2].value = overview.value.borrowingCount
      kpiItems.value[3].value = overview.value.overdueCount
      kpiItems.value[4].value = overview.value.todayBorrowCount
      kpiItems.value[5].value = overview.value.todayReturnCount
    }
  } catch (e) {
  }
}

const handleKpiClick = (item: any) => {
  if (item.actionType === 'books') {
    router.push('/admin/books')
  } else {
    router.push('/admin/borrows')
  }
}

const handleBorrowManage = () => {
  router.push('/admin/borrows')
}

const handleQuickAction = (payload: { key: string }) => {
  switch (payload.key) {
    case 'addBook':
    case 'categoryManage':
      router.push('/admin/books')
      break
    case 'borrowManage':
    case 'userManage':
      router.push('/admin/borrows')
      break
  }
}

onMounted(() => {
  fetchOverview()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 8px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.stat-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 200ms ease, transform 200ms ease, border-color 200ms ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
  border-color: rgba(var(--color-primary-rgb), 0.18);
}

.stat-icon {
  font-size: 18px;
  color: var(--color-primary);
  margin-bottom: 12px;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--color-text);
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  background: rgba(var(--color-primary-rgb), 0.1);
  padding: 4px 8px;
  border-radius: 6px;
}
.charts-section,
.bottom-section {
  margin-top: 8px;
}
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
