/**
 * 系统公告类型定义
 */
export interface SysNotice {
    id?: number;
    msg: string;
    createTime?: string;
    endTime?: string;
    publisherId?: string;
    publisherName?: string;
}

export interface SysNoticeDto {
    id?: number;
    msg: string;
    endTime?: string;
    publisherId?: string;
}
