<template>
  <div class="home-page">
    <section class="hero">
      <div class="page-shell hero-content">
        <span class="eyebrow">Libra Library</span>
        <h1 class="hero-title">发现你的下一本好书</h1>
        <p class="hero-subtitle">探索海量馆藏，轻松借阅，让阅读成为一种优雅的体验。</p>
        <div class="hero-search">
          <div class="search-wrapper">
            <el-input
              v-model="keyword"
              placeholder="搜索书名、作者或 ISBN..."
              size="large"
              clearable
              class="hero-input"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <span>搜索</span>
              <el-icon class="btn-icon"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-value">10,000+</span>
            <span class="stat-label">馆藏图书</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">5,000+</span>
            <span class="stat-label">活跃读者</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">98%</span>
            <span class="stat-label">满意度</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">新书上架</h2>
            <p class="section-subtitle">最新到馆的精选图书</p>
          </div>
          <el-button type="primary" plain @click="goBooks('new')" class="section-cta">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>

        <div v-if="loadingNew" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-else-if="!newBooks.length" class="empty-state">
          <div class="empty-state__title">暂无新书</div>
          <div class="empty-state__description">敬请期待更多精彩图书上架</div>
        </div>
        <div v-else class="book-grid">
          <div
            v-for="book in newBooks"
            :key="book.id"
            class="book-card"
            @click="goBookDetail(book.id)"
          >
            <el-image class="book-cover" :src="resolveCoverUrl(book.coverUrl)" fit="cover" />
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <div class="book-status" :class="{ available: book.canBorrow }">
                {{ book.canBorrow ? '可借阅' : '已借出' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section section--muted">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">热门借阅</h2>
            <p class="section-subtitle">最受读者欢迎的图书</p>
          </div>
        </div>

        <div v-if="loadingPopular" class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>
        <div v-else-if="!popularBooks.length" class="empty-state">
          <div class="empty-state__title">暂无数据</div>
        </div>
        <div v-else class="popular-list">
          <div
            v-for="(book, index) in popularBooks"
            :key="book.id"
            class="popular-item"
            @click="goBookDetail(book.id)"
          >
            <div class="popular-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
            <div class="popular-info">
              <h4 class="popular-title">{{ book.title }}</h4>
              <p class="popular-author">{{ book.author }}</p>
            </div>
            <div class="popular-badge">{{ book.availableCount }} 本可借</div>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="page-shell">
        <div class="section-header">
          <div>
            <h2 class="section-title">分类浏览</h2>
            <p class="section-subtitle">按类别探索更多图书</p>
          </div>
        </div>

        <div v-if="loadingCategory" class="loading-state">
          <el-skeleton :rows="2" animated />
        </div>
        <div v-else-if="!categories.length" class="empty-state">
          <div class="empty-state__title">暂无分类</div>
        </div>
        <div v-else class="category-grid">
          <div
            v-for="c in categories"
            :key="c.id"
            class="category-card"
            @click="goBooksByCategory(c.id)"
          >
            <span class="category-name">{{ c.name }}</span>
            <el-icon class="category-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Search, ArrowRight } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { BookItem, CategoryItem } from '@/api/user'
import { getCategories, getNewBooks, getPopularBooks } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const keyword = ref('')
const newBooks = ref<BookItem[]>([])
const popularBooks = ref<BookItem[]>([])
const categories = ref<CategoryItem[]>([])

const loadingNew = ref(false)
const loadingPopular = ref(false)
const loadingCategory = ref(false)

const apiBase = ((import.meta.env.VITE_APP_BASE_API as string) || '/api').replace(/\/$/, '')
const defaultCover = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  `<svg xmlns="http://www.w3.org/2000/svg" width="120" height="160" viewBox="0 0 120 160">
    <rect width="120" height="160" fill="#f2f3f5"/>
    <rect x="14" y="18" width="92" height="124" rx="8" fill="#ffffff" stroke="#dcdfe6"/>
    <text x="60" y="86" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="18" fill="#909399">BOOK</text>
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

const fetchNewBooks = async () => {
  loadingNew.value = true
  try {
    newBooks.value = await getNewBooks()
  } catch (e) {
    ElMessage.error('获取新书失败')
  } finally {
    loadingNew.value = false
  }
}

const fetchPopularBooks = async () => {
  loadingPopular.value = true
  try {
    popularBooks.value = await getPopularBooks()
  } catch (e) {
    ElMessage.error('获取热门图书失败')
  } finally {
    loadingPopular.value = false
  }
}

const fetchCategories = async () => {
  loadingCategory.value = true
  try {
    categories.value = await getCategories()
  } catch (e) {
    ElMessage.error('获取分类失败')
  } finally {
    loadingCategory.value = false
  }
}

const handleSearch = () => {
  router.push({
    path: '/u/books',
    query: { keyword: keyword.value || undefined }
  })
}

const goBooks = (tag?: string) => {
  router.push({
    path: '/u/books',
    query: { tag }
  })
}

const goBooksByCategory = (categoryId: number) => {
  router.push({
    path: '/u/books',
    query: { categoryId }
  })
}

const goBookDetail = (id: string | number) => {
  router.push(`/u/books/${id}`)
}

onMounted(() => {
  fetchNewBooks()
  fetchPopularBooks()
  fetchCategories()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
}
.hero {
  padding: 88px 0 64px;
  text-align: left;
}

.hero-content {
  display: grid;
  gap: 20px;
}

.eyebrow {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--color-primary);
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.hero-title {
  font-size: clamp(40px, 5vw, 56px);
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.05;
  margin: 0;
  color: var(--color-text);
}

.hero-subtitle {
  font-size: 18px;
  line-height: 1.6;
  color: var(--color-text-secondary);
  margin: 0;
  max-width: 640px;
}

.hero-search {
  max-width: 640px;
}

.hero-search :deep(.el-input__wrapper) {
  padding: 10px 128px 10px 46px;
  border-radius: 14px;
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  height: 54px;
}

.hero-search :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(var(--color-primary-rgb), 0.35);
  box-shadow: 0 0 0 4px rgba(var(--color-primary-rgb), 0.12);
}

.search-icon {
  font-size: 20px;
  color: var(--color-text-secondary);
  margin-left: 4px;
}

.hero-search :deep(.el-input__inner) {
  font-size: 16px;
  height: 100%;
  color: var(--color-text);
  font-weight: 500;
}

.hero-search :deep(.el-input__inner::placeholder) {
  color: var(--color-muted);
}

.search-wrapper {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.search-btn {
  position: absolute;
  right: 8px;
  top: 7px;
  bottom: 7px;
  border-radius: 12px !important;
  padding: 0 18px !important;
  font-weight: 700;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 6px;
  height: auto !important;
}

.search-btn:hover {
  transform: none;
}

.btn-icon {
  font-size: 18px;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  max-width: 720px;
}

.stat-item {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  padding: 20px 18px;
  box-shadow: var(--shadow-sm);
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.03em;
  display: block;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-secondary);
  letter-spacing: 0.02em;
}
.section {
  padding: 56px 0;
}

.section--muted {
  background: rgba(255, 255, 255, 0.6);
  border-top: 1px solid var(--color-border-light);
  border-bottom: 1px solid var(--color-border-light);
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.section-title {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin: 0 0 8px;
  color: var(--color-text);
}

.section-subtitle {
  font-size: 16px;
  color: var(--color-text-secondary);
  margin: 0;
}

.section-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.book-card {
  background: var(--color-surface);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.book-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.book-cover {
  width: 100%;
  aspect-ratio: 3/4;
  background-color: var(--color-background-secondary);
}

.book-info {
  padding: 16px;
}

.book-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px;
  color: var(--color-text);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 12px;
}

.book-status {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 6px;
  background: var(--color-background-secondary);
  color: var(--color-text-secondary);
}

.book-status.available {
  background: rgba(52, 199, 89, 0.12);
  color: #248A3D;
}

.popular-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.popular-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: var(--color-surface);
  border-radius: 14px;
  cursor: pointer;
  transition: all 200ms ease;
  border: 1px solid var(--color-border-light);
}

.popular-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.popular-rank {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: var(--color-background-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.popular-rank.rank-1 {
  background: rgba(var(--color-primary-rgb), 0.12);
  color: var(--color-primary);
}

.popular-rank.rank-2 {
  background: rgba(15, 23, 42, 0.06);
}

.popular-rank.rank-3 {
  background: rgba(15, 23, 42, 0.06);
}

.popular-info {
  flex: 1;
  min-width: 0;
}

.popular-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 2px;
  color: var(--color-text);
}

.popular-author {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.popular-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(var(--color-primary-rgb), 0.1);
  color: var(--color-primary);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.category-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: var(--color-surface);
  border-radius: 12px;
  cursor: pointer;
  transition: all 200ms ease;
  border: 1px solid var(--color-border-light);
}

.category-card:hover {
  box-shadow: var(--shadow-md);
}

.category-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text);
}

.category-arrow {
  color: var(--color-text-secondary);
  transition: transform 200ms ease;
}

.category-card:hover .category-arrow {
  transform: translateX(4px);
  color: var(--color-primary);
}
.loading-state {
  padding: 40px 0;
}

.empty-state {
  text-align: center;
  padding: 64px 24px;
}

.empty-state__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 8px;
}

.empty-state__description {
  font-size: 15px;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .hero {
    padding: 56px 0 40px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-stats {
    gap: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .section {
    padding: 40px 0;
  }

  .section-title {
    font-size: 24px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .book-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}
</style>
