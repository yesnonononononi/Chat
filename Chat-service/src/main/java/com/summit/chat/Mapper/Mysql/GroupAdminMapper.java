package com.summit.chat.Mapper.Mysql;

import com.summit.chat.Dto.PutGroupMemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GroupAdminMapper {

    /*修改用户状态用户*/
    @Update("update group_members set status = #{status} where user_id = #{userId} and group_id = #{groupId}")
    Integer putGroupMember(PutGroupMemberDTO dto);

}
