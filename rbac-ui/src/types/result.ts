/**
 * 通用返回结果接口
 */
export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * 分页查询结果接口
 */
export interface PageResult<T> {
  total: number;
  records: T[];
} 