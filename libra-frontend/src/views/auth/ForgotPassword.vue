<template>
  <div class="auth-page">
    <div class="auth-shell">
      <section class="auth-intro">
        <span class="eyebrow">Reset</span>
        <h1 class="auth-title">轻松一步找回权限。</h1>
        <p class="auth-lede">验证账号和邮箱后即可重置密码，不会打断你的借阅流程。</p>
        <div class="auth-highlights">
          <div class="auth-highlight">
            <strong>2 分钟</strong>
            平均耗时
          </div>
          <div class="auth-highlight">
            <strong>100%</strong>
            安全校验
          </div>
          <div class="auth-highlight">
            <strong>实时</strong>
            邮件通知
          </div>
        </div>
      </section>

      <section class="auth-card">
        <div class="brand brand-compact" @click="goHome">
          <div class="brand-title">重置密码</div>
          <div class="brand-subtitle">验证邮箱后重置</div>
        </div>

        <el-steps :active="step" finish-status="success" class="steps" align-center>
          <el-step title="验证邮箱" />
          <el-step title="设置密码" />
        </el-steps>

        <div v-if="step === 0">
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="账号" prop="username">
              <el-input v-model="form.username" placeholder="学号 / 工号 / 用户名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="账号绑定邮箱" />
            </el-form-item>
            <el-form-item label="验证码" prop="code">
              <div class="code-row">
                <el-input v-model="form.code" placeholder="6位验证码" maxlength="6" />
                <el-button type="primary" :loading="sending" :disabled="sendDisabled" @click="sendCode">
                  {{ sendButtonText }}
                </el-button>
              </div>
            </el-form-item>
            <el-button type="primary" class="submit-btn" :loading="submitting" @click="nextStep" block>
              下一步
            </el-button>
            <div class="extra">
              想起来了？
              <el-link type="primary" :underline="false" @click="goLogin">去登录</el-link>
            </div>
          </el-form>
        </div>

        <div v-else>
          <el-form ref="resetFormRef" :model="resetForm" :rules="resetRules" label-position="top">
            <el-form-item label="新密码" prop="password">
              <el-input v-model="resetForm.password" type="password" show-password placeholder="8位以上，包含字母和数字" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="resetForm.confirmPassword" type="password" show-password placeholder="再次输入" />
            </el-form-item>
            <el-button type="primary" class="submit-btn" :loading="submitting" @click="resetPassword" block>
              完成
            </el-button>
            <div class="extra">
              换个账号？
              <el-link type="primary" :underline="false" @click="goLogin">去登录</el-link>
            </div>
          </el-form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { sendResetCode, resetPassword as resetPasswordApi } from '@/api/auth'

const router = useRouter()
const route = useRoute()

const step = ref(0)
const formRef = ref<FormInstance>()
const resetFormRef = ref<FormInstance>()
const submitting = ref(false)
const sending = ref(false)
const countdown = ref(0)

const form = reactive({
  username: (route.query.username as string) || '',
  email: '',
  code: ''
})

const resetForm = reactive({
  password: '',
  confirmPassword: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const resetRules: FormRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '至少8位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== resetForm.password) return callback(new Error('两次密码不一致'))
        callback()
      },
      trigger: 'blur'
    }
  ]
}

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
  if (!form.email || !form.username) {
    ElMessage.warning('请输入账号和邮箱')
    return
  }
  sending.value = true
  try {
    await sendResetCode({ username: form.username, email: form.email })
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (e) {
    // handled by interceptor
  } finally {
    sending.value = false
  }
}

const nextStep = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      step.value = 1
    } finally {
      submitting.value = false
    }
  })
}

const resetPassword = () => {
  if (!resetFormRef.value) return
  resetFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await resetPasswordApi({
        username: form.username,
        email: form.email,
        code: form.code,
        newPassword: resetForm.password
      })
      ElMessage.success('密码重置成功')
      router.replace({ path: '/login', query: { username: form.username } })
    } catch (e) {
      // handled by interceptor
    } finally {
      submitting.value = false
    }
  })
}

const goHome = () => router.push('/u/home')
const goLogin = () => router.push({ path: '/login', query: { username: form.username } })
</script>

<style scoped>
@import './auth-common.css';

.steps {
  margin: 4px 0 16px;
}

.steps :deep(.el-step__title) {
  font-weight: 600;
  color: var(--color-text);
}

.steps :deep(.el-step__icon) {
  background: rgba(10, 108, 255, 0.08);
  border-color: rgba(10, 108, 255, 0.2);
}

.code-row {
  display: flex;
  gap: 8px;
}

.code-row :deep(.el-input) {
  flex: 1;
}
</style>
