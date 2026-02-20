package com.summit.chat.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.PageConstants;
import com.summit.chat.Dto.GroupChatDto;
import com.summit.chat.Enum.GroupRole;
import com.summit.chat.Enum.GroupStatusEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.GroupMapper;
import com.summit.chat.Mapper.GroupMemberMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.GroupChat;
import com.summit.chat.model.entity.GroupMembers;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberServiceCacheSupport;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupSupport;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupValidator;
import com.summit.chat.service.group.GroupService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    GroupMemberMapper groupMemberMapper;
    @Autowired
    GroupValidator groupValidator;
    @Autowired
    GroupSupport groupSupport;
    @Autowired
    GroupMemberServiceCacheSupport groupMemberServiceCacheSupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addGroup(GroupChatDto dto) {
        try {
            groupValidator.validate(dto);
            //判断群聊名字是否合规
            groupValidator.checkName(dto.getGroupName());
            //创建者id是否匹配
            groupValidator.checkId(dto.getCreatorId());
            // 校验群聊数量限制
            groupValidator.verifyGroupLimit(dto.getCreatorId());
            //判断群聊描述是否合规
            groupValidator.checkDescription(dto.getGroupDescription());
            //填充字段
            GroupChat groupChat = groupSupport.fillProp(dto);
            //添加群聊
            groupMapper.addGroup(groupChat);

            //获取生成的自增id
            Long id = groupChat.getId();

            //将创建者添加为群主
            GroupMembers members = new GroupMembers();
            members.setGroupId(id);
            members.setUserId(dto.getCreatorId());
            members.setRole(GroupRole.OWNER.getRole());
            members.setStatus(GroupStatusEnum.NORMAL.getStatus());
            groupMemberMapper.addMember(members);

            // 添加群聊成员,删除缓存
            groupMemberServiceCacheSupport.evictGroupMemberCache(id);

            log.info("【群聊】添加群聊成功,群聊id:{}", id);

            return Result.ok(id);

        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】添加群聊失败,添加者id:{}, 错误:{}", dto.getCreatorId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result delGroup(Long groupId, String userId) {
        try {
            groupValidator.putGroupValidate(groupId, userId);
            //删除群聊
            groupMapper.delGroup(groupId);

            // Invalidate Cache
            groupMemberServiceCacheSupport.evictGroupMemberCache(groupId);

            log.info("【群聊】删除群聊成功,群聊id:{}", groupId);

            return Result.ok();


        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】删除群聊失败,群聊id:{}, 错误:{}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result putGroup(GroupChatDto dto) {
        try {
            groupValidator.putGroupValidate(dto.getId(), UserHolder.getUserID());
            GroupChat groupChat = new GroupChat();
            BeanUtil.copyProperties(dto, groupChat);
            //修改群聊信息
            groupMapper.putGroup(groupChat);
            log.info("【群聊】修改群聊信息成功,群聊id:{}", dto.getId());
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】修改群聊信息失败,群聊id:{}, 错误:{}", dto.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result queryGroupById(Long groupId) {
        try {
            if (groupId == null) throw new BusinessException(GroupConstants.GROUP_ID_ERROR);

            return Result.ok(groupMapper.queryGroupById(groupId));

        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】查询群聊信息失败,群聊id:{}, 错误:{}", groupId, e.getMessage(), e);
            throw e;
        }
    }




    @Override
    public Result queryGroupByUserId(String userId) {
        String userID = UserHolder.getUserID();
        if (!Objects.equals(userID, userId)) return Result.fail(GroupConstants.USER_NOT_MATCH);
        return Result.ok(groupMapper.queryGroupByUserId(userID));
    }

    /**
     * 查询用户创建的群聊
     *
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result queryUserGroupByUserId(String userId, Integer page, Integer pageSize) {
        try {
            groupValidator.checkId(Long.valueOf(userId));
            Page<Object> po = PageHelper.startPage(page == null ? PageConstants.DEFAULT_PAGE : page, pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize);

            return Result.ok(new PageResult(po.getTotal(), groupMapper.queryUserGroupByUserId(userId)));

        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】查询用户群聊信息失败,用户id:{}, 错误:{}", userId, e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result queryGroupByName(String groupName, Integer page, Integer pageSize) {
        try {
            if (StringUtil.isBlank(groupName)) return Result.fail(GroupConstants.ILLEGAL_CHAR);
            Page<Object> po = PageHelper.startPage(page == null ? PageConstants.DEFAULT_PAGE : page, pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize);
            return Result.ok(new PageResult(po.getTotal(), groupMapper.queryGroupByName(groupName)));
        } catch (Exception e) {
            log.error("【群聊】查询群聊信息失败,群聊名称:{}, 错误:{}", groupName, e.getMessage(), e);
            throw e;
        }
    }


}
