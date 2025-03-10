/**
 * 权限类型
 */
export type PermissionType = 'menu' | 'button'

/**
 * 权限信息
 */
export interface PermissionInfo {
  id: number
  permissionName: string
  permissionCode: string
  description: string | null
  parentId: number
  type: PermissionType
  path?: string
  status: number
  createTime: string
  updateTime: string
  children?: PermissionInfo[]
}

/**
 * 创建权限参数
 */
export interface CreatePermissionParams {
  permissionName: string
  permissionCode: string
  description?: string
  parentId: number
  type: PermissionType
  path?: string
  status?: number
}

/**
 * 更新权限参数
 */
export interface UpdatePermissionParams {
  id: number
  permissionName?: string
  permissionCode?: string
  description?: string
  parentId?: number
  type?: PermissionType
  path?: string
  status?: number
} 