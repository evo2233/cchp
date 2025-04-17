<template>
  <div class="right-column pull-right">
    <div class="author-box">

      <!-- 消息提醒框 -->
      <div class="icon-box">
        <a href="#"
           @click.prevent="handleNotificationClick">
          <i class="icon-Bell"></i>
          <span v-if="unreadCount > 0"
                class="notification-badge">{{ unreadCount }}</span>
        </a>
      </div>

      <div class="author">
        <!-- 头像 -->
        <figure class="author-image">
          <img :src="userAvatar"
               :alt="displayName">
        </figure>

        <!-- 头像下拉菜单 -->
        <div class="select-box">
          <div class="nice-select wide"
               :class="{ open: isOpen }"
               tabindex="0"
               @click="toggleDropdown">
            <span class="current">{{ greetingText  }}</span>
            <ul class="list">
              <template v-if="isAuthenticated">
                <li v-for="(option, index) in menuOptions"
                    :key="index"
                    :class="['option', { 'selected': selectedOptionIndex === index }]"
                    @click.stop="selectOption(option, index)">
                  {{ option.optionName }}
                </li>
              </template>
              <template v-else>
                <li class="option"
                    @click.stop="navigateToLogin">
                  <i class="fas fa-sign-in-alt menu-icon"></i>登录/注册
                </li>
              </template>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from 'vue';
import { useAuthStore } from '@/store/auth';
import { useRouter } from 'vue-router';
import defaultAvatar from '@/assets/images/author-1.png';

const authStore = useAuthStore();
const router = useRouter();

const isOpen = ref(false);
const selectedOptionIndex = ref(null);

// 计算属性
const isAuthenticated = computed(() => authStore.isAuthenticated);
const displayName = computed(() => authStore.account?.realname || '访客');
const userAvatar = computed(() => authStore.account?.avatar || defaultAvatar);
const unreadCount = computed(() => authStore.account?.unreadNotifications || 0);
const greetingText = computed(() =>
  isAuthenticated.value ? '您好' : '请登录'
);

// 菜单选项
const authenticatedOptions = [
  { label: '个人中心', route: { name: 'Profile' }, icon: 'fas fa-user' },
  { label: '账户设置', route: { name: 'Settings' }, icon: 'fas fa-cog' },
  { label: '我的消息', route: { name: 'Messages' }, icon: 'fas fa-envelope' },
  { label: '登出', action: () => authStore.logout(), icon: 'fas fa-sign-out-alt' }
];

// 方法
const toggleDropdown = () => {
  if (!isAuthenticated.value) {
    navigateToLogin();
    return;
  }
  isOpen.value = !isOpen.value;
};

const handleOptionClick = (option, index) => {
  selectedOptionIndex.value = index;
  isOpen.value = false;

  if (option.route) {
    router.push(option.route);
  } else if (option.action) {
    option.action();
  }
};

const navigateToLogin = () => {
  router.push({ name: 'Login' });
};

const handleNotificationClick = () => {
  if (!isAuthenticated.value) {
    navigateToLogin();
    return;
  }
  router.push({ name: 'Notifications' });
};

const handleClickOutside = (e) => {
  if (!e.target.closest('.nice-select')) {
    isOpen.value = false;
  }
};

// 生命周期
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

</script>

<style scoped>
.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon {
  margin-right: 8px;
  width: 16px;
  text-align: center;
}

.option {
  display: flex;
  align-items: center;
  padding: 8px 12px;
}
</style>