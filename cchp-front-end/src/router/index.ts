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
    component: PlaceholderView,
    meta: { title: "医疗数据共享平台" },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/auth/LoginView.vue"),
    meta: { title: "用户登录", guestOnly: true },
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
      title: "门诊记录表",
      requiredRole: "patient",
    },
  },
  {
    path: "/patient/outpatient-detail",
    name: "OutpatientDetail",
    component: () => import("@/views/patient/OutpatientDetail.vue"),
    meta: {
      title: "门诊记录详情",
      requiredRole: "patient",
    },
  },
  {
    path: "/patient/inpatient-detail",
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
    component: PlaceholderView,
    meta: {
      title: "医生工作台",
      requiredRole: "doctor",
      breadcrumb: ["医生门户"],
    },
    children: [
      {
        path: "patients",
        name: "PatientManagement",
        component: PlaceholderView,
        meta: {
          title: "患者管理",
          breadcrumb: ["医生门户", "我的患者"],
        },
      },
      {
        path: "schedule",
        name: "Schedule",
        component: PlaceholderView,
        meta: {
          title: "排班管理",
          breadcrumb: ["医生门户", "工作排班"],
        },
      },
    ],
  },
];

// 错误页面
const errorRoutes: Array<RouteRecordRaw> = [
  {
    path: "/403",
    name: "Forbidden",
    component: PlaceholderView,
    meta: { title: "无权访问" },
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: PlaceholderView,
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
