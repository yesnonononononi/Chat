import { defineStore, type Store } from "pinia";
import type { userInfo, data, userVO } from "@/types/user";
import type { body } from "@/types/auth";
import { UserApi } from "@/api/user";
import { Log } from "@/utils/TipUtil";
import router from "@/router";
import type { register } from "@/types/register";
import { removeUserToken } from "@/utils/UserInfo";
import { Ws } from "@/utils/Socket/webSocket";
import type { chat, ChatGroup } from "@/types/chat";
import type { systemMsg } from "@/types/msg";
import type { notify } from "@/types/notification";
import { msgStore, type session } from "./MessageStore";
import { serverMsgCode } from "@/enums/server-callback";
import type { MsgACKOfServer } from "@/types/msg-ack";
import { BusinessError } from "@/exception/BusinessError";
import { MediaWs } from "@/utils/Socket/MediaWs";
import type { SysNotice } from "@/types/sysNotice";
import { TimeUtil } from "@/utils/time";

interface User {
  userInfo: userInfo | null;
  isLogin: boolean;
  token: string | null;
  ws: Ws | null;
  curMsg: chat | null;
  curMsgOfGroup: ChatGroup | null;

}

export const userStore = defineStore("user", {
  // 1. state：存储全局状态（类似Vue的data），返回一个函数（避免跨实例状态污染
  state: (): User => ({
    userInfo: null,
    token: localStorage.getItem("userToken") || "",
    isLogin: false,
    ws: null,
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
      if (!this.token || !this.userInfo) return;
      this.startListener();
    },

    startListener() {
      if (!this.userInfo) {
        console.error("未获取到用户信息");
        return;
      }
      //初始化连接
      this.ws = Ws.getInstance();
      if (!this.ws) return;
      const msg = msgStore();

      //移除旧监听
      this.ws.removePrivateReceive();
      this.ws.removeSysReceive();

      //开启全局事件监听
      this.ws.onOneByoneMsg(
        (data: chat, ack?: (ackData: MsgACKOfServer) => void) => {
          if (this.userInfo) {
            const sid = msg.getSessionKey(this.userInfo.id, data.emitterId);
            if (!msg.getSession(sid)) {
              msg.registry(this.userInfo.id, data.emitterId);
            }

            const session = msg.getSession(sid);
            // 无论窗口是否打开，都将消息添加到消息列表
            msg.addMsg(sid, data);

            // 如果窗口已打开，发送已读回执
            if (session && session.isOpen) {
              if (ack) {
                ack({
                  msgCode: serverMsgCode.READ,
                  description: "",
                  symbol: "",
                  msgId: data.msgId,
                });
              }
            } else {
              // 如果窗口未打开，触发全局通知
              this.curMsg = data;
              setTimeout(() => {
                this.curMsg = null;
              }, 3000);
            }
          }
        },
      );

      //系统事件监听
      this.ws.systemListen((data: SysNotice) => {
        console.log("收到系统消息",data);
        if(data.createTime)data.createTime = TimeUtil.timestampToTime(data.createTime);
        msg.addSysMsg(data);
      });

      //管理员事件监听
      this.ws.onKick((data: string) => {
        Log.warn(data);
   
        this.ws?.disconnect();
        removeUserToken();
        router.push("/login");
      });

      //群聊事件监听
      this.ws.onGroupMsg(
        (data: ChatGroup, ack?: (ackData: MsgACKOfServer) => void) => {
          if (this.userInfo) {
            const sid = msg.getSessionKey(this.userInfo.id, data.groupId);
            if (!msg.getSession(sid)) {
              msg.registry(this.userInfo.id, data.groupId);
            }

            const session = msg.getSession(sid);
            msg.addGroupMsg(sid, data);

            // 如果不在当前群聊中，触发全局通知
            if (!session || !session.isOpen) {
              this.curMsgOfGroup = data;
              setTimeout(() => {
                this.curMsgOfGroup = null;
              }, 3000);
            }
          }
        },
      );
    },
    removeListener() {
      if (this.ws) {
        this.ws.removeSysReceive();
        this.ws.removePrivateReceive();
        MediaWs.stopListen();
      }
    },
    getWs() {
      return this.ws;
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

        // 断开旧连接（如匿名连接），确保使用新Token连接
        Ws.getInstance().disconnect();

        // 延迟启动监听，确保状态同步
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

        // 断开旧连接（如匿名连接），确保使用新Token连接
        Ws.getInstance().disconnect();

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
      this.removeListener();
      this.ws?.disconnect();
      this.$reset();
      this.ws = null;
      Ws.getInstance().disconnect();
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
