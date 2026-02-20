import request from "@/utils/axios";
import { ApiHelper } from "@/utils/ApiHelper";
import type { userVO } from "@/types/user";
import { BusinessError } from "@/exception/BusinessError";

export class AdminApi {
  static async getUsers(params: any): Promise<any> {
    return ApiHelper.handle(request.get("/admin/users", { params }));
  }

  static async blackUser(userID: string): Promise<void> {
    if (!userID) {
      throw new BusinessError("非法参数");
    }
    await ApiHelper.handle(request.post("/admin/black", { userID: userID }));
  }

  static async unblackUser(userID: string): Promise<void> {
    if (!userID) {
      throw new BusinessError("非法参数");
    }
    await ApiHelper.handle(request.post("/admin/unblack", { userID }));
  }

  static async setAdmin(userID: string): Promise<void> {
    if (!userID) {
      throw new BusinessError("非法参数");
    }
    await ApiHelper.handle(request.post("/admin/setAdmin", { userID }));
  }

  static async getLatestWorkSpaceData(): Promise<any> {
    return ApiHelper.handle(request.get("/admin/statics/workspace/latest"));
  } 
  
  static async queryGroupList(page:number,pageSize:number): Promise<any> {
    return ApiHelper.handle(request.get(`/admin/group/list`,{params:{page,pageSize}}));
  }

  static async auth(){
    return ApiHelper.handle(request.get("/admin/auth"));
  }

  static async banGroup(groupId:string){
    return ApiHelper.handle(request.delete("/admin/group/ban",{params:{groupId}}));
  } 
  static async unBanGroup(groupId:string){
    return ApiHelper.handle(request.get("/admin/group/unban",{params:{groupId}}));
  }
}
