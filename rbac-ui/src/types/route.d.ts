import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    noCache?: boolean
    breadcrumb?: boolean
    activeMenu?: string
    roles?: string[]
    permissions?: string[]
    type?: 'menu' | 'button'
    parentId?: number
    orderNum?: number
  }
}
