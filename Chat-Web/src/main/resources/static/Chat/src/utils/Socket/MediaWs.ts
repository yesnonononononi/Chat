import type { Socket } from "socket.io-client";
import type { Ws } from "./webSocket";
import { Event } from "@/enums/events";
export class MediaWs {
  private static instance: Ws;
  private static socket: Socket;

  constructor(ws: Ws) {
    if (!MediaWs.instance || !MediaWs.socket) {
      MediaWs.instance = ws;
      MediaWs.socket = MediaWs.instance.getSocket();
    }
  }

  public init(
    send: (data: any) => void,
    accept: (data: any) => void,
    reject: (data: any) => void,
    cancel: (data: any) => void,
  ) {
    this.initListenSend(send); //监听收到的视频聊天通知
    this.initListenAccept(accept); //监听对方是否接受
    this.initListenReject(reject); //监听对方是否拒绝
    this.initListenCancel(cancel); //监听对方是否取消
  }

  
    public initListenSend(callback:(data:any)=>void){
        MediaWs.socket.on(Event.MEDIA_APPLY_SEND, callback)
    }

    public static removeListenSend(){
        MediaWs.socket.off(Event.MEDIA_APPLY_SEND)
    }

    public  initListenAccept(callback:(data:any)=>void){
       MediaWs.socket.on(Event.MEDIA_APPLY_ACCEPT, callback)
    }

    public static removeListenAccept( ){
        MediaWs.socket.off(Event.MEDIA_APPLY_ACCEPT)
    }

    public initListenReject(callback:(data:any)=>void){
     MediaWs.socket.on(Event.MEDIA_APPLY_REJECT, callback)
    }

    public static removeListenReject( ){
       MediaWs.socket.off(Event.MEDIA_APPLY_REJECT)
    }

    public initListenCancel(callback:(data:any)=>void){
      MediaWs.socket.on(Event.MEDIA_APPLY_CANCEL, callback)
    }

    public static removeListenCancel(){
       MediaWs.socket.off(Event.MEDIA_APPLY_CANCEL);
    }


  public static stopListen() {
    MediaWs.socket.off(Event.MEDIA_APPLY_ACCEPT);
    MediaWs.socket.off(Event.MEDIA_APPLY_SEND);
    MediaWs.socket.off(Event.MEDIA_APPLY_REJECT);
    MediaWs.socket.off(Event.MEDIA_APPLY_CANCEL);
  }
}
