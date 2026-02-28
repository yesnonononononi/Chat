import request from "@/utils/axios";
import {ApiHelper} from "@/utils/ApiHelper";
import type {
    GroupApplicationDTO,
    GroupChatDto,
    GroupMemberDTO,
    GroupMessageDTO,
    GroupMessageVO,
    putGroupDto,
} from "@/types/group";

export class GroupApi {
  // GroupController
  static async addGroup(dto: GroupChatDto): Promise<any> {
    return ApiHelper.handle(request.post("/group/add", dto));
  }

  static async delGroup(
    groupId: number | string,
    userId: string,
  ): Promise<any> {
    return ApiHelper.handle(
      request.delete("/group/del", { params: { groupId, userId } }),
    );
  }

  static async putGroup(dto: GroupChatDto): Promise<any> {
    return ApiHelper.handle(request.put("/group/update", dto));
  }

  static async queryGroupById(groupId: number | string): Promise<any> {
    return ApiHelper.handle(request.get(`/group/${groupId}`));
  }

  static async queryUserGroupByUserId(
    userId: string,
    page: number = 1,
    pageSize: number = 10,
  ): Promise<any> {
    return ApiHelper.handle(
      request.get(`/group/user/${userId}`, { params: { page, pageSize } }),
    );
  }

  static async queryGroupByName(
    name: string,
    page: number = 1,
    pageSize: number = 10,
  ): Promise<any> {
    return ApiHelper.handle(
      request.get(`/group/query`, { params: { name, page, pageSize } }),
    );
  }

  static async queryGroupByUserID(userId: string):Promise<GroupChatDto[]> {
    return ApiHelper.handle(
      request.get(`/group/user`, {
        params:{userId: userId,}
      }),
    );
  }



  // GroupMemberController
  static async addMember(dto: GroupMemberDTO): Promise<any> {
    return ApiHelper.handle(request.post("/group/member/add", dto));
  }

  static async delMember(dto: GroupMemberDTO): Promise<any> {
    return ApiHelper.handle(request.post("/group/member/del", dto));
  }

  static async queryMemberByGroupId(
    groupId: number | string,
    page: number = 1,
    pageSize: number = 10,
  ): Promise<any> {
    return ApiHelper.handle(
      request.get(`/group/member/list/${groupId}`, {
        params: { page, pageSize },
      }),
    );
  }
 

  /* 
  * 封禁
   */
   static async banMember(body:putGroupDto){
    return ApiHelper.handle(request.put("/group/member/set", body))
  }

 
  // GroupMessageController
  static async queryGroupMsgById(
    groupId: number | string,
    page: number = 1,
    pageSize: number = 10,
  ): Promise<any> {
    return ApiHelper.handle(
      request.get(`/group/msg/history/${groupId}`, {
        params: { page, pageSize },
      }),
    );
  }

  static async queryGroupMsgByUserId(
    dto: GroupMessageDTO,
    page: number = 1,
    pageSize: number = 10,
  ): Promise<any> {
    return ApiHelper.handle(
      request.post("/group/msg/user/history", dto, {
        params: { page, pageSize },
      }),
    );
  }

  static async withdrawnGroupMsg(vo: GroupMessageVO): Promise<any> {
    return ApiHelper.handle(request.post("/group/msg/withdrawn", vo));
  }

  // GroupApplicationController
  static async applyGroup(dto: GroupApplicationDTO): Promise<any> {
    return ApiHelper.handle(request.post("/group-apply/apply", dto));
  }

  static async approveGroupApply(dto: GroupApplicationDTO): Promise<any> {
    return ApiHelper.handle(request.post("/group-apply/approve", dto));
  }

  static async rejectGroupApply(dto: GroupApplicationDTO): Promise<any> {
    return ApiHelper.handle(request.post("/group-apply/reject", dto));
  }

  static async listGroupApplyByGroup(groupId: number | string): Promise<any> {
    return ApiHelper.handle(request.get(`/group-apply/group/${groupId}`));
  }

  static async listGroupApplyByUser(): Promise<any> {
    return ApiHelper.handle(request.get("/group-apply/user"));
  }

  static async publishGroupNotice(dto: GroupChatDto): Promise<any> {
    return ApiHelper.handle(request.post("/group-notice/publish", dto));
  }

  static async queryNotify(groupId: number | string): Promise<any> {
    return ApiHelper.handle(request.get(`/group-notice/group/${groupId}`));
  }

  static async delNotify(groupId:string,notifyId:string){
    return ApiHelper.handle(request.post(`/group-notice/del/${groupId}/${notifyId}`))
  }
}
