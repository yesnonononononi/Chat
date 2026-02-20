import request from "@/utils/axios";
import { ApiHelper } from "@/utils/ApiHelper";
import type { data, userInfo,userPwPutDto,userVO } from "@/types/user";
import { BusinessError } from "@/exception/BusinessError";

export class UserApi {
  /**
   * 根据好友id获取好友基本信息
   */
  static async getUserInfoById(id: string): Promise<userInfo> {
    if (!id || id.length < 8) {
      throw new BusinessError("非法参数");
    }
    return ApiHelper.handle(request.get(`/user/${id}`));
  }

  static async putUserInfoByInfo(newInfo: userInfo): Promise<boolean> {
    if (
      !newInfo ||
      !newInfo.id ||
      newInfo.gender == null ||
     (newInfo.age && (newInfo.age > 100 ||
      newInfo.age < 0 ))||
      (newInfo.gender != 1 && newInfo.gender != 2)
    ) {
      throw new BusinessError("非法参数,请检查");
    }
 
    await ApiHelper.handle(request.put("/user/edit", newInfo));
    return true;
  }

  static async logOff(userID: string): Promise<boolean> {
    if (!userID) {
      throw new BusinessError("非法参数");
    }
    await ApiHelper.handle(request.put("/user/logOff", {}, {
      params: {
        userID: userID,
      },
    }));
    return true;
  }

  static async uploadAvatar(formData: FormData): Promise<string> {
    return ApiHelper.handle(request.post("/user/avatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }));
  }

  static async deleteUser(id: string): Promise<boolean> {
    if (!id || id.length < 8) {
      throw new BusinessError("非法参数,请检查");
    }
    await ApiHelper.handle(request.put(`/user/del/${id}`));
    return true;
  }

  static async getUserInfoByPhone(phoneNumber: string): Promise<userInfo> {
    if (!phoneNumber || phoneNumber.length !== 11) {
      throw new BusinessError("非法参数,请检查");
    }
    return ApiHelper.handle(request.get("/user", {
      params: { phoneNumber: phoneNumber },
    }));
  }

  static async getUserInfoByNick(nickName: string): Promise<userInfo> {
    if (!nickName) {
      throw new BusinessError("非法参数,请检查");
    }
    return ApiHelper.handle(request.get("/user/nickName", {
      params: { nickName: nickName },
    }));
  }

  static async login(data: any): Promise<userVO> {
    return ApiHelper.handle(request.post("/user/password-login", data));
  }

  static async register(data: any): Promise<userVO> {
    return ApiHelper.handle(request.post("/user/register", data));
  }

  static async sendCode(phoneNumber: string): Promise<any> {
    return ApiHelper.handle(request.get("/user/send-sms-code", { params: { phoneNumber } }));
  }

  static async loginByCode(data: any): Promise<userVO> {
    return ApiHelper.handle(request.post("/user/sms-login", data));
  }

  static async loginOut(token: string): Promise<any> {
    return ApiHelper.handle(request.get("/user/loginout", { params: { token } }));
  }

    static async putPw(body:userPwPutDto){
      return ApiHelper.handle(request.post("/user/pw",body));
    }
 

    static async forgetPw(body:userPwPutDto){
      return ApiHelper.handle(request.post("/user/forget",body));
    }
}

