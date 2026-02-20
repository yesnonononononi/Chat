package com.summit.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSession implements Serializable {
    private String userId;   //用户id
    private UUID sessionId;  //会话id
    private boolean isDoNotDisturb;  //是否为免打扰
    private String linkTime; //连接时间
    private String clientIP;//客户端IP
}
