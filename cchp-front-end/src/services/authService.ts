import request from "@/utils/request.js";
import { AxiosResponse } from "axios";

// 类型定义
interface LoginParams {
  username: string;
  password: string;
  rememberMe?: boolean;
}

interface RegisterParams extends LoginParams {
  email: string;
  confirmPassword: string;
}

interface AuthResponse {
  token: string;
  refreshToken?: string;
  userId: string;
  username: string;
}

interface ErrorResponse {
  code: number;
  message: string;
}

// 认证服务
const authService = {
  /**
   * 用户登录
   * @param params 登录参数
   * @returns Promise<AuthResponse>
   */
  async login(params: LoginParams): Promise<AuthResponse> {
    try {
      const response: AxiosResponse<AuthResponse> = await request.post(
        "/auth/login",
        params
      );

      // 存储token
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        if (params.rememberMe && response.data.refreshToken) {
          localStorage.setItem("refreshToken", response.data.refreshToken);
        }
      }

      return response.data;
    } catch (error) {
      console.error("登录失败:", error);
      throw error;
    }
  },

  /**
   * 用户注册
   * @param params 注册参数
   * @returns Promise<AuthResponse>
   */
  async register(params: RegisterParams): Promise<AuthResponse> {
    try {
      const response: AxiosResponse<AuthResponse> = await request.post(
        "/auth/register",
        params
      );
      return response.data;
    } catch (error) {
      console.error("注册失败:", error);
      throw error;
    }
  },

  /**
   * 用户登出
   * @returns Promise<void>
   */
  async logout(): Promise<void> {
    try {
      await request.post("/auth/logout");
    } finally {
      // 无论登出请求是否成功，都清除本地token
      localStorage.removeItem("token");
      localStorage.removeItem("refreshToken");
    }
  },

  /**
   * 刷新访问令牌
   * @returns Promise<AuthResponse>
   */
  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) {
      throw new Error("没有可用的刷新令牌");
    }

    try {
      const response: AxiosResponse<AuthResponse> = await request.post(
        "/auth/refresh-token",
        { refreshToken }
      );

      // 存储新的token
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
      }

      return response.data;
    } catch (error) {
      console.error("刷新令牌失败:", error);
      localStorage.removeItem("token");
      localStorage.removeItem("refreshToken");
      throw error;
    }
  },

  /**
   * 获取当前用户信息
   * @returns Promise<AuthResponse>
   */
  async getCurrentUser(): Promise<AuthResponse> {
    try {
      const response: AxiosResponse<AuthResponse> = await request.get(
        "/auth/me"
      );
      return response.data;
    } catch (error) {
      console.error("获取用户信息失败:", error);
      throw error;
    }
  },

  /**
   * 检查认证状态
   * @returns boolean
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem("token");
  },
};
