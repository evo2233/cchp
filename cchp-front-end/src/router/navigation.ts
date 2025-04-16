// src/utils/navigation.ts
import { logger } from "@/utils/logger";
import type { RouteLocationRaw, RouteRecordNormalized } from "vue-router";
import router from "@/router";
import { useAuthStore } from "@/store/auth";

/**
 * 导航选项配置接口
 */
interface NavigationOptions {
  /**
   * 是否跳过权限检查
   * @default false
   */
  ignoreAuth?: boolean;

  /**
   * 验证失败时重定向路径
   * @default { name: "Login" }
   */
  redirectOnFail?: RouteLocationRaw;

  /**
   * 是否记录导航日志
   * @default true
   */
  logNavigation?: boolean;

  /**
   * 导航失败时是否显示错误提示
   * @default true
   */
  showError?: boolean;

  /**
   * 自定义错误消息
   */
  errorMessage?: string;
}

/**
 * 安全导航到指定路由
 * @param target 目标路由，可以是路径字符串或路由对象
 * @param options 导航选项配置
 * @returns Promise<boolean> 导航是否成功
 *
 * @example
 * // 基本用法
 * await navigateTo('/dashboard');
 *
 * // 带选项的用法
 * await navigateTo('/admin', {
 *   ignoreAuth: false,
 *   redirectOnFail: '/login',
 *   logNavigation: true
 * });
 */
export const navigateTo = async (
  target: RouteLocationRaw,
  options?: NavigationOptions
): Promise<boolean> => {
  const authStore = useAuthStore();

  // 合并默认选项
  const {
    ignoreAuth = false,
    redirectOnFail = { name: "Login" },
    logNavigation = true,
    showError = true,
    errorMessage,
  } = options || {};

  try {
    // 解析目标路由
    const route =
      typeof target === "string"
        ? router.resolve(target)
        : router.resolve(target);

    // 记录导航开始日志
    if (logNavigation) {
      logger.debug("导航开始", {
        from: router.currentRoute.value.path,
        to: route.path,
        meta: route.meta,
        timestamp: new Date().toISOString(),
      });
    }

    // 执行导航
    await router.push(route);

    // 记录成功日志
    if (logNavigation) {
      logger.info("导航成功", {
        path: route.path,
        meta: route.meta,
      });
    }

    return true;
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : String(error);

    // 记录错误日志
    logger.error("导航失败", {
      target,
      error: errorMsg,
      timestamp: new Date().toISOString(),
    });

    // 显示错误提示（可根据实际UI框架替换alert）
    if (showError) {
      alert(errorMessage || errorMsg);
    }

    // 失败时重定向
    try {
      await router.push(redirectOnFail);
    } catch (redirectError) {
      logger.error("重定向失败", {
        error:
          redirectError instanceof Error
            ? redirectError.message
            : String(redirectError),
      });
    }

    return false;
  }
};

/**
 * 安全重定向函数（简化版）
 * @param to 目标路由
 * @returns Promise<boolean> 重定向是否成功
 *
 * @example
 * // 基本用法
 * await safeRedirect('/fallback');
 */
export const safeRedirect = async (to: RouteLocationRaw): Promise<boolean> => {
  return navigateTo(to, {
    redirectOnFail: { path: "/" },
    logNavigation: true,
    showError: true,
  });
};
