import { defineStore } from 'pinia'
import { login as loginApi, loginByCode as loginByCodeApi, type CodeLoginPayload, type LoginPayload } from '@/api/auth'
import { getUserProfile, type UserProfile } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
    userInfo: null as UserProfile | null
  }),
  actions: {
    setTokens(data: { accessToken: string; refreshToken: string }) {
      this.accessToken = data.accessToken
      this.refreshToken = data.refreshToken
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
    },
    async login(loginForm: LoginPayload) {
      const data: any = await loginApi(loginForm)
      this.setTokens(data)
      try {
        await this.fetchUserInfo(true)
      } catch (e) {
        // 用户信息获取失败不阻断登录
      }
    },
    async loginByCode(payload: CodeLoginPayload) {
      const data: any = await loginByCodeApi(payload)
      this.setTokens(data)
      try {
        await this.fetchUserInfo(true)
      } catch (e) {
        // 用户信息获取失败不阻断登录
      }
    },
    async fetchUserInfo(force = false) {
      if (this.userInfo && !force) return this.userInfo
      const profile = await getUserProfile()
      this.userInfo = profile
      return profile
    },
    logout() {
      this.accessToken = ''
      this.refreshToken = ''
      this.userInfo = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      window.location.href = '/login'
    }
  }
})
