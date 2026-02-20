export interface friend_apply {
  applyReason: string;
  icon: string;
  recipientId: string;
  applicantId: string;
  id: string;
  status: number;
  applyTime: string;
  handleTime: string;
  applicantNick: string;
}

export interface friend_apply_dto {
  id: string;
  applyReason: string;
  recipientId:string;
  applicantId:string;
}

export interface friend_info {
  isFrequent: string;
  nickName: string;
  remark: string;
  icon: string;
  linkId: string;
  isDelete: string;
}

export interface UserLinkDto {
    linkID: string;
    [key: string]: any;
}

export interface FriendDto extends Partial<friend_apply_dto> {
    [key: string]: any;
}
