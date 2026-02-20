import { AdminApi } from "@/api/admin";
import TestEscroller from "@/components/testEscroller.vue";
import { userStore } from "@/store/UserStore";
import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";

const Home = () => import("../views/chat.vue");
const Login = () => import("../views/Login.vue");

const register = () => import("../views/register.vue");
const apply = () => import("../views/apply.vue");
const createGroup = () => import("../views/createGroup.vue");
const smsLogin = () => import("../views/sms-login.vue");
const userInfo = () => import("../views/UserDetail.vue");
const groupDetail = () => import("../components/group_detail.vue");
const chatView = () => import("../components/chatView.vue");
const link = () => import("../views/friend_slot.vue");
const groupView = () => import("@/components/groupView.vue");
const userIntroduce = () => import("../components/user_introduce.vue");
//后续组件

//配置路由
const routes: RouteRecordRaw[] = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: Home,
    children: [
      {
        path: "chat/:id",
        name: "chat",
        component: chatView,
      },
      {
        path: "group/:id/:groupName",
        name: "group",
        component: groupView,
        children: [],
      },
      {
        path: "chat/create-group",
        name: "create-group",
        component: createGroup,
      },

      {
        path: "set-group-notify/:id",
        name: "group-notify",
        component: () => import("../views/putGroup.vue"),
      },
      {
        path: "group-detail/:id",
        name: "group-detail",
        component: groupDetail,
      },
      {
        path: "user-introduce/:id",
        name: "user-introduce",
        component: userIntroduce,
      },
    ],
    meta: { requireAuth: true },
  },
  {
    path: "/video-call/:id/:roomName/:role",
    name: "video-call",
    component: () => import("../views/video.vue"),
    meta: { requireAuth: true },
  },
  {
    path: "/admin",
    name: "admin",
    component: () => import("../views/admin.vue"),
    redirect: "/admin/total-data",
    meta: { requireAuth: true, role: ["admin"] },
    children: [
      {
        path: "total-data",
        name: "total-data",
        component: () => import("../components/data_total.vue"),
      },
      {
        path: "user-admin",
        name: "user-admin",
        component: () => import("../components/User_admin.vue"),
      },
      {
        path: "group-admin",
        name: "group-admin",
        component: () => import("../components/Group_admin.vue"),
      },
      {
        path: "system-admin",
        name: "system-admin",
        component: () => import("../components/System_admin.vue"),
      },
    ],
  },
  {
    path: "/apply",
    name: "apply",
    component: apply,
    meta: { requireAuth: true },
  },
  {
    path: "/admin-auth",
    name: "admin-auth",
    component: () => import("../views/admin_auth.vue"),
    meta: { requireAuth: true },
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/register",
    name: "Register",
    component: register,
  },
  {
    path: "/phone-login",
    name: "sms-login",
    component: smsLogin,
  },
  {
    path: "/forget-pw",
    name: "forget-pw",
    component: () => import("../views/forget_pw.vue"),
  },
  {
    path: "/user-info",
    name: "user-info",
    component: userInfo,
    meta: { requireAuth: true },
  },
  {
    path: "/user/link",
    name: "user-link",
    component: link,
    meta: {
      requireAuth: true,
    },
  },
  {
    path: "/test",
    name: "test",
    component: TestEscroller,
    meta: { requireAuth: true },
  },
 

  //后续配置
];
//创建路由实例
const router = createRouter({
  history: createWebHistory("/"),
  routes,
});

//路由守卫
router.beforeEach((to, from, next) => {
  const requireAuth = to.meta.requireAuth;


  //未登录直接跳转登录页
  const isLogin = !!localStorage.getItem("userToken");
 

  //防止重复跳转登录页
  const noAuthPages = ["Login", "Register", "sms-login"];
  if (noAuthPages.includes(to.name as string) && isLogin) {
    next({
      path: "/home",
    });
  }

  if (requireAuth) {
    if (isLogin) {
      // 检查目标路由及其父路由是否需要特定角色（如管理员权限）
      const hasRoleRequirement = to.meta.role || to.matched.some(record => record.meta.role);
      
      if (hasRoleRequirement) {
         // 检查是否已经完成了管理员认证（从sessionStorage或localStorage获取状态）
         // 这里假设 admin-auth 页面认证成功后会设置一个标志，或者仅仅依赖 from.name 是不安全的
         // 但为了满足"从admin-auth跳转过来"的需求，我们先保持这个逻辑，并补充直接访问子路由的情况
         
         const isAdminAuth = sessionStorage.getItem('isAdminAuth') === 'true';

        // 如果是从 admin-auth 页面跳转过来的，或者已经认证过
        if (from.name === "admin-auth" || isAdminAuth) {
          next();
        } 
        // 否则，强制跳转到 admin-auth 进行认证
        else {
           next({ name: "admin-auth" });
        }
      } else {
        next();        
      }
    } else {
      next({
        path: "/login",
      });
    }
  } else {
    //不需要认证,放行
    next();
  }
});

//导出
export default router;
