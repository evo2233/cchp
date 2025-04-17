import { defineStore } from "pinia";
import { logger } from "@/utils/logger";
import {
  UserRole,
  LoginRequrest,
  LoginCredentials,
  RegisterRequest,
  RegisterUser,
} from "@/api/auth";
import { showSuccess } from "@/utils/message";
import { safeRedirect } from "@/router/navigation";

// 账号
interface Account {
  id: string; // id
  identity: string; // 身份证号
  realname: string; // 真实姓名
  gendercode: string; // 性别
  birthdate: string; // 出生日期
  role: UserRole; // 角色
}
// 身份认证状态
interface AuthState {
  account: Account | null;
  token: string | null;
  isLoading: boolean;
  error: string | null;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    account: {
      // 初始化空对象
      id: "",
      identity: "",
      realname: "",
      gendercode: "",
      birthdate: "",
      role: "user",
    },
    token: null, // 有token就是身份认证成功
    isLoading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token, // 判断是否已经认证
    currentAccount: (state) => state.account, // 获取当前的用户信息
    accountName: (state) => state.account?.realname || "未登录", // 获取当前用户的姓名
    accountRole: (state) => state.account?.role || "未知身份", // 获取当前用户的角色
  },

  actions: {
    // 通用登陆函数
    async login(credentials: LoginCredentials) {
      try {
        this.isLoading = true;
        this.error = null;

        await LoginRequrest(credentials); // 向后端发送请求

        showSuccess("登陆成功");
        safeRedirect("/patient");
      } catch (error) {
        this.error = error instanceof Error ? error.message : "登录失败";
        logger.error("登录失败", {
          identity: credentials.identity,
          realname: credentials.realname,
          error: this.error,
        });
        throw error;
      } finally {
        this.isLoading = false;
      }
    },
    // 注册函数
    async register(newUser: RegisterUser) {
      try {
        await RegisterRequest(newUser);
        showSuccess("注册成功");
        safeRedirect("/login");
      } catch (error) {
        showSuccess("注册失败!");
      }
    },
    // 登出
    async logout() {
      if (this.account) {
        logger.info("用户登出", {
          id: this.account.id,
          realname: this.account.realname,
          role: this.account.role,
        });
      }
      this.$reset(); // 重置状态
    },
    // 检查登陆状态
    checkAuth() {
      return !!this.token;
    },
  },
});
