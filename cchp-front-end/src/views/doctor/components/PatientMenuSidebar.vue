<template>
  <div class="profile-box patient-profile">
    <div class="upper-box">
      <figure class="profile-image">
        <img src="@/assets/images/profile-2.png"
             alt="">
      </figure>
      <div class="title-box centred">
        <div class="inner">
          <h3>{{ authStore?.institutionName || '未知医院' }}</h3>
        </div>
      </div>
    </div>
    <div class="profile-info">
      <ul class="list clearfix">
        <li v-for="(item, index) in menuItems"
            :key="index">
          <a :href="item.route"
             :class="{ current: isActive(item.Menuname) }">
            <i :class="item.icon"></i>{{ item.Menuname }}
            <span v-if="item.count">{{ item.count }}</span>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

// 使用计算属性确保响应式
const formattedBirthdate = computed(() => {
  if (!authStore.account?.birthdate) return '未知';
  return new Date(authStore.account.birthdate).toLocaleDateString('zh-CN');
});

const calculatedAge = computed(() => {
  if (!authStore.account?.birthdate) return '?';
  const birthYear = new Date(authStore.account.birthdate).getFullYear();
  return new Date().getFullYear() - birthYear;
});

// 修改为使用路由名称而非HTML文件路径
const menuItems = [
  { Menuname: '仪表盘', routeName: 'PatientDashboard', icon: 'fas fa-columns' },
  { Menuname: '最喜欢的医生', routeName: 'FavouriteDoctors', icon: 'fas fa-heart' },
  { Menuname: '预约', routeName: 'PatientAppointments', icon: 'fas fa-clock' },
  {
    Menuname: '消息',
    routeName: 'PatientMessages',
    icon: 'fas fa-comments',
    count: authStore.user?.unreadMessages || 0
  },
  { Menuname: '个人信息', routeName: 'PatientProfile', icon: 'fas fa-user' },
  { Menuname: '修改密码', routeName: 'ChangePassword', icon: 'fas fa-unlock-alt' },
  {
    Menuname: '登出',
    routeName: 'logout',
    icon: 'fas fa-sign-out-alt',
    action: () => {
      authStore.logout();
      router.push({ name: 'Login' });
    }
  }
];

const isActive = (page) => {
  return router.currentRoute.value.name === page;
};

</script>
