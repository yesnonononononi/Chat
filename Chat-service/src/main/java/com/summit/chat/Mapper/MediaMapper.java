package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.MediaState;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.Media;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MediaMapper {

    @Insert("insert into media(emitter_id, receive_id, status, create_time) values (#{emitterId}, #{receiveId}, #{status}, #{createTime})")
    void insert(Media media);

    @Update("update media set status = #{status.state} where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'wait'")
    void putMedia(@Param("status") MediaState status, @Param("emitterId") String emitterId, @Param("receiveId") String receiveId);

    @Select("select count(*) from media where emitter_id = #{emitterId} and receive_id = #{receiveId} and status = 'wait'")
    Integer countApply(@Param("emitterId") String emitterId, @Param("receiveId") String receiveId);
}
