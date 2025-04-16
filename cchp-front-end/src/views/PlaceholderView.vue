<!-- src/views/PlaceholderView.vue -->
<template>
  <div class="placeholder-container">
    <h2>{{ $route.name }}</h2>
    <p>当前路径: <code>{{ $route.path }}</code></p>
    <div class="mock-ui">
      <div v-for="i in 3"
           :key="i"
           class="mock-card">
        <p>这是 {{ $route.meta.title || '占位' }} 页面的模拟内容区块 {{ i }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.placeholder-container {
  padding: 2rem;
  text-align: center;
}
.mock-ui {
  display: grid;
  gap: 1rem;
  margin-top: 2rem;
}
.mock-card {
  padding: 2rem;
  background: #f5f5f5;
  border-radius: 8px;
}
</style>