<template>
  <div class="profile-box patient-profile">
    <div class="upper-box">
      <figure class="profile-image">
        <img src="@/assets/images/profile-1.png"
             alt="">
      </figure>
      <div class="title-box centred">
        <div class="inner">
          <h3>{{ user.name }}</h3>
          <p><i class="fas fa-calendar-alt"></i>{{ user.birthDate }}, {{ user.age }} Years</p>
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

<script>
export default {
  props: {
    // 用于传递用户信息和当前活动页面
    user: {
      type: Object,
      required: true
    },
    activePage: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      // 菜单项数据
      menuItems: [
        { Menuname: '仪表盘', route: 'patient-dashboard.html', icon: 'fas fa-columns' },
        { Menuname: '最喜欢的医生', route: 'favourite-doctors.html', icon: 'fas fa-heart' },
        { Menuname: '日期', route: 'schedule-timing-2.html', icon: 'fas fa-clock' },
        { Menuname: '信息', route: 'message-2.html', icon: 'fas fa-comments', count: this.user.messagesCount },
        { Menuname: '个人信息', route: 'patient-profile.html', icon: 'fas fa-user' },
        { Menuname: '修改密码', route: 'change-password-2.html', icon: 'fas fa-unlock-alt' },
        { Menuname: '登出', route: 'login-2.html', icon: 'fas fa-sign-out-alt' }
      ]
    }
  },
  methods: {
    isActive (page) {
      // 根据当前活动页面判断菜单项是否为活动状态
      return this.activePage === page;
    }
  }
};
</script>
