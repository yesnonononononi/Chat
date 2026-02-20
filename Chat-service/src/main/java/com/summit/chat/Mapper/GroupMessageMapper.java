package com.summit.chat.Mapper;

import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.model.entity.GroupMessages;
import com.summit.chat.model.vo.GroupMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupMessageMapper {
    /**
     * 根据群组id查询群组信息
     * @param groupId
     * @return
     */
    @Select("select gm.id as msgId, gm.group_id as groupId, gm.sender_id as emitterId, gm.msg, gm.create_time as createTime, gm.message_type as messageType, gm.is_deleted as isDeleted, u.nick_name as nickName, u.icon as icon " +
            "from group_messages gm " +
            "left join user u on gm.sender_id = u.id " +
            "where gm.group_id = #{groupId} order by gm.create_time ")
    List<GroupMessageVO> queryGroupMsgById(Long groupId);

    @Select("select id, group_id, sender_id as emitterId, msg, create_time, message_type, is_deleted from group_messages where group_id = #{groupId} and sender_id = #{emitterId} ")
    List queryGroupMsgByUserId(GroupMessageDTO dto);

    @Update("update group_messages set is_deleted = #{isDeleted} where group_id = #{groupId} and sender_id = #{emitterId} ")
    void withDrawn(GroupMessageVO vo);

    void addGroupMsg(GroupMessages groupMessages);
}
