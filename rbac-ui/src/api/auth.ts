import request from '@/utils/request'
// import { MockService } from '@/mock/service'  // 注释掉
import type { LoginParams, LoginResult, UserInfo } from '@/types/user'
import type { Result } from '@/types/result'

// const IS_MOCK = import.meta.env.VITE_USE_MOCK === 'true'  // 注释掉

/**
 * 用户登录
 */
export function login(data: LoginParams): Promise<LoginResult> {
  // if (IS_MOCK) {                // 注释掉
  //   return MockService.login(data)
  // }
  return request.post<LoginResult>('/api/auth/login', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo(): Promise<UserInfo> {
  // if (IS_MOCK) {                // 注释掉
  //   return MockService.getUserInfo()
  // }
  return request.get<UserInfo>('/api/auth/info')
}

/**
 * 用户登出
 */
export function logout(): Promise<void> {
  // if (IS_MOCK) {                // 注释掉
  //   return MockService.logout()
  // }
  return request.post<void>('/api/auth/logout')
} 