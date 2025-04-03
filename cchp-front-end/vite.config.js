import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // path module to resolve paths

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  }
})
