/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string | null
  email: string | null
  phone: string | null
  avatar: string | null
  status: number // 0-禁用，1-启用
  createTime: string
  updateTime: string
  lastLoginTime: string | null
  loginFailCount: number
  lockTime: string | null
  roles?: RoleInfo[]
  permissions?: string[] // 用户权限列表
}

/**
 * 角色信息
 */
export interface RoleInfo {
  id: number
  roleName: string // role_name
  roleCode: string // role_code
  description: string | null
  status: number // 0-禁用，1-启用
  createTime: string // create_time
  updateTime: string // update_time
}

/**
 * 权限信息
 */
export interface PermissionInfo {
  id: number
  permissionName: string // permission_name
  permissionCode: string // permission_code
  description: string | null
  pid: number | null
  sortOrder: number // sort_order
  type: 'menu' | 'button'
  path: string | null
  status: number // 0-禁用，1-启用
  createTime: string
  updateTime: string
  children?: PermissionInfo[] // 树形结构
}

/**
 * 登录参数
 */
export interface LoginParams {
  username: string
  password: string
  code?: string // 验证码
  uuid?: string // 验证码标识
}

/**
 * 登录响应
 */
export interface LoginResult {
  token: string
  user: UserInfo
}

/**
 * 用户创建参数
 */
export interface CreateUserParams {
  username: string
  password: string
  nickname?: string
  email?: string
  phone?: string
  status?: number
  roleIds?: number[]
}

/**
 * 用户更新参数
 */
export interface UpdateUserParams {
  id: number
  nickname?: string
  email?: string
  phone?: string
  status?: number
  roleIds?: number[]
}

/**
 * 角色创建参数
 */
export interface CreateRoleParams {
  roleName: string
  roleCode: string
  description?: string
  status?: number
}

/**
 * 角色更新参数
 */
export interface UpdateRoleParams {
  id: number
  roleName?: string
  roleCode?: string
  description?: string
  status?: number
} 