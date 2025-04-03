<template>
  <header class="main-header style-three">
    <div class="header-lower">
      <div class="outer-box clearfix">
        <!--左侧-->
        <div class="left-column pull-left">
          <!--logo-->
          <div class="logo-box">
            <figure class="logo">
              <a href="#"
                 @click.prevent="handleClick('index')">
                <img src="@/assets/images/logo-3.png"
                     alt="首页 Logo">
              </a>
            </figure>
          </div>
          <!--菜单-->
          <div class="menu-area">
            <nav class="main-menu navbar-expand-md navbar-light">
              <div class="collapse navbar-collapse show clearfix"
                   id="navbarSupportedContent">
                <ul class="navigation clearfix">
                  <!--首页-->
                  <li class="dropdown">
                    <a href="#"
                       @click.prevent="handleClick('index')">首页模板</a>
                    <ul>
                      <li><a href="#"
                           @click.prevent="handleClick('index')">首页 01</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-2')">首页 02</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-3')">首页 03</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-4')">首页 04</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-5')">首页 05</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-onepage')">单页首页</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('index-rtl')">RTL 首页</a></li>
                      <li class="dropdown">
                        <a href="#"
                           @click.prevent="handleClick('index')">头部风格</a>
                        <ul>
                          <li><a href="#"
                               @click.prevent="handleClick('index')">头部风格 01</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('index-2')">头部风格 02</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('index-3')">头部风格 03</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('index-5')">头部风格 04</a></li>
                        </ul>
                      </li>
                    </ul>
                  </li>
                  <!--菜单-->
                  <li class="dropdown">
                    <a href="#"
                       @click.prevent="handleClick('doctors')">医生界面模板</a>
                    <ul>
                      <li class="dropdown"><a href="doctors-dashboard.html">医生仪表盘</a>
                        <ul>
                          <li><a href="#"
                               @click.prevent="handleClick('doctors-1')">医生页面 01</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('doctors-dashboard')">仪表盘</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('appointment')">预约</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('my-patients')">我的患者</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('add-listing')">添加列表</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('schedule-timing')">排班时间</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('review')">评论</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('message')">消息</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('my-profile')">个人资料</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('change-password')">更改密码</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('login')">登出</a></li>
                        </ul>
                      </li>
                      <li><a href="#"
                           @click.prevent="handleClick('doctors-1')">医生页面 01</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('doctors-2')">医生页面 02</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('doctors-3')">医生页面 03</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('doctors-4')">医生页面 04</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('doctors-details')">医生详情</a></li>
                    </ul>
                  </li>
                  <li class="current dropdown">
                    <a href="#"
                       @click.prevent="handleClick('patients')">患者界面模板</a>
                    <ul>
                      <li class="dropdown">
                        <a href="#"
                           @click.prevent="handleClick('patient-dashboard')">患者仪表盘</a>
                        <ul>
                          <li><a href="#"
                               @click.prevent="handleClick('patient-dashboard')">仪表盘</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('favourite-doctors')">收藏的医生</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('schedule-timing-2')">排班时间</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('message-2')">消息</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('patient-profile')">我的资料</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('change-password-2')">更改密码</a></li>
                          <li><a href="#"
                               @click.prevent="handleClick('login-2')">登出</a></li>
                        </ul>
                      </li>
                      <li><a href="#"
                           @click.prevent="handleClick('submit')">提交评论</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('book-appointment')">预约医生</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('register-page')">注册页面</a></li>
                    </ul>
                  </li>
                  <li class="dropdown">
                    <a href="#"
                       @click.prevent="handleClick('pages')">其他页面模板</a>
                    <div class="megamenu">
                      <div class="row clearfix">
                        <div class="col-lg-6 column">
                          <ul>
                            <li>
                              <h4>页面列表 01</h4>
                            </li>
                            <li><a href="#"
                                 @click.prevent="handleClick('about')">关于我们</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('service-1')">服务 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('service-2')">服务 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('faq')">常见问题</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('pricing')">我们的定价</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('contact')">联系我们</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('error')">404</a></li>
                          </ul>
                        </div>
                        <div class="col-lg-6 column">
                          <ul>
                            <li>
                              <h4>页面列表 02</h4>
                            </li>
                            <li><a href="#"
                                 @click.prevent="handleClick('clinic-1')">诊所页面 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('clinic-2')">诊所页面 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('clinic-3')">诊所页面 03</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('clinic-4')">诊所页面 04</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('clinic-details')">诊所详情</a></li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </li>
                  <li class="dropdown">
                    <a href="#"
                       @click.prevent="handleClick('elements')">模块</a>
                    <div class="megamenu">
                      <div class="row clearfix">
                        <div class="col-lg-6 col-md-12 col-sm-12 column">
                          <ul>
                            <li>
                              <h4>元素 1</h4>
                            </li>
                            <li><a href="#"
                                 @click.prevent="handleClick('about-element-1')">关于模块 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('about-element-2')">关于模块 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('about-element-3')">关于模块 03</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('feature-element-1')">特色模块 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('feature-element-2')">特色模块 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('process-element-1')">流程模块 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('process-element-2')">流程模块 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('team-element-1')">团队模块 01</a></li>
                          </ul>
                        </div>
                        <div class="col-lg-6 col-md-12 col-sm-12 column">
                          <ul>
                            <li>
                              <h4>元素 2</h4>
                            </li>
                            <li><a href="#"
                                 @click.prevent="handleClick('team-element-2')">团队模块 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('news-element-1')">新闻模块 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('news-element-2')">新闻模块 02</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('category-element-1')">类别模块 01</a></li>
                            <li><a href="#"
                                 @click.prevent="handleClick('category-element-2')">类别模块 02</a></li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </li>
                  <li class="dropdown">
                    <a href="#"
                       @click.prevent="handleClick('news')">新闻界面模板</a>
                    <ul>
                      <li><a href="#"
                           @click.prevent="handleClick('blog')">博客网格 01</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('blog-2')">博客网格 02</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('blog-3')">博客标准</a></li>
                      <li><a href="#"
                           @click.prevent="handleClick('blog-details')">博客详情</a></li>
                    </ul>
                  </li>
                </ul>
              </div>
            </nav>
          </div>
        </div>

        <!--右侧-->
        <div class="right-column pull-right">
          <div class="author-box">
            <!-- 消息提醒框 -->
            <div class="icon-box">
              <a href="#"
                 @click.prevent="handleClick('index')">
                <i class="icon-Bell"></i>
              </a>
            </div>
            <div class="author">
              <!-- 头像 -->
              <figure class="author-image">
                <img src="@/assets/images/author-1.png"
                     alt="">
              </figure>
              <!-- 头像下拉菜单 -->
              <div class="select-box">
                <div class="nice-select wide"
                     :class="{ open: isOpen }"
                     tabindex="0"
                     @click="toggleDropdown">
                  <span class="current">你好, {{ UserName }}</span>
                  <ul class="list">
                    <li v-for="(option, index) in options"
                        :key="index"
                        :class="['option', 'selected']"
                        @click="selectOption(option)">
                      {{ option.optionName }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!--粘性头部 鼠标滑动时保持位置不变-->
    <div class="sticky-header">
      <div class="auto-container">
        <div class="outer-box">
          <div class="logo-box">
            <figure class="logo">
              <a href="#"
                 @click.prevent="handleClick('index')">
                <img src="../../assets/images/small-logo.png"
                     alt="首页(Logo)">
              </a>
            </figure>
          </div>
          <div class="menu-area">
            <nav class="main-menu clearfix">
              <!--Keep This Empty / Menu will come through Javascript-->
            </nav>
          </div>
          <div class="btn-box">
            <a href="#"
               @click.prevent="handleClick('register')"
               class="theme-btn-one">
              <i class="icon-image"></i>
              注册
            </a>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'MainHeader',
  data () {
    return {
      isOpen: false, // 控制下拉菜单的显示与隐藏
      UserName: '李闯', // 默认显示选项文本（用户名）
      options: [
        { optionName: '个人信息', route: '/profile' },
        { optionName: '登出', route: '/logout' },
        { optionName: '设置', route: '/settings' },
        { optionName: '帮助', route: '/help' },
        { optionName: '关于我们', route: '/about' },
        { optionName: '隐私政策', route: '/privacy-policy' },
        { optionName: '条款和条件', route: '/terms' },
      ], // 下拉菜单选项
    };
  },
  methods: {
    // 切换菜单的展开与收起
    toggleDropdown () {
      this.isOpen = !this.isOpen;
    },
    // 选择某一项，并进行页面跳转
    selectOption (option) {
      // 这里可以根据不同选项值进行跳转或其他操作
      this.handleClick(option.route);
      this.toggleDropdown(); // 选择后关闭下拉菜单
    },
    handleClick (route) {
      console.log(`用户点击了：${route} 页面`);
      alert(`即将跳转到：${route}（功能待实现）`);
    }
  }
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