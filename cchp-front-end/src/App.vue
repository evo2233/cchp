<template>
  <MainHeader />
  <!-- <PatientDashboard /> -->
  <!-- <Detail /> -->
  <!-- <inpatient-detail /> -->
  <router-view />
</template>

<script setup>
import MainHeader from '@/components/layouts/Header.vue'
// import Footer from '@/components/layouts/Footer.vue'
import PatientDashboard from '@/views/patient/Dashboard.vue'
import Detail from '@/views/patient/OutpatientDetail.vue'
import InpatientDetail from './views/patient/InpatientDetail.vue'

</script>