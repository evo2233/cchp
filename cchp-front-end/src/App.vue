<template>
  <MainHeader />
  <PatientDashboard />

  <!-- <nav>
    <router-link to="/">首页</router-link> |
    <router-link to="/about">关于</router-link>
  </nav>
  
  <router-view/> -->
</template>

<script>
import MainHeader from './components/layouts/Header.vue'
// import Footer from './components/layouts/Footer.vue'
import PatientDashboard from './views/patient/Dashboard.vue'

export default {
  components: {
    MainHeader,
    PatientDashboard
  }
}
</script>