package com.summit.chat.model.vo;

import com.summit.chat.Enum.MsgEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 发给客户端发送者的回执
 * msgId:消息id
 */
@Data
@Builder
public class MsgCallbackForPrivateVO {
    /**
     * 消息状态
     *  READ(0),  //已读
     *  WITHDRAWN(1), //已撤回
     *  NOT_ONLINE(2),//未读
     *  FAIL(3);  //发送失败
     */
    private Integer msgCode;
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息标志
     */
    private String symbol;
    /**
     * 消息描述
     */
    private String description;

    @JsonCreator
    public MsgCallbackForPrivateVO(
            @JsonProperty("msgCode") Integer msgCode,
            @JsonProperty("msgId") String msgId,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("description") String description) {
        this.msgCode = msgCode;
        this.msgId = msgId;
        this.symbol = symbol;
        this.description = description;
    }
}
