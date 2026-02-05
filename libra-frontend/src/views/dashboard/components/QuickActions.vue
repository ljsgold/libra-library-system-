<template>
  <el-card class="quick-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div>
          <span class="card-title">系统快捷入口</span>
          <span class="card-subtitle">常用高频操作快速导航</span>
        </div>
      </div>
    </template>
    <el-row :gutter="16">
      <el-col
        v-for="action in actions"
        :key="action.key"
        :xs="12"
        :sm="12"
        :md="12"
        :lg="12"
      >
        <div class="quick-item" @click="handleClick(action)">
          <div class="quick-icon">
            <component :is="action.icon" />
          </div>
          <div class="quick-content">
            <div class="quick-title">{{ action.label }}</div>
            <div class="quick-desc">{{ action.desc }}</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup lang="ts">
import { Plus, Collection, List, User } from '@element-plus/icons-vue'

type QuickKey = 'addBook' | 'borrowManage' | 'categoryManage' | 'userManage'

interface QuickAction {
  key: QuickKey
  label: string
  desc: string
  icon: any
}

const emit = defineEmits<{
  (e: 'quick', payload: { key: QuickKey }): void
}>()

const actions: QuickAction[] = [
  {
    key: 'addBook',
    label: '新增图书',
    desc: '录入新书，维护馆藏信息',
    icon: Plus
  },
  {
    key: 'borrowManage',
    label: '借阅管理',
    desc: '借阅登记、归还、续借',
    icon: Collection
  },
  {
    key: 'categoryManage',
    label: '分类管理',
    desc: '维护图书分类体系',
    icon: List
  },
  {
    key: 'userManage',
    label: '用户管理',
    desc: '管理账户与借阅权限',
    icon: User
  }
]

const handleClick = (action: QuickAction) => {
  emit('quick', { key: action.key })
}
</script>

<style scoped>
.quick-card {
  height: 100%;
  border-radius: 18px;
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

.quick-item {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  margin-bottom: 10px;
  border-radius: 14px;
  background-color: rgba(107, 63, 42, 0.04);
  cursor: pointer;
  border: 1px solid rgba(107, 63, 42, 0.12);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.quick-item:hover {
  border-color: rgba(138, 90, 62, 0.4);
  box-shadow: var(--shadow-md);
}

.quick-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(138, 90, 62, 0.12);
  color: var(--color-cta);
  margin-right: 10px;
}

.quick-icon :deep(svg) {
  width: 18px;
  height: 18px;
}

.quick-content {
  flex: 1;
  min-width: 0;
}

.quick-title {
  font-size: 14px;
  font-weight: 500;
}

.quick-desc {
  margin-top: 2px;
  font-size: 12px;
  color: var(--color-muted);
}

@media (prefers-reduced-motion: reduce) {
  .quick-item:hover {
    box-shadow: none;
  }
}
</style>
