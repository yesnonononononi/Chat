package com.summit.chat.Mapper;

import com.summit.chat.model.entity.WorkSpace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface WorkSpaceMapper {
    @Insert("insert into work_space(today_new_user, total_user, total_msg,today_new_msg, create_time)  values(#{todayNewUser},#{totalUser},#{totalMsg},#{todayNewMsg},#{createTime})")
    public void insert(WorkSpace workSpace);



    public WorkSpace queryAllData();


    public List<WorkSpace> queryAllDataByDate(List<Date> dateList);


}
