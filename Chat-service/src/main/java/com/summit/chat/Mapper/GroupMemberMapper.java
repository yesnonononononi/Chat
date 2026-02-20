package com.summit.chat.Mapper;

import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.model.entity.GroupMembers;
import com.summit.chat.model.vo.GroupMembersVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMemberMapper {
    @Select("select * from group_members where id = #{id}")
    GroupMembersVO queryGroupMemberById(String id);

    @Insert("insert into group_members(group_id, user_id, join_time, role) values (#{groupId},#{userId},NOW(),#{role})")
    void addMember(GroupMembers members);


    @Delete("delete from group_members where group_id = #{groupId} and user_id = #{memberId}")
    void delMemberByGroupIdAndUserId(@org.apache.ibatis.annotations.Param("groupId") Long groupId, @org.apache.ibatis.annotations.Param("memberId") Long memberId);

    @Select("select u.id, nick_name as nickName, u.icon as avatar, gn.role from group_members gn left join user u on u.id = gn.user_id where group_id = #{groupId} and u.is_delete != 2")
    List<GroupMembersVO> queryMemberByGroupId(Long groupId);

    @Select("select user_id from group_members where group_id = #{groupId}")
    List<String> queryUserIdsByGroupId(Long groupId);

    @Select("select u.id as userId,  join_time as joinTime,nick_name as nickName, u.hobby,u.gender,gn.status, u.icon as avatar, gn.role from group_members gn left join user u on u.id = gn.user_id  where group_id = #{groupId} and user_id = #{userId} and u.is_delete != 2")
    GroupMembersVO queryMemberByGroupIdAndUserId( Long groupId,  Long userId);
}
