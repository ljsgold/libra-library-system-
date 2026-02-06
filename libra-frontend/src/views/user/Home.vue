<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <span class="eyebrow">Libra Library</span>
        <h1 class="hero-title">发现你的下一本好书</h1>
        <p class="hero-subtitle">探索海量馆藏，轻松借阅，让阅读成为一种优雅的体验。</p>
        <div class="hero-search">
          <el-input
            v-model="keyword"
            placeholder="搜索书名、作者或 ISBN..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-value">10,000+</span>
            <span class="stat-label">馆藏图书</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">5,000+</span>
            <span class="stat-label">活跃读者</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">98%</span>
            <span class="stat-label">满意度</span>
          </div>
        </div>
      </div>
    </section>

    <!-- New Books Section -->
    <section class="section">
      <div class="section-header">
        <div>
          <h2 class="section-title">新书上架</h2>
          <p class="section-subtitle">最新到馆的精选图书</p>
        </div>
        <el-button text type="primary" @click="goBooks('new')" class="link-arrow">
          查看全部
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
          <div class="book-cover" :style="{ backgroundImage: 'url(' + (book.coverUrl || defaultCover) + ')' }"></div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-status" :class="{ available: book.canBorrow }">
              {{ book.canBorrow ? '可借阅' : '已借出' }}
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Popular Books Section -->
    <section class="section section--gray">
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
          <div class="popular-badge">
            {{ book.availableCount }} 本可借
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="section">
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
          <span class="category-arrow">→</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
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

const defaultCover = 'https://via.placeholder.com/120x160?text=Book'

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

const goBookDetail = (id: number) => {
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

/* Hero Section */
.hero {
  padding: 80px 24px;
  text-align: center;
  background: linear-gradient(180deg, var(--color-background) 0%, var(--color-background-secondary) 100%);
}

.hero-content {
  max-width: 680px;
  margin: 0 auto;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  letter-spacing: -0.025em;
  line-height: 1.07;
  margin: 16px 0 20px;
  color: var(--color-text);
}

.hero-subtitle {
  font-size: 21px;
  line-height: 1.381;
  color: var(--color-text-secondary);
  margin: 0 0 40px;
}

.hero-search {
  max-width: 560px;
  margin: 0 auto 48px;
}

.hero-search :deep(.el-input__wrapper) {
  padding: 12px 8px 12px 20px;
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
}

.hero-search :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary);
}

.hero-search :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.1), var(--shadow-lg);
}

.hero-search :deep(.el-input__inner) {
  font-size: 17px;
}

.hero-search :deep(.el-input-group__append) {
  background: transparent;
  border: none;
  padding: 0;
  margin-left: 8px;
}

.hero-search :deep(.el-input-group__append .el-button) {
  border-radius: 12px;
  padding: 12px 24px;
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: var(--color-border);
}

/* Sections */
.section {
  padding: 64px 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.section--gray {
  background: var(--color-background-secondary);
  max-width: none;
  padding-left: 24px;
  padding-right: 24px;
}

.section--gray > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.section :deep(.el-card) {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border-light);
  border-radius: 20px;
  transition: all 300ms ease;
}

.section :deep(.el-card:hover) {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-lg);
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 32px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0 0 8px;
  color: var(--color-text);
}

.section-subtitle {
  font-size: 17px;
  color: var(--color-text-secondary);
  margin: 0;
}

.link-arrow::after {
  content: ' →';
  transition: transform 200ms ease;
}

.link-arrow:hover::after {
  transform: translateX(4px);
}

/* Book Grid */
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
}

.book-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl);
}

.book-cover {
  width: 100%;
  aspect-ratio: 3/4;
  background-size: cover;
  background-position: center;
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

/* Popular List */
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
}

.popular-item:hover {
  background: var(--color-surface);
  box-shadow: var(--shadow-md);
  transform: translateX(4px);
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
  background: linear-gradient(135deg, #FFD700, #FFA500);
  color: white;
}

.popular-rank.rank-2 {
  background: linear-gradient(135deg, #C0C0C0, #A0A0A0);
  color: white;
}

.popular-rank.rank-3 {
  background: linear-gradient(135deg, #CD7F32, #B87333);
  color: white;
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
  background: rgba(0, 122, 255, 0.1);
  color: var(--color-primary);
}

/* Category Grid */
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
  background: var(--color-background-secondary);
  border-radius: 12px;
  cursor: pointer;
  transition: all 200ms ease;
}

.category-card:hover {
  background: var(--color-surface);
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

/* Loading & Empty States */
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

/* Responsive */
@media (max-width: 768px) {
  .hero {
    padding: 48px 16px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 17px;
  }

  .hero-stats {
    gap: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .section {
    padding: 40px 16px;
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
