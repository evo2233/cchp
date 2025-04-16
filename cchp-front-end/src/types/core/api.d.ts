// 标准API响应格式
export interface ApiResponse<T = any> {
  code: number;
  message?: string;
  data?: T;
  timestamp?: number;
}

// 分页参数
export interface PaginationParams {
  page: number;
  pageSize: number;
}

// 带分页的返回数据
export interface PaginatedData<T> {
  items: T[];
  total: number;
}
