import type { chat } from "@/types/chat";
export interface GlobalValidate<T>{
  validate:(t:T)=>boolean,
  throwError:(msg:string)=>void
}



export class validate implements GlobalValidate<chat>{
    public validate(msg:chat):boolean{
        if(!msg){
            this.throwError("非法参数");
        }
        if(!msg.emitterId||!msg.receiveId||!msg.msg){
            this.throwError("发送消息体缺少关键参数");
        }
        if(msg.emitterId == msg.receiveId){
            this.throwError("不能自己给自己发消息");
        }
        if(msg.msg.trim().length <1 ||msg.msg.length >80){
            this.throwError("消息长度不能超过80");
        }
        return true;
    }
    public throwError(msg:string){
        throw new Error(msg)
    }
}