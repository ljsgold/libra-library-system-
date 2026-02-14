import request from '@/utils/request'

export interface LoginPayload {
  username: string
  password: string
}

export interface CodeLoginPayload {
  username: string
  code: string
}

export interface RegisterPayload {
  name: string
  username: string
  email?: string
  password: string
}

export interface ResetCodePayload {
  username: string
  email: string
}

export interface ResetPasswordPayload extends ResetCodePayload {
  code: string
  newPassword: string
}

export function login(payload: LoginPayload) {
  return request.post('/auth/login', payload)
}

export function sendLoginCode(payload: { username: string }) {
  return request.post('/auth/login/code', payload)
}

export function loginByCode(payload: CodeLoginPayload) {
  return request.post('/auth/login/by-code', payload)
}

/**
 * 获取微信登录跳转URL
 */
export function getWechatLoginUrl() {
  return request.get<string>('/auth/wechat/url')
}

/**
 * 微信回调登录
 */
export function wechatLogin(code: string) {
  return request.post<any>('/auth/wechat/login', null, {
    params: { code }
  })
}

export function register(payload: RegisterPayload) {
  return request.post('/auth/register', payload)
}

export function sendResetCode(payload: ResetCodePayload) {
  return request.post('/auth/forgot/code', payload)
}

export function resetPassword(payload: ResetPasswordPayload) {
  return request.post('/auth/forgot/reset', payload)
}
