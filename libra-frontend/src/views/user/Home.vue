<template>
  <div class="home-page page-shell">
    <header class="home-hero">
      <div>
        <span class="eyebrow">Discover</span>
        <h1 class="hero-title">探索一本喜欢的书。</h1>
        <p class="hero-subtitle">浏览热门借阅、最新上架的书籍，探索符合自己的阅读内容。</p>
      </div>
    </header>

    <el-card class="search-card" shadow="never">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="书名 / 作者 / ISBN"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </el-card>

    <el-row :gutter="16" class="mt-16">
      <el-col :xs="24" :md="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <span class="card-title">最新上架</span>
                <span class="card-subtitle">最新到馆</span>
              </div>
              <el-button size="small" text type="primary" @click="goBooks('new')">
                更多
              </el-button>
            </div>
          </template>

          <div v-if="loadingNew" class="card-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!newBooks.length" class="card-empty">
            <el-empty description="暂无数据" />
          </div>
          <el-row v-else :gutter="12">
            <el-col v-for="book in newBooks" :key="book.id" :xs="12" :sm="8" :md="8">
              <div class="book-card" @click="goBookDetail(book.id)">
                <div class="cover" :style="{ backgroundImage: 'url(' + (book.coverUrl || defaultCover) + ')' }" />
                <div class="info">
                  <div class="title" :title="book.title">{{ book.title }}</div>
                  <div class="author">{{ book.author }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8" class="mt-md-0 mt-16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <span class="card-title">热门借阅</span>
                <span class="card-subtitle">近期借阅最多</span>
              </div>
            </div>
          </template>

          <div v-if="loadingPopular" class="card-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!popularBooks.length" class="card-empty">
            <el-empty description="暂无数据" />
          </div>
          <el-scrollbar v-else class="hot-list">
            <div
              v-for="(book, index) in popularBooks"
              :key="book.id"
              class="hot-item"
              @click="goBookDetail(book.id)"
            >
              <div class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
              <div class="hot-info">
                <div class="title" :title="book.title">{{ book.title }}</div>
                <div class="meta">{{ book.author }}</div>
              </div>
              <div class="count">{{ book.availableCount }} 可借</div>
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt-16" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">图书分类</span>
            <span class="card-subtitle">按类别浏览</span>
          </div>
        </div>
      </template>
      <div v-if="loadingCategory" class="card-loading">
        <el-skeleton :rows="2" animated />
      </div>
      <div v-else-if="!categories.length" class="card-empty">
        <el-empty description="暂无分类" />
      </div>
      <div v-else class="category-list">
        <el-tag
          v-for="c in categories"
          :key="c.id"
          size="large"
          @click="goBooksByCategory(c.id)"
        >
          {{ c.name }}
        </el-tag>
      </div>
    </el-card>
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

const defaultCover = 'https://via.placeholder.com/80x110?text=%E5%9B%BE%E4%B9%A6'

const fetchNewBooks = async () => {
  loadingNew.value = true
  try {
    newBooks.value = await getNewBooks()
  } catch (e) {
    ElMessage.error('获取数据失败')
  } finally {
    loadingNew.value = false
  }
}

const fetchPopularBooks = async () => {
  loadingPopular.value = true
  try {
    popularBooks.value = await getPopularBooks()
  } catch (e) {
    ElMessage.error('获取热门借阅失败')
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
  gap: 16px;
}

.home-hero {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hero-title {
  margin: 8px 0 4px;
  font-size: clamp(28px, 3.4vw, 44px);
}

.hero-subtitle {
  margin: 0;
  color: var(--color-muted);
  max-width: 520px;
}

.search-card {
  border-radius: 18px;
}

.search-bar :deep(.el-input__wrapper) {
  padding-right: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
}

.card-subtitle {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-muted);
}

.book-card {
  background: rgba(107, 63, 42, 0.04);
  border-radius: 14px;
  padding: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
  cursor: pointer;
  border: 1px solid rgba(107, 63, 42, 0.12);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.book-card:hover {
  border-color: rgba(138, 90, 62, 0.3);
  box-shadow: var(--shadow-md);
}

.cover {
  width: 64px;
  height: 88px;
  border-radius: 10px;
  background-size: cover;
  background-position: center;
  background-color: rgba(107, 63, 42, 0.08);
}

.info {
  min-width: 0;
}

.title {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author {
  font-size: 12px;
  color: var(--color-muted);
  margin-top: 4px;
}

.hot-list {
  max-height: 320px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  cursor: pointer;
  background: rgba(107, 63, 42, 0.04);
  border: 1px solid rgba(107, 63, 42, 0.12);
  margin-bottom: 8px;
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.hot-item:hover {
  border-color: rgba(138, 90, 62, 0.3);
  box-shadow: var(--shadow-md);
}

.rank {
  width: 28px;
  height: 28px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-weight: 600;
  background: rgba(107, 63, 42, 0.12);
  color: var(--color-text);
}

.rank-1 {
  background: rgba(138, 90, 62, 0.18);
  color: var(--color-cta);
}

.rank-2,
.rank-3 {
  background: rgba(107, 63, 42, 0.16);
}

.hot-info {
  flex: 1;
  min-width: 0;
}

.hot-info .meta {
  font-size: 12px;
  color: var(--color-muted);
}

.count {
  font-size: 12px;
  color: var(--color-cta);
  font-weight: 600;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-list :deep(.el-tag) {
  cursor: pointer;
  border-radius: 999px;
  border-color: rgba(107, 63, 42, 0.2);
  color: var(--color-text);
  background: rgba(107, 63, 42, 0.06);
}

.mt-16 {
  margin-top: 16px;
}

.mt-md-0 {
  margin-top: 0;
}

.card-loading,
.card-empty {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 991px) {
  .mt-md-0 {
    margin-top: 16px;
  }
}
</style>
