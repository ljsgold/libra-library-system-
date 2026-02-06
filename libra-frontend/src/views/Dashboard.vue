<template>
  <div class="dashboard">
    <!-- Stats Grid -->
    <section class="stats-section">
      <div class="stats-grid">
        <div v-for="item in kpiItems" :key="item.key" class="stat-card" :class="item.type" @click="handleKpiClick(item)">
          <div class="stat-icon">{{ item.icon }}</div>
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

    <!-- Charts Row -->
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

    <!-- Bottom Row -->
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
import BorrowTrendChart from './dashboard/components/BorrowTrendChart.vue'
import HotBooksList from './dashboard/components/HotBooksList.vue'
import OverdueReminder from './dashboard/components/OverdueReminder.vue'
import QuickActions from './dashboard/components/QuickActions.vue'

const router = useRouter()
const overview = ref<DashboardOverview | null>(null)

const kpiItems = ref([
  { key: 'totalBookTypes', icon: '📚', label: '图书种类', value: 0, type: 'primary', actionType: 'books' },
  { key: 'totalInventory', icon: '📖', label: '馆藏总量', value: 0, type: 'default', actionType: 'books' },
  { key: 'borrowingCount', icon: '📝', label: '当前借出', value: 0, type: 'warning', actionType: 'borrow' },
  { key: 'overdueCount', icon: '⚠️', label: '逾期未还', value: 0, type: 'danger', actionType: 'overdue' },
  { key: 'todayBorrowCount', icon: '📥', label: '今日借出', value: 0, type: 'success', trend: '+12%', actionType: 'borrow' },
  { key: 'todayReturnCount', icon: '📤', label: '今日归还', value: 0, type: 'success', trend: '+8%', actionType: 'borrow' },
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
    // Handle error
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
  background: radial-gradient(circle at 10% 20%, rgba(167, 139, 250, 0.05) 0%, transparent 50%),
            radial-gradient(circle at 90% 80%, rgba(249, 115, 22, 0.05) 0%, transparent 50%);
  padding: 24px 0;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border-light);
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  border-radius: 16px 16px 0 0;
  opacity: 0;
  transition: opacity 200ms ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
  background: rgba(255, 255, 255, 0.9);
  border-color: var(--color-primary);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card.primary::before { background: var(--color-primary); }
.stat-card.success::before { background: var(--color-success); }
.stat-card.warning::before { background: var(--color-warning); }
.stat-card.danger::before { background: var(--color-danger); }

.stat-icon {
  font-size: 24px;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
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
  color: var(--color-success);
  background: rgba(52, 199, 89, 0.1);
  padding: 4px 8px;
  border-radius: 6px;
}

/* Sections */
.charts-section,
.bottom-section {
  margin-top: 8px;
}

/* Responsive */
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
