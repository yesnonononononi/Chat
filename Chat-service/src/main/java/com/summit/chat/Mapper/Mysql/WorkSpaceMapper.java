package com.summit.chat.Mapper.Mysql;

import com.summit.chat.model.entity.mysql.WorkSpace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface WorkSpaceMapper {
    @Insert("insert into work_space(today_new_user, total_user, total_msg,today_new_msg, create_time)  values(#{todayNewUser},#{totalUser},#{totalMsg},#{todayNewMsg},#{createTime})")
    public void insert(WorkSpace workSpace);



    public WorkSpace queryAllData();


    public List<WorkSpace> queryAllDataByDate(List<Date> dateList);


}
