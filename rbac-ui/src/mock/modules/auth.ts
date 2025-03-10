import type { MockMethod } from 'vite-plugin-mock'
import Mock from 'mockjs'
import type { ApiResult } from '@/types/api'
import type { LoginResult, UserInfo } from '@/types/user'

// 定义请求参数接口
interface RequestParams {
  url: string;
  body?: Record<string, any>;
  query?: Record<string, any>;
  headers?: Record<string, any>;
}

// 定义测试用的token常量
export const TEST_TOKENS = {
  ADMIN: 'mock-token-admin-12345',        // 管理员token
  TEST_ADMIN: 'mock-token-test-12345',    // 测试管理员token
  EXPIRED: 'mock-token-expired-12345',    // 过期token
  DISABLED: 'mock-token-disabled-12345'   // 禁用用户token
};

const Random = Mock.Random

// 生成默认的管理员用户信息
const generateAdminUser = (): UserInfo => ({
  id: 1,
  username: 'admin',
  nickname: '管理员',
  email: 'admin@example.com',
  phone: '13800138000',
  avatar: Random.image('100x100', Random.color(), 'Admin'),
  status: 1,
  createTime: Random.datetime(),
  updateTime: Random.datetime(),
  lastLoginTime: Random.datetime(),
  loginFailCount: 0,
  lockTime: null,
  roles: [
    {
      id: 1,
      roleName: '超级管理员',
      roleCode: 'SUPER_ADMIN',
      description: '系统超级管理员',
      status: 1,
      createTime: Random.datetime(),
      updateTime: Random.datetime()
    }
  ],
  permissions: ['*:*:*']
})

export default [
  // 用户登录
  {
    url: '/api/auth/login',
    method: 'post',
    response: ({ body }: RequestParams): ApiResult<LoginResult> => {
      const { username, password } = body || {}
      
      // 验证用户名和密码
      if (!username || !password) {
        return {
          code: 400,
          message: '用户名和密码不能为空',
          data: null
        }
      }

      // 检查是否是管理员账号
      if (username === 'admin' && password === '123456') {
        const user = generateAdminUser()
        return {
          code: 200,
          message: 'success',
          data: {
            token: TEST_TOKENS.ADMIN,
            user
          }
        }
      }

      // 检查是否是测试账号
      if (username === 'test_admin' && password === '123456') {
        return {
          code: 200,
          message: 'success',
          data: {
            token: TEST_TOKENS.TEST_ADMIN,
            user: {
              ...generateAdminUser(),
              id: 2,
              username: 'test_admin',
              nickname: '测试管理员',
              roles: [{
                id: 2,
                roleName: '测试管理员',
                roleCode: 'TEST_ADMIN',
                description: '测试管理员角色',
                status: 1,
                createTime: Random.datetime(),
                updateTime: Random.datetime()
              }],
              permissions: ['sys:test:*']
            }
          }
        }
      }

      // 检查是否是禁用账号
      if (username === 'disabled_user') {
        return {
          code: 401,
          message: '用户已被禁用',
          data: null
        }
      }

      // 检查是否是锁定账号
      if (username === 'locked_user') {
        return {
          code: 401,
          message: '用户已被锁定',
          data: null
        }
      }

      // 其他情况返回用户名或密码错误
      return {
        code: 401,
        message: '用户名或密码错误',
        data: null
      }
    }
  },

  // 获取用户信息
  {
    url: '/api/auth/info',
    method: 'get',
    response: ({ headers }: RequestParams): ApiResult<UserInfo> => {
      const token = headers?.authorization?.replace('Bearer ', '');

      // 根据不同token返回不同结果
      switch (token) {
        case TEST_TOKENS.ADMIN:
          return {
            code: 200,
            message: 'success',
            data: generateAdminUser()
          };
        case TEST_TOKENS.TEST_ADMIN:
          return {
            code: 200,
            message: 'success',
            data: {
              ...generateAdminUser(),
              id: 2,
              username: 'test_admin',
              nickname: '测试管理员',
              roles: [{
                id: 2,
                roleName: '测试管理员',
                roleCode: 'TEST_ADMIN',
                description: '测试管理员角色',
                status: 1,
                createTime: Random.datetime(),
                updateTime: Random.datetime()
              }],
              permissions: ['sys:test:*']
            }
          };
        case TEST_TOKENS.EXPIRED:
          return {
            code: 401,
            message: 'token已过期',
            data: null
          };
        case TEST_TOKENS.DISABLED:
          return {
            code: 401,
            message: '用户已被禁用',
            data: null
          };
        default:
          return {
            code: 401,
            message: '未认证',
            data: null
          };
      }
    }
  },

  // 退出登录
  {
    url: '/api/auth/logout',
    method: 'post',
    response: (): ApiResult<null> => {
      return {
        code: 200,
        message: 'success',
        data: null
      }
    }
  }
] as MockMethod[] 