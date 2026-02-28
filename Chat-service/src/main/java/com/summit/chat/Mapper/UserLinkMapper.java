package com.summit.chat.Mapper;

import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.model.vo.UserLinkVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserLinkMapper {


    List<UserLinkVO> getLinkById(@org.apache.ibatis.annotations.Param("userID") String userID);


    void saveLink(UserLinkDto dto);

//存在关系1 不存在0

    Integer linkExist(@org.apache.ibatis.annotations.Param("userID") String userID, @org.apache.ibatis.annotations.Param("linkID") String linkID);

    @Delete("delete from user_link where (user_id = #{userID} and link_user_id = #{linkID}) or (user_id = #{linkID} and link_user_id = #{userID})")
    void delLink(UserLinkDto dto);
}

