import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

// 公共路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'dashboard', keepAlive: true }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    name: 'System',
    meta: { title: '系统管理', icon: 'setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'user', keepAlive: true }
      },
      {
        path: 'user/detail/:id',
        name: 'UserDetail',
        component: () => import('@/views/system/user/UserDetail.vue'),
        meta: { title: '用户详情', hidden: true }
      },
      {
        path: 'user/edit/:id',
        name: 'UserEdit',
        component: () => import('@/views/system/user/UserEdit.vue'),
        meta: { title: '编辑用户', hidden: true }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'peoples', keepAlive: true }
      },
      {
        path: 'role/detail/:id',
        name: 'RoleDetail',
        component: () => import('@/views/system/role/RoleDetail.vue'),
        meta: { title: '角色详情', hidden: true }
      },
      {
        path: 'role/edit/:id',
        name: 'RoleEdit',
        component: () => import('@/views/system/role/RoleEdit.vue'),
        meta: { title: '编辑角色', hidden: true }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'tree-table', keepAlive: true }
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: { title: '权限管理', icon: 'lock', keepAlive: true }
      }
    ]
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: { hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 预加载组件
const preloadComponents = () => {
  constantRoutes.forEach(route => {
    if (typeof route.component === 'function') {
      route.component()
    }
    if (route.children) {
      route.children.forEach(child => {
        if (typeof child.component === 'function') {
          child.component()
        }
      })
    }
  })
}

// 白名单路由
const whiteList = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const hasToken = userStore.token

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!userStore.userInfo) {
        try {
          await userStore.getUserInfo()
          // 预加载组件
          preloadComponents()
          next({ ...to, replace: true })
        } catch (error) {
          await userStore.resetToken()
          next(`/login?redirect=${to.path}`)
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router
