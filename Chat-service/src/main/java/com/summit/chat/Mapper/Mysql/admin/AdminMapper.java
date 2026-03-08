package com.summit.chat.Mapper.Mysql.admin;

import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.model.entity.mysql.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    List<User> getAllUsers(UserPageQueryDTO userPageQueryDTO);

    void blackUser(String userID);

    void unblackUser(String userID);

    Integer setAdmin(String userID);


    Integer delAdmin(String userID);
}