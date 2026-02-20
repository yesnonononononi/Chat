import { serverMsgCode } from "@/enums/server-callback"


export interface chat{
    emitterId:string,
    receiveId:string,
    msg:string,
    msgId:string,
    sendTime:number //发送时间戳
    icon?:string,
    emitterNick?:string,
    status:number|null
}
export interface ChatGroup{
    groupId:string,
    msg:string,
    msgId:string,
    emitterId:string,
    messageType:string,
    createTime:number,
    icon?:string,
    nickName?:string
}

