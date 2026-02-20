package com.summit.chat.Dto;

import lombok.Data;
/**
 * 群聊信息表Dto
 * @create 2020-03-09 16:07
 */
@Data
public class GroupChatDto {
    private Long id;
    private String groupName;
    private Long creatorId;
    private String groupDescription;
    private Integer status;
    private String icon;

}
