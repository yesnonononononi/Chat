package com.summit.chat.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.FriendDto;
import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.FriendApplyMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.service.Friend.FriendApply;
import com.summit.chat.service.UserLink.UserLinkService;
import com.summit.chat.service.validate.FriendValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
public class FriendApplyImpl implements FriendApply {
    @Autowired
    FriendApplyMapper friendApplyMapper;
    @Autowired
    FriendValidateService validateService;
    @Autowired
    UserLinkService userLinkService;


    /**
     * 发送好友请求
     *
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result sendApplication(@Validated FriendDto dto) {

        try {
            String userID = UserHolder.getUserID();
            if (userID == null) {
                return Result.fail(UserConstants.USER_NOT_LOGIN);
            }
            // 必须先设置申请人ID，防止越权申请
            dto.setApplicantId(userID);

            validateService.validateOfSend(dto);

            String id00 = GlobalIDWorker.generateId();

            dto.setId(id00);

            friendApplyMapper.sendApplication(dto);

            return Result.ok();

        } catch (BusinessException e) {

            return Result.fail(e.getMessage());

        } catch (Exception e) {

            log.error("【好友申请】在发送好友申请时,发生错误:{}", dto.getApplicantId(), e);

            throw e;
        }
    }

    /**
     * 接受好友申请
     *
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result ackApplication(FriendDto dto) {

        String id;
        try {
            id = dto.getId();

            validateService.validate(dto);

            // 获取真实的申请信息，防止参数篡改
            com.summit.chat.model.vo.FriendApplyVO applyVO = friendApplyMapper.queryAppById(id);
            if (applyVO == null) {
                return Result.fail(BaseConstants.ARGV_ERROR);
            }

            friendApplyMapper.ackApplication(id);

            //接受好友请求时,要建立双向关系
            // 1. 保存 接受者 -> 申请者
            UserLinkDto linkToApplicant = new UserLinkDto();
            linkToApplicant.setUserID(applyVO.getRecipientId());
            linkToApplicant.setLinkID(applyVO.getApplicantId());
            Result res1 = userLinkService.saveLink(linkToApplicant);
            if (!(res1.getCode() == 1)) {
                throw new BusinessException(res1.getMsg());
            }

            // 2. 保存 申请者 -> 接受者
            UserLinkDto linkToRecipient = new UserLinkDto();
            linkToRecipient.setUserID(applyVO.getApplicantId());
            linkToRecipient.setLinkID(applyVO.getRecipientId());
            Result res2 = userLinkService.saveLink(linkToRecipient);
            if (!(res2.getCode() == 1)) {
                throw new BusinessException(res2.getMsg());
            }

            return Result.ok();

        } catch (BusinessException e) {
            log.error("【好友申请】接受好友申请失败:{}", dto.getId(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(e.getMessage());

        } finally {
            UserHolder.remove();
        }
    }

    /**
     * 拒绝申请
     */
    @Override
    public Result rejectApplication(FriendDto dto) {
        try {
            validateService.validate(dto);
            friendApplyMapper.rejectApplication(dto.getId());
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【好友申请】拒绝用户请求时,出现问题:{}", dto.getApplicantId(), e);
            throw e;
        }
    }

    /**
     * 查询请求
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result queryApplication(Integer page, Integer pageSize) {
        page = page == null ? BaseConstants.DEFAULT_PAGE : page;
        pageSize = pageSize == null ? BaseConstants.DEFAULT_PAGESIZE : pageSize;

        try (Page<Object> pageRes = PageHelper.startPage(page, pageSize)) {
            String userID = UserHolder.getUserID();
            if (userID == null) {
                throw new BusinessException(BaseConstants.UNCACHE_USERID);
            }
            try {
                return Result.ok(new PageResult(pageRes.getTotal(), friendApplyMapper.queryAppByUserId(userID)));
            } catch (Exception e) {
                log.error("【好友申请】查询用户好友请求时,出现错误:{}", userID, e);
                throw e;
            }

        }


    }


}
