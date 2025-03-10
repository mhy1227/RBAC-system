import request from '@/utils/request'
import type { Permission, PermissionQuery } from '@/views/system/permission/types/permission'
import type { Result } from '@/types/result'

/**
 * 获取权限树
 * @param query 查询参数
 * @returns 权限树数据
 */
export function getPermissionTree(query: PermissionQuery): Promise<Permission[]> {
  return request.get('/api/permission/tree', { params: query })
}

/**
 * 新增权限
 * @param data 权限数据
 */
export function createPermission(data: Partial<Permission>): Promise<Result<void>> {
  return request.post('/api/permission', data)
}

/**
 * 更新权限
 * @param id 权限ID
 * @param data 权限数据
 */
export function updatePermission(id: number, data: Partial<Permission>): Promise<Result<void>> {
  return request.put(`/api/permission/${id}`, data)
}

/**
 * 删除权限
 * @param id 权限ID
 */
export function deletePermission(id: number): Promise<Result<void>> {
  return request.delete(`/api/permission/${id}`)
}

/**
 * 批量删除权限
 * @param ids 权限ID数组
 */
export function batchDeletePermission(ids: number[]): Promise<Result<void>> {
  return request.delete('/api/permission/batch', { data: ids })
}

/**
 * 更新权限状态
 * @param id 权限ID
 * @param status 状态
 */
export function updatePermissionStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/api/permission/${id}/status`, { status })
} 