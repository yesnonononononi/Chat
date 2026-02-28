import type { EmojiVO } from "./emoji"

export interface chat{
    emitterId:string,
    receiveId:string,
    msg:string,
    msgId:string,
    type?:string,
     
    sendTime:number //发送时间戳
    icon?:string,
    emoji?:EmojiVO,
    emitterNick?:string,
    status:number|null
}
export interface ChatGroup{
    groupId:string,
    msg:string,
    msgId:string,
    emitterId:string,
    createTime:number,
    icon?:string,
    nickName?:string,
    type?:string,
    emoji?:EmojiVO,
}

