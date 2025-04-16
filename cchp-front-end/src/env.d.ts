// src/env.d.ts
/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_BASE_API: string; // 对应 .env 文件中的变量
  readonly VITE_APP_ENV: "development" | "production"; // 环境类型
  // 添加其他自定义环境变量...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
