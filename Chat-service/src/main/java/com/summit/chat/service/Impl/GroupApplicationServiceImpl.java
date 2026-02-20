package com.summit.chat.service.Impl;

import com.summit.chat.Constants.GroupApplicationConstants;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Dto.GroupApplicationDTO;
import com.summit.chat.Enum.GroupApplicationEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.GroupApplicationMapper;
import com.summit.chat.Mapper.GroupMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.GroupApplications;
import com.summit.chat.model.vo.GroupApplicationVO;
import com.summit.chat.service.Impl.Support.group.GroupApplicationSupport.GroupApplicationValidator;
import com.summit.chat.service.Impl.Support.group.GroupApplicationSupport.GroupApplySupport;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberServiceCacheSupport;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupServiceCacheSupport;
import com.summit.chat.service.group.GroupApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class GroupApplicationServiceImpl implements GroupApplicationService {


    @Autowired
    private GroupApplicationMapper groupApplicationMapper;

    @Autowired
    private GroupApplicationValidator groupApplicationValidator;


    @Autowired
    private GroupMemberServiceCacheSupport groupMemberServiceCacheSupport;
    @Autowired
    private GroupServiceCacheSupport groupServiceCacheSupport;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupApplySupport groupApplySupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result apply(GroupApplicationDTO dto) {
        // 校验基础参数
        groupApplicationValidator.validateApply(dto);

        // 校验群组是否存在
        groupApplicationValidator.isGroupExist(dto.getGroupId());

        // 校验是否已经是成员
        groupApplicationValidator.isAlreadyMember(dto.getGroupId(), dto.getApplicantId());

        // 校验是否有待处理申请
        groupApplicationValidator.hasPendingApplication(dto.getGroupId(), dto.getApplicantId());

        // 插入申请
        groupApplicationMapper.insert(dto);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result approve(GroupApplicationDTO dto) {
        // dto需要包含 id (申请ID), processedBy (处理人ID)
        GroupApplications app = groupApplicationMapper.selectById(dto.getId());
        if (app == null) {
            return Result.fail(GroupApplicationConstants.APPLY_NO_EXIST);
        }
        //群聊是否存在以及是否被封禁
        groupApplicationValidator.isGroupExist(app.getGroupId());
        // 校验权限
        groupApplicationValidator.hasPermission(app.getGroupId(), dto.getProcessedBy());

        // 校验是否已经是成员 (防止重复同意)
        try {
            groupApplicationValidator.isAlreadyMember(app.getGroupId(), app.getApplicantId());
        } catch (BusinessException e) {
            // 如果已经是成员，直接把申请状态改为已同意即可，或者返回成功
            groupApplicationMapper.updateStatus(dto.getId(), GroupApplicationEnum.APPROVE.getStatus(), dto.getProcessedBy(), null);
            return Result.ok(GroupConstants.ALREADY_GROUP_MEMBER);
        }
        // 更新申请状态为已同意 (1)
        groupApplicationMapper.updateStatus(dto.getId(), GroupApplicationEnum.APPROVE.getStatus(), dto.getProcessedBy(), null);
        groupApplySupport.addMember(app);
        // 更新群组人数
        groupMapper.updateGroupNumber(app.getGroupId());
        // 删除缓存
        groupMemberServiceCacheSupport.evictGroupMemberCache(app.getGroupId());
        groupServiceCacheSupport.evictGroupInfoCache(app.getGroupId());

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result reject(GroupApplicationDTO dto) {
        GroupApplications app = groupApplicationMapper.selectById(dto.getId());
        if (app == null) {
            return Result.fail(GroupApplicationConstants.APPLY_NO_EXIST);
        }

        //群聊是否被封禁
        groupApplicationValidator.isGroupExist(app.getGroupId());
        // 校验权限
        groupApplicationValidator.hasPermission(app.getGroupId(), dto.getProcessedBy());

        // 更新申请状态为已拒绝 (2)
        groupApplicationMapper.updateStatus(dto.getId(), GroupApplicationEnum.REJECT.getStatus(), dto.getProcessedBy(), dto.getRejectionReason());

        return Result.ok();
    }

    @Override
    public Result listByGroup(Long groupId, Long userId) {
        // 校验权限 (只有群主/管理员能看申请列表)
        groupApplicationValidator.hasPermission(groupId, userId);

        List<GroupApplicationVO> list = groupApplicationMapper.selectByGroupId(groupId);
        return Result.ok(list);
    }

    @Override
    public Result listByUser(Long userId) {
        List<GroupApplicationVO> list = groupApplicationMapper.selectByApplicantId(userId);
        return Result.ok(list);
    }
}
