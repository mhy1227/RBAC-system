import request from '@/utils/request'
import type { UserInfo } from '@/types/user'
import type { PageQuery, PageResult } from '@/types/api'
import type { Result } from '@/types/result'
// import { mockUserPageResponse, mockCreateUser, mockUpdateUser, mockDeleteUser, mockBatchDeleteUser, mockUpdateUserStatus } from '@/mock/data/user'
// import { mockUserListData } from '@/mock/data/user'

// const IS_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

/**
 * 获取用户列表（分页）
 */
export function getUserPage(params: PageQuery): Promise<PageResult<UserInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUserPageResponse(params))
  // }
  return request.get<PageResult<UserInfo>>('/api/user/page', { params })
}

/**
 * 获取用户详情
 */
export function getUserInfo(id: number): Promise<Result<UserInfo>> {
  // if (IS_MOCK) {
  //   const user = mockUserListData.find(item => item.id === id)
  //   if (!user) {
  //     return Promise.reject(new Error('用户不存在'))
  //   }
  //   return Promise.resolve(user)
  // }
  return request.get<Result<UserInfo>>(`/api/user/${id}`)
}

/**
 * 创建用户
 */
export function createUser(data: Partial<UserInfo>): Promise<Result<UserInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockCreateUser(data))
  // }
  return request.post<Result<UserInfo>>('/api/user', data)
}

/**
 * 更新用户
 */
export function updateUser(data: Partial<UserInfo>): Promise<Result<UserInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUpdateUser(data))
  // }
  return request.put<Result<UserInfo>>('/api/user', data)
}

/**
 * 删除用户
 */
export function deleteUser(id: number): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockDeleteUser(id))
  // }
  return request.delete<Result<void>>(`/api/user/${id}`)
}

/**
 * 批量删除用户
 */
export function batchDeleteUser(ids: number[]): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockBatchDeleteUser(ids))
  // }
  return request.delete<Result<void>>('/api/user/batch', { data: ids })
}

/**
 * 更新用户状态
 */
export function updateUserStatus(id: number, status: number): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUpdateUserStatus(id, status))
  // }
  return request.put<Result<void>>(`/api/user/${id}/status/${status}`)
}

/**
 * 检查用户名是否存在
 */
export function checkUsername(username: string): Promise<Result<{ exists: boolean }>> {
  // if (IS_MOCK) {
  //   const users = mockUserPageResponse({ pageNum: 1, pageSize: 100 }).list
  //   return Promise.resolve({ exists: users.some(user => user.username === username) })
  // }
  return request.get<Result<{ exists: boolean }>>('/api/user/check', { params: { username } })
}

/**
 * 修改密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export function updatePassword(oldPassword: string, newPassword: string): Promise<Result<void>> {
  return request.put<Result<void>>('/api/user/password', {
    oldPassword,
    newPassword
  })
}

/**
 * 上传头像
 * @param file 头像文件
 */
export function uploadAvatar(file: File): Promise<Result<{ url: string }>> {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<Result<{ url: string }>>('/api/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}