package com.summit.chat.service.validate;

import cn.hutool.core.util.ObjectUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.FriendApplyConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Constants.UserLinkConstants;
import com.summit.chat.Dto.FriendDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.FriendApplyMapper;
import com.summit.chat.Mapper.UserLinkMapper;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.FriendApplyVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FriendValidateService extends GlobalValidatorImpl<FriendDto> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendApplyMapper friendApplyMapper;
    @Autowired
    private UserLinkMapper userLinkMapper;



    /**
     * 查看需要操作的用户是否存在
     *
     * @param id
     * @return
     */
    public boolean userNoExist(String id) {
        try {
            long userId = Long.parseLong(id);
            return ObjectUtil.isNull(userMapper.getUserById(userId));
        } catch (ClassCastException e) {
            throw new BusinessException(BaseConstants.UNCACHE_USERID);
        } catch (Exception e) {
            throw new BusinessException(BaseConstants.UNCATCH_ERROR);
        }

    }

    /**
     * 查看申请的次数是否达到上限
     *
     * @param dto
     * @return
     */
    public boolean queryApply(FriendDto dto) {
        Integer count = friendApplyMapper.countApplication(dto);
        return (count != null && count > FriendApplyConstants.APPLY_MAX);
    }

    /**
     * 判断关系是否存在
     *
     * @param dto
     * @return
     */
    public boolean friendExist(FriendDto dto) {
        String applicantID = dto.getApplicantId();
        String recipientID = dto.getRecipientId();
        return userLinkMapper.linkExist(applicantID, recipientID) == 1;
    }

    /**
     * 判断请求是否被处理
     *
     * @param dto
     * @return
     */
    public boolean isNotDone(FriendDto dto, boolean isTrueWhenRelationIsNull) {

        String id = dto.getId();
        try {
            //查询请求是否已经处理
            FriendApplyVO friendApplyVO = friendApplyMapper.queryAppById(id);

            if (isTrueWhenRelationIsNull && friendApplyVO == null) return true;

            if (friendApplyVO != null && friendApplyVO.getStatus() == 0) {
                //请求用户是否匹配
                String userID = UserHolder.getUserID();

                String recipientID = friendApplyVO.getRecipientId();
                String applicantId = friendApplyVO.getApplicantId();

                if (!applicantId.equals(dto.getApplicantId()) || !recipientID.equals(userID)) {
                    log.error("用户无权处理申请");
                    return false;
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("验证请求时出错", e);
            return false;
        }
    }


    /**
     * 校验逻辑包括,
     * dto的存在校验,
     * 目标是否存在于数据库,
     * 校验请求是否为未处理,
     * 请求主体于目标是否匹配
     * 关系是否存在
     */
    @Override
    public boolean validate(FriendDto dto) {
        baseValidate(dto);
        //校验是否id是否存在
        if(dto.getId() == null){
            super.throwException(BaseConstants.ARGV_ERROR);
        }
        //校验请求是否被处理
        if (!isNotDone(dto, false)) {
            super.throwException(UserLinkConstants.APPLICATION_IS_HANDLE);
        }
        return true;
    }

    /**
     * 发送好友申请的校验
     *
     * @param dto
     * @return
     */

    public boolean validateOfSend(FriendDto dto) {
        baseValidate(dto);
        //申请者是否为自己
        if(dto.getApplicantId().equals(dto.getRecipientId())){
            super.throwException(UserLinkConstants.APPLICANT_IS_SELF);
        }
        //请求是否达到上限
        if (queryApply(dto)) {
            super.throwException(UserLinkConstants.REQUEST_EXCEED_MAX);
        }
        //校验请求是否被处理
        if (!isNotDone(dto, true)) {
            super.throwException(UserLinkConstants.APPLICATION_IS_HANDLE);
        }
        return true;
    }


    private void baseValidate(FriendDto dto) {
        //首先,先校验dto
        if (dto == null || dto.getApplicantId() == null) {
            super.throwException(BaseConstants.ARGV_ERROR);
        }
        //校验用户是否存在于数据库
        if (userNoExist(dto.getApplicantId()) || userNoExist(dto.getRecipientId())) {
            super.throwException(UserConstants.USER_NO_EXIST);
        }
        //校验关系是否存在
        if (friendExist(dto)) {
            super.throwException(UserLinkConstants.USER_LINK_EXIST);
        }
    }
}

