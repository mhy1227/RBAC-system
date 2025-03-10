import router from '@/router'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import type { RouteRecordRaw } from 'vue-router'
import type { UserInfo } from '@/types/user'

NProgress.configure({ showSpinner: false })

// 白名单路由
const whiteList = ['/login', '/404', '/403']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  
  // 获取token
  const hasToken = userStore.token
  
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      // 检查用户信息是否已获取
      const hasUserInfo = userStore.userInfo
      
      try {
        if (!hasUserInfo) {
          // 获取用户信息
          await userStore.getUserInfo()
          // 生成路由
          const accessRoutes = await permissionStore.generateRoutes()
          // 添加路由
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })
          // 重定向到目标路由
          next({ ...to, replace: true })
        } else {
          // 如果路由不存在，跳转到404
          if (to.matched.length === 0) {
            next('/404')
          } else {
            next()
          }
        }
      } catch (error) {
        // 重置token
        await userStore.resetToken()
        next(`/login?redirect=${to.path}`)
        NProgress.done()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})