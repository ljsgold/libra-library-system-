<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand" @click="goDashboard">
        <div class="brand-title">Libra 管理后台</div>
        <div class="brand-subtitle">图书管理系统</div>
      </div>
      <el-menu class="menu" :default-active="activePath" router>
        <el-menu-item index="/admin/dashboard">
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/borrows">
          <span>借阅管理</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="main">
      <header class="topbar surface">
        <div class="topbar-title">管理后台</div>
        <div class="topbar-actions">
          <el-button size="small" plain @click="goReader">读者端</el-button>
          <el-dropdown @command="handleCommand">
            <div class="user-trigger">
              <el-avatar size="small">{{ avatarText }}</el-avatar>
              <span class="user-name">{{ displayName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
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
  if (route.path.startsWith('/admin/borrows')) return '/admin/borrows'
  if (route.path.startsWith('/admin/books')) return '/admin/books'
  return '/admin/dashboard'
})

const displayName = computed(() => userStore.userInfo?.name || '管理员')
const avatarText = computed(() => displayName.value.slice(0, 1))

const goDashboard = () => {
  router.push('/admin/dashboard')
}

const goReader = () => {
  router.push('/u/home')
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
  }
}

onMounted(() => {
  userStore.fetchUserInfo()
})
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px 1fr;
  background: var(--color-background);
}

.sidebar {
  padding: 32px 20px;
  background: linear-gradient(180deg, #2b1a12 0%, #3a251b 55%, #5a3a2b 100%);
  color: #ffffff;
}

.brand {
  cursor: pointer;
  margin-bottom: 28px;
}

.brand-title {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.brand-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 6px;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu) {
  background: transparent;
  border-right: none;
}

.menu :deep(.el-menu-item) {
  border-radius: 12px;
  margin-bottom: 6px;
  color: rgba(255, 255, 255, 0.8);
}

.menu :deep(.el-menu-item.is-active) {
  background: rgba(138, 90, 62, 0.26);
  color: #ffffff;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.main {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 24px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 20px;
  border-radius: 16px;
}

.topbar-title {
  font-size: 16px;
  font-weight: 600;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 13px;
  color: var(--color-text);
}

.content {
  flex: 1;
}

@media (max-width: 992px) {
  .admin-layout {
    grid-template-columns: 200px 1fr;
  }
}

@media (max-width: 768px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    border-radius: 0 0 20px 20px;
  }
}
</style>


