import type {chat} from "./chat"

export interface systemMsg{
    msgId:string,
    msg:string,
    role:'System-tip'|'System-error'|'System-primary'
}

export interface historyPage{
    dto:chat,
    page:number,
    pageSize:number
}



