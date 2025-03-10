// 用户信息
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
}

// 角色信息
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description: string | null
  status: number // 0-禁用，1-启用
  createTime: string
  updateTime: string
}

// 权限信息
export interface PermissionInfo {
  id: number
  permissionName: string
  permissionCode: string
  description: string | null
  pid: number | null
  sortOrder: number
  type: 'menu' | 'button'
  path: string | null
  status: number
  createTime: string
  updateTime: string
  children?: PermissionInfo[]
}

// 登录参数
export interface LoginParams {
  username: string
  password: string
  code?: string
  uuid?: string
}

// 登录响应
export interface LoginResult {
  token: string
  user: UserInfo
}

// 分页查询参数
export interface UserPageQuery {
  pageNum: number
  pageSize: number
  username?: string
  nickname?: string
  email?: string
  phone?: string
  status?: number
  createTime?: [string, string] // 创建时间范围
}
