package com.summit.chat.Mapper;

import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.UserPwPutDto;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    void register(User vo);
    
    User getUserByPhone(String mobile);

    void putUser(UserDTO dto);

    User getUserById(Long id);

    List<UserVO> getUserByNickName(String nickName);

    void deleteUserById(String userID);

    @Update("update user set icon=#{icon} where id=#{userID}")
    void putUserIcon(String icon,String userID);

    @Update("update user set call_state = #{state} where id= #{userID}")
    void putUserState(Integer state ,String userID);

    @Select("select user.call_state from user where id= #{userID}")
    Integer getUserState( String userID);



    @Select("select DATE(create_time) as date ,count(id) as total   from user where create_time >= DATE_SUB(CURRENT_DATE(), INTERVAL #{n}-1 DAY) and create_time < DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY) and is_delete = 1 group by date order by date asc")
    List<Map<String,Object>> getNewUserByRangeOfDay(Integer n);

    @Select("select count(*) from user where DATE(create_time) <= #{dateTime} and  is_delete = 1")
    Integer getTotalUsers(Date dateTime);

    @Update("update user set pw = #{pw} where mobile = #{mobile}")
    Integer putPw(UserPwPutDto dto);





}