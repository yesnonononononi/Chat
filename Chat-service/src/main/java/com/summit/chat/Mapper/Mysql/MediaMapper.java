package com.summit.chat.Mapper.Mysql;

import com.summit.chat.Enum.MediaState;
import com.summit.chat.model.entity.mysql.Media;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MediaMapper {

    @Insert("insert into media(emitter_id, receive_id, status, create_time) values (#{emitterId}, #{receiveId}, #{status}, #{createTime})")
    void insert(Media media);

    @Update("update media set status = #{status},end_time= now() where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'wait'")
    void putMedia(@Param("status") MediaState status, @Param("emitterId") String emitterId, @Param("receiveId") String receiveId);

    @Update("update media set status = 'accept',create_time = now() where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'wait'")
    void accept(String emitterId,String receiveId);

    @Update("update media set end_time = now() where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'accept'")
    void endMedia(String emitterId,String receiveId);

    @Select("select count(*) from media where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'wait'")
    Integer countApply(@Param("emitterId") String emitterId, @Param("receiveId") String receiveId);
}
