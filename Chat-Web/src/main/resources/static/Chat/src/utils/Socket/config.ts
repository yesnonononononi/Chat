import type {ManagerOptions, SocketOptions} from "socket.io-client";
import {io, Socket,} from "socket.io-client";

export class configuration {
    // 自动适配协议：如果当前是https，则使用wss；否则使用ws
    // 同时自动获取当前域名/IP，避免硬编码localhost
    public url: string = `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}`;
    
    public config(): Partial<ManagerOptions & SocketOptions> {
        const token = localStorage.getItem("userToken");
        
        const config: Partial<ManagerOptions & SocketOptions> = {
            reconnection: true,
            reconnectionAttempts: 30, //重连次数
            reconnectionDelay: 1000, //重连间隔
            reconnectionDelayMax: 10000, //重连最大间隔
            transports: ["websocket"],
            
            path: "/chat-io",
            timeout: 100000, //超时时间
             autoConnect: true,
            query: token ? {
                Authorization: `Bearer ${token}`
            } : {}
        };
        return config;
    }

    public getInstance(config?: Partial<ManagerOptions & SocketOptions>, url?: string): Socket {
     
        
        if (config && url) {
            return io(url, config)
        }
        return io();
    }

    public getUrl(): string {
        return this.url;
    }
}
