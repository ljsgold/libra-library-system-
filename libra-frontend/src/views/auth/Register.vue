<template>
  <div class="auth-page">
    <div class="auth-shell">
      <section class="auth-intro">
        <span class="eyebrow">Create account</span>
        <h1 class="auth-title">开启你的读者体验。</h1>
        <p class="auth-lede">注册后即可预约新书、管理借阅记录并接收还书提醒。</p>
        <div class="auth-highlights">
          <div class="auth-highlight">
            <strong>12,000+</strong>
            活跃读者
          </div>
          <div class="auth-highlight">
            <strong>24/7</strong>
            自助服务
          </div>
          <div class="auth-highlight">
            <strong>99%</strong>
            通知送达
          </div>
        </div>
      </section>

      <section class="auth-card">
        <div class="brand brand-compact" @click="goHome">
          <div class="brand-title">注册账号</div>
          <div class="brand-subtitle">注册后可借阅与预约图书</div>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="真实姓名" />
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="学号 / 工号 / 用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="用于找回密码" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="8位以上，包含字母和数字" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" show-password placeholder="再次输入" />
          </el-form-item>
          <el-button type="primary" class="submit-btn" :loading="submitting" @click="submit" block>
            注册
          </el-button>
          <div class="extra">
            已有账号？
            <el-link type="primary" :underline="false" @click="goLogin">去登录</el-link>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  name: '',
  username: (route.query.username as string) || '',
  email: '',
  password: '',
  confirmPassword: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '至少8位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次密码不一致'))
        } else callback()
      },
      trigger: 'blur'
    }
  ]
}

const submit = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await register({
        name: form.name,
        username: form.username,
        email: form.email || undefined,
        password: form.password
      })
      ElMessage.success('注册成功')
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
</style>
