import request from "@/utils/axios";
import { ApiHelper } from "@/utils/ApiHelper";
import type { FriendDto, UserLinkDto } from "@/types/friend";

export class FriendApi {
  // FriendApplyController
  static async sendApplication(dto: FriendDto): Promise<any> {
    return ApiHelper.handle(request.post("/friend/apply", dto));
  }

  static async ackApplication(dto: FriendDto): Promise<any> {
    return ApiHelper.handle(request.post("/friend/ack", dto));
  }

  static async rejectApplication(dto: FriendDto): Promise<any> {
    return ApiHelper.handle(request.post("/friend/reject", dto));
  }

  static async queryApplication(page: number = 1, pageSize: number = 10): Promise<any> {
    return ApiHelper.handle(request.get("/friend/query", { params: { page, pageSize } }));
  }

  // UserLinkController
  static async getLinkList(): Promise<any> {
    return ApiHelper.handle(request.get("/user/link-user"));
  }

  static async saveLink(dto: UserLinkDto): Promise<any> {
    return ApiHelper.handle(request.post("/user/link-user/save", dto));
  }

  static async getLInkById(){}
}
