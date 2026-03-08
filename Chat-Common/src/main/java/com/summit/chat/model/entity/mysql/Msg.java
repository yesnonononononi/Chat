package com.summit.chat.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Msg implements Serializable {
    @NotBlank(message = "发送者id不能为空")
    public String emitterId;
    @NotBlank(message = "接收者id不能为空")
    public String receiveId;
    public String msgId;
    @NotBlank(message = "消息内容不能为空")
    public String msg;
}
