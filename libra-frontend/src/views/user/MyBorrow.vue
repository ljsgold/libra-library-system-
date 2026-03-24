<template>
  <div class="borrow-page page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Borrowing</span>
        <h1 class="page-title">我的借阅</h1>
        <p class="page-subtitle">当前借阅、历史记录与预约提醒。</p>
      </div>
    </header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">借阅记录</span>
            <span class="card-subtitle">切换标签查看不同状态</span>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="当前借阅" name="current">
          <div v-if="loadingCurrent" class="table-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!currentList.length" class="table-empty">
            <el-empty description="暂无借阅" />
          </div>
          <div v-else>
            <el-table :data="currentList" class="borrow-table borrow-table--clean">
              <el-table-column prop="bookTitle" label="书名" min-width="200" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="110" show-overflow-tooltip />
              <el-table-column prop="borrowDate" label="借出日期" width="128" align="center" />
              <el-table-column prop="dueDate" label="应还日期" width="128" align="center" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="scope">
                  <span class="status-badge" :class="scope.row.status === 'OVERDUE' ? 'status-overdue' : 'status-ongoing'">
                    {{ scope.row.status === 'OVERDUE' ? '逾期' : '借阅中' }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center" class-name="col-actions">
                <template #default="scope">
                  <div class="action-btns">
                    <el-button
                      type="primary"
                      size="small"
                      class="btn-action"
                      :loading="returningId === scope.row.id"
                      @click="handleReturn(scope.row.id)"
                    >
                      归还
                    </el-button>
                    <el-button
                      v-if="scope.row.renewable"
                      type="default"
                      size="small"
                      class="btn-action btn-secondary"
                      :loading="renewingId === scope.row.id"
                      @click="handleRenew(scope.row.id)"
                    >
                      续借
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="历史记录" name="history">
          <div v-if="loadingHistory" class="table-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!historyList.length" class="table-empty">
            <el-empty description="暂无历史记录" />
          </div>
          <div v-else>
            <el-table :data="historyList" class="borrow-table borrow-table--clean">
              <el-table-column prop="bookTitle" label="书名" min-width="200" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="110" show-overflow-tooltip />
              <el-table-column prop="borrowDate" label="借出日期" width="128" align="center" />
              <el-table-column prop="dueDate" label="应还日期" width="128" align="center" />
              <el-table-column prop="returnDate" label="归还日期" width="128" align="center" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="scope">
                  <span class="status-badge status-returned">{{ scope.row.status === 'OVERDUE' ? '逾期' : '已归还' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="预约" name="reservation">
          <div v-if="loadingReservation" class="table-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!reservationList.length" class="table-empty">
            <el-empty description="暂无预约" />
          </div>
          <div v-else>
            <el-table :data="reservationList" class="borrow-table borrow-table--clean">
              <el-table-column prop="bookTitle" label="书名" min-width="200" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="110" show-overflow-tooltip />
              <el-table-column prop="queueNo" label="排队号" width="90" align="center" />
              <el-table-column prop="expectedDate" label="预计到馆" width="120" align="center" />
              <el-table-column prop="pickupDeadline" label="取书截止" width="120" align="center" />
              <el-table-column label="状态" width="110" align="center">
                <template #default="scope">
                  <span class="status-badge" :class="'status-' + (scope.row.status === 'NOTIFIED' ? 'notified' : scope.row.status === 'CANCELLED' ? 'cancelled' : 'waiting')">
                    {{ statusText(scope.row.status) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center" class-name="col-actions">
                <template #default="scope">
                  <div class="action-btns">
                    <el-button
                      v-if="scope.row.status === 'WAITING' && !scope.row.notified"
                      type="primary"
                      size="small"
                      class="btn-action"
                      :loading="subscribingId === scope.row.id"
                      @click="handleSubscribe(scope.row.id)"
                    >
                      到书提醒
                    </el-button>
                    <el-button
                      v-if="scope.row.status === 'WAITING' || scope.row.status === 'NOTIFIED'"
                      type="danger"
                      plain
                      size="small"
                      class="btn-action"
                      :loading="cancellingId === scope.row.id"
                      @click="handleCancel(scope.row.id)"
                    >
                      取消预约
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { BorrowRecord, ReservationRecord } from '@/api/user'
import { getCurrentBorrowList, getHistoryBorrowList, renewBorrow, returnBorrow, getReservationList, cancelReservation, subscribeArrival } from '@/api/user'
import { ElMessage } from 'element-plus'

const activeTab = ref<'current' | 'history' | 'reservation'>('current')

const currentList = ref<BorrowRecord[]>([])
const historyList = ref<BorrowRecord[]>([])
const reservationList = ref<ReservationRecord[]>([])

const loadingCurrent = ref(false)
const loadingHistory = ref(false)
const loadingReservation = ref(false)
const renewingId = ref<string | null>(null)
const returningId = ref<string | null>(null)
const cancellingId = ref<string | null>(null)
const subscribingId = ref<string | null>(null)

const fetchCurrent = async () => {
  loadingCurrent.value = true
  try {
    currentList.value = await getCurrentBorrowList()
  } catch (e) {
    ElMessage.error('获取当前借阅失败')
  } finally {
    loadingCurrent.value = false
  }
}

const fetchHistory = async () => {
  loadingHistory.value = true
  try {
    historyList.value = await getHistoryBorrowList()
  } catch (e) {
    ElMessage.error('获取历史记录失败')
  } finally {
    loadingHistory.value = false
  }
}

const fetchReservation = async () => {
  loadingReservation.value = true
  try {
    reservationList.value = await getReservationList()
  } catch (e) {
    ElMessage.error('获取预约失败')
  } finally {
    loadingReservation.value = false
  }
}

const handleRenew = async (id: string) => {
  renewingId.value = id
  try {
    await renewBorrow(id)
    ElMessage.success('续借成功')
    fetchCurrent()
  } catch (e) {
    ElMessage.error('续借失败')
  } finally {
    renewingId.value = null
  }
}

const handleReturn = async (id: string) => {
  returningId.value = id
  try {
    await returnBorrow(id)
    ElMessage.success('归还成功')
    fetchCurrent()
    if (historyList.value.length) {
      fetchHistory()
    }
  } catch (e) {
  } finally {
    returningId.value = null
  }
}

const handleCancel = async (id: string) => {
  cancellingId.value = id
  try {
    await cancelReservation(id)
    ElMessage.success('取消成功')
    fetchReservation()
  } catch (e) {
    ElMessage.error('取消失败')
  } finally {
    cancellingId.value = null
  }
}

const handleSubscribe = async (id: string) => {
  subscribingId.value = id
  try {
    await subscribeArrival(id)
    ElMessage.success('订阅成功')
    fetchReservation()
  } catch (e) {
    ElMessage.error('订阅失败')
  } finally {
    subscribingId.value = null
  }
}

const handleTabChange = (name: string) => {
  if (name === 'history' && !historyList.value.length) {
    fetchHistory()
  }
  if (name === 'reservation' && !reservationList.value.length) {
    fetchReservation()
  }
}

const statusText = (status: ReservationRecord['status']) => {
  switch (status) {
    case 'WAITING':
      return '排队中'
    case 'NOTIFIED':
      return '可取书'
    case 'CANCELLED':
      return '已取消'
    default:
      return status
  }
}

onMounted(() => {
  fetchCurrent()
})
</script>

<style scoped>
.borrow-page {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.borrow-page :deep(.el-card) {
  background: var(--glass-bg);
  border: var(--glow-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  backdrop-filter: blur(var(--glass-blur));
  transition: all var(--transition-normal);
}

.borrow-page :deep(.el-card__header) {
  border-bottom: 1px solid var(--color-border);
  padding: 20px 24px;
  background: rgba(15, 23, 42, 0.5);
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 12px;
}

.eyebrow {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: var(--color-primary);
  margin-bottom: 8px;
  display: inline-block;
  padding: 6px 12px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: var(--radius-full);
}

.page-title {
  margin: 0;
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--color-text);
  font-family: 'Space Grotesk', sans-serif;
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
  background: var(--gradient-primary);
  border-radius: 2px;
  box-shadow: 0 0 10px var(--color-primary-glow);
}

.card-subtitle {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.borrow-page :deep(.el-tabs__nav-wrap::after) {
  background-color: var(--color-border);
  height: 1px;
}

.borrow-page :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-secondary);
  height: 48px;
  transition: all var(--transition-normal);
}

.borrow-page :deep(.el-tabs__item:hover) {
  color: var(--color-primary-light);
}

.borrow-page :deep(.el-tabs__item.is-active) {
  color: var(--color-primary);
  font-weight: 700;
}

.borrow-page :deep(.el-tabs__active-bar) {
  background: var(--gradient-primary);
  height: 3px;
  border-radius: 3px;
  box-shadow: 0 0 15px var(--color-primary-glow);
}

.table-loading,
.table-empty {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  margin-top: 16px;
}

.borrow-table {
  width: 100%;
  margin-top: 20px;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.borrow-table--clean :deep(.el-table__inner-wrapper::before),
.borrow-table--clean :deep(.el-table::before) {
  display: none;
}

.borrow-table--clean :deep(.el-table__header th.el-table__cell) {
  background: rgba(15, 23, 42, 0.95);
  font-weight: 600;
  font-size: 12px;
  color: var(--color-text);
  letter-spacing: 0.05em;
  text-transform: uppercase;
  height: 52px;
  padding: 0 16px;
  border-bottom: 1px solid var(--color-border);
}

.borrow-table--clean :deep(.el-table__body td.el-table__cell) {
  padding: 16px;
  font-size: 14px;
  color: var(--color-text-secondary);
  border-bottom: 1px solid var(--color-border-light);
  transition: background var(--transition-fast);
}

.borrow-table--clean :deep(.el-table__body tr:hover td.el-table__cell) {
  background: rgba(59, 130, 246, 0.08) !important;
}

.borrow-table--clean :deep(.el-table__body tr:last-child td.el-table__cell) {
  border-bottom: none;
}

.borrow-table--clean :deep(td.col-actions) {
  vertical-align: middle;
  min-height: 56px;
}

.action-btns {
  display: inline-flex;
  flex-wrap: nowrap;
  gap: 10px;
  justify-content: center;
  align-items: center;
  min-height: 36px;
}

.action-btns .btn-action {
  font-weight: 600;
  border-radius: var(--radius-md);
  min-width: 76px;
  margin: 0;
  border: var(--glow-border);
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
}

.action-btns .btn-action:hover:not(:disabled) {
  border: var(--glow-border-hover);
  box-shadow: var(--glow-shadow);
}

.action-btns .btn-secondary {
  border: var(--glow-border);
  background: var(--glass-bg);
}

.action-btns .btn-secondary:hover:not(:disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary-light);
  background: rgba(59, 130, 246, 0.1);
}

.status-badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 0.02em;
}

.status-ongoing {
  background: rgba(34, 197, 94, 0.15);
  color: var(--color-neon);
  border: 1px solid rgba(34, 197, 94, 0.3);
  box-shadow: 0 0 15px rgba(34, 197, 94, 0.15);
}

.status-overdue {
  background: rgba(239, 68, 68, 0.15);
  color: #EF4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
  box-shadow: 0 0 15px rgba(239, 68, 68, 0.15);
  animation: pulse-danger 2s infinite;
}

@keyframes pulse-danger {
  0%, 100% { box-shadow: 0 0 15px rgba(239, 68, 68, 0.15); }
  50% { box-shadow: 0 0 25px rgba(239, 68, 68, 0.3); }
}

.status-returned {
  background: rgba(59, 130, 246, 0.15);
  color: var(--color-primary);
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.status-waiting {
  background: rgba(251, 191, 36, 0.15);
  color: #FBBF24;
  border: 1px solid rgba(251, 191, 36, 0.3);
  box-shadow: 0 0 15px rgba(251, 191, 36, 0.1);
}

.status-notified {
  background: rgba(34, 197, 94, 0.15);
  color: var(--color-neon);
  border: 1px solid rgba(34, 197, 94, 0.3);
  box-shadow: 0 0 15px rgba(34, 197, 94, 0.15);
  animation: pulse-success 2s infinite;
}

@keyframes pulse-success {
  0%, 100% { box-shadow: 0 0 15px rgba(34, 197, 94, 0.15); }
  50% { box-shadow: 0 0 25px rgba(34, 197, 94, 0.3); }
}

.status-cancelled {
  background: rgba(255, 255, 255, 0.05);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}
</style>
