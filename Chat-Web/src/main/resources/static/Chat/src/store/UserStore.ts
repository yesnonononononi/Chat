import {defineStore} from "pinia";
import type {userInfo} from "@/types/user";
import {UserApi} from "@/api/user";
import {Log} from "@/utils/TipUtil";
import router from "@/router";
import type {register} from "@/types/register";
import {removeUserToken} from "@/utils/UserInfo";
import type {chat, ChatGroup} from "@/types/chat";
import {socketStore} from "./SocketStore";
import {BusinessError} from "@/exception/BusinessError";

interface User {
  userInfo: userInfo | null;
  isLogin: boolean;
  token: string | null;
  curMsg: chat | null;
  curMsgOfGroup: ChatGroup | null;
}

export const userStore = defineStore("user", {
  // 1. state：存储全局状态（类似Vue的data），返回一个函数（避免跨实例状态污染
  state: (): User => ({
    userInfo: null,
    token: localStorage.getItem("userToken") || "",
    isLogin: false,
    curMsg: null,
    curMsgOfGroup: null,
  }),
  getters: {
    NickName: (state) => state.userInfo?.nickName || "游客",
  },
  persist: {
    key: "user_store",
    pick: ["token", "userInfo", "isLogin"],
  },
  actions: {
    initApp() {
       const socket = socketStore();
       socket.initApp();
    },

    //保存用户信息
    setUserInfo(userData: userInfo, token: string) {
      this.userInfo = userData;
      this.token = token;
      this.isLogin = true;
      localStorage.setItem("userToken", token);
    },

    async login(body: any) {
      try {
        const res = await UserApi.login(body);
        this.setUserInfo(res.userVO, res.token);
        Log.ok("登录成功");
        router.push("/");

  
        // 延迟启动监听，确保状态同步
        // 这里必须使用 setTimeout 确保 token 已写入 localStorage，因为 Socket 配置会读取它
        setTimeout(() => {
          this.initApp();
        }, 100);
      } catch (error: any) {
        if (error instanceof BusinessError) {
          Log.error(error.message);
        } else {
          Log.error("服务繁忙");
          console.error(error);
        }
        // 不再抛出异常，防止控制台打印堆栈，且前端逻辑不需要后续处理
      }
    },

    login_sms: async function (loginForm: {
      mobile: string;
      verifyCode: string;
    }) {
      try {
        const res = await UserApi.loginByCode(loginForm);
        Log.ok("登录成功");
        this.setUserInfo(res.userVO, res.token);
 

        setTimeout(() => {
          this.initApp();
          router.push("/");
        }, 100);
      } catch (error: any) {
        if (error instanceof BusinessError) {
          Log.error(error.message);
        } else {
          Log.error("服务繁忙");
          console.error("发送验证码出现问题", error);
        }
      }
    },

    // 发送验证码
    sendVerifyCode: async function (mobile: string): Promise<boolean> {
      try {
        await UserApi.sendCode(mobile);
        Log.ok("验证码已发送");
        return true;
      } catch (error: any) {
        if (error instanceof BusinessError) {
          Log.error(error.message);
        } else {
          Log.error("服务繁忙");
          console.error("发送验证码出现问题", error);
        }
        return false;
      }
    },

    register: async function (registerData: register): Promise<boolean> {
      try {
        await UserApi.register(registerData);
        Log.ok("注册成功，即将跳转登录页");
        const timeout = 2000;
        setTimeout(() => {
          router.push("/login");
        }, timeout);
        return true;
      } catch (error: any) {
        if (error instanceof BusinessError) {
          Log.error(error.message);
        } else {
          Log.error("服务繁忙");
          console.error("注册失败", error);
        }
        return false;
      }
    },

    end: function () {
      removeUserToken();
      const socket = socketStore();
      socket.disconnect();
      this.$reset();
      localStorage.removeItem("user_store");
      router.push("/login");
    },
    async logout() {
      if (this.token) {
        try {
          await UserApi.loginOut(this.token);
          localStorage.clear();
          sessionStorage.clear();
        } catch (error: any) {
          if (error instanceof BusinessError) {
            Log.error(error.message);
          } else {
            Log.error("服务繁忙");
            console.error("退出登录失败", error);
          }
        }
      }
      this.end();
    },
  },
});
