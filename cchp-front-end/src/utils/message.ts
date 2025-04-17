import { ElMessage, ElMessageBox, ElNotification } from "element-plus";
import type { MessageParams, MessageOptions } from "element-plus";

// 定义消息类型
type MessageType = "success" | "warning" | "info" | "error";

/**
 * 显示消息提示
 * @param message 消息内容
 * @param type 消息类型
 * @param options 额外配置选项
 */
export function showMessage(
  message: string,
  type: MessageType = "info",
  options?: MessageOptions
) {
  return ElMessage({
    message,
    type,
    ...options,
  });
}

/**
 * 显示成功消息
 * @param message 消息内容
 * @param options 额外配置选项
 */
export function showSuccess(message: string, options?: MessageOptions) {
  return showMessage(message, "success", options);
}

/**
 * 显示错误消息
 * @param message 消息内容
 * @param options 额外配置选项
 */
export function showError(message: string, options?: MessageOptions) {
  return showMessage(message, "error", options);
}

/**
 * 显示警告消息
 * @param message 消息内容
 * @param options 额外配置选项
 */
export function showWarning(message: string, options?: MessageOptions) {
  return showMessage(message, "warning", options);
}

/**
 * 显示确认对话框
 * @param message 消息内容
 * @param title 标题
 * @param options 额外配置选项
 */
export function showConfirm(message: string, title = "提示", options = {}) {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    ...options,
  });
}

/**
 * 显示通知
 * @param title 标题
 * @param message 消息内容
 * @param type 通知类型
 * @param options 额外配置选项
 */
export function showNotification(
  title: string,
  message: string,
  type: MessageType = "info",
  options = {}
) {
  return ElNotification({
    title,
    message,
    type,
    ...options,
  });
}

// 默认导出常用方法
export default {
  msg: showMessage,
  success: showSuccess,
  error: showError,
  warning: showWarning,
  confirm: showConfirm,
  notify: showNotification,
};
