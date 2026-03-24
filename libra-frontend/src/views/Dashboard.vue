<template>
  <div class="dashboard">
    <section class="stats-section">
      <div class="stats-grid">
        <div 
          v-for="item in kpiItems" 
          :key="item.key" 
          class="stat-card" 
          :class="item.type" 
          @click="handleKpiClick(item)"
          @mouseenter="handleCardHover($event)"
          @mousemove="handleCardTilt($event)"
          @mouseleave="handleCardLeave($event)"
        >
          <div class="stat-glare"></div>
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

// 3D 卡片倾斜效果
const handleCardTilt = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2
  const rotateX = (y - centerY) / 10
  const rotateY = (centerX - x) / 10
  
  card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.02)`
}

const handleCardHover = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  
  const glare = card.querySelector('.stat-glare') as HTMLElement
  if (glare) {
    glare.style.background = `radial-gradient(circle at ${x}px ${y}px, rgba(255,255,255,0.3) 0%, transparent 60%)`
    glare.style.opacity = '1'
  }
}

const handleCardLeave = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1)'
  
  const glare = card.querySelector('.stat-glare') as HTMLElement
  if (glare) {
    glare.style.opacity = '0'
  }
}

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
  background: var(--glass-bg);
  border: var(--glow-border);
  border-radius: var(--radius-xl);
  padding: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(var(--glass-blur));
  transform-style: preserve-3d;
  perspective: 1000px;
}

.stat-glare {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at var(--x, 50%) var(--y, 50%), rgba(255,255,255,0.3) 0%, transparent 60%);
  opacity: 0;
  transition: opacity var(--transition-normal);
  pointer-events: none;
  z-index: 10;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.stat-card:hover {
  border: var(--glow-border-hover);
  box-shadow: var(--glow-shadow-hover);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card.type-primary::before { background: var(--gradient-primary); opacity: 1; }
.stat-card.type-success::before { background: linear-gradient(135deg, #22C55E, #10B981); opacity: 0.6; }
.stat-card.type-warning::before { background: linear-gradient(135deg, #FBBF24, #F59E0B); opacity: 0.6; }
.stat-card.type-danger::before { background: linear-gradient(135deg, #EF4444, #DC2626); opacity: 0.6; }

.stat-icon {
  font-size: 20px;
  color: var(--color-primary);
  margin-bottom: 12px;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
  position: relative;
  z-index: 5;
}

.type-primary .stat-icon { background: rgba(59, 130, 246, 0.1); color: var(--color-primary); }
.type-success .stat-icon { background: rgba(34, 197, 94, 0.1); color: var(--color-neon); }
.type-warning .stat-icon { background: rgba(251, 191, 36, 0.1); color: #FBBF24; }
.type-danger .stat-icon { background: rgba(239, 68, 68, 0.1); color: #EF4444; }

.stat-value {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--color-text);
  line-height: 1;
  margin-bottom: 4px;
  font-family: 'Space Grotesk', sans-serif;
  position: relative;
  z-index: 5;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
  position: relative;
  z-index: 5;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 11px;
  font-weight: 700;
  color: var(--color-neon);
  background: rgba(34, 197, 94, 0.1);
  padding: 4px 8px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(34, 197, 94, 0.2);
  box-shadow: 0 0 10px rgba(34, 197, 94, 0.1);
  z-index: 5;
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
