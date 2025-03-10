import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo, LoginParams } from '@/types/user'
import { getItem, setItem, removeItem } from '@/utils/storage'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth'
import { usePermissionStore } from './permission'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getItem('token') || '')
  const userInfo = ref<UserInfo | null>(getItem('userInfo'))

  const setToken = (value: string) => {
    token.value = value
    setItem('token', value)
  }

  const setUserInfo = (value: UserInfo) => {
    userInfo.value = value
    setItem('userInfo', value)
  }

  const resetToken = () => {
    token.value = ''
    userInfo.value = null
    removeItem('token')
    removeItem('userInfo')
    // 清除权限相关数据
    const permissionStore = usePermissionStore()
    permissionStore.$reset()
  }

  const login = async (loginParams: LoginParams) => {
    try {
      const { token: accessToken, user } = await loginApi(loginParams)
      if (!accessToken || !user) {
        throw new Error('登录返回数据格式错误')
      }
      setToken(accessToken)
      setUserInfo(user)
      return user
    } catch (error) {
      resetToken()
      throw error
    }
  }

  const getUserInfo = async () => {
    if (!token.value) {
      throw new Error('Token不存在')
    }
    try {
      const data = await getUserInfoApi()
      if (!data) {
        throw new Error('获取用户信息失败')
      }
      setUserInfo(data)
      return data
    } catch (error) {
      resetToken()
      throw error
    }
  }

  const logout = async () => {
    try {
      await logoutApi()
    } finally {
      resetToken()
    }
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    resetToken,
    login,
    getUserInfo,
    logout
  }
}, {
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'userInfo']
  }
})
