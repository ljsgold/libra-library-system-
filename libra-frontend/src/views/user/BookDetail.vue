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
          <el-image class="cover-large" :src="resolveCoverUrl(detail.coverUrl)" fit="cover" />
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
                size="large"
                class="btn-borrow"
                :disabled="!detail.canBorrow || borrowing"
                :loading="borrowing"
                @click="handleBorrow"
              >
                <el-icon><Document /></el-icon>
                <span>立即借阅</span>
              </el-button>
              <el-button
                type="default"
                size="large"
                class="btn-reserve"
                :disabled="reserving"
                :loading="reserving"
                @click="handleReserve"
              >
                <el-icon><AlarmClock /></el-icon>
                <span>预约</span>
              </el-button>
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
import { AlarmClock, Document } from '@element-plus/icons-vue'
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

const apiBase = ((import.meta.env.VITE_APP_BASE_API as string) || '/api').replace(/\/$/, '')
const defaultCover = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  `<svg xmlns="http://www.w3.org/2000/svg" width="300" height="420" viewBox="0 0 300 420">
    <rect width="300" height="420" fill="#f2f3f5"/>
    <rect x="32" y="44" width="236" height="332" rx="16" fill="#ffffff" stroke="#dcdfe6"/>
    <text x="150" y="215" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="34" fill="#909399">图书</text>
  </svg>`
)}`

const resolveCoverUrl = (raw?: string) => {
  if (!raw) return defaultCover
  if (raw.startsWith('data:') || raw.startsWith('blob:')) return raw

  if (/^https?:\/\//i.test(raw)) {
    try {
      const u = new URL(raw)
      if (u.hostname.endsWith('doubanio.com')) {
        return `${apiBase}/public/image-proxy?url=${encodeURIComponent(raw)}`
      }
    } catch {
      return defaultCover
    }
    return raw
  }

  const path = raw.startsWith('/') ? raw : `/${raw}`
  return `${apiBase}${path}`
}

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
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-page :deep(.el-card) {
  background: var(--glass-bg);
  border: var(--glow-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  backdrop-filter: blur(var(--glass-blur));
  transition: all var(--transition-normal);
}

.detail-page :deep(.el-card__header) {
  border-bottom: 1px solid var(--color-border);
  padding: 16px 24px;
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
  display: block;
  padding: 6px 12px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: var(--radius-full);
  width: fit-content;
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
  justify-content: flex-end;
  align-items: center;
}

.card-header :deep(.el-button) {
  border: var(--glow-border);
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
}

.card-header :deep(.el-button:hover) {
  border: var(--glow-border-hover);
  box-shadow: var(--glow-shadow);
}

.detail-loading {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-body {
  padding: 12px;
}

.basic {
  display: flex;
  gap: 32px;
}

.cover-large {
  width: 180px;
  height: 250px;
  border-radius: 16px;
  background-size: cover;
  background-position: center;
  background-color: var(--color-background-secondary);
  border: var(--glow-border);
  box-shadow: var(--shadow-lg), var(--glow-shadow);
  transition: all var(--transition-normal);
}

.cover-large:hover {
  box-shadow: var(--shadow-xl), var(--shadow-glow);
  border-color: rgba(59, 130, 246, 0.4);
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.title {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: var(--color-text);
  line-height: 1.3;
  font-family: 'Space Grotesk', sans-serif;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.meta span {
  background: rgba(59, 130, 246, 0.08);
  padding: 6px 12px;
  border-radius: var(--radius-md);
  font-weight: 500;
  border: 1px solid rgba(59, 130, 246, 0.15);
}

.status {
  margin-top: 4px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.can-borrow {
  color: var(--color-neon);
  font-weight: 700;
  background: rgba(34, 197, 94, 0.15);
  padding: 6px 12px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(34, 197, 94, 0.2);
  box-shadow: 0 0 15px rgba(34, 197, 94, 0.15);
}

.cannot-borrow {
  color: #EF4444;
  font-weight: 700;
  background: rgba(239, 68, 68, 0.15);
  padding: 6px 12px;
  border-radius: var(--radius-md);
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.actions .el-button {
  height: 44px;
  border-radius: var(--radius-md);
  padding: 0 24px;
  font-weight: 600;
  font-size: 15px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
  justify-content: center;
}

.btn-borrow .el-icon,
.btn-reserve .el-icon {
  font-size: 18px;
}

.btn-reserve {
  border: var(--glow-border) !important;
  background: var(--glass-bg) !important;
  backdrop-filter: blur(10px);
}

.btn-reserve:hover:not(:disabled) {
  border: var(--glow-border-hover) !important;
  box-shadow: var(--glow-shadow);
  color: var(--color-primary-light) !important;
}

.detail-page :deep(.el-divider) {
  border-color: var(--color-border);
  margin: 32px 0;
}

.section {
  margin-top: 24px;
  background: rgba(59, 130, 246, 0.05);
  padding: 24px;
  border-radius: var(--radius-lg);
  border: var(--glow-border);
}

.section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Space Grotesk', sans-serif;
}

.section h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--gradient-primary);
  border-radius: 2px;
  box-shadow: 0 0 10px var(--color-primary-glow);
}

.desc {
  margin: 0;
  font-size: 15px;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.rules {
  padding-left: 20px;
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 2;
}

.rules li {
  margin-bottom: 8px;
  position: relative;
  padding-left: 16px;
}

.rules li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10px;
  width: 6px;
  height: 6px;
  background: var(--gradient-primary);
  border-radius: 50%;
  box-shadow: 0 0 8px var(--color-primary-glow);
}

.rules li strong {
  color: var(--color-text);
  font-weight: 600;
}

@media (max-width: 768px) {
  .basic {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .info {
    align-items: center;
  }
  
  .actions {
    justify-content: center;
  }
}
</style>
