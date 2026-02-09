<template>
  <div class="not-found page-shell">
    <el-card shadow="never">
      <el-empty description="页面不存在或链接不正确" />
      <div class="actions">
        <el-button @click="goBack">返回上一页</el-button>
        <el-button type="primary" @click="goHome">回到首页</el-button>
      </div>
      <div class="hint">
        当前地址：{{ fullPath }}
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const fullPath = computed(() => route.fullPath)

const goBack = () => {
  router.back()
}

const goHome = () => {
  const token = localStorage.getItem('accessToken')
  router.push(token ? '/u/home' : '/login')
}
</script>

<style scoped>
.not-found {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.not-found :deep(.el-card) {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border-light);
  border-radius: 20px;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  padding: 0 0 12px;
}

.hint {
  text-align: center;
  color: var(--color-text-secondary);
  font-size: 13px;
  padding-bottom: 12px;
  word-break: break-all;
}
</style>

