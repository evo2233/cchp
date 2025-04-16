import "axios";

// 扩展 Axios 接口
declare module "axios" {
  interface AxiosResponse<T = any> {
    code?: number;
    message?: string;
    // 其他自定义响应字段
  }

  interface AxiosRequestConfig {
    _retry?: boolean; // 用于标记是否已重试过
  }
}
