<template>
  <div class="detail-page page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Detail</span>
        <h1 class="page-title">图书详情</h1>
        <p class="page-subtitle">查看馆藏信息与借阅规则</p>
      </div>
    </header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-button text type="primary" @click="goBack">返回</el-button>
        </div>
      </template>

      <div v-if="loading" class="detail-loading">
        <el-skeleton :rows="6" animated />
      </div>
      <div v-else-if="!detail" class="detail-loading">
        <el-empty description="未找到图书" />
      </div>
      <div v-else class="detail-body">
        <div class="basic">
          <div
            class="cover-large"
            :style="{ backgroundImage: 'url(' + (detail.coverUrl || defaultCover) + ')' }"
          />
          <div class="info">
            <h2 class="title">{{ detail.title }}</h2>
            <div class="meta">
              <span>作者：{{ detail.author }}</span>
              <span v-if="detail.isbn">ISBN：{{ detail.isbn }}</span>
              <span v-if="detail.publisher">出版社：{{ detail.publisher }}</span>
            </div>
            <div class="status">
              <span>可借：</span>
              <span :class="detail.canBorrow ? 'can-borrow' : 'cannot-borrow'">
                {{ detail.availableCount }} / {{ detail.totalCount }}
              </span>
            </div>
            <div class="actions">
              <el-button
                type="primary"
                :disabled="!detail.canBorrow || borrowing"
                @click="handleBorrow"
              >
                借阅
              </el-button>
              <el-button :disabled="reserving" @click="handleReserve">预约</el-button>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="section">
          <h3>图书简介</h3>
          <p class="desc">
            {{ detail.description || '暂无简介' }}
          </p>
        </div>

        <div class="section">
          <h3>借阅规则</h3>
          <ul class="rules">
            <li v-for="rule in rules" :key="rule.title">
              <strong>{{ rule.title }}：</strong>{{ rule.content }}
            </li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { BookDetail, BorrowRuleItem } from '@/api/user'
import { borrowBook, getBookDetail, getBorrowRules, reserveBook } from '@/api/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const borrowing = ref(false)
const reserving = ref(false)

const detail = ref<BookDetail | null>(null)
const rules = ref<BorrowRuleItem[]>([])

const defaultCover = 'https://via.placeholder.com/120x160?text=%E5%9B%BE%E4%B9%A6'

const fetchDetail = async () => {
  const id = route.params.id as string
  loading.value = true
  try {
    detail.value = await getBookDetail(id)
  } catch (e) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const fetchRules = async () => {
  try {
    rules.value = await getBorrowRules()
  } catch (e) {
    // ignore
  }
}

const refreshDetailSafe = async () => {
  try {
    await fetchDetail()
  } catch (e) {
    // ignore
  }
}

const handleBorrow = async () => {
  if (!detail.value || borrowing.value) return
  borrowing.value = true
  try {
    await borrowBook(detail.value.id)
    ElMessage.success('借阅申请已提交')
    await refreshDetailSafe()
  } catch (e) {
    ElMessage.error('借阅失败')
  } finally {
    borrowing.value = false
  }
}

const handleReserve = async () => {
  if (!detail.value || reserving.value) return
  reserving.value = true
  try {
    await reserveBook(detail.value.id)
    ElMessage.success('预约已提交')
    await refreshDetailSafe()
  } catch (e) {
    ElMessage.error('预约失败')
  } finally {
    reserving.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchDetail()
  fetchRules()
})
</script>

<style scoped>
.detail-page {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  margin: 8px 0 0;
  font-size: clamp(24px, 2.6vw, 32px);
}

.page-subtitle {
  margin: 0;
  color: var(--color-muted);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-loading {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-body {
  padding-top: 4px;
}

.basic {
  display: flex;
  gap: 20px;
}

.cover-large {
  width: 140px;
  height: 190px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  background-color: rgba(107, 63, 42, 0.08);
  border: 1px solid rgba(107, 63, 42, 0.12);
}

.info {
  flex: 1;
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--color-muted);
}

.status {
  margin-top: 8px;
  font-size: 13px;
}

.can-borrow {
  color: var(--el-color-success);
  font-weight: 600;
}

.cannot-borrow {
  color: var(--el-color-danger);
  font-weight: 600;
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.section {
  margin-top: 12px;
}

.section h3 {
  margin: 0 0 8px;
  font-size: 15px;
}

.desc {
  margin: 0;
  font-size: 13px;
  color: var(--color-muted);
  line-height: 1.6;
}

.rules {
  padding-left: 18px;
  margin: 0;
  font-size: 13px;
  color: var(--color-muted);
  line-height: 1.6;
}

@media (max-width: 768px) {
  .basic {
    flex-direction: column;
    align-items: center;
  }

  .info {
    width: 100%;
  }
}
</style>
