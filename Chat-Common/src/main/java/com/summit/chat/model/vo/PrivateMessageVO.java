package com.summit.chat.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.summit.chat.model.entity.Msg;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统向单个用户广播的vo
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateMessageVO extends Msg {
    @NotBlank(message = "消息发送者昵称不能为空")
    private String emitterNick;
    private String icon;
    private Long sendTime;
    private Integer status; //0 已发送 1已撤回 2未发送
}
