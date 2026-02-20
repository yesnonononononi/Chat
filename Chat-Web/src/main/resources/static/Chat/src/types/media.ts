export interface MediaApplyDTO {
    userId?: string; // 接收时会有，发送时不需要
    receiverId: string;
    nickName: string;
    icon: string;
}
