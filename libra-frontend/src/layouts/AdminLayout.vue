<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand" @click="goDashboard">
        <div class="brand-logo">L</div>
        <div class="brand-info">
          <div class="brand-title">Libra</div>
          <div class="brand-subtitle">管理后台</div>
        </div>
      </div>
      <el-menu class="menu" :default-active="activePath" router>
        <el-menu-item index="/admin/dashboard">
          <span class="menu-icon">📊</span>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <span class="menu-icon">📚</span>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/borrows">
          <span class="menu-icon">📝</span>
          <span>借阅管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="user-card" @click="goReader">
          <div class="avatar">{{ avatarText }}</div>
          <div class="user-info">
            <div class="user-name">{{ displayName }}</div>
            <div class="user-role">管理员</div>
          </div>
        </div>
      </div>
    </aside>

    <div class="main">
      <header class="topbar">
        <div class="topbar-left">
          <h1 class="topbar-title">{{ pageTitle }}</h1>
        </div>
        <div class="topbar-actions">
          <el-button size="small" @click="goReader">
            切换到读者端
          </el-button>
          <el-dropdown @command="handleCommand">
            <el-button size="small" circle>
              <span style="font-size: 16px;">⋯</span>
            </el-button>
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

const pageTitle = computed(() => {
  if (route.path.startsWith('/admin/borrows')) return '借阅管理'
  if (route.path.startsWith('/admin/books')) return '图书管理'
  return '仪表盘'
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
  grid-template-columns: 260px 1fr;
  background: var(--color-background-secondary);
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 24px 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid var(--color-border-light);
  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 12px;
  transition: background 200ms ease;
  margin-bottom: 32px;
}

.brand:hover {
  background: var(--color-background-secondary);
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary), #5AC8FA);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 20px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 11px;
  color: var(--color-text-secondary);
  margin-top: 2px;
}

.menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu) {
  background: transparent;
  border-right: none;
}

.menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  border-radius: 10px;
  margin-bottom: 4px;
  color: var(--color-text-secondary);
  font-weight: 500;
  transition: all 200ms ease;
}

.menu :deep(.el-menu-item:hover) {
  background: var(--color-background-secondary);
  color: var(--color-text);
}

.menu :deep(.el-menu-item.is-active) {
  background: var(--color-text);
  color: #FFFFFF;
}

.menu-icon {
  margin-right: 10px;
  font-size: 16px;
}

.sidebar-footer {
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 200ms ease;
}

.user-card:hover {
  background: var(--color-background-secondary);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #5AC8FA);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.user-role {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.main {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  background: rgba(245, 245, 247, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--color-border-light);
}

.topbar-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
  letter-spacing: -0.02em;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.content {
  flex: 1;
  padding: 32px;
}

@media (max-width: 992px) {
  .admin-layout {
    grid-template-columns: 220px 1fr;
  }
}

@media (max-width: 768px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }

  .topbar {
    padding: 16px;
  }

  .content {
    padding: 16px;
  }
}
</style>
