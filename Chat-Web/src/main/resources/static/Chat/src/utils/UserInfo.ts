import type {userInfo} from "@/types/user";
import {reactive} from "vue";
import {Log} from "./TipUtil";
import {BusinessError} from "@/exception/BusinessError";
import {UserApi} from "@/api/user";

const info: userInfo = reactive({
  id: "",
  nickName: "",
  mobile:"",
  icon: "",
  status: 1,
  token: "",
  ip: "",
  age: 0,
  hobby: "",
  gender: 1,
  isVIP:1,
  superVip:0,
  email:""
});


export async function getUserById(id: string) {
  try {
    return await UserApi.getUserInfoById(id);
  } catch (error: any) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    } else {
      Log.error("服务繁忙");
      console.error("获取好友信息失败", error);
    }
    return null;
  }
}
export async function getUserInfo():Promise<userInfo> {
  const USERID = getUser();
  if(USERID == null){
    console.error("未找到用户信息")
    return info;
  }
  try {
    const data = await UserApi.getUserInfoById(USERID);
    Object.keys(info).forEach(key => {
        const typeKey = key as keyof userInfo;
        (info as any)[typeKey] = data[typeKey];
    })
    return info;
  } catch (error) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    } else {
      Log.error("服务繁忙");
      console.error(error);
    }
    return info;
  }
}

export function getInfo(){
  return info;
}


export function removeUser(){
    localStorage.removeItem("USERID");
}

export function setUser(id:string){
  localStorage.setItem("USERID",id)
}
export function getUser():string|null{
  return localStorage.getItem("USERID");
}
export function setUserToken(token: string) {
  localStorage.setItem("userToken", token);
}

export function removeUserToken(){
  localStorage.removeItem("userToken");
}

export function getUserToken(){
  return localStorage.getItem("userToken");
}



export function handleAfterLoginAndRegister(id:string,token:string){
  console.debug("用户id:",id
  )
  if(getUser()){
    removeUser();
  }
  setUser(id);
  setUserToken(token);
}