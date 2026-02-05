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

const goDetail = (id: number) => {
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
  max-width: 1100px;
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

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.filter-item {
  min-width: 140px;
}

.keyword {
  flex: 1;
  min-width: 220px;
}

.helper-bar {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.helper-title {
  font-size: 12px;
  color: var(--color-muted);
}

.chips {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.table-loading,
.table-empty {
  min-height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-table {
  margin-top: 12px;
}

.can-borrow {
  color: var(--el-color-success);
  font-weight: 600;
}

.cannot-borrow {
  color: var(--el-color-danger);
  font-weight: 600;
}

.pager {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
}
</style>
