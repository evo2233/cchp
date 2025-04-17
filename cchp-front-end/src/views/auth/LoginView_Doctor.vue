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
            <router-link to="/login">Not a Doctor?</router-link>
          </div>
          <!-- 登陆表单 -->
          <div class="inner">
            <!-- 使用handleLogin函数处理登陆登录事件 -->
            <form @submit.prevent="handleDoctorLogin"
                  method="post"
                  class="registration-form">
              <div class="row clearfix">
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>医疗机构名称</label>
                  <input type="identity"
                         v-model="doctorForm.institutionName"
                         placeholder="请输入医疗机构名称" />
                </div>
                <div class="col-lg-12 col-md-12 col-sm-12 form-group">
                  <label>医疗机构编号</label>
                  <input type="realname"
                         v-model="doctorForm.institutionCode"
                         placeholder="请输入医疗机构编号" />
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
import { showError, showSuccess } from '@/utils/message'

// 医生登录表单数据
const doctorForm = reactive({
  institutionCode: '',
  institutionName: '',
})

// 登录状态和错误信息
const isLoading = ref(false)
const errorMessage = ref('')

// 使用auth store
const authStore = useAuthStore()

// 医生登录处理
const handleDoctorLogin = async () => {
  // 验证所有字段
  if (!doctorForm.institutionCode || !doctorForm.institutionName) {
    const missingFields = []
    if (!doctorForm.institutionCode) missingFields.push('医疗机构名称')
    if (!doctorForm.institutionName) missingFields.push('医疗机构编号')

    const errorMessage = `请填写完整的登录信息，缺少：${missingFields.join(
      '、'
    )}`
    showError(errorMessage)
    return
  }

  try {
    isLoading.value = true
    errorMessage.value = ''

    // 调用auth store的登录方法
    await authStore.Doctorlogin({
      institutionCode: doctorForm.institutionCode,
      institutionName: doctorForm.institutionName,
    })
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请重试'
    logger.error('登录错误:', error)
    showError(errorMessage.value)
  } finally {
    isLoading.value = false
  }
}

function goToRegister() {
  safeRedirect('/register')
}
</script>
