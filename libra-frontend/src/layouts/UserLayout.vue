<template>
  <div class="user-layout">
    <header class="user-header surface">
      <div class="brand" @click="goHome">
        <span class="brand-title">Libra</span>
        <span class="brand-subtitle">图书借阅平台</span>
      </div>
      <nav class="nav">
        <el-menu
          mode="horizontal"
          :default-active="activePath"
          @select="handleSelect"
          router
          :ellipsis="false"
        >
          <el-menu-item index="/u/home">首页</el-menu-item>
          <el-menu-item index="/u/books">图书</el-menu-item>
          <el-menu-item index="/u/my-borrow">我的借阅</el-menu-item>
          <el-menu-item index="/u/profile">个人中心</el-menu-item>
        </el-menu>
      </nav>
      <div class="user-actions">
        <el-button v-if="isAdmin" size="small" type="primary" plain @click="goAdmin">管理后台</el-button>
        <el-dropdown @command="handleCommand">
          <div class="user-trigger">
            <div class="avatar">{{ avatarText }}</div>
            <div class="user-meta">
              <div class="user-name">{{ displayName }}</div>
              <div v-if="identityText" class="user-id">{{ identityText }}</div>
            </div>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <main class="user-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activePath = computed(() => {
  if (route.path.startsWith('/u/books')) return '/u/books'
  if (route.path.startsWith('/u/my-borrow')) return '/u/my-borrow'
  if (route.path.startsWith('/u/profile')) return '/u/profile'
  return '/u/home'
})

const displayName = computed(() => userStore.userInfo?.name || '读者')
const identityText = computed(() => userStore.userInfo?.studentNo || userStore.userInfo?.employeeNo || '')
const avatarText = computed(() => displayName.value.slice(0, 1))
const isAdmin = computed(() => !!userStore.userInfo?.isAdmin)

const handleSelect = (index: string) => {
  router.push(index)
}

const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/u/profile')
    return
  }
  if (command === 'logout') {
    userStore.logout()
  }
}

const goHome = () => {
  router.push('/u/home')
}

const goAdmin = () => {
  router.push('/admin/dashboard')
}

onMounted(() => {
  userStore.fetchUserInfo()
})
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-background-secondary);
}

.user-header {
  position: sticky;
  top: 16px;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  margin: 16px 24px 0;
  gap: 24px;
}

.brand {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: opacity 200ms ease;
}

.brand:hover {
  opacity: 0.7;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 11px;
  font-weight: 500;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav :deep(.el-menu--horizontal) {
  background: transparent;
  border-bottom: none;
}

.nav :deep(.el-menu-item) {
  border-radius: 8px;
  margin: 0 4px;
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-secondary);
  border-bottom: none !important;
  transition: all 200ms ease;
}

.nav :deep(.el-menu-item:hover) {
  background: var(--color-background-secondary);
  color: var(--color-text);
}

.nav :deep(.el-menu-item.is-active) {
  background: var(--color-text);
  color: var(--color-surface) !important;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 12px;
  transition: background 200ms ease;
}

.user-trigger:hover {
  background: var(--color-background-secondary);
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #5AC8FA);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.user-id {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.user-main {
  flex: 1;
  padding: 24px;
}

@media (max-width: 900px) {
  .user-header {
    flex-direction: column;
    align-items: stretch;
    margin: 12px 12px 0;
    padding: 16px;
    gap: 16px;
  }

  .brand {
    align-items: center;
  }

  .nav {
    width: 100%;
    justify-content: center;
  }

  .nav :deep(.el-menu-item) {
    padding: 0 12px;
    font-size: 14px;
  }

  .user-actions {
    justify-content: center;
  }

  .user-main {
    padding: 16px 12px;
  }
}
</style>
