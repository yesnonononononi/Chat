package com.summit.chat.service.User;

import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.UserPwPutDto;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {

    Result putUser(UserDTO dto);

    Result getUserById(String userID);

    Result getUserByPhone(String phoneNumber);

    Result getUserByNick(String nickName);

    Result delUser(String userID);

    Result putUserIcon(MultipartFile file);

    Result putPw(UserPwPutDto dto);

    Result forgetPw(UserPwPutDto dto);
}

