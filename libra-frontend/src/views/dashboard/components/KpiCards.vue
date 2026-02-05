<template>
  <el-card class="kpi-card-wrapper" shadow="never">
    <div class="kpi-header">
      <div class="kpi-title">核心指标概览</div>
      <div class="kpi-subtitle">当前系统运营状态一览</div>
    </div>
    <el-row :gutter="16">
      <el-col v-for="item in items" :key="item.key" :xs="12" :sm="8" :md="4">
        <div
          class="kpi-card"
          :class="[{ 'is-clickable': !!item.actionKey }, item.className]"
          @click="handleClick(item)"
        >
          <div class="kpi-label">
            <span>{{ item.label }}</span>
          </div>
          <div class="kpi-value-wrapper">
            <transition name="kpi-count">
              <div :key="item.value" class="kpi-value">
                <span>{{ item.value }}</span>
              </div>
            </transition>
          </div>
          <div v-if="item.desc" class="kpi-desc">{{ item.desc }}</div>
        </div>
      </el-col>
    </el-row>
    <el-skeleton v-if="loading" :rows="1" animated class="kpi-skeleton" />
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { DashboardOverview } from '@/api/dashboard'
import { getDashboardOverview } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const emit = defineEmits<{
  (e: 'navigate', payload: { type: 'books' | 'borrow' | 'overdue' }): void
}>()

const loading = ref(false)
const data = ref<DashboardOverview | null>(null)

const items = computed(() => {
  const d = data.value
  return [
    {
      key: 'totalBookTypes',
      label: '图书种类数',
      value: d?.totalBookTypes ?? 0,
      desc: '去重后的图书品种数',
      className: 'kpi-primary',
      actionKey: 'books' as const
    },
    {
      key: 'totalInventory',
      label: '馆藏总量',
      value: d?.totalInventory ?? 0,
      desc: '在馆图书总册数',
      className: 'kpi-normal',
      actionKey: 'books' as const
    },
    {
      key: 'borrowingCount',
      label: '当前借出',
      value: d?.borrowingCount ?? 0,
      desc: '当前尚未归还册数',
      className: 'kpi-warning',
      actionKey: 'borrow' as const
    },
    {
      key: 'overdueCount',
      label: '逾期未还',
      value: d?.overdueCount ?? 0,
      desc: '全部逾期借阅记录',
      className: 'kpi-danger',
      actionKey: 'overdue' as const
    },
    {
      key: 'todayBorrowCount',
      label: '今日借出',
      value: d?.todayBorrowCount ?? 0,
      desc: '今天新增借阅记录',
      className: 'kpi-accent',
      actionKey: 'borrow' as const
    },
    {
      key: 'todayReturnCount',
      label: '今日归还',
      value: d?.todayReturnCount ?? 0,
      desc: '今天已归还记录',
      className: 'kpi-accent',
      actionKey: 'borrow' as const
    }
  ]
})

const fetchData = async () => {
  loading.value = true
  try {
    data.value = await getDashboardOverview()
  } catch (error) {
    ElMessage.error('加载核心指标失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleClick = (item: (typeof items.value)[number]) => {
  if (!item.actionKey) return
  emit('navigate', { type: item.actionKey })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.kpi-card-wrapper {
  border-radius: 20px;
}

.kpi-header {
  margin-bottom: 16px;
}

.kpi-title {
  font-size: 16px;
  font-weight: 600;
}

.kpi-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-muted);
}

.kpi-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 12px 14px;
  margin-bottom: 8px;
  border: 1px solid rgba(107, 63, 42, 0.12);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.kpi-card.is-clickable {
  cursor: pointer;
}

.kpi-card.is-clickable:hover {
  border-color: rgba(138, 90, 62, 0.4);
  box-shadow: var(--shadow-md);
}

.kpi-label {
  font-size: 13px;
  color: var(--color-muted);
}

.kpi-value-wrapper {
  margin-top: 4px;
}

.kpi-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
}

.kpi-desc {
  margin-top: 2px;
  font-size: 11px;
  color: rgba(82, 82, 91, 0.7);
}

.kpi-primary .kpi-value {
  color: var(--color-cta);
}

.kpi-normal .kpi-value {
  color: var(--color-text);
}

.kpi-warning .kpi-value {
  color: #f59e0b;
}

.kpi-danger .kpi-value {
  color: #ef4444;
}

.kpi-accent .kpi-value {
  color: #22c55e;
}

.kpi-count-enter-active {
  transition: all 0.25s ease-out;
}

.kpi-count-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.kpi-count-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.kpi-skeleton {
  margin-top: 8px;
}

@media (prefers-reduced-motion: reduce) {
  .kpi-card.is-clickable:hover {
    box-shadow: none;
  }
}
</style>
