// src/api/auth.ts
import request from "@/utils/request";
import { logger } from "@/utils/logger";
import { useAuthStore } from "@/store/auth";
import { safeRedirect } from "@/router/navigation";

/**
 * 用户角色
 */
export type UserRole = "user" | "doctor";

/**
 * 登录凭证类型
 */
export type LoginCredentials = {
  identity: string; // 身份证号
  realname: string; // 真实姓名
  password: string; // 密码
};

/**
 * 获取用户信息响应类型
 */
export interface UserInfoResponceData {
  identity: string;
  realname: string;
  gendercode: string; // M 或者
  birthdate: string; // 时间 2004-02-28
}
/**
 * 根据token获取用户信息
 * 无需任何参数
 */
const getUserInfo = async () => {
  try {
    const authStore = useAuthStore();
    logger.debug("开始获取用户信息", authStore.token);
    const { data } = await request.get<UserInfoResponceData>("/patient/info");
    // 存储用户数据
    logger.info("获取用户信息成功", data);

    authStore.account.identity = data.identity;
    authStore.account.realname = data.realname;
    authStore.account.gendercode = data.gendercode;
    authStore.account.birthdate = data.birthdate;
    authStore.account.role = "user";
  } catch (error) {
    logger.error(error);
  }
};

/**
 * 用户登录服务
 */
export const LoginRequrest = async (credentials: LoginCredentials) => {
  try {
    // 获取后端响应数据
    const { data } = await request.post<string>("/patient/login", {
      identity: credentials.identity,
      realname: credentials.realname,
      password: credentials.password,
    });

    logger.info("登陆成功，获取的token", data);

    // 存储token
    const authStore = useAuthStore();
    authStore.token = data;
    // 获取用户信息
    await getUserInfo();
  } catch (error) {
    console.log;
  }
};

/**
 * 用户注册需要的信息
 */
export type RegisterUser = {
  identity: string; // 身份（user/doctor）
  realname: string; // 真实姓名
  gendercode: string; // 性别
  password: string;
  confirmPassword: string;
  role: UserRole;
};

/**
 * 用户注册响应数据
 */
export interface RegisterResponseData {
  data: string; // 返回注册成功信息 需要跳转到登陆界面
}

/**
 * 用户注册服务
 */
export const RegisterRequest = async (registerUser: RegisterUser) => {
  try {
    // 获取后端响应数据
    const { data } = await request.post<RegisterResponseData>(
      "/patient/register",
      {
        identity: registerUser.identity,
        realname: registerUser.realname,
        password: registerUser.password,
        gendercode: registerUser.gendercode,
      }
    );

    logger.info("注册成功", data);
    return data;
  } catch (error) {
    throw error;
  }
};

/**
 * 医生登录凭证类型
 */
export type DoctorLoginCredentials = {
  institutionCode: string; // 医疗机构编号
  institutionName: string; // 医疗机构名称
};

/**
 * 医生登录服务
 */
export const DoctorLoginRequrest = async (
  credentials: DoctorLoginCredentials
) => {
  try {
    // 获取后端响应数据
    const { data } = await request.post<string>("/doctor/login", {
      institutionCode: credentials.institutionCode,
      institutionName: credentials.institutionName,
    });

    logger.info("登陆成功，获取的token", data);

    // 存储token
    const authStore = useAuthStore();
    authStore.token = data;
    authStore.institutionName = credentials.institutionName;
    authStore.institutionCode = credentials.institutionCode;
    authStore.account.role = "doctor";
  } catch (error) {}
};
