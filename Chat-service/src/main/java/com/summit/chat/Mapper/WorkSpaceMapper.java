package com.summit.chat.Mapper;

import com.summit.chat.model.entity.WorkSpace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkSpaceMapper {
    @Insert("insert into work_space(today_new_user, total_user, today_new_order, total_order, today_new_income, total_income, create_time)  values(#{todayNewUser},#{totalUser},#{todayNewOrder},#{totalOrder},#{todayNewIncome},#{totalIncome},#{createTime})")
    public void insert(WorkSpace workSpace);



    @Select(
            "select u.totalUser as totalUser, uvr.totalOrder as totalOrder, uvr.totalIncome as totalIncome,(uvr.totalOrder - ws.tor) as todayNewOrder, (u.totalUser - ws.tu) as todayNewUser, (uvr.totalIncome-ws.ti) as todayNewIncome" +
                    " from  (select ifnull(count(user.id),0) as totalUser  from user where status = 1 ) as  u, " +
                    "(select IFNULL(MAX(total_order),0) as tor,IFNULL(MAX(total_income),0) as ti,IFNULL(MAX(total_user),0) as tu " +
                    "  from work_space where DATE(create_time) = DATE_SUB(CURDATE(),interval 1 day )) as ws, " +
                    "  (select 0 as totalIncome, 0 as totalOrder) uvr"
    )
    public WorkSpace queryAllData();


}
