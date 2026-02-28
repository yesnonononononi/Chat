import {Socket} from "socket.io-client";
import {configuration} from "./config";
import type {chat, ChatGroup} from "@/types/chat";
import {Event, Status} from "@/enums/events";
import type {systemMsg} from "@/types/msg";
import type {MsgACKOfServer, withdrawn} from "@/types/msg-ack";
import {PrivateHandler} from "./PrivateHandler";
import {GroupHandler} from "./GroupHandler";
import {SystemHandler} from "./SystemHandler";

export interface MsgAck {
  success: boolean;
  msgId?: string;
  sendTime?: number;
  errorMsg?: string;
}

export class Ws {
  private conf = new configuration();
  private obj: Socket | null = null;
  private static instance: Ws;
  
  // Handlers
  private privateHandler: PrivateHandler | null = null;
  private groupHandler: GroupHandler | null = null;
  private systemHandler: SystemHandler | null = null;

  private initSocket(){
    //已有实例,直接返回
    if(this.obj && this.obj.connected){
      return this.obj;
    }
    
    // 获取配置
    const config = this.conf.config();
    const url = this.conf.getUrl();
    
    // 如果没有token（未登录），则不建立连接
    // 如果 token 不存在，getInstance 将返回一个未连接的 socket 实例，或者我们可以在这里直接返回 null
    if (!localStorage.getItem("userToken")) {
    
      return null;
    }

    this.obj = this.conf.getInstance(config, url);
    
    // 如果实例存在但未连接（autoConnect: false），且现在有token，手动连接
    if (this.obj && !this.obj.connected && config.autoConnect !== false) {
       this.obj.connect();
    }

    this.init();
    
    // 初始化Handlers
    if (this.obj) {
      this.privateHandler = new PrivateHandler(this.obj);
      this.groupHandler = new GroupHandler(this.obj);
      this.systemHandler = new SystemHandler(this.obj);
    }

    return this.obj;

  }
  private init() {
    if(!this.obj)return;
    
    this.obj.on(Status.CONNECT, () => {
      console.info("连接成功");
      this.obj?.emit(Event.CONNECT_SUCCESS); //向服务端发送已连接
    });

    this.obj.on(Event.UN_LOGIN,()=>{
      console.info("用户未登录")
    })

    this.obj.on(Status.DISCONNECT, () => {
      console.warn("连接已断开");
    });

    this.obj.on(Status.CONNECT_ERROR, (error: Error) => {
      console.error("连接出现问题", error);
    });

    this.obj.on(Status.CONNECT_RECONNECT, (num:number) => {
      console.debug(`重新连接第${num}次`);
    });

    this.obj.on(Status.ERROR, (error) => {
      console.error("出现错误");
    });
    return this.obj;
  }
  private constructor() {
  
  }
  

  public static getInstance(): Ws {
    if (!this.instance) {
      this.instance = new Ws();
    }
    return this.instance;
  }

  public getSocket(){
    if(!this.obj){
      // 尝试初始化
      const socket = this.initSocket();
      // 如果因为未登录返回null，则直接返回null
      return socket;
    }
    return this.obj;
  }
  
  /**
   * 私聊相关
   */
  public sendOfPrivate(data: chat, ackCallBack: (ackData: MsgAck) => void): boolean {
    this.getSocket(); // 确保Socket已初始化
    return this.privateHandler?.send(data, ackCallBack) || false;
  }

  public onOneByoneMsg(callback: (data: chat, ack?: (ackData: any) => void) => void) {
    this.getSocket();
    this.privateHandler?.onReceive(callback);
  }

  public onOneByOneMsgACK(ack: (data: MsgACKOfServer) => void) {
    this.getSocket();
    this.privateHandler?.onMsgStatus(ack);
  }

  public onWithdrawn(callback: (data: withdrawn) => void) {
    this.getSocket();
    this.privateHandler?.onWithdrawn(callback);
  }

  public removeWithdrawnListener() {
    this.privateHandler?.removeWithdrawnListener();
  }

  public removeMsgACKListener() {
    this.privateHandler?.removeMsgStatusListener();
  }

  public removePrivateReceive() {
    this.privateHandler?.removeReceiveListener();
    this.obj?.off(Event.CONNECT_SUCCESS);
  }

  /**
   * 群聊相关
   */
  public sendOfGroup(data: ChatGroup, ackCallBack: (ackData: MsgAck) => void): boolean {
    this.getSocket();
    return this.groupHandler?.send(data, ackCallBack) || false;
  }

  public onGroupMsg(callback: (data: ChatGroup, ack?: (ackData: any) => void) => void) {
    this.getSocket();
    this.groupHandler?.onReceive(callback);
  }

  /**
   * 系统消息相关
   */
  public systemListen(method: (data: systemMsg) => void) {
    this.getSocket();
    this.systemHandler?.onNotice(method);
  }

  public onKick(method: (data: string) => void) {
    this.getSocket();
    this.systemHandler?.onKick(method);
  }

  public removeSysReceive() {
    this.systemHandler?.removeNoticeListener();
  }

  public disconnect() {
    const socket = this.getSocket();
    socket?.removeAllListeners();
    socket?.disconnect();
    this.obj = null;
    this.privateHandler = null;
    this.groupHandler = null;
    this.systemHandler = null;
 
  }
}
