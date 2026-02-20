package com.summit.chat.service.group;

import com.summit.chat.Dto.GroupApplicationDTO;
import com.summit.chat.Result.Result;

public interface GroupApplicationService {

    /**
     * 申请加入群聊
     */
    Result apply(GroupApplicationDTO dto);

    /**
     * 同意申请
     */
    Result approve(GroupApplicationDTO dto);

    /**
     * 拒绝申请
     */
    Result reject(GroupApplicationDTO dto);

    /**
     * 获取群聊的申请列表
     */
    Result listByGroup(Long groupId, Long userId);

    /**
     * 获取用户的申请列表
     */
    Result listByUser(Long userId);
}
