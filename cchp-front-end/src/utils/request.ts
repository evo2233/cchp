import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
  type AxiosRequestHeaders, // 请求头
} from "axios";
import { devLog, logger } from "./logger"; // 引入日志工具
import { useAuthStore } from "@/store/auth";
import { showError, showMessage } from "./message";

// 定义服务实例
const service: AxiosInstance = axios.create({
  baseURL: "http://113.44.76.249:8080",
  timeout: 5000, // 超时
  withCredentials: true, // ✅ 允许跨域请求时携带 cookie 等认证信息
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
    const authStore = useAuthStore();

    // 添加请求头
    if (authStore.token) {
      const token = authStore.token.trim();
      console.log("添加请求头Token:", token);
      config.headers = config.headers || ({} as AxiosRequestHeaders);
      config.headers["Authorization"] = `Bearer ${token}`;
      console.log("最终请求头:", config.headers); // 检查是否被覆盖
    }

    return config;
  },
  (error) => {
    // Do something with request error
    logger.error("请求拦截器拦截错误", error);
    return Promise.reject(error);
  }
);

// 通用响应拦截器
service.interceptors.response.use(
  (response) => {
    console.log("拦截器进入 - response:", response.data);
    const res = response.data;

    if (res.code !== undefined) {
      if (res.code === "0") {
        console.log("获取请求数据成功(code=0)", res);
        return res; // 将请求数据全部返回
      } else {
        const errorMsg = res.message || "请求错误";
        showError(errorMsg);
        logger.warn(`Business Error: ${errorMsg}`, {
          url: response.config.url,
          code: res.code,
        });
        return Promise.reject(new Error(errorMsg));
      }
    }

    // 没有code字段的直接返回
    console.log("获取请求数据成功(code=None)", res);
    return res;
  },
  (error) => {
    console.log("拦截器进入 - error:", error);
    const errorMessage = error.response.data.message;
    showError(errorMessage);

    return Promise.reject(error); // 继续传递错误
  }
);

export default service;
export const request = service;
