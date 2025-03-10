import type { FormRules, FormItemRule } from 'element-plus'
import type { UserInfo } from '@/types/user'
import { checkUsername } from '@/api/system/user'

interface ValidateCallback {
  (error?: Error | string): void
}

// 用户名验证规则
const usernameRules: FormItemRule[] = [
  { required: true, message: '请输入用户名', trigger: 'blur' },
  { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
  { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '以字母开头，只能包含字母、数字和下划线', trigger: 'blur' },
  {
    validator: (rule: any, value: string, callback: ValidateCallback) => {
      if (value && value.trim() !== value) {
        callback(new Error('用户名不能包含首尾空格'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  },
  {
    validator: (rule: any, value: string, callback: ValidateCallback) => {
      if (!value) {
        callback()
        return
      }
      checkUsername(value).then(
        (response: { exists: boolean }) => {
          if (response.exists) {
            callback(new Error('该用户名已被使用'))
          } else {
            callback()
          }
        }
      ).catch((error: Error) => {
        callback(error.message || '用户名验证失败')
      })
    },
    trigger: 'blur'
  }
]

// 密码验证规则
const passwordRules: FormItemRule[] = [
  { required: true, message: '请输入密码', trigger: 'blur' },
  { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
  {
    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/,
    message: '密码必须包含大小写字母和数字',
    trigger: 'blur'
  }
]

// 昵称验证规则
const nicknameRules: FormItemRule[] = [
  { required: true, message: '请输入昵称', trigger: 'blur' },
  { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
]

// 邮箱验证规则
const emailRules: FormItemRule[] = [
  { required: true, message: '请输入邮箱', trigger: 'blur' },
  { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  { max: 50, message: '邮箱长度不能超过50个字符', trigger: 'blur' }
]

// 手机号验证规则
const phoneRules: FormItemRule[] = [
  { required: true, message: '请输入手机号', trigger: 'blur' },
  { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
]

// 角色验证规则
const createRoleRules = (isAdmin: boolean): FormItemRule[] => [
  { required: true, message: '请选择角色', trigger: 'change' },
  { type: 'array', min: 1, message: '至少选择一个角色', trigger: 'change' },
  { type: 'array', max: 10, message: '最多选择10个角色', trigger: 'change' },
  {
    validator: (rule: any, value: number[], callback: ValidateCallback) => {
      if (value?.includes(1) && !isAdmin) {
        callback(new Error('无权分配超级管理员角色'))
      } else {
        callback()
      }
    },
    trigger: 'change'
  }
]

// 状态验证规则
const createStatusRules = (formData: Partial<UserInfo>): FormItemRule[] => [
  { required: true, message: '请选择状态', trigger: 'change' },
  {
    validator: (rule: any, value: number, callback: ValidateCallback) => {
      if (formData.id === 1 && value === 0) {
        callback(new Error('不能禁用超级管理员账号'))
      } else {
        callback()
      }
    },
    trigger: 'change'
  }
]

// 创建表单验证规则
export const createFormRules = (formData: Partial<UserInfo>, isAdmin: boolean): FormRules => {
  return {
    username: usernameRules,
    password: formData.id ? [] : passwordRules, // 编辑时不验证密码
    nickname: nicknameRules,
    email: emailRules,
    phone: phoneRules,
    roleIds: createRoleRules(isAdmin),
    status: createStatusRules(formData)
  }
}

// 导出验证工具函数
export default function useUserValidation() {
  return {
    createFormRules
  }
}