package com.summit.chat.Mapper.Mysql;

import com.summit.chat.Dto.FriendDto;
import com.summit.chat.model.vo.FriendApplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendApplyMapper {
    void sendApplication(FriendDto dto);

    void ackApplication(@Param("id") String id);

    Integer countApplication(FriendDto dto);

    FriendApplyVO queryAppById(@Param("id") String id);

    void rejectApplication(@Param("id") String id);

    List<FriendApplyVO> queryAppByUserId(@Param("userId") String userId);
}