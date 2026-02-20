package com.summit.chat.service.Impl.Support.SysNoticeSupport;

import com.summit.chat.Constants.SysNoticeConstants;
import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.Mapper.SysNoticeMapper;
import com.summit.chat.model.vo.SysNoticeVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统公告校验类
 */
@Component
public class SysNoticeValidator extends GlobalValidatorImpl<SysNoticeDTO> {

    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    @Override
    public boolean validate(SysNoticeDTO dto) {
        if (dto == null) {
            return false;
        }
        if (dto.getMsg() == null || dto.getMsg().trim().isEmpty()) {
            super.throwException(SysNoticeConstants.NOTICE_MSG_NULL);
        }
        return true;
    }

    /**
     * 校验发布参数
     */
    public void verifyPublish(SysNoticeDTO dto) {
        if (dto.getMsg() == null || dto.getMsg().trim().isEmpty()) {
            super.throwException(SysNoticeConstants.NOTICE_MSG_NULL);
        }
        if (dto.getPublisherId() == null) {
            super.throwException(SysNoticeConstants.PUBLISHER_ID_NULL);
        }
    }

    /**
     * 校验更新参数
     */
    public void verifyUpdate(SysNoticeDTO dto) {
        if (dto.getId() == null) {
            super.throwException(SysNoticeConstants.NOTICE_ID_NULL);
        }
        verifyExists(dto.getId());
        if (dto.getMsg() != null && dto.getMsg().trim().isEmpty()) {
            super.throwException(SysNoticeConstants.NOTICE_MSG_NULL);
        }
    }

    /**
     * 校验公告是否存在
     */
    public void verifyExists(Long id) {
        if (id == null) {
            super.throwException(SysNoticeConstants.NOTICE_ID_NULL);
        }
        SysNoticeVO notice = sysNoticeMapper.selectById(id);
        if (notice == null) {
            super.throwException(SysNoticeConstants.NOTICE_NOT_EXIST);
        }
    }
}
