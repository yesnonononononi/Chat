package com.summit.chat.service.SysNotice;

import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.Result.Result;

public interface SysNoticeService {
    
    /**
     * 发布系统公告
     */
    Result publish(SysNoticeDTO dto);

    /**
     * 根据ID查询系统公告
     */
    Result getById(Long id);

    /**
     * 查询所有有效的系统公告
     */
    Result list();

    /**
     * 更新系统公告
     */
    Result update(SysNoticeDTO dto);

    /**
     * 删除系统公告
     */
    Result delete(Long id);

    Result like(String id);

    Result queryLikeList(String ids);
}
