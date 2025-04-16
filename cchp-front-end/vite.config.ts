import { defineConfig } from "vite";
import { fileURLToPath } from "node:url";
import path from "node:path";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  plugins: [
    // 开发环境注入mock
    process.env.NODE_ENV === "development" && {
      name: "msw-loader",
      configureServer(server) {
        import("@/src/mocks/browser").then(({ worker }) => {
          worker.start({ onUnhandledRequest: "bypass" });
        });
      },
    },
  ],
});
