package com.summit.chat.service.SysNotice;

import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.model.vo.SysNoticeVO;

public interface SysNoticeSupport {
    public void publishToClient(SysNoticeDTO dto);
}
