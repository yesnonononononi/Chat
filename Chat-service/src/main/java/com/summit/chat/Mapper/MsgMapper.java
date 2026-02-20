package com.summit.chat.Mapper;

import com.summit.chat.model.vo.PrivateMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MsgMapper {
    @Insert("insert into user_msg(id, content, send_time, emitter_id, receive_id, status) values (#{msgId},#{msg},#{sendTime},#{emitterId},#{receiveId},#{status})")
    void save(PrivateMessageVO dto);
    @Select("select id, content, send_time, emitter_id, receive_id, status from user_msg where id = #{id} and status != 1")
    List<PrivateMessageVO> queryById(String id);
    @Update("update user_msg set status = 1 where id = #{msgId} and emitter_id = #{emitterId} and receive_id = #{receiveId} and send_time = #{sendTime} and status != 1")
    void withdrawn(PrivateMessageVO dto);
    @Select("select um.id, content, send_time, emitter_id, receive_id, um.status, u.nick_name as emitterNick from user_msg um join user u on u.id = um.emitter_id  where content like (concat(#{msg},'%')) and um.status != 1")
    List<PrivateMessageVO>queryByContent(PrivateMessageVO dto);
    @Select("select um.id as msgId, content as msg, send_time,u.nick_name as emitterNick, emitter_id, receive_id, um.status from user_msg um left join user u on u.id = um.emitter_id where ((um.emitter_id = #{emitterId} and um.receive_id = #{receiveId}) or (um.emitter_id = #{receiveId} and um.receive_id = #{emitterId})) and um.status != 1 order by um.send_time desc ")
    List<PrivateMessageVO>queryHistoryMsg(PrivateMessageVO dto);

    @Update("update user_msg set status = 0 where emitter_id = #{emitterId} and receive_id = #{userId} and status = 2")
    void readMsgFromUser(@Param("emitterId") String emitterId, @Param("userId") String userId);
}

