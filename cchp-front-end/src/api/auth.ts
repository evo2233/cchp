// src/api/auth.ts
import request from "@/utils/request";
import { logger } from "@/utils/logger";

/**
 * 登录凭证类型
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * 登录响应类型（简化版）
 */
export interface LoginResponse {
  success: boolean;
  userInfo: {
    id: string;
    name: string;
    role: "patient" | "doctor";
  };
}

/**
 * 用户登录服务
 */
export const login = async (
  credentials: LoginCredentials
): Promise<LoginResponse> => {
  try {
    const { data } = await request.post<LoginResponse>("/auth/login", {
      username: credentials.username,
      password: credentials.password,
    });

    logger.info("登录成功", {
      userId: data.userInfo.id,
      role: data.userInfo.role,
    });

    return data;
  } catch (error) {
    const errorMsg = error.response?.data?.message || "登录服务不可用";
    logger.error("登录失败", {
      username: credentials.username,
      error: errorMsg,
    });
    throw new Error(errorMsg);
  }
};
