<template>
  <div class="app-container page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Catalog</span>
        <h1 class="page-title">图书管理</h1>
        <p class="page-subtitle">维护馆藏信息、库存与上架状态。</p>
      </div>
      <div class="header-actions">
        <el-button>导出清单</el-button>
      </div>
    </header>

    <el-card>
      <div class="filter-container">
        <el-input
          v-model="queryParams.title"
          placeholder="图书名称"
          class="filter-input"
          @keyup.enter="handleQuery"
        />
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="success" icon="Plus" @click="handleAdd">新增图书</el-button>
      </div>

      <el-table v-loading="loading" :data="bookList" border stripe class="book-table">
        <el-table-column label="ISBN" prop="isbn" width="150" />
        <el-table-column label="书名" prop="title" min-width="200" show-overflow-tooltip />
        <el-table-column label="作者" prop="author" width="150" />
        <el-table-column label="出版社" prop="publisher" width="150" />
        <el-table-column label="总量" prop="totalCount" width="80" align="center" />
        <el-table-column label="在馆" prop="stockCount" width="80" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="scope">
            <el-button type="warning" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
        class="pager"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const bookList = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: ''
})

const getList = async () => {
  loading.value = true
  try {
    const data: any = await request({
      url: '/book/list',
      method: 'get',
      params: queryParams
    })
    bookList.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  handleQuery()
}

const handleAdd = () => { /* TODO: 实现新增逻辑 */ }
const handleEdit = (_row: any) => { /* TODO: 实现编辑逻辑 */ }
const handleDelete = (_row: any) => { /* TODO: 实现删除逻辑 */ }

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
}

.page-title {
  margin: 8px 0 6px;
  font-size: clamp(24px, 2.6vw, 32px);
}

.page-subtitle {
  margin: 0;
  color: var(--color-muted);
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.filter-input {
  width: min(260px, 100%);
}

.book-table {
  margin-top: 8px;
}

.pager {
  margin-top: 20px;
  text-align: right;
}

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
