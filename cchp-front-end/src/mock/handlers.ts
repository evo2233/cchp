import { http, HttpResponse, delay } from "msw";
import type { RequestHandler } from "msw";

// 类型定义
interface User {
  id: number;
  name: string;
  age: number;
  email: string;
}

interface LoginBody {
  username: string;
  password: string;
}

interface ApiResponse<T = any> {
  code: number;
  message?: string;
  data?: T;
}

interface LoginResponse {
  token: string;
  expiresIn: number;
}

// Mock 数据
const mockUser: ApiResponse<User> = {
  code: 200,
  data: {
    id: 1,
    name: "Alice",
    age: 28,
    email: "alice@example.com",
  },
};

// 模拟网络延迟（可选）
const SIMULATE_DELAY = 300;

// 处理不同的响应结构
export const handlers: RequestHandler[] = [
  // 获取用户信息
  http.get("/api/user", async () => {
    await delay(SIMULATE_DELAY); // 模拟网络延迟
    return HttpResponse.json<ApiResponse<User>>(mockUser);
  }),

  // 用户登录
  http.post<never, LoginBody, ApiResponse<LoginResponse>>(
    "/api/login",
    async ({ request }) => {
      const body = await request.json();
      await delay(SIMULATE_DELAY);

      if (body.username === "admin") {
        return HttpResponse.json({
          code: 200,
          data: {
            token: "mock-jwt-token",
            expiresIn: 3600,
          },
        });
      }

      return HttpResponse.json(
        {
          code: 401,
          message: "Invalid credentials",
        },
        { status: 401 }
      );
    }
  ),
];
