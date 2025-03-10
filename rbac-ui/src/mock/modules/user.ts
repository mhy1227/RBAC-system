import type { MockMethod } from 'vite-plugin-mock'
import Mock from 'mockjs'
import type { PageResult, ApiResult } from '@/types/api'
import type { UserInfo } from '@/types/user'
import { TEST_TOKENS } from './auth'  // 引入测试token常量

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

  // 检查是否有权限
  if (token === 'mock-token-test-12345') {
    return {
      code: 403,
      message: '无权限访问',
      data: null
    } as ApiResult<T>
  }

  return null
}

// 验证参数类型
const validateParams = (params: any): ApiResult<null> | null => {
  if (!params) {
    return {
      code: 400,
      message: '请求参数不能为空',
      data: null
    };
  }

  // 验证用户名
  if ('username' in params) {
    if (!params.username) {
      return {
        code: 400,
        message: '用户名不能为空',
        data: null
      };
    }
    if (typeof params.username !== 'string') {
      return {
        code: 400,
        message: '用户名必须是字符串类型',
        data: null
      };
    }
  }

  // 验证密码
  if ('password' in params) {
    if (!params.password) {
      return {
        code: 400,
        message: '密码不能为空',
        data: null
      };
    }
    if (typeof params.password !== 'string') {
      return {
        code: 400,
        message: '密码必须是字符串类型',
        data: null
      };
    }
  }

  return null; // 验证通过
};

// 生成用户列表数据
const generateUserList = (count: number) => {
  const list: UserInfo[] = []
  for (let i = 0; i < count; i++) {
    list.push({
      id: i + 1,
      username: Random.word(5, 10),
      nickname: Random.cname(),
      email: Random.email(),
      phone: Random.string('number', 11),
      avatar: Random.image('100x100', Random.color(), Random.word(2, 4)),
      status: Random.integer(0, 1),
      createTime: Random.datetime(),
      updateTime: Random.datetime(),
      lastLoginTime: Random.datetime(),
      loginFailCount: Random.integer(0, 5),
      lockTime: null,
      roles: [
        {
          id: Random.integer(1, 3),
          roleName: Random.pick(['超级管理员', '普通管理员', '普通用户']),
          roleCode: Random.pick(['SUPER_ADMIN', 'ADMIN', 'USER']),
          description: Random.sentence(3, 6),
          status: 1,
          createTime: Random.datetime(),
          updateTime: Random.datetime()
        }
      ]
    })
  }
  return list
}

export default [
  // 用户分页查询
  {
    url: '/api/user/page',
    method: 'get',
    response: ({ headers, query }: RequestParams): ApiResult<PageResult<UserInfo>> => {
      // 验证token和权限
      const authError = validateTokenAndPermission<PageResult<UserInfo>>(headers);
      if (authError) return authError;

      const { pageNum = 1, pageSize = 10, username, nickname, status } = query || {}
      const list = generateUserList(100)
      
      // 根据搜索条件过滤
      const filteredList = list.filter(user => {
        const matchUsername = !username || (user.username?.toLowerCase().includes(String(username).toLowerCase()) ?? false)
        const matchNickname = !nickname || (user.nickname?.toLowerCase().includes(String(nickname).toLowerCase()) ?? false)
        const matchStatus = status === undefined || status === '' || user.status === Number(status)
        return matchUsername && matchNickname && matchStatus
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

  // 获取用户详情
  {
    url: /\/api\/user\/(\d+)/,  // 使用正则表达式匹配ID
    method: 'get',
    response: (req: any): ApiResult<UserInfo> => {
      try {
        // 从URL中提取ID
        const id = req.url.match(/\/api\/user\/(\d+)/)[1]
        if (!id) {
          return {
            code: 400,
            message: '缺少用户ID',
            data: null as any
          }
        }
        
      const user = generateUserList(1)[0]
        user.id = parseInt(id)
      return {
        code: 200,
        message: 'success',
        data: user
        }
      } catch (error) {
        console.error('获取用户详情错误:', error)
        return {
          code: 500,
          message: '服务器内部错误',
          data: null as any
        }
      }
    }
  },

  // 新增用户
  {
    url: '/api/user',
    method: 'post',
    response: ({ headers, body }: RequestParams): ApiResult<UserInfo | null> => {
      // 验证token和权限
      const authError = validateTokenAndPermission<UserInfo | null>(headers);
      if (authError) return authError;

      // 验证参数
      const paramError = validateParams(body);
      if (paramError) return paramError;

      // 验证通过，返回成功
      return {
        code: 200,
        message: 'success',
        data: {
          id: Random.integer(1, 100),
          username: body?.username || '',
          nickname: body?.nickname || Random.cname(),
          email: body?.email || Random.email(),
          phone: body?.phone || Random.string('number', 11),
          avatar: body?.avatar || Random.image('100x100', Random.color(), Random.word(2, 4)),
          status: 1,
          createTime: Random.datetime(),
          updateTime: Random.datetime(),
          lastLoginTime: null,
          loginFailCount: 0,
          lockTime: null,
          roles: [
            {
              id: Random.integer(1, 3),
              roleName: Random.pick(['普通用户']),
              roleCode: Random.pick(['USER']),
              description: '新建用户默认角色',
              status: 1,
              createTime: Random.datetime(),
              updateTime: Random.datetime()
            }
          ]
        }
      };
    }
  },

  // 更新用户
  {
    url: '/api/user',
    method: 'put',
    response: (): ApiResult<null> => {
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  },

  // 删除用户
  {
    url: /\/api\/user\/(\d+)/,  // 使用正则表达式匹配ID
    method: 'delete',
    response: (req: any): ApiResult<null> => {
      try {
        // 从URL中提取ID
        const id = req.url.match(/\/api\/user\/(\d+)/)[1]
        if (!id) {
          return {
            code: 400,
            message: '缺少用户ID',
            data: null
          }
        }
        
      return {
        code: 200,
        message: 'success',
        data: null
        }
      } catch (error) {
        console.error('删除用户错误:', error)
        return {
          code: 500,
          message: '服务器内部错误',
          data: null
        }
      }
    }
  },

  // 更新用户状态
  {
    url: /\/api\/user\/(\d+)\/status\/(\d+)/,  // 使用正则表达式匹配ID和状态
    method: 'put',
    response: (req: any): ApiResult<null> => {
      try {
        // 从URL中提取ID和状态
        const matches = req.url.match(/\/api\/user\/(\d+)\/status\/(\d+)/)
        const id = matches[1]
        const status = matches[2]
        
        if (!id || !status) {
          return {
            code: 400,
            message: '缺少必要参数',
            data: null
          }
        }
        
        return {
          code: 200,
          message: 'success',
          data: null
        }
      } catch (error) {
        console.error('更新用户状态错误:', error)
        return {
          code: 500,
          message: '服务器内部错误',
          data: null
        }
      }
    }
  },

  {
    url: '/api/user/batch',
    method: 'delete',
    response: ({ body }: RequestParams): ApiResult<null> => {
      if (!body || !Array.isArray(body)) {
        return {
          code: 400,
          message: '无效的用户ID列表',
          data: null
        }
      }
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  },

  {
    url: '/api/user/check',
    method: 'get',
    response: ({ query }: RequestParams): ApiResult<{ exists: boolean }> => {
      if (!query?.username) {
        return {
          code: 400,
          message: '用户名不能为空',
          data: { exists: false }
        }
      }
      const userList = generateUserList(100)
      const exists = userList.some(user => user.username === query.username)
      return {
        code: 200,
        message: 'success',
        data: { exists }
      }
    }
  }
] as MockMethod[] 