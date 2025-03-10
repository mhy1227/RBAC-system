import type { MockMethod } from 'vite-plugin-mock'
import Mock from 'mockjs'
import type { PageResult, ApiResult } from '@/types/api'
import type { RoleInfo } from '@/types/user'
import type { Result } from '@/types/result'

// 定义请求参数接口
interface RequestParams {
  url: string;
  body?: Record<string, any>;
  query?: Record<string, any>;
  headers?: Record<string, any>;
}

const Random = Mock.Random

// 验证token和权限
const validateTokenAndPermission = <T>(headers: Record<string, any> | undefined): ApiResult<T> | null => {
  // 检查token是否存在
  if (!headers?.authorization) {
    return {
      code: 401,
      message: '未登录或token已过期',
      data: null
    } as ApiResult<T>
  }

  // 检查token是否有效
  const token = headers.authorization.replace('Bearer ', '')
  if (token === 'mock-token-expired-12345') {
    return {
      code: 401,
      message: 'token已过期',
      data: null
    } as ApiResult<T>
  }

  return null
}

// 预定义一些角色类型
const roleTypes = [
  { prefix: 'ADMIN', name: '管理员' },
  { prefix: 'USER', name: '用户' },
  { prefix: 'OPERATOR', name: '操作员' },
  { prefix: 'AUDITOR', name: '审计员' },
  { prefix: 'VIEWER', name: '访客' }
]

// 生成角色列表数据
const generateRoleList = (count: number): RoleInfo[] => {
  const roles: RoleInfo[] = []
  for (let i = 0; i < count; i++) {
    // 随机选择一个角色类型
    const roleType = roleTypes[Math.floor(Math.random() * roleTypes.length)]
    const deptNo = Math.floor(Math.random() * 5) + 1  // 1-5的部门编号
    
    roles.push({
      id: i + 1,
      roleName: `${roleType.name}${deptNo}组`,
      roleCode: `${roleType.prefix}_DEPT_${deptNo}`,
      description: Random.paragraph(),  // 生成随机描述
      status: Math.random() > 0.2 ? 1 : 0,   // 80%概率为启用状态
      createTime: Random.datetime(),  // 生成随机时间
      updateTime: Random.datetime()
    })
  }
  return roles
}

// 初始化更多的角色数据
let roleList = generateRoleList(50)  // 增加初始数据量

// 模拟角色权限数据
const rolePermissionsMap = new Map<number, number[]>()

// 初始化一些测试数据
rolePermissionsMap.set(1, [1, 2, 3, 4]) // 管理员角色拥有所有权限
rolePermissionsMap.set(2, [2, 3]) // 普通用户角色拥有部分权限

export default [
  // 角色分页查询
  {
    url: '/api/role/page',
    method: 'get',
    response: ({ query }: { query: Record<string, any> }): ApiResult<PageResult<RoleInfo>> => {
      const { pageNum = 1, pageSize = 10, roleName, roleCode, status } = query
      
      // 使用现有roleList而不是重新生成
      const filteredList = roleList.filter(role => {
        const matchRoleName = !roleName || role.roleName.toLowerCase().includes(String(roleName).toLowerCase())
        const matchRoleCode = !roleCode || role.roleCode.toLowerCase().includes(String(roleCode).toLowerCase())
        const matchStatus = status === undefined || status === '' || role.status === Number(status)
        return matchRoleName && matchRoleCode && matchStatus
      })

      // 分页处理
      const startIndex = (Number(pageNum) - 1) * Number(pageSize)
      const endIndex = startIndex + Number(pageSize)
      const pageList = filteredList.slice(startIndex, endIndex)

      return {
        code: 200,
        message: 'success',
        data: {
          list: pageList,
          total: filteredList.length,
          pageNum: Number(pageNum),
          pageSize: Number(pageSize)
        }
      }
    }
  },

  // 获取角色详情
  {
    url: /\/api\/role\/(\d+)$/,
    method: 'get',
    response: ({ url }: { url: string }): Result<RoleInfo | null> => {
      try {
        // 从URL中提取ID
        const id = url.match(/\/api\/role\/(\d+)$/)?.[1]
        if (!id) {
          return {
            code: 400,
            message: '缺少角色ID',
            data: null
          }
        }

        // 从roleList中查找对应的角色
        const roleId = parseInt(id)
        const role = roleList.find(item => item.id === roleId)
        
        if (!role) {
          return {
            code: 404,
            message: '角色不存在',
            data: null
          }
        }

      return {
        code: 200,
        message: 'success',
          data: role
        }
      } catch (error) {
        console.error('获取角色详情错误:', error)
        return {
          code: 500,
          message: '服务器内部错误',
          data: null
        }
      }
    }
  },

  // 创建角色
  {
    url: '/api/role',
    method: 'post',
    response: ({ body }: { body: Partial<RoleInfo> }): ApiResult<RoleInfo> => {
      // 生成新ID（取最大ID + 1）
      const maxId = Math.max(...roleList.map(role => role.id), 0)
      const newRole: RoleInfo = {
        id: maxId + 1,
        roleName: '',
        roleCode: '',
        description: '',
        status: 1,
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString(),
        ...body
      }
      
      // 添加到roleList
      roleList.push(newRole)
      
      return {
        code: 200,
        message: 'success',
        data: newRole
      }
    }
  },

  // 更新角色
  {
    url: '/api/role',
    method: 'put',
    response: ({ body }: { body: RoleInfo }): ApiResult<RoleInfo> => {
      const index = roleList.findIndex(item => item.id === body.id)
      if (index === -1) {
        return {
          code: 404,
          message: '角色不存在',
          data: null as any
        }
      }
      
      // 更新roleList中的数据
      const updatedRole = {
        ...roleList[index],
        ...body,
        updateTime: new Date().toISOString()
      }
      roleList[index] = updatedRole
      
      return {
        code: 200,
        message: 'success',
        data: updatedRole
      }
    }
  },

  // 删除角色
  {
    url: /\/api\/role\/(\d+)/,
    method: 'delete',
    response: ({ url }: { url: string }): ApiResult<null> => {
      const id = url.match(/\/api\/role\/(\d+)/)?.[1]
      if (!id) {
        return {
          code: 400,
          message: '缺少角色ID',
          data: null
        }
      }

      const roleId = parseInt(id)
      const index = roleList.findIndex(item => item.id === roleId)
      if (index === -1) {
        return {
          code: 404,
          message: '角色不存在',
          data: null
        }
      }

      // 从roleList中删除
      roleList.splice(index, 1)
      
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  },

  // 批量删除角色
  {
    url: '/api/role/batch',
    method: 'delete',
    response: ({ body }: { body: number[] }): ApiResult<null> => {
      if (!body || !body.length) {
        return {
          code: 400,
          message: '请选择要删除的角色',
          data: null
        }
      }

      // 从roleList中批量删除
      roleList = roleList.filter(role => !body.includes(role.id))
      
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  },

  // 更新角色状态
  {
    url: /\/api\/role\/(\d+)\/status\/(\d+)/,
    method: 'put',
    response: ({ url }: { url: string }): ApiResult<null> => {
      const matches = url.match(/\/api\/role\/(\d+)\/status\/(\d+)/)
      if (!matches) {
        return {
          code: 400,
          message: '无效的请求URL',
          data: null
        }
      }
      
      const [, roleId, status] = matches
      if (!roleId || (status !== '0' && status !== '1')) {
        return {
          code: 400,
          message: '无效的参数',
          data: null
        }
      }

      // 更新roleList中的状态
      const index = roleList.findIndex(item => item.id === parseInt(roleId))
      if (index !== -1) {
        roleList[index].status = parseInt(status)
        roleList[index].updateTime = new Date().toISOString()
      }
      
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  },

  // 获取角色权限
  {
    url: /\/api\/role\/(\d+)\/permissions/,
    method: 'get',
    response: ({ url }: RequestParams): ApiResult<number[]> => {
      try {
        const id = url.match(/\/api\/role\/(\d+)\/permissions/)?.[1]
        if (!id) {
          return {
            code: 400,
            message: '缺少角色ID',
            data: null
          }
        }

        const roleId = parseInt(id)
        // 如果没有预设的权限数据，生成一些随机权限
        if (!rolePermissionsMap.has(roleId)) {
          const randomPermissions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
            .filter(() => Math.random() > 0.5)
          rolePermissionsMap.set(roleId, randomPermissions)
        }

        return {
          code: 200,
          message: 'success',
          data: rolePermissionsMap.get(roleId) || []
        }
      } catch (error) {
        console.error('获取角色权限错误:', error)
        return {
          code: 500,
          message: '获取角色权限失败',
          data: []
        }
      }
    }
  },

  // 更新角色权限
  {
    url: /\/api\/role\/(\d+)\/permissions/,
    method: 'post',
    response: ({ url, body }: RequestParams): ApiResult<void> => {
      try {
        const id = url.match(/\/api\/role\/(\d+)\/permissions/)?.[1]
        if (!id) {
          return {
            code: 400,
            message: '缺少角色ID',
            data: null
          }
        }

        const roleId = parseInt(id)
        const permissionIds = body as number[]
        
        if (!Array.isArray(permissionIds)) {
          return {
            code: 400,
            message: '无效的权限数据',
            data: null
          }
        }

        // 更新角色权限
        rolePermissionsMap.set(roleId, permissionIds)

        return {
          code: 200,
          message: 'success',
          data: null
        }
      } catch (error) {
        console.error('更新角色权限错误:', error)
        return {
          code: 500,
          message: '更新角色权限失败',
          data: null
        }
      }
    }
  }
] as MockMethod[] 