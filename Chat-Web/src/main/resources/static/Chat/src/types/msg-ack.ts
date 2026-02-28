 

export interface MsgACKOfServer{
    msgCode:number,
    description:string,
    symbol:string,
    msgId:string
}


export interface withdrawn{
    type:'group'|'private',
    msgId:string,
    emitterId:string,
    receiverId?:string,
    groupId?:string
}