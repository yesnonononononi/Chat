import request from "@/utils/axios";
import {ApiHelper} from "@/utils/ApiHelper";
import {BusinessError} from "@/exception/BusinessError";

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

  static async delAdmin(userID: string) {
    return ApiHelper.handle(request.post("/admin/delAdmin", { userID }));
  }

  static async getLatestWorkSpaceData(): Promise<any> {
    return ApiHelper.handle(request.get("/admin/statics/workspace/latest"));
  }

  static async getWorkSpaceDataByDate(dateList: string[]): Promise<any> {
    return ApiHelper.handle(
      request.post("/admin/statics/workspace/list", {
        dateList,
      }),
    );
  }

  static async queryGroupList(page: number, pageSize: number): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/group/list`, { params: { page, pageSize } }),
    );
  }

  static async auth() {
    return ApiHelper.handle(request.get("/admin/auth"));
  }

  static async banGroup(groupId: string) {
    return ApiHelper.handle(
      request.delete("/admin/group/ban", { params: { groupId } }),
    );
  }
  static async unBanGroup(groupId: string) {
    return ApiHelper.handle(
      request.get("/admin/group/unban", { params: { groupId } }),
    );
  }

  static async getUserActive(): Promise<any> {
    return ApiHelper.handle(request.get("/admin/statics/user/active"));
  }

  static async getUserActiveByDate(dateList: string[]): Promise<any> {
    return ApiHelper.handle(
      request.post("/admin/statics/user/active/list", {
        dateList,
      }),
    );
  }

  // 获取用户活跃度topN
  static async getUserAllActiveTOPN(n: number):Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/top`, { params: { n } }),
    );
  }

  // 获取所有用户活跃度
  static async getAllUserActive():Promise<any>  {
    return ApiHelper.handle(request.get(`/admin/statics/user-active/all`));
  }
  /*
   * 获取当前用户总活跃度
   */
  static async getCurrentUserTotalActive():Promise<any>  {
    return ApiHelper.handle(request.get(`/admin/statics/user-active/me`));
  }

  /* 获取指定用户总活跃度 */
  static async getUserTotalActiveById(userID: string) :Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/${userID}`),
    );
  }
  /* 获取指定用户排名 */
  static async getUserRank(userID: string):Promise<any>  {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/rank/${userID}`),
    );
  }
  /* 获取当前用户排名 */
  static async getCurrentUserRank(userID: string):Promise<any>  {
    return ApiHelper.handle(request.get(`/admin/statics/user-active/rank/me`));
  }

  // ==================== 月度活跃度统计API ====================
  
  /**
   * 查询月度用户活跃度排行榜前N名
   * @param limit 限制数量
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getMonthlyUserActiveTopN(limit: number, yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/top`, { 
        params: { limit, yearMonth } 
      }),
    );
  }

  /**
   * 查询指定月份的所有用户活跃度数据
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getMonthlyAllUserActive(yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/all`, { 
        params: { yearMonth } 
      }),
    );
  }

  /**
   * 查询当前用户在指定月份的活跃度
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getCurrentMonthlyUserActive(yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/me`, { 
        params: { yearMonth } 
      }),
    );
  }

  /**
   * 查询指定用户在指定月份的活跃度
   * @param userID 用户ID
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getMonthlyUserActiveById(userID: string, yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/${userID}`, { 
        params: { yearMonth } 
      }),
    );
  }

  /**
   * 查询当前用户在指定月份的排名
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getCurrentMonthlyUserRank(yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/rank/me`, { 
        params: { yearMonth } 
      }),
    );
  }

  /**
   * 查询指定用户在指定月份的排名
   * @param userID 用户ID
   * @param yearMonth 年月格式(yyyy-MM)
   */
  static async getMonthlyUserRank(userID: string, yearMonth: string): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/monthly/rank/${userID}`, { 
        params: { yearMonth } 
      }),
    );
  }

  // ==================== 周活跃度统计API ====================
  
  /**
   * 查询本周用户活跃度排行榜前10名
   */
  static async getWeeklyUserActiveTop10(): Promise<any> {
    return ApiHelper.handle(
      request.get(`/admin/statics/user-active/weekly/top10`),
    );
  }
}
