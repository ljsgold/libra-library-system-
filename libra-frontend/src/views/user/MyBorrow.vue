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
            <el-table :data="currentList" size="small" class="borrow-table" border>
              <el-table-column prop="bookTitle" label="书名" min-width="180" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="100" show-overflow-tooltip />
              <el-table-column prop="borrowDate" label="借出日期" width="120" />
              <el-table-column prop="dueDate" label="应还日期" width="120" />
              <el-table-column label="状态" width="90" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.status === 'OVERDUE' ? 'danger' : 'success'"
                    size="small"
                  >
                    {{ scope.row.status === 'OVERDUE' ? '逾期' : '借阅中' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="140" align="center">
                <template #default="scope">
                  <el-button
                    type="primary"
                    link
                    size="small"
                    :loading="returningId === scope.row.id"
                    @click="handleReturn(scope.row.id)"
                  >
                    归还
                  </el-button>
                  <el-button
                    v-if="scope.row.renewable"
                    type="primary"
                    link
                    size="small"
                    :loading="renewingId === scope.row.id"
                    @click="handleRenew(scope.row.id)"
                  >
                    续借
                  </el-button>
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
            <el-table :data="historyList" size="small" class="borrow-table" border>
              <el-table-column prop="bookTitle" label="书名" min-width="180" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="100" show-overflow-tooltip />
              <el-table-column prop="borrowDate" label="借出日期" width="120" />
              <el-table-column prop="dueDate" label="应还日期" width="120" />
              <el-table-column prop="returnDate" label="归还日期" width="120" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.status === 'OVERDUE' ? 'danger' : 'success'"
                    size="small"
                  >
                    {{ scope.row.status === 'OVERDUE' ? '逾期' : '已归还' }}
                  </el-tag>
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
            <el-table :data="reservationList" size="small" class="borrow-table" border>
              <el-table-column prop="bookTitle" label="书名" min-width="180" show-overflow-tooltip />
              <el-table-column prop="author" label="作者" min-width="100" show-overflow-tooltip />
              <el-table-column prop="queueNo" label="排队号" width="90" />
              <el-table-column prop="expectedDate" label="预计到馆" width="120" />
              <el-table-column prop="pickupDeadline" label="取书截止" width="140" />
              <el-table-column label="状态" width="110" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.status === 'NOTIFIED' ? 'success' : scope.row.status === 'CANCELLED' ? 'info' : 'warning'"
                    size="small"
                  >
                    {{ statusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="140" align="center">
                <template #default="scope">
                  <div class="actions">
                    <el-button
                      v-if="scope.row.status === 'WAITING' || scope.row.status === 'NOTIFIED'"
                      type="danger"
                      link
                      size="small"
                      :loading="cancellingId === scope.row.id"
                      @click="handleCancel(scope.row.id)"
                    >
                      取消
                    </el-button>
                    <el-button
                      v-if="scope.row.status === 'WAITING' && !scope.row.notified"
                      type="primary"
                      link
                      size="small"
                      :loading="subscribingId === scope.row.id"
                      @click="handleSubscribe(scope.row.id)"
                    >
                      到书提醒
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
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 200ms ease, transform 200ms ease;
}

.borrow-page :deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
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

.borrow-page :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(0,0,0,0.05);
  height: 1px;
}

.borrow-page :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-secondary);
  height: 48px;
  transition: all 0.3s ease;
}

.borrow-page :deep(.el-tabs__item.is-active) {
  color: var(--color-primary);
  font-weight: 700;
}

.borrow-page :deep(.el-tabs__active-bar) {
  background-color: var(--color-primary);
  height: 3px;
  border-radius: 3px;
}

.table-loading,
.table-empty {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.65);
  border-radius: 16px;
  margin-top: 16px;
}

.borrow-table {
  width: 100%;
  margin-top: 16px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  --el-table-border-color: rgba(0, 0, 0, 0.05);
  --el-table-header-bg-color: rgba(249, 250, 251, 0.8);
}

.borrow-table :deep(th.el-table__cell) {
  background-color: rgba(249, 250, 251, 0.8);
  font-weight: 600;
  color: var(--color-text-secondary);
  height: 48px;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.actions .el-button {
  font-weight: 600;
}
</style>
