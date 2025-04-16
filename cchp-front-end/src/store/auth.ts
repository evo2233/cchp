import { defineStore } from "pinia";
import { logger } from "@/utils/logger";

// 通用账户接口
type UserRole = "user" | "doctor";

// 账号
interface Account {
  id: string; // id
  identity: string; // 身份证号
  realname: string; // 真实姓名
  role: UserRole; // 角色
}
// 身份认证状态
interface AuthState {
  account: Account | null;
  token: string | null;
  isLoading: boolean;
  error: string | null;
}
// 登陆需要的信息
export type LoginCredentials = {
  identity: string; // 身份证号
  realname: string; // 真实姓名
  password: string; // 密码
};

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    account: null,
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

        logger.info("用户登录尝试", { 登陆信息: credentials });

        // 模拟API请求延迟
        await new Promise((resolve) => setTimeout(resolve, 1000));

        // 模拟账号数据库
        const mockAccounts: (Account & {
          password: string;
          identity: string;
        })[] = [
          {
            identity: "230623200401171238",
            password: "123456",
            id: "user-001",
            realname: "李闯",
            role: "user",
          },
          {
            identity: "310101198805053333",
            password: "user123",
            id: "user-002",
            realname: "普通用户",
            role: "user",
          },
          {
            identity: "110101197201012222",
            password: "doctor123",
            id: "doc-001",
            realname: "张医生",
            role: "doctor",
          },
          {
            identity: "310101197505052222",
            password: "123456",
            id: "doc-002",
            realname: "李医生",
            role: "doctor",
          },
        ];

        // TODO: 这里需要调用API去获取数据
        const found = mockAccounts.find(
          (a) =>
            a.realname === credentials.realname &&
            a.identity == credentials.identity &&
            a.password === credentials.password
        );

        if (found) {
          const { password, ...account } = found;
          this.account = account; // 存储账号信息
          this.token = `${account.role}-token-${account.id}`; // 存储身份认证的Token

          logger.info("登录成功", {
            id: account.id,
            indetity: account.identity,
            realname: account.realname,
            role: account.role,
          });
          return true;
        }

        throw new Error("邮箱或密码错误");
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
    async register(newUser: {
      identity: string; // 身份（user/doctor）
      realname: string; // 真实姓名
      gendercode: string; // 性别
      birthdate: string; // 出生日期
      password: string;
      confirmPassword: string;
      role: UserRole;
    }) {
      // 模拟API延迟
      await new Promise((resolve) => setTimeout(resolve, 1000));

      // 创建模拟账户对象
      const account: Account = {
        id: `${newUser.identity}-${Date.now()}`,
        identity: newUser.identity,
        realname: newUser.realname,
        role: newUser.role,
      };

      this.account = account;
      this.token = `${account.role}-token-${account.id}`;

      logger.info("注册成功", {
        id: account.id,
        identity: account.identity,
        realname: account.realname,
        gendercode: newUser.gendercode,
        birthdate: newUser.birthdate,
        role: account.role,
      });
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
