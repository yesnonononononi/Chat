package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.GroupChat;
import com.summit.chat.model.vo.GroupChatVO;
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
    @Select("select g.id, g.group_name, g.group_description, g.creator_id, g.create_time, g.update_time, g.status, g.number, g.icon, " +
            "IFNULL(u.nick_name, u.mobile) as creator_name " +
            "from group_chat g " +
            "left join user u on g.creator_id = u.id " +
            "where g.id = #{groupId}")
    GroupChatVO queryGroupById(Long groupId);

    @Select("select g.id, g.group_name, g.group_description, g.creator_id, g.create_time, g.update_time, g.status, g.number, g.icon, " +
            "IFNULL(u.nick_name, u.mobile) as creator_name " +
            "from group_chat g " +
            "left join user u on g.creator_id = u.id")
    List<GroupChatVO> queryAllGroup();

    @AutoFill(type = OperationType.INSERT)
    void addGroup(GroupChat groupChat);

    @Delete("delete from group_chat where id = #{groupId}")
    void delGroup(Long groupId);

    @AutoFill(type = OperationType.UPDATE)
    void putGroup(GroupChat groupChat);

    @Select("select g.id, g.group_name, g.group_description, g.creator_id, g.create_time, g.update_time, g.status, g.number, g.icon, " +
            "IFNULL(u.nick_name, u.mobile) as creator_name " +
            "from group_chat g " +
            "left join user u on g.creator_id = u.id " +
            "where g.creator_id = #{userId}")
    List<GroupChatVO> queryUserGroupByUserId(String userId);


    @Select("select g.id, g.group_name, g.group_description, g.creator_id, g.create_time, g.update_time, g.status, g.number, g.icon, " +
            "IFNULL(u.nick_name, u.mobile) as creator_name " +
            "from group_chat g " +
            "left join user u on g.creator_id = u.id " +
            "where g.group_name like concat(#{groupName},'%')")
    List<GroupChatVO> queryGroupByName(String groupName);


    @Select("select gc.id, gc.group_name, gc.group_description, gc.creator_id, gc.create_time, gc.update_time, gc.status, gc.number, gc.icon, " +
            "IFNULL(u.nick_name, u.mobile) as creator_name " +
            "from (select group_id from group_members where user_id = #{userID} and status != 2) gm " +
            "inner join group_chat gc on gc.id = gm.group_id " +
            "left join user u on gc.creator_id = u.id")
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

