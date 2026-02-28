import request from "@/utils/axios";
import {ApiHelper} from "@/utils/ApiHelper";
import {BusinessError} from "@/exception/BusinessError";
import type {mediaToken} from "@/types/auth";
import type {MediaApplyDTO} from "@/types/media";

export class MediaApi {
    /**
     * 获取媒体服务器的token
     */
    static async getMediaToken(body: mediaToken): Promise<string> {
        return ApiHelper.handle(request.post("/mediaToken", body));
    }

    /**
     * 发起视频聊天
     * @param dto 申请信息
     */
    static async send(dto: MediaApplyDTO): Promise<void> {
        if (!dto.receiverId) {
            throw new BusinessError("参数不能为空");
        }
        return ApiHelper.handle(request.post("/media/send", dto));
    }

    /**
     * 接受视频聊天
     * @param emitterId 申请者ID
     * @returns 房间号
     */
    static async accept(emitterId: string): Promise<string> {
        if (!emitterId) {
            throw new BusinessError("参数不能为空");
        }
        return ApiHelper.handle(request.get(`/media/accept/${emitterId}`));
    }

    /**
     * 拒绝视频聊天
     * @param emitterId 申请者ID
     */
    static async reject(emitterId: string): Promise<void> {
        if (!emitterId) {
            throw new BusinessError("参数不能为空");
        }
        return ApiHelper.handle(request.get(`/media/reject/${emitterId}`));
    }

    /**
     * 取消视频聊天
     * @param receiverId 接收者ID
     */
    static async cancel(receiverId: string): Promise<void> {
        if (!receiverId) {
            throw new BusinessError("参数不能为空");
        }
        return ApiHelper.handle(request.get(`/media/cancel/${receiverId}`));
    }
}
