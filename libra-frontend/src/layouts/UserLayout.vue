<template>
  <div class="user-layout">
    <header class="user-header glass">
      <div class="brand" @click="goHome">
        <div class="brand-logo">L</div>
        <div class="brand-text">
          <span class="brand-title">Libra</span>
          <span class="brand-subtitle">Library</span>
        </div>
      </div>
      <nav class="nav">
        <el-menu
          mode="horizontal"
          :default-active="activePath"
          @select="handleSelect"
          router
          :ellipsis="false"
        >
          <el-menu-item index="/u/home">
            <el-icon class="nav-icon"><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/u/books">
            <el-icon class="nav-icon"><Collection /></el-icon>
            <span>图书</span>
          </el-menu-item>
          <el-menu-item index="/u/my-borrow">
            <el-icon class="nav-icon"><Calendar /></el-icon>
            <span>借阅</span>
          </el-menu-item>
          <el-menu-item index="/u/profile">
            <el-icon class="nav-icon"><User /></el-icon>
            <span>我的</span>
          </el-menu-item>
        </el-menu>
      </nav>
      <div class="user-actions">
        <el-button v-if="isAdmin" size="small" type="primary" plain @click="goAdmin" class="admin-btn">
          管理后台
        </el-button>
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-trigger">
            <div class="avatar">{{ avatarText }}</div>
            <div class="user-meta">
              <div class="user-name">{{ displayName }}</div>
              <div v-if="identityText" class="user-id">{{ identityText }}</div>
            </div>
            <el-icon class="dropdown-icon"><CaretBottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="custom-dropdown">
              <el-dropdown-item command="profile">
                <span class="dropdown-item-content">
                  <el-icon class="dropdown-item-icon"><Setting /></el-icon>
                  <span>个人中心</span>
                </span>
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <span class="dropdown-item-content danger">
                  <el-icon class="dropdown-item-icon"><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <main class="user-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Calendar, CaretBottom, Collection, House, Setting, SwitchButton, User } from '@element-plus/icons-vue'

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
}

.user-header {
  position: sticky;
  top: 16px;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 18px;
  margin: 16px auto 0;
  width: calc(100% - 48px);
  max-width: var(--container-max);
  gap: 24px;
  transition: all 0.3s ease;
}

.user-header:hover {
  transform: translateY(-2px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding-right: 24px;
}

.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: var(--color-text);
  color: white;
  font-weight: 800;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 18px;
  font-weight: 800;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav :deep(.el-menu--horizontal) {
  background: transparent;
  border-bottom: none;
  height: 42px;
}

.nav :deep(.el-menu-item) {
  border-radius: 12px;
  margin: 0 4px;
  padding: 0 14px;
  height: 42px;
  line-height: 42px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-secondary);
  border-bottom: none !important;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-icon {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.nav :deep(.el-menu-item:hover) {
  background: rgba(15, 23, 42, 0.04);
  color: var(--color-text);
}

.nav :deep(.el-menu-item.is-active) {
  background: rgba(15, 23, 42, 0.06) !important;
  color: var(--color-text) !important;
}

.nav :deep(.el-menu-item.is-active .nav-icon) {
  color: var(--color-text);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-btn {
  border-radius: 12px;
  font-weight: 600;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 8px 6px 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid var(--color-border-light);
  transition: all 0.3s ease;
}

.user-trigger:hover {
  background: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text);
  font-weight: 700;
  font-size: 15px;
  box-shadow: none;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-right: 4px;
}

.user-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text);
}

.user-id {
  font-size: 10px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-right: 4px;
}

.user-main {
  flex: 1;
  width: 100%;
  max-width: var(--container-max);
  margin: 0 auto;
  padding: 28px var(--page-padding) 64px;
  box-sizing: border-box;
}
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@media (max-width: 900px) {
  .user-header {
    flex-direction: column;
    align-items: stretch;
    margin: 12px 12px 0;
    padding: 14px;
    gap: 16px;
    top: 12px;
    width: calc(100% - 24px);
  }

  .nav {
    overflow-x: auto;
    justify-content: flex-start;
    padding-bottom: 4px;
  }
  
  .user-main {
    padding: 24px 16px;
  }
}

.dropdown-item-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-item-icon {
  font-size: 16px;
}
</style>
