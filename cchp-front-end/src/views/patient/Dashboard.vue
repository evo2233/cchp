<template>
  <!-- 页面的标题 -->
  <PageTitle :pageTitle="'患者仪表盘'"
             :currentPage="'患者仪表盘'"
             :homeText="'主页'"
             :homeLink="'index.html'"
             :backgroundPattern1="shape70"
             :backgroundPattern2="shape71" />

  <section class="
             patient-dashboard
             bg-color-3">
    <!-- 左侧 -->
    <div class="left-panel">
      <!-- 菜单侧栏 -->
      <PatientMenuSidebar :user="userData"
                          :activePage="currentPage" />
    </div>
    <!-- 右侧 -->
    <div class="right-panel">
      <div class="right-panel">
        <div class="content-container">
          <div class="outer-container">
            <AppointmentTable />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import shape70 from '@/assets/images/shape/shape-70.png'
import shape71 from '@/assets/images/shape/shape-71.png'
import PatientMenuSidebar from './components/PatientMenuSidebar.vue';
import PageTitle from './components/PageTitle.vue';
import AppointmentTable from './components/AppointmentTable.vue';

export default {
  components: {
    PatientMenuSidebar,
    PageTitle,
    AppointmentTable
  },
  data () {
    return {
      userData: {
        name: '李闯',
        birthDate: '2004-01-17',
        age: 22,
        profileImage: '@/assets/images/profile-1.png', // 用户头像
        messagesCount: 99 // 消息数量
      },
      currentPage: '仪表盘',
      shape70,
      shape71
    };
  },

}
</script>