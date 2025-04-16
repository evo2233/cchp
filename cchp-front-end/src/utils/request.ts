import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from "axios";
import { devLog, logger } from "./logger"; // 引入日志工具

// 定义服务实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || "/api",
  timeout: 5000,
});

// 开发环境特殊配置
if (import.meta.env.VITE_APP_ENV === "development") {
  // 请求日志
  service.interceptors.request.use((config) => {
    logger.info(`HTTP Request: ${config.method?.toUpperCase()} ${config.url}`, {
      baseURL: config.baseURL,
      params: config.params,
      data: config.data,
      headers: config.headers,
    });
    return config;
  });

  // 响应日志
  service.interceptors.response.use(
    (response) => {
      logger.info(
        `HTTP Response: ${response.config.method?.toUpperCase()} ${
          response.config.url
        }`,
        {
          status: response.status,
          data: response.data,
        }
      );
      return response;
    },
    (error) => {
      logger.error(
        `HTTP Error: ${error.config?.method?.toUpperCase()} ${
          error.config?.url
        }`,
        {
          status: error.response?.status,
          message: error.message,
          stack: error.stack,
          responseData: error.response?.data,
        }
      );
      return Promise.reject(error);
    }
  );
}

// 通用请求拦截器
service.interceptors.request.use(
  (config) => {
    // TODO: 添加认证token
    return config;
  },
  (error) => {
    // Do something with request error
    logger.error("Request Interceptor Error", error);
    return Promise.reject(error);
  }
);

// 通用响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;

    // 处理不同的响应结构
    if (res.code !== undefined) {
      // 有明确code字段的响应
      if (res.code === 200) {
        return res.data !== undefined ? res.data : res;
      } else {
        const errorMsg = res.message || "请求错误";
        logger.warn(`Business Error: ${errorMsg}`, {
          url: response.config.url,
          code: res.code,
        });
        return Promise.reject(new Error(errorMsg));
      }
    }

    // 没有code字段的直接返回
    return res;
  },
  (error) => {
    if (!import.meta.env.DEV) {
      logger.error("Network Error", {
        url: error.config?.url,
        message: error.message,
      });
    }

    return Promise.reject(error);
  }
);

export default service;
export const request = service;
