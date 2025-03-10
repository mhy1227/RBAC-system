// 通用响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页请求参数
export interface PageQuery {
  pageNum: number
  pageSize: number
  [key: string]: unknown
}

// 分页响应数据
export interface PageResult<T> {
  total: number
  list: T[]
}
