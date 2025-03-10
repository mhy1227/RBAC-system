import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 响应数据的基础接口
interface BaseResponse<T = any> {
  code: number
  message: string
  data: T
}

const service = axios.create({
  baseURL: '',  // 保持为空，确保现有功能正常
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 调试日志
    if (import.meta.env.DEV) {
      console.log('[Request]:', {
        url: config.url,
        method: config.method,
        headers: config.headers,
        data: config.data,
        params: config.params
      })
    }

    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<BaseResponse>) => {
    const res = response.data

    // 调试日志
    if (import.meta.env.DEV) {
      console.log('[Response]:', {
        status: response.status,
        data: res
      })
    }

    if (!res) {
      ElMessage.error('响应数据为空')
      return Promise.reject(new Error('响应数据为空'))
    }

    // 处理错误响应
    if (res.code !== 200) {
      // 处理特定错误码
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.$reset()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        return Promise.reject(new Error('登录已过期'))
      }
      
      // 处理其他错误响应
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res.data
  },
  (error) => {
    console.error('Response error:', error.response || error)
    const message = error.response?.data?.message || error.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config)
  }
}

export default request
