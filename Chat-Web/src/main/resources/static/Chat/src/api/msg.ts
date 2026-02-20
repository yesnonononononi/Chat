import type { chat } from "@/types/chat";
import type { historyPage } from "@/types/msg";
import request from "@/utils/axios";
import { ApiHelper } from "@/utils/ApiHelper";

export class MsgApi {
  /**
   * 根据发送者id和接收者id获取历史消息
   */
  static async getHistoryByDto(dto: historyPage): Promise<any> {
    if (!dto.dto || !dto.dto.emitterId || !dto.dto.receiveId) {
      throw new Error("非法参数,请检查");
    }
    if (!dto.dto.msg) dto.dto.msg = "__DEFAULT_HISTORY__";
    return ApiHelper.handle(request.post("/msg/history", dto));
  }

  /**
   * 模糊查询聊天记录(暂时不用,未做分页)
   */
  static async getMsgByName(dto: chat): Promise<chat[]> {
    if (!dto || !dto.msg.trim()) {
      throw new Error("非法参数,请检查");
    }
    if (!dto.emitterNick) dto.emitterNick = "__DEFAULT_NICK__";
    return ApiHelper.handle(request.post("/msg", dto));
  }

  /**
   * 撤回消息
   */
  static async withDrawnMsg(dto: chat): Promise<boolean> {
    if (!dto || !dto.msg.trim() || !dto.msgId || !dto.sendTime) {
      throw new Error("非法参数,请检查");
    }
    if (!dto.emitterNick) dto.emitterNick = "__DEFAULT_NICK__";
    await ApiHelper.handle(request.post("/msg/withdrawn", dto));
    return true;
  }

  /**
   * 标记消息为已读
   */
  static async readMsg(emitterId: string): Promise<any> {
    return ApiHelper.handle(request.get("/msg/read", { params: { emitterId: emitterId } }));
  }

  static async queryById(id: string): Promise<any> {
    return ApiHelper.handle(request.get(`/msg/${id}`));
  }

  static async saveMsg(dto: any): Promise<any> {
    return ApiHelper.handle(request.post("/msg/save", dto));
  }
}
