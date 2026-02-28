import {Socket} from "socket.io-client";
import {Event} from "@/enums/events";
import {Log} from "@/utils/TipUtil";
import type {chat} from "@/types/chat";
import type {MsgACKOfServer, withdrawn} from "@/types/msg-ack";
import {serverMsgCode} from "@/enums/server-callback";
import {BusinessError} from "@/exception/BusinessError";
import type {MsgAck} from "./webSocket";

export class PrivateHandler {
  private socket: Socket;

  constructor(socket: Socket) {
    this.socket = socket;
  }

  /**
   * 发送私聊消息
   */
  public send(data: chat, ackCallBack: (ackData: MsgAck) => void): boolean {
    try {
      if (this.socket && this.socket.connected) {
        this.socket.emit(Event.CHAT_PRIVATE_SEND, data, (ackData: MsgAck) => {
          ackCallBack(ackData);
        });
      } else {
        console.log("连接断开,发送失败");
        Log.error("网络繁忙,尝试刷新网页试试");
        return false;
      }
    } catch (error) {
      if (error instanceof BusinessError) {
        Log.error(error.message);
      } else {
        Log.error("服务繁忙");
        console.error("发送信息出现问题", error);
      }
      return false;
    }
    return true;
  }

  /**
   * 监听私聊消息
   */
  public onReceive(
    callback: (data: chat, ack?: (ackData: any) => void) => void,
  ) {
    this.socket.on(Event.CHAT_PRIVATE_RECEIVE, (data, ack) => {
      callback(data as chat, ack);
    });
  }

  /**
   * 监听已读,未读消息通知
   */
  public onMsgStatus(callback: (data: MsgACKOfServer) => void) {
    this.socket.on(Event.CHAT_DELIVERED, (data: any) => {
      // 尝试解析JSON字符串
      if (typeof data === "string") {
        try {
          data = JSON.parse(data);
        } catch (error) {
          if (error instanceof BusinessError) {
            Log.error(error.message);
          } else {
            Log.error("服务繁忙");
            console.error("解析消息回执JSON失败", error);
          }
        }
      }
      if (
        data &&
        data.msgCode === serverMsgCode.NOT_ONLINE &&
        !data.description
      ) {
        data.description = "发送成功";
      }
      callback(data);
    });
  }

  /*
   *撤回消息事件监听
   */
  public onWithdrawn(callback: (data: withdrawn) => void) {
    this.socket.on(Event.CHAT_WITHDRAWN, (data: withdrawn) => {
      callback(data);
    });
  }

  /*
   * 移除撤回消息监听
   */
  public removeWithdrawnListener() {
    this.socket.off(Event.CHAT_WITHDRAWN);
  }

  /**
   * 移除消息状态监听
   */
  public removeMsgStatusListener() {
    this.socket.off(Event.CHAT_DELIVERED);
  }

  /**
   * 移除私聊接收监听
   */
  public removeReceiveListener() {
    this.socket.off(Event.CHAT_PRIVATE_RECEIVE);
  }
}
