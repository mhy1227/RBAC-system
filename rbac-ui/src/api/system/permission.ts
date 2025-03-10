import request from '@/utils/request'
import type { PermissionInfo } from '@/types/permission'

/**
 * 获取权限树
 */
export function getPermissionTree() {
  return request.get<PermissionInfo[]>('/permission/tree')
}

/**
 * 获取角色权限
 * @param roleId 角色ID
 */
export function getRolePermissions(roleId: number) {
  return request.get<PermissionInfo[]>(`/permission/role/${roleId}`)
}

/**
 * 更新角色权限
 * @param roleId 角色ID
 * @param permissionIds 权限ID列表
 */
export function updateRolePermissions(roleId: number, permissionIds: number[]) {
  return request.post(`/role/${roleId}/permission`, permissionIds)
} 