import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // path module to resolve paths

// https://vite.dev/config/
export default defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      "/api": {
        target: "http://113.44.76.249:8080",
        changeOrigin: true
      }
    }
  },
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})
