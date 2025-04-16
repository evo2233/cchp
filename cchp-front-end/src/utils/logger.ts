import { saveAs } from "file-saver";
import dayjs from "dayjs";

/**
 * 开发日志记录工具，支持多级别日志、控制台输出和文件保存
 * @class DevLogger
 * @example
 * logger.info("系统启动");
 * logger.warn("缓存即将过期", { key: "user_data", ttl: 10 });
 * logger.error(new Error("API请求失败"));
 */
class DevLogger {
  private logs: string[] = [];
  private readonly MAX_LOG_SIZE = 1000;
  private readonly LOG_DIRECTORY = "logs"; // 日志目录

  constructor() {
    // window.addEventListener("beforeunload", () => this.saveToFile());
  }

  /**
   * 记录普通信息
   * @param {string} message - 日志消息
   * @param {any} [data] - 附加数据（自动转为JSON）
   * @example logger.info("用户登录", { userId: 123 });
   */
  info(message: string, data?: any): void {
    this._log("INFO", message, data);
  }

  /**
   * 记录警告信息（黄色高亮）
   * @param {string} message - 警告消息
   * @param {any} [data] - 附加数据
   * @example logger.warn("低电量", { battery: 15 });
   */
  warn(message: string, data?: any): void {
    this._log("WARN", message, data, "color: #FFA500");
  }

  /**
   * 记录错误信息（红色高亮）
   * @param {Error|string} error - 错误对象或消息
   * @param {any} [data] - 附加数据
   * @example logger.error(new Error("加载失败"));
   */
  error(error: Error | string, data?: any): void {
    const message =
      error instanceof Error ? error.stack || error.message : error;
    this._log("ERROR", message, data, "color: #FF0000");
  }

  /**
   * 记录调试信息（灰色文字，仅在开发环境显示）
   * @param {string} message - 调试消息
   * @param {any} [data] - 附加数据
   * @example logger.debug("状态更新", { prev: 1, next: 2 });
   */
  debug(message: string, data?: any): void {
    if (import.meta.env.DEV) {
      this._log("DEBUG", message, data, "color: #888");
    }
  }

  /**
   * 内部统一日志处理方法
   * @private
   */
  private _log(
    level: string,
    message: string,
    data?: any,
    consoleStyle?: string
  ): void {
    const timestamp = dayjs().format("YYYY-MM-DD HH:mm:ss");
    const logEntry = `[${timestamp}] [${level}] ${message}${
      data ? "\n" + JSON.stringify(data, null, 2) : ""
    }`;

    // 控制台输出（带样式）
    if (consoleStyle) {
      console.log(`%c${logEntry}`, consoleStyle);
    } else {
      console.log(logEntry);
    }

    // 存储日志
    this.logs.push(logEntry);
    if (this.logs.length > this.MAX_LOG_SIZE) {
      this.logs.shift();
    }
  }

  /**
   * 保存日志到文件
   * @param {string} [filename] - 自定义文件名（默认按时间生成）
   * @example logger.saveToFile("error_log_20231001.txt");
   */
  saveToFile(
    filename: string = `dev_log_${dayjs().format("YYYYMMDD_HHmmss")}.txt`
  ): void {
    const blob = new Blob([this.logs.join("\n\n")], {
      type: "text/plain;charset=utf-8",
    });
    saveAs(blob, filename);
    this.logs = [];
  }
}

// 单例导出
export const logger = new DevLogger();

/**
 * 快捷日志方法集合
 * @namespace devLog
 * @example
 * devLog.info("快捷信息");
 * devLog.error(new Error("测试错误"));
 */
export const devLog = {
  /** @see DevLogger.info */
  info: (message: string, data?: any) => logger.info(message, data),
  /** @see DevLogger.warn */
  warn: (message: string, data?: any) => logger.warn(message, data),
  /** @see DevLogger.error */
  error: (error: Error | string, data?: any) => logger.error(error, data),
  /** @see DevLogger.debug */
  debug: (message: string, data?: any) => logger.debug(message, data),
  /** @see DevLogger.saveToFile */
  save: (filename?: string) => logger.saveToFile(filename),
};
