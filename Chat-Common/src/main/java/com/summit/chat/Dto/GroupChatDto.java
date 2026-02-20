package com.summit.chat.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 群聊信息表Dto
 * @create 2020-03-09 16:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatDto implements Serializable {
    private Long id;
    private String groupName;
    private Long creatorId;
    private String groupDescription;
    private Integer status;
    private String icon;

}
