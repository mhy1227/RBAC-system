import type { MockMethod } from 'vite-plugin-mock'

import Mock from 'mockjs'
import type { ApiResult } from '@/types/api'
import type { Permission } from '@/views/system/permission/types/permission'
import type { PermissionInfo } from '@/types/user'

// 定义请求参数接口
interface RequestParams {
  url: string;
  body?: Record<string, any>;
  query?: Record<string, any>;
  headers?: Record<string, any>;
}

const Random = Mock.Random

// 生成权限树数据
const generatePermissionTree = (): Permission[] => {
  return [
    {
      id: 1,
      permissionName: '系统管理',
      permissionCode: 'system',
      description: '系统管理模块',
      pid: 0,
      sortOrder: 1,
      type: 'MENU',
      path: '/system',
      status: 1,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString(),
      children: [
        {
          id: 2,
          permissionName: '用户管理',
          permissionCode: 'system:user',
          description: '用户管理模块',
          pid: 1,
          sortOrder: 1,
          type: 'MENU',
          path: '/system/user',
          status: 1,
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString(),
          children: [
            {
              id: 3,
              permissionName: '查看用户',
              permissionCode: 'system:user:view',
              description: '查看用户列表',
              pid: 2,
              sortOrder: 1,
              type: 'BUTTON',
              path: undefined,
              status: 1,
              createTime: new Date().toISOString(),
              updateTime: new Date().toISOString()
            },
            {
              id: 4,
              permissionName: '新增用户',
              permissionCode: 'system:user:add',
              description: '新增用户',
              pid: 2,
              sortOrder: 2,
              type: 'BUTTON',
              path: undefined,
              status: 1,
              createTime: new Date().toISOString(),
              updateTime: new Date().toISOString()
            }
          ]
        },
        {
          id: 5,
          permissionName: '角色管理',
          permissionCode: 'system:role',
          description: '角色管理模块',
          pid: 1,
          sortOrder: 2,
          type: 'MENU',
          path: '/system/role',
          status: 1,
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString(),
          children: [
            {
              id: 6,
              permissionName: '查看角色',
              permissionCode: 'system:role:view',
              description: '查看角色列表',
              pid: 5,
              sortOrder: 1,
              type: 'BUTTON',
              path: undefined,
              status: 1,
              createTime: new Date().toISOString(),
              updateTime: new Date().toISOString()
            },
            {
              id: 7,
              permissionName: '新增角色',
              permissionCode: 'system:role:add',
              description: '新增角色',
              pid: 5,
              sortOrder: 2,
              type: 'BUTTON',
              path: undefined,
              status: 1,
              createTime: new Date().toISOString(),
              updateTime: new Date().toISOString()
            }
          ]
        }
      ]
    }
  ]
}

// 模拟数据存储
let permissionList: Permission[] = generatePermissionTree()

// 递归查找权限
const findPermission = (id: number, list: Permission[] = permissionList): Permission | null => {
  for (const item of list) {
    if (item.id === id) {
      return item
    }
    if (item.children) {
      const found = findPermission(id, item.children)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 递归删除权限
const deletePermissionById = (id: number, list: Permission[] = permissionList): boolean => {
  for (let i = 0; i < list.length; i++) {
    if (list[i].id === id) {
      list.splice(i, 1)
      return true
    }
    if (list[i].children) {
      if (deletePermissionById(id, list[i].children)) {
        return true
      }
    }
  }
  return false
}

// 验证token和权限
const validateTokenAndPermission = <T>(headers: Record<string, any> | undefined): ApiResult<T> | null => {
  // 检查token是否存在
  if (!headers?.authorization) {
    return {
      code: 401,
      message: '未登录或token已过期',
      data: null
    }
  }

  // 检查token是否有效
  const token = headers.authorization.replace('Bearer ', '')
  if (token === 'mock-token-expired-12345') {
    return {
      code: 401,
      message: 'token已过期',
      data: null
    }
  }

  // 检查是否有权限
  if (token === 'mock-token-test-12345') {
    return {
      code: 403,
      message: '无权限访问',
      data: null
    }
  }

  return null
}

export default [
  // 获取权限树
  {
    url: '/api/permission/tree',
    method: 'get',
    response: (): ApiResult<Permission[]> => {
      try {
        const data = generatePermissionTree()
        return {
          code: 200,
          message: 'success',
          data: data as unknown as Permission[]
        }
      } catch (error) {
        console.error('生成权限树错误:', error)
        return {
          code: 500,
          message: '获取权限树失败',
          data: null
        }
      }
    }
  },

  // 创建权限
  {
    url: '/api/permission',
    method: 'post',
    response: ({ body }: RequestParams): ApiResult<Permission | null> => {
      try {
        console.log('创建权限, 请求数据:', body)
        
        // 参数验证
        if (!body?.permissionName || !body?.permissionCode || !body?.type) {
          return {
            code: 400,
            message: '缺少必要参数',
            data: null
          }
        }

        // 检查权限编码是否存在
        const exists = findPermission(body.id)
        if (exists) {
          return {
            code: 400,
            message: '权限编码已存在',
            data: null
          }
        }

        const permission: Permission = {
          id: Random.integer(100, 999),
          permissionName: body.permissionName,
          permissionCode: body.permissionCode,
          description: body.description || null,
          pid: body.pid || 0,
          sortOrder: body.sortOrder || 0,
          type: body.type,
          path: body.path || null,
          status: body.status || 1,
          createTime: Random.datetime(),
          updateTime: Random.datetime()
        }

        // 添加到对应的父节点
        if (permission.pid) {
          const parent = findPermission(permission.pid)
          if (parent) {
            parent.children = parent.children || []
            parent.children.push(permission)
          }
        } else {
          permissionList.push(permission)
        }

        return {
          code: 200,
          message: 'success',
          data: permission
        }
      } catch (error) {
        console.error('创建权限错误:', error)
        return {
          code: 500,
          message: '创建权限失败',
          data: null
        }
      }
    }
  },

  // 更新权限
  {
    url: '/api/permission/:id',
    method: 'put',
    response: ({ body }: RequestParams): ApiResult<Permission | null> => {
      try {
        console.log('更新权限, 请求数据:', body)
        
        if (!body?.id) {
          return {
            code: 400,
            message: '缺少权限ID',
            data: null
          }
        }

        const permission = findPermission(body.id)
        if (!permission) {
          return {
            code: 404,
            message: '权限不存在',
            data: null
          }
        }

        // 更新权限信息
        Object.assign(permission, {
          ...body,
          updateTime: Random.datetime()
        })

        return {
          code: 200,
          message: 'success',
          data: permission
        }
      } catch (error) {
        console.error('更新权限错误:', error)
        return {
          code: 500,
          message: '更新权限失败',
          data: null
        }
      }
    }
  },

  // 删除权限
  {
    url: /\/api\/permission\/(\d+)/,
    method: 'delete',
    response: ({ url }: RequestParams): ApiResult<null> => {
      try {
        console.log('删除权限, URL:', url)
        const id = url.match(/\/api\/permission\/(\d+)/)?.[1]
        
        if (!id) {
          return {
            code: 400,
            message: '缺少权限ID',
            data: null
          }
        }

        const permission = findPermission(parseInt(id))
        if (!permission) {
          return {
            code: 404,
            message: '权限不存在',
            data: null
          }
        }

        // 检查是否有子权限
        if (permission.children?.length) {
          return {
            code: 400,
            message: '该权限下有子权限，不能删除',
            data: null
          }
        }

        // 删除权限
        if (deletePermissionById(parseInt(id))) {
          return {
            code: 200,
            message: 'success',
            data: null
          }
        } else {
          return {
            code: 500,
            message: '删除权限失败',
            data: null
          }
        }
      } catch (error) {
        console.error('删除权限错误:', error)
        return {
          code: 500,
          message: '删除权限失败',
          data: null
        }
      }
    }
  },

  // 批量删除权限
  {
    url: '/api/permission/batch',
    method: 'delete',
    response: ({ body }: RequestParams): ApiResult<null> => {
      try {
        console.log('批量删除权限, 请求数据:', body)
        const ids = body as number[]
        
        if (!ids || !ids.length) {
          return {
            code: 400,
            message: '请选择要删除的权限',
            data: null
          }
        }

        // 检查是否有子权限
        for (const id of ids) {
          const permission = findPermission(id)
          if (permission?.children?.length) {
            return {
              code: 400,
              message: `权限[${permission.permissionName}]下有子权限，不能删除`,
              data: null
            }
          }
        }

        // 批量删除
        let success = true
        for (const id of ids) {
          if (!deletePermissionById(id)) {
            success = false
            break
          }
        }

        if (success) {
          return {
            code: 200,
            message: 'success',
            data: null
          }
        } else {
          return {
            code: 500,
            message: '批量删除失败',
            data: null
          }
        }
      } catch (error) {
        console.error('批量删除权限错误:', error)
        return {
          code: 500,
          message: '批量删除权限失败',
          data: null
        }
      }
    }
  },

  // 更新权限状态
  {
    url: /\/api\/permission\/(\d+)\/status/,
    method: 'put',
    response: ({ url, body }: RequestParams): ApiResult<null> => {
      try {
        console.log('更新权限状态, URL:', url, '请求数据:', body)
        const id = url.match(/\/api\/permission\/(\d+)\/status/)?.[1]
        const { status } = body || {}
        
        if (!id) {
          return {
            code: 400,
            message: '缺少权限ID',
            data: null
          }
        }

        if (status !== 0 && status !== 1) {
          return {
            code: 400,
            message: '无效的状态值',
            data: null
          }
        }

        const permission = findPermission(parseInt(id))
        if (!permission) {
          return {
            code: 404,
            message: '权限不存在',
            data: null
          }
        }

        // 更新状态
        permission.status = status
        permission.updateTime = Random.datetime()

        return {
          code: 200,
          message: 'success',
          data: null
        }
      } catch (error) {
        console.error('更新权限状态错误:', error)
        return {
          code: 500,
          message: '更新权限状态失败',
          data: null
        }
      }
    }
  }
] as MockMethod[] 