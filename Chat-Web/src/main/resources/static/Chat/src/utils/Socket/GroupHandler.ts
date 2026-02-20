import { Socket } from "socket.io-client";
import { Event } from "@/enums/events";
import { Log } from "@/utils/TipUtil";
import type { ChatGroup } from "@/types/chat";
import { BusinessError } from "@/exception/BusinessError";
import type { MsgAck } from "./webSocket";

export class GroupHandler {
    private socket: Socket;

    constructor(socket: Socket) {
        this.socket = socket;
    }

    /**
     * 发送群聊消息
     */
    public send(data: ChatGroup, ackCallBack: (ackData: MsgAck) => void): boolean {
        if (data.msg.trim().length == 0 || data.msg.length > 200) {
            throw new BusinessError("消息长度不能为空或者大于200");
        }
        try {
            if (this.socket && this.socket.connected) {
                this.socket.emit(
                    Event.CHAT_GROUP_RECEIVE,
                    data,
                    (ackData: MsgAck) => {
                        ackCallBack(ackData);
                    }
                );
                return true;
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
    }

    /**
     * 监听群聊消息
     */
    public onReceive(callback: (data: ChatGroup, ack?: (ackData: any) => void) => void) {
        this.socket.on(Event.CHAT_GROUP_SEND, (data, ack) => {
            callback(data as ChatGroup, ack);
        })
    }
}
