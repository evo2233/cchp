<template>
  <div class="user-profile">
    <h1>用户信息</h1>

    <div v-if="loading">加载中...</div>
    <div v-else-if="error"
         class="error">
      {{ error }}
    </div>
    <div v-else>
      <p>姓名: {{ userData?.name }}</p>
      <p>年龄: {{ userData?.age }}</p>
    </div>

    <button @click="fetchUserData">获取用户数据</button>

    <div class="login-form">
      <h2>登录</h2>
      <input v-model="username"
             placeholder="用户名" />
      <input v-model="password"
             type="password"
             placeholder="密码" />
      <button @click="handleLogin">登录</button>
      <p v-if="loginMessage">{{ loginMessage }}</p>
    </div>
  </div>

</template>

<script lang="ts" setup>
import { ref } from 'vue'
import request from '@/utils/request'
import { TestContext } from 'node:test'
import { logger, devLog } from '@/utils/logger'

interface User {
  name: string
  age: number
}

interface LoginResponse {
  token?: string
}

// 用户数据相关
const userData = ref<User | null>(null)
const loading = ref(false)
const error = ref('')

// 登录相关
const username = ref('')
const password = ref('')
const loginMessage = ref('')

const fetchUserData = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await request.get('/api/user')
    userData.value = response.data
  } catch (err) {
    error.value = err.message || '获取用户数据失败'
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  try {
    logger.info('登录请求', {
      username: username.value,
      password: password.value,
    })

    const response = await request.post<LoginResponse>('/login', {
      username: username.value,
      password: password.value,
    })

    if (response.data.token) {
      loginMessage.value = '登录成功！Token: ' + response.data.token
      // 实际项目中这里会存储token
    }
  } catch (err) {
    loginMessage.value = err.message || '登录失败'
  }
}
</script>

<style scoped>
.user-profile {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}
.error {
  color: red;
}
.login-form {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #eee;
}
input {
  display: block;
  margin: 10px 0;
  padding: 8px;
  width: 100%;
}
button {
  padding: 8px 16px;
  background: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
</style>