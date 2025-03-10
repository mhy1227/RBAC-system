/**
 * API响应结果
 */
export interface ApiResult<T> {
  code: number
  message: string
  data: T | null
}

/**
 * 分页查询参数
 */
export interface PageQuery {
  pageNum: number
  pageSize: number
  [key: string]: any
}

/**
 * 分页查询结果
 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
} 