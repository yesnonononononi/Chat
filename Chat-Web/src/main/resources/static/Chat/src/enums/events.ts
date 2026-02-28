export enum Event{
 // 一对一聊天
  CHAT_PRIVATE_SEND = "chat_private_send",
  CHAT_PRIVATE_RECEIVE = "chat_private_receive",
  // 消息回执
  CHAT_DELIVERED = "chat_delivered",
  CHAT_ERROR = "chat_error",
  CHAT_WITHDRAWN = "chat_withdrawn", // 消息撤回
  // 群聊
  JOIN_GROUP = "join_group",
  CHAT_GROUP_SEND = "chat_group",
  CHAT_GROUP_RECEIVE = "chat_group_receive",
  // 系统事件（Socket.IO 内置/自定义）
  CONNECT_SUCCESS = "connect_success",
  SYSTEM_NOTICE = "system_notice",
  UN_LOGIN = "un_login",
  KICK_USER = "force_logout",
  //视频聊天
  MEDIA_APPLY_SEND = "media_apply_send",
  MEDIA_APPLY_ACCEPT="media_apply_accept",
  MEDIA_APPLY_REJECT= "media_apply_reject",
  MEDIA_APPLY_CANCEL = "media_apply_cancel"
}

export enum Status{
    CONNECT = "connect",
    DISCONNECT = "disconnect",
    CONNECT_ERROR= "connect_error",
    CONNECT_RECONNECT = "reconnect",
    ERROR = "error"
}
