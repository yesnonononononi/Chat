package com.summit.chat.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.PageConstants;
import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.GroupMapper;
import com.summit.chat.Mapper.GroupMemberMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.GroupMembers;
import com.summit.chat.model.vo.GroupChatVO;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberServiceCacheSupport;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberSupport;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberValidator;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupServiceCacheSupport;
import com.summit.chat.service.group.GroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {
    @Autowired
    GroupMemberValidator groupMemberValidator;
    @Autowired
    GroupMemberMapper groupMemberMapper;
    @Autowired
    GroupMemberSupport groupMemberSupport;
    @Autowired
    GroupMemberServiceCacheSupport groupMemberServiceCacheSupport;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    GroupServiceCacheSupport groupServiceCacheSupport;


    /**
     * 邀请群成员加入
     */
    @Override
    public Result addMember(GroupMemberDTO dto) {

        Long userId = null;
        Long inviteeID = null;
        try {
            groupMemberValidator.validate(dto);
            inviteeID = dto.getMemberId();
            Long groupId = dto.getGroupId();
            userId = dto.getUserId();
            //判断群聊是否存在
            GroupChatVO groupById = groupMemberValidator.isGroupExist(groupId);
            //判断群成员是否已存在
            GroupMembersVO inviteeInfo = groupMemberValidator.isMemberExist(groupId, inviteeID);
            //判断邀请者是否存在于群聊
            GroupMembersVO userInfo = groupMemberValidator.isMemberExist(groupId, userId);
            //判断群聊是否已满
            groupMemberValidator.isFull(groupById);
            //填充属性
            GroupMembers groupMembers = groupMemberSupport.fillProps(dto);
            //插入群成员
            groupMemberMapper.addMember(groupMembers);

            // 更新群人数
            groupMapper.updateGroupNumber(groupId);

            // 清除缓存
            groupMemberServiceCacheSupport.evictGroupMemberCache(groupId);
            groupServiceCacheSupport.evictGroupInfoCache(groupId);
            
            log.info("【群聊】群聊：{}的成员：{}，成功邀请：{}进入群聊", groupId, userId, inviteeID);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】在邀请添加成员时发生错误：群聊:{},邀请人:{},被邀请人:{}, 错误:{}", dto.getGroupId(), userId, inviteeID, e.getMessage(), e);
            throw e;  //交给全局异常处理器
        }
    }

    /**
     * 删除群成员
     */
    @Override
    public Result delMember(GroupMemberDTO dto) {
        Long userId = null;
        Long groupId;
        Long memberId = null;
        try {
            groupMemberValidator.validate(dto);
            userId = dto.getUserId();
            memberId = dto.getMemberId();
            groupId = dto.getGroupId();
            //判断群聊是否存在
            GroupChatVO groupById = groupMemberValidator.isGroupExist(groupId);
            //判断群成员是否存在
            GroupMembersVO memberInfo = groupMemberValidator.isMemberExist(groupId, userId);
            //判断群成员是否是群主
             groupMemberValidator.isOwner(memberInfo);
           //删除群成员
            groupMemberMapper.delMemberByGroupIdAndUserId(groupId, userId);

            // 更新群人数
            groupMapper.updateGroupNumber(groupId);

            // 清除缓存
            groupMemberServiceCacheSupport.evictGroupMemberCache(groupId);
            groupServiceCacheSupport.evictGroupInfoCache(groupId);

            log.info("【群聊】群聊：{}的成员：{}，成功删除成员：{}", groupId, userId, memberId);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】在删除成员时发生错误：操作人:{},被删除人:{}, 错误:{}", userId, memberId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 查询群成员
     */
    @Override
    public Result queryMemberByGroupId(Long groupId,Integer page,Integer pageSize) {

        try {
            Page<Object> po = PageHelper.startPage(page == null ? PageConstants.DEFAULT_PAGE : page, pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize);

            if(groupId ==null)throw new BusinessException(GroupConstants.GROUP_ID_ERROR);
            //判断群聊是否存在
            GroupChatVO group = groupMemberValidator.isGroupExist(groupId);

            List<GroupMembersVO> groupMembersVOS = groupMemberMapper.queryMemberByGroupId(group.getId());

            return Result.ok(new PageResult(po.getTotal(), groupMembersVOS));
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】在查询群成员时发生错误:群聊id:{}, 错误:{}", groupId, e.getMessage(), e);
            throw e;
        }
    }

}
