<template>
  <header class="main-header style-three">
    <div class="header-lower">
      <div class="outer-box clearfix">
        <!--左侧区域-->
        <div class="left-column pull-left">
          <!--logo-->
          <div class="logo-box">
            <figure class="logo">
              <router-link to="/">
                <img src="@/assets/images/logo-3.png"
                     alt="首页 Logo">
              </router-link>
            </figure>
          </div>

          <!--导航-->
          <div class="menu-area">
            <nav class="main-menu navbar-expand-md navbar-light">
              <div class="collapse navbar-collapse show clearfix"
                   id="navbarSupportedContent">
                <ul class="navigation clearfix">
                  <!-- 动态生成菜单 -->
                  <li v-for="menu in mainMenus"
                      :key="menu.id"
                      :class="{ 
                  'dropdown': menu.children, 
                  'current': currentMenu === menu.id 
                  }">
                    <template v-if="menu.children">
                      <a href="#"
                         @click.prevent="toggleSubMenu(menu.id)">
                        {{ menu.title }}<i class="dropdown-icon"></i>
                      </a>
                      <ul class="sub-menu">
                        <li v-for="subMenu in menu.children"
                            :key="subMenu.id">
                          <router-link :to="subMenu.route">
                            {{ subMenu.title }}
                          </router-link>
                        </li>
                      </ul>
                    </template>
                  </li>
                </ul>
              </div>
            </nav>
          </div>
        </div>

        <!--右侧-->
        <UserProfileDropdown :user-name="userData.userName"
                             :avatar-url="userData.avatarUrl"
                             :menu-options="menuOptions"
                             :is-open="isOpen"
                             @toggle="toggleDropdown"
                             @select="handleSelect" />
      </div>
    </div>

    <!--粘性头部 鼠标滑动时保持位置不变-->
    <div class="sticky-header">
      <div class="auto-container">
        <div class="outer-box">
          <div class="logo-box">
            <figure class="logo">
              <router-link to="/"
                           class="sticky-logo">
                <img src="@/assets/images/small-logo.png"
                     alt="首页(Logo)">
              </router-link>
            </figure>
          </div>
          <div class="menu-area">
            <nav class="main-menu clearfix">
              <!--Keep This Empty / Menu will come through Javascript-->
            </nav>
          </div>
          <div class="btn-box">
            <router-link to="/register"
                         class="register-btn"
                         v-if="!isAuthenticated">
              <i class="icon-user"></i>
              注册/登录
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import UserProfileDropdown from '@/components/common/UserProfileDropdown.vue'
import { useAuthStore } from '@/store/auth'

// 使用auth store
const authStore = useAuthStore()

interface MenuItem {
  id: string
  title: string
  route: string
  children?: MenuItem[]
}

interface UserData {
  userName: string
  avatarUrl: string
}

interface MenuOption {
  optionName: string
  route: string
  icon?: string
}

// 菜单配置
const currentMenu = ref('home')
const mainMenus = ref<MenuItem[]>([
  {
    id: 'home',
    title: '首页',
    route: '/',
    children: [
      { id: 'home-1', title: '首页样式1', route: '/' },
      { id: 'home-2', title: '首页样式2', route: '/home-2' },
      { id: 'home-3', title: '首页样式3', route: '/home-3' },
      { id: 'home-4', title: '首页样式4', route: '/home-4' },
      { id: 'home-5', title: '首页样式5', route: '/home-5' },
      { id: 'onepage', title: '单页首页', route: '/onepage' },
      { id: 'rtl-home', title: 'RTL 首页', route: '/rtl-home' },
    ],
  },
  {
    id: 'doctor',
    title: '医生界面',
    route: '/doctors',
    children: [
      {
        id: 'doctor-dashboard',
        title: '医生仪表盘',
        route: '/doctor/dashboard',
      },
      { id: 'doctor-schedule', title: '排班管理', route: '/doctor/schedule' },
      {
        id: 'doctor-appointment',
        title: '预约管理',
        route: '/doctor/appointments',
      },
      { id: 'doctor-profile', title: '个人资料', route: '/doctor/profile' },
      { id: 'doctor-settings', title: '设置', route: '/doctor/settings' },
      { id: 'doctor-messages', title: '消息', route: '/doctor/messages' },
      { id: 'doctor-reviews', title: '评论', route: '/doctor/reviews' },
      { id: 'doctor-patients', title: '我的患者', route: '/doctor/patients' },
      { id: 'doctor-login', title: '登出', route: '/doctor/logout' },
    ],
  },
  {
    id: 'patient',
    title: '患者界面',
    route: '/patients',
    children: [
      {
        id: 'patient-dashboard',
        title: '患者仪表盘',
        route: '/patient/dashboard',
      },
      { id: 'appointment', title: '预约管理', route: '/appointments' },
      { id: 'patient-profile', title: '个人资料', route: '/patient/profile' },
      { id: 'patient-settings', title: '设置', route: '/patient/settings' },
      { id: 'patient-messages', title: '消息', route: '/patient/messages' },
      {
        id: 'patient-favourites',
        title: '收藏的医生',
        route: '/patient/favourites',
      },
      { id: 'patient-login', title: '登出', route: '/patient/logout' },
    ],
  },
])

const userData = ref<UserData>({
  userName: '李闯',
  avatarUrl: new URL('@/assets/images/author-1.png', import.meta.url).href, // 正确处理静态资源路径
})
const isAuthenticated = false

const menuOptions = ref<MenuOption[]>([
  { optionName: '个人信息', route: '/profile', icon: 'user' },
  { optionName: '设置', route: '/settings', icon: 'settings' },
  { optionName: '帮助', route: '/help', icon: 'help' },
  { optionName: '关于我们', route: '/about', icon: 'info' },
  { optionName: '隐私政策', route: '/privacy-policy' },
  { optionName: '条款和条件', route: '/terms' },
  { optionName: '登出', route: '/logout', icon: 'logout' },
])

// UI状态
const isOpen = ref(false) // 下拉菜单是否展开

const toggleSubMenu = (menuId: string) => {
  currentMenu.value = menuId
}

const toggleDropdown = () => {
  isOpen.value = !isOpen.value
}

const handleSelect = (option: MenuOption) => {
  if (option.route === '/logout') {
    handleLogout()
  } else {
    navigateTo(option.route)
  }
  toggleDropdown()
}

const navigateTo = (route: string) => {
  console.log(`跳转到: ${route}`)
}

const handleLogout = () => {
  console.log('执行登出逻辑')
  // TODO: 调用登出API
}
</script>

<style>
@import url('../../assets/styles/font-awesome-all.css');
@import url('../../assets/styles/flaticon.css');
@import url('../../assets/styles/owl.css');
@import url('../../assets/styles/bootstrap.css');
@import url('../../assets/styles/jquery.fancybox.min.css');
@import url('../../assets/styles/animate.css');
@import url('../../assets/styles/color.css');
@import url('../../assets/styles/nice-select.css');
@import url('../../assets/styles/style.css');
@import url('../../assets/styles/responsive.css');
</style>