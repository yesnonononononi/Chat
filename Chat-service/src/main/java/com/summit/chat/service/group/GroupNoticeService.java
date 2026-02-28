package com.summit.chat.service.group;

import com.summit.chat.Dto.GroupNoticeDTO;
import com.summit.chat.Result.Result;

import java.util.List;

public interface GroupNoticeService {

    /**
     * 发布群公告
     */
    Result publish(GroupNoticeDTO dto);

    /**
     * 根据ID查询群公告
     */
    Result getById(Long id);

    /**
     * 根据群ID查询群公告列表
     */
    Result listByGroupId(Long groupId);

    /**
     * 更新群公告
     */
    Result update(GroupNoticeDTO dto);

    /**
     * 删除群公告
     */
    Result delete(Long id, Long groupId);

    /**
     * 批量删除群公告
     */
    Result batchDelete(List<Long> ids,Long groupId);

    /**
     * 统计群组公告数量
     */
    Result countByGroupId(Long groupId);
}