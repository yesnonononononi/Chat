package com.summit.chat.service.Impl.Support.group.GroupNoticeSupport;

import com.summit.chat.Constants.GroupNoticeConstants;
import com.summit.chat.Dto.GroupNoticeDTO;
import com.summit.chat.Mapper.Mysql.GroupNoticeMapper;
import com.summit.chat.model.vo.GroupNoticeVO;
import com.summit.chat.service.Impl.Support.group.AbstractGroupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 群公告校验类
 */
@Component
public class GroupNoticeValidator extends AbstractGroupValidator<GroupNoticeDTO> {

    @Autowired
    private GroupNoticeMapper groupNoticeMapper;

    /**
     * 校验发布参数
     * @param dto 群公告数据传输对象
     */
    public void verifyPublish(GroupNoticeDTO dto) {
        String content = dto.getContent();
        if (dto.getGroupId() == null) {
            super.throwException(GroupNoticeConstants.GROUP_ID_NULL);
        }
        if (dto.getPublisherId() == null) {
            super.throwException(GroupNoticeConstants.PUBLISHER_ID_NULL);
        }
        if (dto.getContent() == null ||content.trim().isEmpty()) {
            super.throwException(GroupNoticeConstants.NOTICE_CONTENT_NULL);
        }
        if(content.length() > GroupNoticeConstants.DEFAULT_MAX_LENGTH){
            super.throwException(GroupNoticeConstants.NOTICE_CONTENT_TOO_LONG);
        }
    }

    /**
     * 校验更新参数
     * @param dto 群公告数据传输对象
     * @param operatorId 操作人ID
     */
    public void verifyUpdate(GroupNoticeDTO dto, String operatorId) {
        if (dto.getId() == null) {
            super.throwException(GroupNoticeConstants.NOTICE_ID_NULL);
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            super.throwException(GroupNoticeConstants.NOTICE_CONTENT_NULL);
        }

        // 查询现有公告
        GroupNoticeVO existingNotice = groupNoticeMapper.selectById(dto.getId());
        if (existingNotice == null) {
            super.throwException(GroupNoticeConstants.NOTICE_NOT_EXIST);
        }

        // 校验是否有资格更新
        verifyOwnerOrAdmin(getMember(existingNotice.getGroupId(), Long.valueOf(operatorId)));
    }

    /**
     * 校验存在性
     * @param id 公告ID
     */
    public void verifyExists(Long id,Long groupId) {
        if (id == null) {
            super.throwException(GroupNoticeConstants.NOTICE_ID_NULL);
        }
        GroupNoticeVO notice = groupNoticeMapper.selectById(id);
        if (notice == null) {
            super.throwException(GroupNoticeConstants.NOTICE_NOT_EXIST);
        }
        if (!notice.getGroupId().equals(groupId)) {
            super.throwException(GroupNoticeConstants.NOTICE_NOT_EXIST);
        }
    }

    /**
     * 校验批量删除参数
     * @param ids 公告ID列表
     */
    public void verifyBatchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            super.throwException(GroupNoticeConstants.NOTICE_ID_LIST_NULL);
        }
    }



    @Override
    public boolean validate(GroupNoticeDTO dto) {
        return false;
    }
}
