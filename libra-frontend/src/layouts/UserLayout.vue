<template>
  <div class="user-layout">
    <header class="user-header surface">
      <div class="brand" @click="goHome">
        <span class="brand-title">Libra 读者端</span>
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
            <el-avatar size="small">{{ avatarText }}</el-avatar>
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
  gap: 20px;
  padding: 24px;
}

.user-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-radius: 18px;
  gap: 16px;
}

.brand {
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.brand-title {
  font-size: 16px;
  font-weight: 600;
}

.brand-subtitle {
  font-size: 11px;
  color: var(--color-muted);
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
  border-radius: 999px;
  margin: 0 4px;
  color: var(--color-text);
}

.nav :deep(.el-menu-item.is-active) {
  background: rgba(138, 90, 62, 0.12);
  color: var(--color-cta);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
}

.user-id {
  font-size: 11px;
  color: var(--color-muted);
}

.user-main {
  flex: 1;
}

@media (max-width: 900px) {
  .user-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .nav {
    width: 100%;
    justify-content: flex-start;
  }

  .user-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

