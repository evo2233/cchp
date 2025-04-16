<template>
  <div class="right-column pull-right">
    <div class="author-box">
      <!-- 消息提醒框 -->
      <div class="icon-box">
        <a href="#"
           @click.prevent="handleNotificationClick">
          <i class="icon-Bell"></i>
        </a>
      </div>
      <div class="author">
        <!-- 头像 -->
        <figure class="author-image">
          <img :src="avatarUrl"
               :alt="userName">
        </figure>
        <!-- 头像下拉菜单 -->
        <div class="select-box">
          <div class="nice-select wide"
               :class="{ open: isOpen }"
               tabindex="0"
               @click="toggleDropdown">
            <span class="current">你好, {{ userName }}</span>
            <ul class="list">
              <li v-for="(option, index) in menuOptions"
                  :key="index"
                  :class="['option', { 'selected': selectedOptionIndex === index }]"
                  @click.stop="selectOption(option, index)">
                {{ option.optionName }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserProfileDropdown',
  props: {
    // 用户名
    userName: {
      type: String,
      default: '用户'
    },
    // 头像URL
    avatarUrl: {
      type: String,
      default: '@/assets/images/author-1.png'
    },
    // 下拉菜单选项
    menuOptions: {
      type: Array,
      default: () => [
        { optionName: '个人信息', route: '/profile' },
        { optionName: '登出', route: '/logout' },
        { optionName: '设置', route: '/settings' },
        { optionName: '帮助', route: '/help' },
        { optionName: '关于我们', route: '/about' },
        { optionName: '隐私政策', route: '/privacy-policy' },
        { optionName: '条款和条件', route: '/terms' },
      ]
    }
  },
  data () {
    return {
      isOpen: false,
      selectedOptionIndex: null
    }
  },
  methods: {
    // 切换下拉菜单显示状态
    toggleDropdown () {
      this.isOpen = !this.isOpen
    },
    // 选择菜单项
    selectOption (option, index) {
      this.selectedOptionIndex = index
      this.isOpen = false
      this.$emit('menu-item-click', option)
    },
    // 处理通知点击事件
    handleNotificationClick () {
      this.$emit('notification-click')
    },
    // 点击外部关闭下拉菜单
    handleClickOutside (e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    }
  },
  mounted () {
    // 添加全局点击事件监听
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy () {
    // 移除事件监听
    document.removeEventListener('click', this.handleClickOutside)
  }
}
</script>
