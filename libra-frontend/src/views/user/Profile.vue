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
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.page-title {
  margin: 8px 0 0;
  font-size: clamp(24px, 2.6vw, 32px);
}

.page-subtitle {
  margin: 0;
  color: var(--color-muted);
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

.card-loading {
  min-height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-info {
  font-size: 13px;
}

.rules ul {
  padding-left: 16px;
  margin: 0;
  font-size: 13px;
  color: var(--color-muted);
  line-height: 1.6;
}

.mt-16 {
  margin-top: 16px;
}

.mt-md-0 {
  margin-top: 0;
}

@media (max-width: 991px) {
  .mt-md-0 {
    margin-top: 16px;
  }
}
</style>
