import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

type RequestInstance = {
  <T = any>(config: AxiosRequestConfig): Promise<T>
  request<T = any>(config: AxiosRequestConfig): Promise<T>
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  head<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  options<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>
  interceptors: AxiosInstance['interceptors']
  defaults: AxiosInstance['defaults']
}

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 10000
}) as unknown as RequestInstance

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

let isRefreshing = false
let requests: Array<(token: string) => void> = []

// 响应拦截器
service.interceptors.response.use(
  async (response) => {
    const res = response.data
    const config = response.config

    if (res.code === 401 && !config.url?.includes('/auth/refresh')) {
      if (!isRefreshing) {
        isRefreshing = true
        const refreshToken = localStorage.getItem('refreshToken')
        if (refreshToken) {
          try {
            const data = await service.post<{ accessToken: string; refreshToken: string }>(
              '/auth/refresh',
              { refreshToken }
            )
            localStorage.setItem('accessToken', data.accessToken)
            localStorage.setItem('refreshToken', data.refreshToken)
            isRefreshing = false
            requests.forEach((cb) => cb(data.accessToken))
            requests = []
            return service(config)
          } catch (e) {
            localStorage.removeItem('accessToken')
            localStorage.removeItem('refreshToken')
            window.location.href = '/login'
          }
        } else {
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
          window.location.href = '/login'
        }
      } else {
        return new Promise((resolve) => {
          requests.push((token: string) => {
            config.headers['Authorization'] = `Bearer ${token}`
            resolve(service(config))
          })
        })
      }
    }

    if (res.code !== 200) {
      ElMessage.error(res.msg || '系统错误')
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res.data
    }
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
