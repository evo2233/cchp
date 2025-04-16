<template>
  <!-- 注册/登录区域 -->
  <section class="registration-section bg-color-3">
    <div class="pattern">
      <div class="pattern-1"
           style="background-image: url(assets/images/shape/shape-85.png)"></div>
      <div class="pattern-2"
           style="background-image: url(assets/images/shape/shape-86.png)"></div>
    </div>
    <div class="auto-container">
      <div class="inner-box">
        <div class="content-box">
          <div class="title-box">
            <h3>Login as Doctor</h3>
            <a href="register-page.html">Not a Doctor?</a>
          </div>
          <!-- 登陆表单 -->
          <div class="inner">
            <!-- 使用handleLogin函数处理登陆登录事件 -->
            <form @submit.prevent="handleLogin"
                  method="post"
                  class="registration-form">
              <div class="row clearfix">
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Identity</label>
                  <input type="identity"
                         v-model="form.identity"
                         placeholder="Enter your ID"
                         required />
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Realname</label>
                  <input type="realname"
                         v-model="form.realname"
                         placeholder="Enter your realname"
                         required />
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Password</label>
                  <input type="password"
                         v-model="form.password"
                         placeholder="Your password"
                         required />
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <div class="forgot-passowrd clearfix">
                    <a href="login.html">Forget Password?</a>
                  </div>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group message-btn">
                  <button type="submit"
                          class="theme-btn-one">
                    Login Now<i class="icon-Arrow-Right"></i>
                  </button>
                </div>
              </div>
            </form>
            <div class="text"><span>or</span></div>
            <ul class="social-links clearfix">
              <li><a href="register-page.html">Login with Facebook</a></li>
              <li>
                <a href="register-page.html">Login with Google Plus</a>
              </li>
            </ul>
            <div class="login-now">
              <p>
                Don’t have an account?
                <a href="#"
                   @click.prevent="goToRegister">Register Now</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <!-- registration-section end -->
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { logger } from '@/utils/logger'
import { useAuthStore } from '@/store/auth'
import { navigateTo, safeRedirect } from '@/router/navigation'

// 表单数据
const form = reactive({
  identity: '',
  realname: '',
  password: '',
})

// 登录状态和错误信息
const isLoading = ref(false)
const errorMessage = ref('')

// 使用auth store
const authStore = useAuthStore()

const handleLogin = async () => {
  if (!form.identity || !form.realname || !form.password) {
    errorMessage.value = '登陆信息不全'
    logger.error(errorMessage.value)
    return
  }

  try {
    logger.info('开始登陆', form)
    isLoading.value = true
    errorMessage.value = ''

    // 调用auth store的登录方法
    await authStore.login({
      identity: form.identity,
      realname: form.realname,
      password: form.password,
    })

    safeRedirect('/patient')
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请重试'
    logger.error('登录错误:', error)
  } finally {
    isLoading.value = false
  }
}

function goToRegister() {
  safeRedirect('/register')
}
</script>
