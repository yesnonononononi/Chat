package com.summit.chat.service.Impl;

import com.summit.chat.Constants.GroupNoticeConstants;
import com.summit.chat.Dto.GroupNoticeDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.GroupNoticeMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.mysql.GroupNotice;
import com.summit.chat.model.vo.GroupNoticeVO;
import com.summit.chat.service.Impl.Support.group.GroupNoticeSupport.GroupNoticeValidator;
import com.summit.chat.service.group.GroupNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GroupNoticeServiceImpl implements GroupNoticeService {

    @Autowired
    private GroupNoticeMapper groupNoticeMapper;
    
    @Autowired
    private GroupNoticeValidator groupNoticeValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publish(GroupNoticeDTO dto) {
        try {
            // 参数校验
            groupNoticeValidator.verifyPublish(dto);

            // 校验群组是否存在
            groupNoticeValidator.isGroupExist(dto.getGroupId());

            // 校验发布者
            groupNoticeValidator.verifyOwnerOrAdmin(groupNoticeValidator.getMember(dto.getGroupId(), dto.getPublisherId()));

            // 创建群公告实体
            GroupNotice groupNotice = new GroupNotice();
            BeanUtils.copyProperties(dto, groupNotice);
            groupNotice.setIsDeleted(GroupNoticeConstants.STATUS_NORMAL); // 正常状态

            // 插入数据库
            groupNoticeMapper.insert(groupNotice);

            log.info("【群公告】发布群公告成功，群组ID: {}, 发布者ID: {}", dto.getGroupId(), dto.getPublisherId());
            return Result.ok(groupNotice.getId());
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群公告】发布群公告失败,群聊id:{}", dto.getGroupId(), e);
            throw e;
        }

    }

    @Override
    public Result getById(Long id) {
        if (id == null) {
            return Result.fail(GroupNoticeConstants.NOTICE_ID_NULL);
        }

        GroupNoticeVO notice = groupNoticeMapper.selectById(id);
        if (notice == null) {
            return Result.fail(GroupNoticeConstants.NOTICE_NOT_EXIST);
        }

        return Result.ok(notice);
    }

    @Override
    public Result listByGroupId(Long groupId) {
        try {
            if (groupId == null) {
                return Result.fail(GroupNoticeConstants.GROUP_ID_NULL);
            }

            List<GroupNoticeVO> notices = groupNoticeMapper.selectByGroupId(groupId);
            return Result.ok(notices);
        } catch (Exception e) {
            log.error("【群公告】获取列表失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(GroupNoticeDTO dto) {
        try {
            // 参数校验
            groupNoticeValidator.verifyUpdate(dto, UserHolder.getUserID());

            // 更新公告
            GroupNotice groupNotice = new GroupNotice();
            BeanUtils.copyProperties(dto, groupNotice);
            groupNoticeMapper.update(groupNotice);

            log.info("【群公告】更新群公告成功，公告ID: {}", dto.getId());
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【群公告】更新群公告失败,群聊id:{}, 错误:{}", dto.getGroupId(), e.getMessage(), e);
            throw e;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(Long id, Long groupId) {
        try {
            // 校验公告是否存在当前群组
            groupNoticeValidator.verifyExists(id,groupId);

            //校验是否是群管理员或者群主
            groupNoticeValidator.verifyOwnerOrAdmin(groupNoticeValidator.getMember(groupId, Long.parseLong(Objects.requireNonNull(UserHolder.getUserID()))));

            // 删除公告（逻辑删除）
            groupNoticeMapper.deleteById(id);

            log.info("【群公告】删除群公告成功，公告ID: {}", id);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【群公告】删除群公告失败,群聊id:{}", groupId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchDelete(List<Long> ids,Long groupId) {
        try {
            // 参数校验
            groupNoticeValidator.verifyBatchDelete(ids);

            // 校验是否是群管理员或者群主
            groupNoticeValidator.verifyOwnerOrAdmin(groupNoticeValidator.getMember(groupId, Long.parseLong(Objects.requireNonNull(UserHolder.getUserID()))));


            // 批量删除公告（逻辑删除）
            groupNoticeMapper.batchDelete(ids, groupId);

            log.info("【群公告】批量删除群公告成功，公告IDs: {}", ids);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【群公告】批量删除群公告失败,群聊id:{}, 错误:{}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result countByGroupId(Long groupId) {
        if (groupId == null) {
            return Result.fail(GroupNoticeConstants.GROUP_ID_NULL);
        }

        Integer count = groupNoticeMapper.countByGroupId(groupId);
        return Result.ok(count);
    }
}
