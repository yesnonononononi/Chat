export interface WorkSpace {
  todayNewUser: number;
  totalUser: number;

  totalMsg: number;
  todayNewMsg: number;
  onlineUser?: number;
  date?: string;
}
export interface userActiveVO {
  userID: string;
  nickName: string;
  icon: string;
  active: number;
}

export interface UserTotalActiveVO{
  userID: string;
  nickName: string;
  icon: string;
  totalActive: number;
  rank: number;
}
