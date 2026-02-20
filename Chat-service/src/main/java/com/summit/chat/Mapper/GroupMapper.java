package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.GroupChat;
import com.summit.chat.model.vo.GroupChatVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupMapper {
    /**
     * 根据群组id查询群组信息
     * @param groupId
     * @return
     */
    @Select("select id, group_name, group_description, creator_id, create_time, update_time, status, number, icon from group_chat where id = #{groupId}")
    GroupChatVO queryGroupById(Long groupId);

    @Select("select id, group_name, group_description, creator_id, create_time, update_time, status, number, icon from group_chat")
    List<GroupChatVO> queryAllGroup();

    @AutoFill(type = OperationType.INSERT)
    void addGroup(GroupChat groupChat);

    @Delete("delete from group_chat where id = #{groupId}")
    void delGroup(Long groupId);

    @AutoFill(type = OperationType.UPDATE)
    void putGroup(GroupChat groupChat);

    @Select("select id, group_name, group_description, creator_id, create_time, update_time, status, number, icon from group_chat where creator_id = #{userId}")
    List<GroupChatVO> queryUserGroupByUserId(String userId);


    @Select("select id, group_name, group_description, creator_id, create_time, update_time, status, number, icon from group_chat where group_name like concat(#{groupName},'%')")
    List<GroupChatVO> queryGroupByName(String groupName);


    @Select("select group_id, id, group_name, group_description, creator_id, create_time, update_time, status, number, icon from (select group_id from group_members where user_id = #{userID} and status != 2) u inner join group_chat gc on gc.id = u.group_id")
    List<GroupChatVO> queryGroupByUserId(String userID);

    @AutoFill(type = OperationType.UPDATE)
    @Update("update group_chat set number = number + 1 where id = #{groupId}")
    void updateGroupNumber(long groupId);


    @AutoFill(type = OperationType.UPDATE)
    @Update("update group_chat set status = 0 where id = #{groupId}")
    Integer banGroup(String groupId);

    @AutoFill(type = OperationType.UPDATE)
    @Update("update group_chat set status = 1 where id = #{groupId}")
    Integer unBanGroup(String groupId);
}

