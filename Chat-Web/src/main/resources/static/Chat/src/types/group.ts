export interface GroupChatDto {
  id: string;
  groupName?: string;
  groupDescription?: string;
  creatorId?: string;
  icon?: string;
  [key: string]: any;
}
export interface Group extends GroupChatDto {
  number: number;
  status: number;
  createTime: string;
  [key: string]: any;
}
export interface GroupChatVO extends GroupChatDto {
  createTime: string;
  status: number;
  number: number;
}
export interface GroupMemberDTO {
  groupId: string;
  userId: string;
  [key: string]: any;
}

export interface GroupMemberVO {
  id: string;
  nickName: string;
  avatar: string;
  role: string;
}

export interface GroupMessageDTO {
  groupId: string;
  userId?: string;
  msg?: string;
  [key: string]: any;
}

export interface GroupApplicationDTO {
  groupId: string;
  applicationReason?: string;
  applicantId?: string;
  processedBy?: string;
  [key: string]: any;
}

export interface GroupApplicationVO {
  id: string;
  groupId: string;
  groupName: string;
  groupIcon?: string;
  applicantId: string;
  applicantNick?: string;
  applicantIcon?: string;
  applicationReason?: string;
  status: string; // 0, 1, 2
  rejectionReason?: string;
  createdAt?: string;
  processedAt?: string;
  [key: string]: any;
}

export interface GroupMessageVO {
  msgId: string;
  [key: string]: any;
}

export interface groupNoticeVO {
  id: string;
  groupId: string;
  publisherId: string;
  publisherName: string;
  publisherAvatar: string;
  /**
   * 公告内容
   */
  content: string;
  /**
   * 创建时间
   */
  createTime: string;
  /**
   * 更新时间
   */
  updateTime: string;
}
