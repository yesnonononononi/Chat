import type { serverMsgCode } from "@/enums/server-callback";

export interface MsgACKOfServer{
    msgCode:number,
    description:string,
    symbol:string,
    msgId:string
}