package com.summit.chat.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.UserPwPutDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.Impl.Support.User.UserSupport;
import com.summit.chat.service.Impl.Support.User.UserValidator;
import com.summit.chat.service.User.UserService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    FileLoadServiceImpl fileLoadService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private UserSupport userSupport;



    @Override
    public Result putUser(UserDTO dto) {
        try {
            // 权限校验：确保只能修改自己的信息
            userValidator.checkEditAuth(String.valueOf(dto.getId()));
            
            Integer age = dto.getAge();
            Integer gender = dto.getGender();
            userValidator.checkGender(gender);
            userValidator.checkAge(age);
            if (dto.getId() == null) return Result.fail(UserConstants.ILLEGAL_CHAR);
            userValidator.checkPwAndNick(dto.getPw(), dto.getNickName());
            userMapper.putUser(dto);

            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【用户】修改用户信息失败,用户id:{}, 错误:{}", dto.getId(), e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public Result getUserById(String userID) {


        User user = userMapper.getUserById(Long.valueOf(userID));
        if (user == null) {
            return Result.fail(UserConstants.USER_NO_EXIST);
        }
        UserVO u = new UserVO();

        BeanUtil.copyProperties(user, u);


        return Result.ok(u);
    }

    @Override
    public Result getUserByPhone(String phoneNumber) {
        User user = userMapper.getUserByPhone(phoneNumber);
        if (user == null) {
            return Result.fail(UserConstants.USER_NO_EXIST);
        }
        user.setPw(""); // 擦除密码
        return Result.ok(user);
    }

    @Override
    public Result getUserByNick(String nickName) {
        if (StringUtil.isBlank(nickName)) {
            return Result.fail(UserConstants.ILLEGAL_CHAR);
        }
        return Result.ok(userMapper.getUserByNickName(nickName));

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result delUser(String userID) {
        if (StringUtil.isBlank(userID)) {
            return Result.fail(UserConstants.ILLEGAL_CHAR);
        }

        try {
            //检查用户权限
            userValidator.checkAuth(userID);

            userMapper.deleteUserById(userID);

            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}注销失败, 错误:{}", userID, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Result putUserIcon(MultipartFile file) {
        Object data = fileLoadService.upload(file, "avatar").getData();
        if (data != null) {
            userMapper.putUserIcon(data.toString(), UserHolder.getUserID());

            return Result.ok(data.toString());
        } else {
            return Result.fail(UserConstants.ILLEGAL_CHAR);
        }
    }

    @Override
    public Result putPw(UserPwPutDto dto) {
        try {
            //验证密码
            userValidator.putPwCheck(dto);
            return userSupport.setPw(dto);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【用户】修改密码失败");
            throw e;
        }
    }


    @Override
    public Result forgetPw(UserPwPutDto dto) {
        try {
            //验证验证码
            userValidator.forgetPwCheck(dto);
            //手机号格式校验
            userValidator.phoneCheck(dto.getMobile());
            return userSupport.setPw(dto);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        }catch(Exception e){
            log.error("【忘记密码】未知错误: {}", e.getMessage(), e);
           return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }


}

