<template>
  <!-- page-title-two -->
  <section class="page-title-two">
    <div class="title-box centred bg-color-2">
      <div class="pattern-layer">
        <div class="pattern-1"
             :style="{ backgroundImage: `url(${backgroundPattern1})`}"></div>
        <div class="pattern-2"
             :style="{ backgroundImage: `url(${backgroundPattern2})` }"></div>
      </div>
      <div class="auto-container">
        <div class="title">
          <h1>{{ pageTitle }}</h1>
        </div>
      </div>
    </div>
    <div class="lower-content">
      <ul class="bread-crumb clearfix">
        <li><a :href="homeLink">{{ homeText }}</a></li>
        <li>{{ currentPage }}</li>
      </ul>
    </div>
  </section>
  <!-- page-title-two end -->
</template>

<script>
export default {
  props: {
    // 页面的标题
    pageTitle: {
      type: String,
      required: true
    },
    // 当前页面的名称
    currentPage: {
      type: String,
      required: true
    },
    // 父级传递的首页链接文字
    homeText: {
      type: String,
      default: 'Home'
    },
    // 父级传递的首页链接
    homeLink: {
      type: String,
      default: 'index.html'
    },
    // 背景图案1
    backgroundPattern1: {
      type: String,
      default: "../../../assets/images/shape/shape-70.png"
    },
    // 背景图案2
    backgroundPattern2: {
      type: String,
      default: "../../../assets/images/shape/shape-70.png"
    },
  }
};
</script>
