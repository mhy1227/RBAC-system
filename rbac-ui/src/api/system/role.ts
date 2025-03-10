import request from '@/utils/request'
import type { RoleInfo } from '@/types/user'
import type { PageQuery, PageResult } from '@/types/api'
import type { Result } from '@/types/result'
// import { mockRolePageResponse, mockCreateRole, mockUpdateRole, mockDeleteRole, mockBatchDeleteRole, mockUpdateRoleStatus, mockGetRolePermissions, mockUpdateRolePermissions } from '@/mock/data/role'
// import { mockRoleListData } from '@/mock/data/role'

// const IS_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

/**
 * 获取角色列表（分页）
 */
export function getRolePage(params: PageQuery): Promise<PageResult<RoleInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockRolePageResponse(params))
  // }
  return request.get<PageResult<RoleInfo>>('/api/role/page', { params })
}

/**
 * 获取角色详情
 */
export function getRoleInfo(id: number): Promise<RoleInfo> {
  // if (IS_MOCK) {
  //   const role = mockRoleListData.find(item => item.id === id)
  //   if (!role) {
  //     return Promise.reject(new Error('角色不存在'))
  //   }
  //   return Promise.resolve(role)
  // }
  return request.get<RoleInfo>(`/api/role/${id}`)
}

/**
 * 创建角色
 */
export function createRole(data: Partial<RoleInfo>): Promise<Result<RoleInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockCreateRole(data))
  // }
  return request.post<Result<RoleInfo>>('/api/role', data)
}

/**
 * 更新角色
 */
export function updateRole(data: Partial<RoleInfo>): Promise<Result<RoleInfo>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUpdateRole(data))
  // }
  return request.put<Result<RoleInfo>>('/api/role', data)
}

/**
 * 删除角色
 */
export function deleteRole(id: number): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockDeleteRole(id))
  // }
  return request.delete<Result<void>>(`/api/role/${id}`)
}

/**
 * 批量删除角色
 */
export function batchDeleteRole(ids: number[]): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockBatchDeleteRole(ids))
  // }
  return request.delete<Result<void>>('/api/role/batch', { data: ids })
}

/**
 * 更新角色状态
 */
export function updateRoleStatus(id: number, status: number): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUpdateRoleStatus(id, status))
  // }
  return request.put<Result<void>>(`/api/role/${id}/status/${status}`)
}

/**
 * 获取角色权限
 */
export function getRolePermissions(roleId: number): Promise<number[]> {
  return request.get<number[]>(`/api/role/${roleId}/permissions`)
}

/**
 * 更新角色权限
 */
export function updateRolePermissions(roleId: number, permissionIds: number[]): Promise<Result<void>> {
  // if (IS_MOCK) {
  //   return Promise.resolve(mockUpdateRolePermissions(roleId, permissionIds))
  // }
  return request.post<Result<void>>(`/api/role/${roleId}/permissions`, permissionIds)
} 