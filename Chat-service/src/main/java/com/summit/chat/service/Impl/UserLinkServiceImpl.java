package com.summit.chat.service.Impl;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.UserLinkMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.UserLinkVO;
import com.summit.chat.service.UserLink.UserLinkService;
import com.summit.chat.service.validate.UserLinkNullValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserLinkServiceImpl implements UserLinkService {
    @Autowired
    UserLinkMapper userLinkMapper;
    @Autowired
    UserLinkNullValidateService validateService;
    @Override
    public Result getUserLinkById() {

        String userID = null;
        try {
            userID = UserHolder.getUserID();

            if (validateService.baseValidate(userID)) {
                log.error("【好友关系】未获取到用户id,可能是未登录or携带token");
                return Result.fail(BaseConstants.SERVER_EXCEPTION);
            }

            List<UserLinkVO> linkById = userLinkMapper.getLinkById(userID);

            return Result.ok(validateService.filter(linkById));

        } catch (Exception e) {
            log.error("【好友关系】查询用户联系人发生错误:{}, 错误:{}", userID, e.getMessage(), e);
            throw new BusinessException(BaseConstants.UNKNOWN_ERROR);
        }
    }

    /**
     * 保存用户联系人
     */
    @Override
    public Result saveLink(UserLinkDto dto) {
        String id = dto.getUserID();
        if (id == null) {
            id = UserHolder.getUserID();
            dto.setUserID(id);
        }
        if (validateService.baseValidate(id)) {
            throw new BusinessException(BaseConstants.UNCACHE_USERID);
        }

        try {
            if (validateService.baseValidate(dto.getIsFrequent())) {
                dto.setIsFrequent(0);
            }
            userLinkMapper.saveLink(dto);
        } catch (Exception e) {
            log.error("【好友关系】保存用户联系人时出现问题:{}, 错误:{}", dto.getUserID(), e.getMessage(), e);
            throw new BusinessException(BaseConstants.UNKNOWN_ERROR);
        }
        return Result.ok();
    }

    @Override
    public Result delLink(UserLinkDto dto) {
        String id = dto.getUserID();
        if (id == null) {
            id = UserHolder.getUserID();
            dto.setUserID(id);
        }
        if (validateService.baseValidate(id)) {
            throw new BusinessException(BaseConstants.UNCACHE_USERID);
        }
        try {
            validateService.validateOfDelete(dto);
            userLinkMapper.delLink(dto);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【好友关系】{}删除好友关系时发生错误: {}", dto.getUserID(), e.getMessage(), e);
            throw e;
        }
    }
}

