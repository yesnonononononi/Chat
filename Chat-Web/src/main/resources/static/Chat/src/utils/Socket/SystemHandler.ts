import {Socket} from "socket.io-client";
import {Event} from "@/enums/events";
import type {systemMsg} from "@/types/msg";

export class SystemHandler {
    private socket: Socket;

    constructor(socket: Socket) {
        this.socket = socket;
    }

    /**
     *  系统事件监听
     * */
    public onNotice(callback: (data: systemMsg) => void) {
        this.socket.on(Event.SYSTEM_NOTICE, (data) => {
            callback(data);
        })
    }

    /**
     * 监听用户被踢出
     */
    public onKick(method: (data: string) => void) {
        this.socket.on(Event.KICK_USER, (data: string) => {
            method(data);
        })
    }

    /**
     * 关闭系统事件监听
     */
    public removeNoticeListener() {
        this.socket.off(Event.SYSTEM_NOTICE);
        this.socket.off(Event.KICK_USER);
    }
}
