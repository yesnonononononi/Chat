
import type{ManagerOptions,SocketOptions } from "socket.io-client";
import { io, Socket, } from "socket.io-client";

export class configuration {
    public url:string = `http://${window.location.hostname}:9090`
  public  config(): Partial<ManagerOptions & SocketOptions> {
    const config: Partial<ManagerOptions & SocketOptions> = {
      reconnection: true,
      reconnectionAttempts: 30, //重连次数
      reconnectionDelay: 1000, //重连间隔
      reconnectionDelayMax: 10000, //重连最大间隔
      transports: ["websocket","polling"],
      port:9090,
      host:window.location.hostname,
      path:"/chat-io",
      timeout: 100000, //超时时间
      query:{
        Authorization:`Bearer ${localStorage.getItem("userToken")}`
      }
      
    };
    return config;
  }

  public getInstance(config?:Partial<ManagerOptions&SocketOptions>,url?:string):Socket {
    if(config&&url){
        return io(url,config)
    }
    return io();
  }

  public  getUrl():string{
    return this.url;
  }
}
