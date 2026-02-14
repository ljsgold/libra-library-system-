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
          <el-icon class="menu-icon"><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <el-icon class="menu-icon"><Collection /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/borrows">
          <el-icon class="menu-icon"><Tickets /></el-icon>
          <span>借阅管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <div class="user-card">
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
          <el-dropdown @command="handleCommand" placement="bottom-end">
            <el-button size="small" class="topbar-user-trigger">
              <span class="topbar-user-name">{{ displayName }}</span>
              <el-icon class="topbar-dropdown-icon"><CaretBottom /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="switch-reader">
                  <el-icon><User /></el-icon>
                  <span>切换到读者端</span>
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
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
import { CaretBottom, Collection, DataAnalysis, SwitchButton, Tickets, User } from '@element-plus/icons-vue'

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
  if (command === 'switch-reader') {
    goReader()
    return
  }
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
  grid-template-columns: 280px 1fr;
  background: var(--color-background);
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 32px 20px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-right: 1px solid rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  margin-bottom: 40px;
}

.brand:hover {
  background: rgba(0,0,0,0.03);
  transform: scale(1.02);
}

.brand-logo {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #007AFF, #5856D6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 800;
  font-size: 24px;
  box-shadow: 0 8px 16px rgba(0, 122, 255, 0.3);
}

.brand-info {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 20px;
  font-weight: 800;
  color: #1d1d1f;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 12px;
  font-weight: 600;
  color: #86868b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-top: 2px;
}

.menu {
  flex: 1;
  border-right: none;
  background: transparent;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu :deep(.el-menu) {
  background: transparent;
  border-right: none;
}

.menu :deep(.el-menu-item) {
  height: 52px;
  line-height: 52px;
  border-radius: 16px;
  margin-bottom: 4px;
  color: #86868b;
  font-weight: 600;
  font-size: 15px;
  padding-left: 16px !important;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(0,0,0,0.03);
  color: #1d1d1f;
  transform: translateX(4px);
}

.menu :deep(.el-menu-item.is-active) {
  background: #1d1d1f;
  color: #FFFFFF;
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
}

.sidebar-footer {
  padding-top: 24px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  cursor: default;
  transition: all 0.3s ease;
  background: rgba(255,255,255,0.5);
  border: 1px solid rgba(0,0,0,0.05);
}

.user-card:hover {
  background: rgba(255,255,255,0.5);
  box-shadow: none;
  transform: none;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF9500, #FF3B30);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(255, 149, 0, 0.3);
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 15px;
  font-weight: 700;
  color: #1d1d1f;
}

.user-role {
  font-size: 12px;
  color: #86868b;
  font-weight: 500;
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
  padding: 24px 40px;
  background: rgba(245, 245, 247, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.topbar-title {
  font-size: 28px;
  font-weight: 800;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.02em;
}

.topbar-actions {
  display: flex;
  align-items: center;
}

.topbar-user-trigger {
  min-width: 116px;
  justify-content: space-between;
}

.topbar-user-name {
  max-width: 96px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topbar-dropdown-icon {
  margin-left: 8px;
}

.content {
  flex: 1;
  padding: 40px;
  max-width: 1600px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

@media (max-width: 992px) {
  .admin-layout {
    grid-template-columns: 240px 1fr;
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
    padding: 16px 24px;
  }

  .content {
    padding: 24px;
  }
}
</style>
