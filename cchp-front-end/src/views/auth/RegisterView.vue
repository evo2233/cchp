<template>
  <!-- registration-section -->
  <section class="registration-section bg-color-3">
    <div class="pattern">
      <div class="pattern-1"
           style="background-image: url(assets/images/shape/shape-85.png);"></div>
      <div class="pattern-2"
           style="background-image: url(assets/images/shape/shape-86.png);"></div>
    </div>
    <div class="auto-container">
      <div class="inner-box">
        <div class="content-box">
          <div class="title-box">
            <h3>User Registration</h3>
          </div>
          <div class="inner">
            <form @submit.prevent='handleRegister'
                  method="post"
                  class="registration-form">
              <div class="row clearfix">
                <div class="col-lg-6 col-md-6 col-sm-12 form-group">
                  <label>Real Name</label>
                  <input v-model="form.realname"
                         type="text"
                         name="realname"
                         placeholder="Enter your real name"
                         required>
                </div>
                <div class="col-lg-6 col-md-6 col-sm-12 form-group">
                  <label>Gender</label>
                  <select v-model="form.gendercode"
                          name="gender"
                          required
                          class="custom-select">
                    <option value=""
                            disabled
                            selected>请选择性别</option>
                    <option value="M">男</option>
                    <option value="F">女</option>
                  </select>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Identity</label>
                  <input v-model="form.identity"
                         type="identity"
                         name="identity"
                         placeholder="Enter your Identity"
                         required>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Birthdate</label>
                  <input v-model="form.birthdate"
                         type="date"
                         name="birthdate"
                         placeholder="Enter your BirthDate"
                         required />
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Password</label>
                  <input v-model="form.password"
                         type="password"
                         name="password"
                         placeholder="Your password"
                         required>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>Confirm password</label>
                  <input v-model="form.confirmPassword"
                         type="password"
                         name="cpassword"
                         placeholder="Confirm password"
                         required>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <div class="custom-check-box">
                    <div class="custom-controls-stacked">
                      <label class="custom-control material-checkbox">
                        <input type="checkbox"
                               class="material-control-input">
                        <span class="material-control-indicator"></span>
                        <span class="description">I accept <a href="book-appointment.html">terms</a> and <a href="book-appointment.html">conditions</a> and general policy</span>
                      </label>
                    </div>
                  </div>
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group message-btn">
                  <button type="submit"
                          class="theme-btn-one">
                    Register Now<i class="icon-Arrow-Right"></i>
                  </button>
                </div>
              </div>
            </form>
            <div class="text"><span>or</span></div>
            <ul class="social-links clearfix">
              <li><a href="register-page.html">Login with Facebook</a></li>
              <li><a href="register-page.html">Login with Google Plus</a></li>
            </ul>
            <div class="login-now">
              <p>Already have an account? <router-link to="/login">Login Now</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <!-- registration-section end -->
</template>

<script setup lang="ts">
import { logger } from '@/utils/logger'
import { ref, reactive } from 'vue'
import { navigateTo, safeRedirect } from '@/router/navigation'
import { useAuthStore } from '@/store/auth'
import { showError, showSuccess } from '@/utils/message'

// 注册表单
const form = reactive({
  identity: '',
  realname: '',
  gendercode: '',
  birthdate: '',
  password: '',
  confirmPassword: '',
})

// 注册状态和错误信息
const isLoading = ref(false)
const errorMessage = ref('')

// 使用auth store
const authStore = useAuthStore()

const handleRegister = async () => {
  if (
    !form.identity ||
    !form.realname ||
    !form.gendercode ||
    !form.birthdate ||
    !form.password ||
    !form.confirmPassword
  ) {
    errorMessage.value = '请填写完整的注册信息'
    showError(errorMessage.value)
    logger.error(errorMessage.value)
    return
  }

  if (form.password !== form.confirmPassword) {
    showError('两次密码不一致')
    return
  }

  try {
    logger.info('开始注册', form)
    isLoading.value = true
    errorMessage.value = ''

    // 调用auth store的登录方法
    await authStore.register({
      identity: form.identity, // 身份（user/doctor）
      realname: form.realname, // 真实姓名
      gendercode: form.gendercode, // 性别
      birthdate: form.birthdate, // 出生日期
      password: form.password,
      confirmPassword: form.confirmPassword,
      role: 'user',
    })
  } catch (error) {
    errorMessage.value = error.message || '注册失败，请重试'
    logger.error('注册错误:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<style>
.custom-select {
  position: relative;
  width: 100%;
  height: 50px;
  border: 1px solid #e5e7ec;
  border-radius: 10px;
  font-size: 15px;
  color: #808080;
  padding: 10px 20px;
  transition: all 500ms ease;
  appearance: none; /* 移除默认箭头（可选） */
  background-color: white;
}
</style>