import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";
import { navigateTo, safeRedirect } from "./navigation";
import PlaceholderView from "@/views/PlaceholderView.vue";
import { useAuthStore } from "@/store/auth";

// 公共路由
const publicRoutes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    redirect: "/login",
    meta: { title: "医疗数据共享平台" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/auth/LoginView_User.vue"),
    meta: { title: "用户登录", guestOnly: true },
  },
  {
    path: "/login-doctor",
    name: "LoginDoctor",
    component: () => import("@/views/auth/LoginView_Doctor.vue"),
    meta: { title: "医生登录", guestOnly: true },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/auth/RegisterView.vue"),
    meta: { title: "用户注册", guestOnly: true },
  },
];

// 患者专属路由
const patientRoutes: Array<RouteRecordRaw> = [
  {
    path: "/patient",
    name: "PatientDashboard",
    component: () => import("@/views/patient/Dashboard.vue"),
    meta: {
      title: "患者仪表盘",
      requiredRole: "patient",
    },
  },
  {
    path: "/patient/OutpatientDetail/:id",
    name: "OutpatientDetail",
    component: () => import("@/views/patient/OutpatientDetail.vue"),
    meta: {
      title: "门诊记录详情",
      requiredRole: "patient",
    },
  },
  {
    path: "/patient/InpatientDetail/:id",
    name: "InpatientDetail",
    component: () => import("@/views/patient/InpatientDetail.vue"),
    meta: {
      title: "住院记录详情",
      requiredRole: "patient",
    },
  },
];

// 医生专属路由
const doctorRoutes: Array<RouteRecordRaw> = [
  {
    path: "/doctor",
    name: "DoctorDashboard",
    component: () => import("@/views/doctor/Dashboard.vue"),
    meta: {
      title: "医生仪表盘",
      requiredRole: "doctor",
    },
  },
  {
    path: "/doctor/patient/InpatientDetail/:id",
    name: "DoctorInpatientDetail",
    component: () => import("@/views/doctor/InpatientDetail.vue"),
    meta: {
      title: "患者住院记录详情",
      requiredRole: "doctor",
    },
  },
];

// 错误页面
const errorRoutes: Array<RouteRecordRaw> = [
  {
    path: "/403",
    name: "Forbidden",
    redirect: "/login",
    meta: { title: "无权访问" },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    redirect: "/login",
    meta: { title: "页面不存在" },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...publicRoutes, ...patientRoutes, ...doctorRoutes, ...errorRoutes],
  scrollBehavior(to, from, savedPosition) {
    // 保持页面滚动位置
    return savedPosition || { top: 0 };
  },
});

// 路由守卫 - 权限控制
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;

  // 设置页面标题
  document.title = `${to.meta.title} | 医疗共享平台` || "医疗共享平台";

  // 检查是否需要认证
  if (to.meta.requiredRole) {
    if (!isAuthenticated) {
      return next({
        name: "Login",
        query: { redirect: to.fullPath },
      });
    }
  }

  next();
});

export default router;
