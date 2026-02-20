import type { chat, ChatGroup } from "@/types/chat";
import type { notify } from "@/types/notification";
import type { SysNotice } from "@/types/sysNotice";
import { defineStore } from "pinia";

export interface session {
  msgList: chat[]; //信息列表
  groupMsgList: ChatGroup[];
  page: number; //当前页码,
  pageSize: number; //每页大小
  hasMore: boolean; //是否还有更多历史消息
  isOpen: boolean; // 窗口是否打开
}

interface MessageState {
  notification: SysNotice[];
  sessionMap: Record<string, session>;
}

export const msgStore = defineStore("msg", {
  state: (): MessageState => ({
    //系统消息
    notification: [],
    sessionMap: {},
  }),
  persist: {
    key: "msg_store", //存储键名
    storage: localStorage, //本地
    pick: ["notification"], //存储元数据
  },
  actions: {
    //获取系统信息列表
    getNotifyList() {
      return this.notification;
    },
    //添加系统消息
    addSysMsg(msg: SysNotice) {
      this.notification.unshift(msg);
    },
    //移除系统消息
    removeSysMsg() {
      this.notification.length = 0;
      this.notification = [];
    },

    //获取会话id
    getSessionKey(id: string, friendId: string): string {
      return id + friendId;
    },

    getSession(id: string) {
      if (!id) return;
      return this.sessionMap[id];
    },
    //注册会话
    registry(id: string, objId: string) {
      const sid = this.getSessionKey(id, objId);
      if (this.sessionMap[sid]) return sid; //如果本地持久化了,直接返回
      this.sessionMap[sid] = {
        msgList: [],
        groupMsgList: [],
        page: 1,
        pageSize: 15,
        hasMore: true,
        isOpen: false,
      };
      return sid;
    },

    //获取会话信息列表
    getMsgList(id: string): chat[] | null {
      let session: session | undefined = this.sessionMap[id];
      if (!session) {
        // 静默返回null，不打印错误日志，避免初始化时大量报错
        return null;
      }
      return session.msgList;
    },

    // 获取群组信息列表
    getGroupMsgList(id: string): ChatGroup[] | null {
      let session: session | undefined = this.sessionMap[id];
      if (!session) {
        // 静默返回null，不打印错误日志，避免初始化时大量报错
        return null;
      }
      return session.groupMsgList;
    },

    //添加私聊信息
    addMsg(id: string, msg: chat) {
      let msgList = this.getMsgList(id);
      if (!msgList) {
        // 静默处理，避免控制台刷屏
        return;
      }
      // 避免重复添加
      if (msgList.some(existingMsg => existingMsg.msgId === msg.msgId)) {
        return;
      }
      msgList.push(msg);
    },
    //添加群组信息
    addGroupMsg(id: string, msg: ChatGroup) {
      let msgList = this.getGroupMsgList(id);
      if (!msgList) {
        console.log("未检测到消息列表");
        return;
      }
      // 避免重复添加
      if (msgList.some(existingMsg => existingMsg.msgId === msg.msgId)) {
        return;
      }
      msgList.push(msg);
    },
    //添加私聊信息列表
    addMsgs(id: string, msg: chat[]) {
      let msgList = this.getMsgList(id);
      if (!msgList) {
        // 静默处理，避免控制台刷屏
        return;
      }
      // 过滤掉已存在的消息
      const newMsgs = msg.filter(newMsg => 
        !msgList.some(existingMsg => existingMsg.msgId === newMsg.msgId)
      );
      msgList.unshift(...newMsgs);
    },
    //添加群组信息列表
    addGroupMsgs(id: string, msg: ChatGroup[]) {
      let msgList = this.getGroupMsgList(id);
      if (!msgList) {
        // 静默处理，避免控制台刷屏
        return;
      }
      // 过滤掉已存在的消息
      const newMsgs = msg.filter(newMsg => 
        !msgList.some(existingMsg => existingMsg.msgId === newMsg.msgId)
      );
      msgList.unshift(...newMsgs);
    },

    //添加系统消息列表
    addNotifyMsgs(notices: SysNotice[]) {
      //清空列表
      this.notification.splice(0);
      this.notification.unshift(...notices);
    },
    removeAllLocalState() {
      this.$reset();
      localStorage.removeItem("msg_store");
    },
    //删除单个会话
    removeSession(id: string) {
      delete this.sessionMap[id];
    },
    //删除所有会话
    removeAllSession() {
      this.sessionMap = {};
    },
    // 更新私聊消息状态
    updateMsgStatus(sessionId: string, msgId: string, status: number) {
      const msgList = this.getMsgList(sessionId);
      if (!msgList) return;

      let found = false;
      // 从后往前查找（假设新消息在后面，回执通常是针对最近的消息）
      for (let i = msgList.length - 1; i >= 0; i--) {
        const msg = msgList[i];
        if (!msg) continue;
        if (msg.msgId === msgId) {
          msg.status = status;
          found = true;
          break;
        }
      }

      // 如果未找到对应ID的消息，且存在ID为空的消息（本地刚刚发送但未收到ACKID的消息），则尝试匹配
      if (!found) {
        for (let i = msgList.length - 1; i >= 0; i--) {
          const msg = msgList[i];
          if (!msg) continue;
          if (!msg.msgId) {
            // msgId 为空字符串
            msg.msgId = msgId; // 更新为服务端返回的ID
            msg.status = status;
            found = true;
            break;
          }
        }
      }
    },
    // 批量更新私聊会话中所有消息的状态（例如全部已读）
    updateAllMsgStatus(sessionId: string, status: number) {
      const msgList = this.getMsgList(sessionId);
      if (!msgList) return;

      // 遍历所有消息，仅更新状态不一致的
      msgList.forEach((msg) => {
        if (msg.status !== status) {
          msg.status = status;
        }
      });
    },
    // 重置会话（用于重新进入会话时清空旧数据，强制拉取最新）
    resetSession(id: string) {
      const session = this.sessionMap[id];
      if (session) {
        session.msgList = [];
        session.groupMsgList = [];
        session.page = 1;
        session.hasMore = true;
      }
    },
    // 设置会话打开状态
    setSessionOpen(id: string, isOpen: boolean) {
      const session = this.sessionMap[id];
      if (session) {
        session.isOpen = isOpen;
      }
    },
  },
});
