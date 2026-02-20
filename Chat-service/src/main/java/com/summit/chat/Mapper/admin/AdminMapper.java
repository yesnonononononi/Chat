package com.summit.chat.Mapper.admin;

import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("<script>" +
            "select * from user " +
            "<where>" +
            "is_delete = 1 " +
            "<if test='nickName != null and nickName != \"\"'>" +
            "and nick_name like concat( #{nickName}, '%') " +
            "</if>" +
            "<if test='gender != null'>" +
            "and gender = #{gender} " +
            "</if>" +
            "<if test='status != null'>" +
            "and status = #{status} " +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            "and role = #{role} " +
            "</if>" +
            "</where>" +
            "</script>")
    List<User> getAllUsers(UserPageQueryDTO userPageQueryDTO);

    @Update("update user set status = 0 where id = #{userID}")
    void blackUser(String userID);

    @Update("update user set status = 1 where id = #{userID}")
    void unblackUser(String userID);

    @Update("update user set role = 'admin' where id = #{userID}")
    void setAdmin(String userID);
}
