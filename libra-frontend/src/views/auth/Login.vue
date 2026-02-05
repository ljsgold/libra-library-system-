<template>
  <div class="auth-page">
    <div class="auth-shell">
      <section class="auth-intro">
        <span class="eyebrow">Libra Reader</span>
        <h1 class="auth-title">让借阅变成一种更优雅的体验。</h1>
        <p class="auth-lede">
          以更清晰的流程、更一致的服务，把读者、馆藏与借阅体验连接在一起。
        </p>
        <div class="auth-highlights">
          <div class="auth-highlight">
            <strong>18,420</strong>
            今日访问
          </div>
          <div class="auth-highlight">
            <strong>98%</strong>
            满意度
          </div>
          <div class="auth-highlight">
            <strong>12 分钟</strong>
            平均借阅完成
          </div>
        </div>
      </section>

      <section class="auth-card">
        <div class="brand brand-compact" @click="goHome">
          <div class="brand-title">Libra 读者端</div>
          <div class="brand-subtitle">图书馆读者服务</div>
        </div>

        <el-tabs v-model="loginMode" class="auth-tabs" stretch>
          <el-tab-pane label="密码登录" name="password" />
          <el-tab-pane label="验证码登录" name="code" />
        </el-tabs>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="账号" prop="username">
            <el-input
              v-model="form.username"
              placeholder="学号 / 工号 / 用户名"
              @keyup.enter="submit"
            />
          </el-form-item>

          <el-form-item v-if="loginMode === 'password'" label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              @keyup.enter="submit"
            />
          </el-form-item>

          <el-form-item v-else label="验证码" prop="code">
            <div class="code-row">
              <el-input
                v-model="form.code"
                placeholder="6位验证码"
                maxlength="6"
                @keyup.enter="submit"
              />
              <el-button type="primary" :loading="sending" :disabled="sendDisabled" @click="sendCode">
                {{ sendButtonText }}
              </el-button>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="goForgot">忘记密码？</el-link>
          </div>

          <el-button type="primary" class="submit-btn" :loading="submitting" @click="submit" block>
            登录
          </el-button>

          <div class="extra">
            没有账号？
            <el-link type="primary" :underline="false" @click="goRegister">去注册</el-link>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { sendLoginCode } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const sending = ref(false)
const countdown = ref(0)
const loginMode = ref<'password' | 'code'>('password')

const form = reactive({
  username: (route.query.username as string) || '',
  password: '',
  code: '',
  remember: true
})

const rules = computed<FormRules>(() => {
  const base = {
    username: [{ required: true, message: '请输入账号', trigger: 'blur' }]
  }
  if (loginMode.value === 'password') {
    return {
      ...base,
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }
  }
  return {
    ...base,
    code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
  }
})

const sendDisabled = computed(() => sending.value || countdown.value > 0)
const sendButtonText = computed(() => (countdown.value > 0 ? `${countdown.value}秒` : '发送验证码'))

const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const sendCode = async () => {
  if (!form.username) {
    ElMessage.warning('请输入账号')
    return
  }
  sending.value = true
  try {
    await sendLoginCode({ username: form.username })
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (e) {
    // handled by interceptor
  } finally {
    sending.value = false
  }
}

const submit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (loginMode.value === 'password') {
        await userStore.login({ username: form.username, password: form.password })
      } else {
        await userStore.loginByCode({ username: form.username, code: form.code })
      }
      ElMessage.success('登录成功')
      const defaultRedirect = userStore.userInfo?.isAdmin ? '/admin/dashboard' : '/u/home'
      const redirect = (route.query.redirect as string) || defaultRedirect
      router.replace(redirect)
    } catch (e: any) {
      // handled by interceptor
    } finally {
      submitting.value = false
    }
  })
}

watch(loginMode, () => {
  form.password = ''
  form.code = ''
  formRef.value?.clearValidate()
})

const goHome = () => router.push('/u/home')
const goRegister = () => router.push({ path: '/register', query: { username: form.username } })
const goForgot = () => router.push({ path: '/forgot-password', query: { username: form.username } })
</script>

<style scoped>
@import './auth-common.css';

.auth-tabs {
  margin-bottom: 12px;
}

.auth-tabs :deep(.el-tabs__item) {
  font-weight: 600;
}

.auth-tabs :deep(.el-tabs__active-bar) {
  background: var(--color-cta);
}

.code-row {
  display: flex;
  gap: 8px;
}

.code-row :deep(.el-input) {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
