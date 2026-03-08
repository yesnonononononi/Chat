import request from "@/utils/axios";
import {ApiHelper} from "@/utils/ApiHelper";
import type {SysNoticeDto} from "@/types/sysNotice";

/**
 * 系统公告 API
 */
export class SysNoticeApi {
    /**
     * 发布系统公告
     */
    static async publish(dto: SysNoticeDto): Promise<any> {
        return ApiHelper.handle(request.post("/sys-notice/publish", dto));
    }

    /**
     * 根据ID查询系统公告
     */
    static async getById(id: number): Promise<any> {
        return ApiHelper.handle(request.get(`/sys-notice/${id}`));
    }

    /**
     * 查询所有有效的系统公告
     */
    static async list(): Promise<any> {
        return ApiHelper.handle(request.get("/sys-notice/list"));
    }

    static async likeList(ids:string): Promise<any> {
        if (!ids) return;
        return ApiHelper.handle(request.get(`/sys-notice/queryLikeList/${ids}`));
    }

    /**
     * 更新系统公告
     */
    static async update(dto: SysNoticeDto): Promise<any> {
        return ApiHelper.handle(request.put("/sys-notice/update", dto));
    }

    /**
     * 逻辑删除系统公告
     */
    static async delete(id: number): Promise<any> {
        return ApiHelper.handle(request.delete(`/sys-notice/${id}`));
    }

    static async like(id:number):Promise<any>{
        return ApiHelper.handle(request.get(`/sys-notice/like/${id}`))
    }
}
