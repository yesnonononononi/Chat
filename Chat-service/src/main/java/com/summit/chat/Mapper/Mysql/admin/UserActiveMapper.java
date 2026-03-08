package com.summit.chat.Mapper.Mysql.admin;

import com.summit.chat.model.vo.UserAllActiveRangeVO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface UserActiveMapper {


    Integer batchInsert(List<?> list);


    List<UserAllActiveRangeVO> queryUserActiveByRange(List<Date> dateList);
}
