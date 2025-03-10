import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import type { RoleInfo } from '@/types/user'
import { constantRoutes } from '@/router'
import { getPermissionTree } from '@/api/permission'
import { useUserStore } from './user'

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])
  const permissions = ref<any[]>([])
  const dynamicRoutes = ref<RouteRecordRaw[]>([])

  const setRoutes = (newRoutes: RouteRecordRaw[]) => {
    routes.value = constantRoutes.concat(newRoutes)
  }

  const setAddRoutes = (newRoutes: RouteRecordRaw[]) => {
    addRoutes.value = newRoutes
  }

  const setPermissions = (perms: any[]) => {
    permissions.value = perms
  }

  // 获取权限树
  const getPermissions = async () => {
    try {
      const res = await getPermissionTree({})
      setPermissions(res)
      return res
    } catch (error) {
      console.error('获取权限树失败:', error)
      return []
    }
  }

  // 根据角色生成可访问路由
  const generateRoutes = async () => {
    const userStore = useUserStore()
    const roles = userStore.userInfo?.roles || []
    
    let accessedRoutes
    if (roles.includes('admin')) {
      accessedRoutes = constantRoutes
    } else {
      accessedRoutes = filterAsyncRoutes(constantRoutes, roles)
    }
    
    routes.value = constantRoutes.concat(accessedRoutes)
    dynamicRoutes.value = accessedRoutes
    
    return accessedRoutes
  }

  // 过滤路由
  const filterAsyncRoutes = (routes: RouteRecordRaw[], roles: string[]): RouteRecordRaw[] => {
    const res: RouteRecordRaw[] = []
    
    routes.forEach(route => {
      const tmp = { ...route }
      if (hasPermission(roles, tmp)) {
        if (tmp.children) {
          tmp.children = filterAsyncRoutes(tmp.children, roles)
        }
        res.push(tmp)
      }
    })
    
    return res
  }

  // 判断是否有权限
  const hasPermission = (roles: string[], route: RouteRecordRaw): boolean => {
    if (route.meta && route.meta.roles) {
      return roles.some(role => (route.meta?.roles as string[]).includes(role))
    }
    return true
  }

  // 重置状态
  const $reset = () => {
    routes.value = []
    addRoutes.value = []
    permissions.value = []
    dynamicRoutes.value = []
  }

  return {
    routes,
    addRoutes,
    permissions,
    dynamicRoutes,
    setRoutes,
    setAddRoutes,
    setPermissions,
    getPermissions,
    generateRoutes,
    $reset
  }
}, {
  persist: {
    key: 'permission-store',
    storage: localStorage,
    paths: ['routes', 'permissions']
  }
})
