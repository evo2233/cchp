<template>
  <!-- <TestPage /> -->
  <MainHeader />
  <!-- <PatientDashboard /> -->
  <!-- <LoginView /> -->
  <router-view />
</template>

<script setup>
import TestPage from '@/views/test/TestUserProfile.vue'
import MainHeader from '@/components/layouts/Header.vue'
// import Footer from '@/components/layouts/Footer.vue'
import PatientDashboard from '@/views/patient/Dashboard.vue'
import LoginView from '@/views/auth/LoginView.vue'

</script>