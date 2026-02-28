package com.summit.chat.service.SysNotice;

import com.summit.chat.Dto.SysNoticeDTO;

public interface SysNoticeSupport {
    public void publishToClient(SysNoticeDTO dto);
}
