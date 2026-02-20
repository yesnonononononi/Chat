export interface data<T>{
    code: number,
    msg: string,
    data: T,
    sign?: string
}

export interface page<T>{
    total:number,
    records: T
}

export interface userVO{
    expiration:string,
    token:string,
    userVO:userInfo
}

export interface userInfo{
    nickName:string,
    icon: string,
    id: string,
    status?:number,
    ip?:string,
    gender?:number,
    age?:number,
    hobby?:string,
    mobile:string,
    email?:string,
    createTime?:string,
    role?:string
}


export interface search_user{
   nickName:string,
   id:string,
   icon:string,
   hobby:string,
   gender:number
}

export interface userPwPutDto{
    id:string,
    oldPw?:string,
    pw?:string,
    mobile?:string,
    verifyCode?:string
}
