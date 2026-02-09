<template>
  <div class="book-list-page page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Browse</span>
        <h1 class="page-title">图书检索</h1>
        <p class="page-subtitle">按关键词、分类、可借状态快速定位。</p>
      </div>
    </header>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">筛选条件</span>
            <span class="card-subtitle">支持分类、排序与可借筛选</span>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="query.keyword"
          placeholder="书名 / 作者 / ISBN"
          clearable
          class="filter-item keyword"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="query.categoryId"
          placeholder="分类"
          clearable
          class="filter-item"
        >
          <el-option
            v-for="c in categories"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
        <el-select
          v-model="query.sort"
          placeholder="排序"
          class="filter-item"
        >
          <el-option label="最新上架" value="latest" />
          <el-option label="借阅最多" value="popular" />
          <el-option label="可借优先" value="available" />
        </el-select>
        <el-checkbox v-model="query.onlyAvailable" class="filter-item">
          仅显示可借
        </el-checkbox>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <div class="helper-bar" v-if="recentKeywords.length">
        <span class="helper-title">最近搜索：</span>
        <div class="chips">
          <el-tag
            v-for="k in recentKeywords"
            :key="k"
            size="small"
            @click="applyKeyword(k)">
            {{ k }}
          </el-tag>
          <el-link type="info" :underline="false" @click="clearHistory">清空</el-link>
        </div>
      </div>

      <div v-if="loading" class="table-loading">
        <el-skeleton :rows="6" animated />
      </div>
      <div v-else-if="!books.length" class="table-empty">
        <el-empty description="暂无结果" />
      </div>
      <div v-else>
        <el-table
          :data="books"
          border
          size="small"
          class="book-table"
        >
          <el-table-column label="封面" width="84" align="center">
            <template #default="scope">
              <el-image
                :src="resolveCoverUrl(scope.row.coverUrl)"
                fit="cover"
                style="width: 48px; height: 64px; border-radius: 6px"
                :preview-src-list="[resolveCoverUrl(scope.row.coverUrl)]"
                preview-teleported
              />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
          <el-table-column prop="author" label="作者" min-width="120" show-overflow-tooltip />
          <el-table-column prop="categoryName" label="分类" min-width="100" />
          <el-table-column label="可借/总量" width="120" align="center">
            <template #default="scope">
              <span :class="scope.row.canBorrow ? 'can-borrow' : 'cannot-borrow'">
                {{ scope.row.availableCount }} / {{ scope.row.totalCount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="center">
            <template #default="scope">
              <el-button type="primary" link size="small" @click="goDetail(scope.row.id)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pager">
          <el-pagination
            background
            layout="prev, pager, next, jumper"
            :page-size="query.size"
            :current-page="query.page"
            :total="total"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { BookItem, CategoryItem } from '@/api/user'
import { getCategories, searchBooks } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const categories = ref<CategoryItem[]>([])
const books = ref<BookItem[]>([])
const total = ref(0)
const loading = ref(false)
const recentKeywords = ref<string[]>(JSON.parse(localStorage.getItem('recentKeywords') || '[]'))

const apiBase = ((import.meta.env.VITE_APP_BASE_API as string) || '/api').replace(/\/$/, '')
const defaultCover = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  `<svg xmlns="http://www.w3.org/2000/svg" width="48" height="64" viewBox="0 0 48 64">
    <rect width="48" height="64" fill="#f2f3f5"/>
    <rect x="6" y="8" width="36" height="48" rx="6" fill="#ffffff" stroke="#dcdfe6"/>
    <text x="24" y="34" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="10" fill="#909399">BOOK</text>
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

const query = reactive({
  keyword: (route.query.keyword as string) || '',
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : undefined,
  sort: (route.query.sort as string) || 'latest',
  onlyAvailable: route.query.onlyAvailable === 'true' || route.query.onlyAvailable === true,
  page: route.query.page ? Number(route.query.page) : 1,
  size: 10
})

const fetchCategories = async () => {
  try {
    categories.value = await getCategories()
  } catch (e) {
    ElMessage.error('获取分类失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await searchBooks({
      keyword: query.keyword || undefined,
      categoryId: query.categoryId,
      onlyAvailable: query.onlyAvailable,
      sort: query.sort as any,
      page: query.page,
      size: query.size
    })
    books.value = res.records
    total.value = res.total
  } catch (e) {
    ElMessage.error('获取图书失败')
  } finally {
    loading.value = false
  }
}

const buildRouteQuery = () => ({
  keyword: query.keyword || undefined,
  categoryId: query.categoryId,
  onlyAvailable: query.onlyAvailable ? 'true' : undefined,
  sort: query.sort,
  page: query.page,
  size: query.size
})

const pushKeywordHistory = () => {
  if (!query.keyword) return
  const arr = recentKeywords.value.filter((k) => k !== query.keyword)
  arr.unshift(query.keyword)
  recentKeywords.value = arr.slice(0, 6)
  localStorage.setItem('recentKeywords', JSON.stringify(recentKeywords.value))
}

const handleSearch = () => {
  query.page = 1
  pushKeywordHistory()
  router.replace({
    path: '/u/books',
    query: buildRouteQuery()
  })
}

const handlePageChange = (page: number) => {
  query.page = page
  router.replace({
    path: '/u/books',
    query: buildRouteQuery()
  })
}

const applyKeyword = (k: string) => {
  query.keyword = k
  handleSearch()
}

const clearHistory = () => {
  recentKeywords.value = []
  localStorage.removeItem('recentKeywords')
}

const goDetail = (id: string | number) => {
  router.push(`/u/books/${id}`)
}

watch(
  () => route.query,
  () => {
    query.keyword = (route.query.keyword as string) || ''
    query.categoryId = route.query.categoryId ? Number(route.query.categoryId) : undefined
    query.sort = (route.query.sort as string) || 'latest'
    query.onlyAvailable = route.query.onlyAvailable === 'true' || route.query.onlyAvailable === true
    query.page = route.query.page ? Number(route.query.page) : 1
    fetchData()
  },
  { immediate: true }
)

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.book-list-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.book-list-page :deep(.el-card) {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 200ms ease, transform 200ms ease;
}

.book-list-page :deep(.el-card:hover) {
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
  letter-spacing: 0.08em;
  color: var(--color-primary);
  margin-bottom: 8px;
  display: block;
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

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding: 4px 0 20px;
}

.filter-item {
  min-width: 160px;
}

.keyword {
  flex: 1;
  min-width: 260px;
}

.helper-bar {
  margin-top: 0;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 16px;
  background: rgba(15, 23, 42, 0.03);
  border-radius: 12px;
}

.helper-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
}

.chips {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.chips .el-tag {
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.chips .el-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  color: var(--color-primary);
}

.table-loading,
.table-empty {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.65);
  border-radius: 16px;
}

.book-table {
  margin-top: 8px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  --el-table-border-color: rgba(0, 0, 0, 0.05);
  --el-table-header-bg-color: rgba(249, 250, 251, 0.8);
}

.book-table :deep(th.el-table__cell) {
  background-color: rgba(249, 250, 251, 0.8);
  font-weight: 600;
  color: var(--color-text-secondary);
  height: 48px;
}

.can-borrow {
  color: #059669;
  font-weight: 600;
  background: rgba(5, 150, 105, 0.1);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.cannot-borrow {
  color: #dc2626;
  font-weight: 600;
  background: rgba(220, 38, 38, 0.1);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.pager {
  display: flex;
  justify-content: center;
  padding-top: 32px;
  padding-bottom: 16px;
}

.filter-bar .el-button {
  height: 40px;
}
</style>
