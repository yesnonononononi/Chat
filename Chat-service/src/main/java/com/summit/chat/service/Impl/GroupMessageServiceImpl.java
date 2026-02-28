package com.summit.chat.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.PageConstants;
import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.GroupMessageMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.GroupMessages;
import com.summit.chat.model.vo.GroupMembersVO;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberValidator;
import com.summit.chat.service.Impl.Support.group.GroupMessageSupport.GroupMessageSupport;
import com.summit.chat.service.Impl.Support.group.GroupMessageSupport.GroupMessageValidator;
import com.summit.chat.service.group.GroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupMessageServiceImpl implements GroupMessageService {
    @Autowired
    GroupMessageMapper groupMessageMapper;
    @Autowired
    GroupMessageValidator groupMessageValidator;
    @Autowired
    GroupMessageSupport groupMessageSupport;
    @Autowired
    private GroupMemberValidator groupMemberValidator;

    @Override
    public Result queryGroupMsgById(Long groupId, Integer page, Integer pageSize) {
        try {

            groupMessageValidator.isGroupExist(groupId);
            groupMemberValidator.isMemberExist(groupId, Long.valueOf(UserHolder.getUserID()));
            Page<Object> po = PageHelper.startPage(page == null ? PageConstants.DEFAULT_PAGE : page, pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize);

            return Result.ok(new PageResult(po.getTotal(), groupMessageMapper.queryGroupMsgById(groupId)));
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】查询群聊消息失败,群聊id:{}", groupId, e);
            throw e;
        }
    }

    @Override
    public Result queryGroupMsgByUserId(GroupMessageDTO dto, Integer page, Integer pageSize) {
        Long groupId = dto.getGroupId();

        try {
            groupMessageValidator.isGroupExist(groupId);

            Page<Object> po = PageHelper.startPage(page == null ? PageConstants.DEFAULT_PAGE : page, pageSize == null ? PageConstants.DEFAULT_PAGE_SIZE : pageSize);
            return Result.ok(new PageResult(po.getTotal(), groupMessageMapper.queryGroupMsgByUserId(dto)));


        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群聊】查询群聊消息失败,群聊id:{}", groupId, e);
            throw e;
        }
    }

    @Override
    public Result withdrawn(GroupMessageVO vo) {
        try {

            //检查是否是群成员

            GroupMembersVO member = groupMessageValidator.getMember(vo.getGroupId(), vo.getEmitterId());

            //检查群聊是否存在

            groupMessageValidator.isGroupExist(vo.getGroupId());

            //消息是否是自己发的

            groupMessageValidator.checkId(vo);

            //查询消息的具体情况,如果时间超过10分钟则不能撤回
            groupMessageValidator.checkMsgTime(vo);

            //撤回消息
            vo.setIsDeleted(MsgEnum.WITHDRAWN.getStatus());

            groupMessageMapper.withdrawn(vo);

            groupMessageSupport.withdrawn(vo);
            return Result.ok();
        }catch (BusinessException e){

            return Result.fail(e.getMessage());

        }catch (Exception e){
            log.error("【群聊】用户{}撤回群聊消息:{}时发生错误, 错误:{}", vo.getEmitterId(), vo.getMsg(), e.getMessage(), e);
            return Result.fail("系统异常");
        }
    }

    @Override
    public Result addGroupMsg(GroupMessageVO vo) {
        try {
            //填充消息
            GroupMessages groupMessages = groupMessageSupport.fillProp(vo);
            //添加群聊消息
            groupMessageMapper.addGroupMsg(groupMessages);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【群聊】用户{}发送群聊消息:{}时发生错误, 错误:{}", vo.getEmitterId(), vo.getMsg(), e.getMessage(), e);
            throw e;
        }
    }
}
