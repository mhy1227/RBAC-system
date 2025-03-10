/**
 * 权限类型枚举
 */
export enum PermissionType {
  MENU = 'MENU',      // 菜单权限
  BUTTON = 'BUTTON',  // 按钮权限
  API = 'API'         // 接口权限
}

/**
 * 权限状态枚举
 */
export enum PermissionStatus {
  DISABLED = 0,  // 禁用
  ENABLED = 1    // 启用
}

/**
 * 权限实体接口
 */
export interface Permission {
  id: number;
  permissionName: string;    // 权限名称
  permissionCode: string;    // 权限标识
  description?: string;      // 权限描述
  pid: number;              // 父权限ID
  sortOrder: number;        // 排序号
  type: string;             // 权限类型
  path?: string;            // 路由路径
  status: number;           // 状态
  createTime: string;       // 创建时间
  updateTime: string;       // 更新时间
  children?: Permission[];  // 子权限（前端树形结构使用）
}

/**
 * 权限查询参数接口
 */
export interface PermissionQuery {
  permissionName?: string;   // 权限名称
  permissionCode?: string;   // 权限标识
  type?: string;            // 权限类型
  status?: number;          // 状态
} 