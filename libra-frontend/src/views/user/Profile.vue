<template>
  <div class="profile-page page-shell">
    <header class="page-header">
      <div>
        <span class="eyebrow">Profile</span>
        <h1 class="page-title">个人中心</h1>
        <p class="page-subtitle">查看个人信息与借阅规则。</p>
      </div>
    </header>

    <el-row :gutter="16">
      <el-col :xs="24" :md="10">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">个人信息</span>
            </div>
          </template>

          <div v-if="loadingProfile" class="card-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!profile" class="card-loading">
            <el-empty description="暂无信息" />
          </div>
          <div v-else class="profile-info">
            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="姓名">{{ profile.name }}</el-descriptions-item>
              <el-descriptions-item v-if="profile.studentNo" label="学号">
                {{ profile.studentNo }}
              </el-descriptions-item>
              <el-descriptions-item v-if="profile.employeeNo" label="工号">
                {{ profile.employeeNo }}
              </el-descriptions-item>
              <el-descriptions-item v-if="profile.department" label="院系">
                {{ profile.department }}
              </el-descriptions-item>
              <el-descriptions-item v-if="profile.email" label="邮箱">
                {{ profile.email }}
              </el-descriptions-item>
              <el-descriptions-item v-if="profile.phone" label="手机号">
                {{ profile.phone }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="14" class="mt-md-0 mt-16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">借阅规则</span>
            </div>
          </template>

          <div v-if="loadingRules" class="card-loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else-if="!rules.length" class="card-loading">
            <el-empty description="暂无规则" />
          </div>
          <div v-else class="rules">
            <ul>
              <li v-for="rule in rules" :key="rule.title">
                <strong>{{ rule.title }}：</strong>{{ rule.content }}
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { BorrowRuleItem, UserProfile } from '@/api/user'
import { getBorrowRules, getUserProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const profile = ref<UserProfile | null>(null)
const rules = ref<BorrowRuleItem[]>([])

const loadingProfile = ref(false)
const loadingRules = ref(false)

const fetchProfile = async () => {
  loadingProfile.value = true
  try {
    profile.value = await getUserProfile()
  } catch (e) {
    ElMessage.error('获取个人信息失败')
  } finally {
    loadingProfile.value = false
  }
}

const fetchRules = async () => {
  loadingRules.value = true
  try {
    rules.value = await getBorrowRules()
  } catch (e) {
    ElMessage.error('获取借阅规则失败')
  } finally {
    loadingRules.value = false
  }
}

onMounted(() => {
  fetchProfile()
  fetchRules()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.profile-page :deep(.el-card) {
  background: var(--color-surface);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 200ms ease, transform 200ms ease;
  height: 100%;
}

.profile-page :deep(.el-card:hover) {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 12px;
}

.eyebrow {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--color-primary);
  margin-bottom: 8px;
  display: inline-block;
}

.page-title {
  margin: 0;
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: var(--color-text);
}

.page-subtitle {
  margin: 0;
  font-size: 17px;
  color: var(--color-text-secondary);
  max-width: 600px;
  line-height: 1.5;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 18px;
  background: var(--color-primary);
  border-radius: 2px;
}

.card-loading {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-info {
  font-size: 14px;
}

.profile-page :deep(.el-descriptions__cell) {
  padding: 16px 24px !important;
  background-color: transparent !important;
}

.profile-page :deep(.el-descriptions__label) {
  color: var(--color-text-secondary);
  font-weight: 500;
  width: 100px;
}

.profile-page :deep(.el-descriptions__content) {
  color: var(--color-text);
  font-weight: 600;
}

.rules {
  padding: 8px;
}

.rules ul {
  padding-left: 20px;
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.rules li {
  margin-bottom: 8px;
}

.rules strong {
  color: var(--color-text);
  font-weight: 600;
}

.mt-16 {
  margin-top: 32px;
}

.mt-md-0 {
  margin-top: 0;
}

@media (max-width: 991px) {
  .mt-md-0 {
    margin-top: 32px;
  }
}
</style>
