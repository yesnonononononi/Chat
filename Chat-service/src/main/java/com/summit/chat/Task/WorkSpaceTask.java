package com.summit.chat.Task;

import com.summit.chat.Mapper.UserMapper;
import com.summit.chat.Mapper.WorkSpaceMapper;
import com.summit.chat.model.entity.WorkSpace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkSpaceTask {
    @Autowired
    private WorkSpaceMapper workSpaceMapper;


    /**
    * 执行每日0点任务,更新工作空间数据
    * */
    @Scheduled(cron = "55 59 23 * * ? ")
    public void task() {
        try {
            WorkSpace workSpace = workSpaceMapper.queryAllData();
            workSpaceMapper.insert(workSpace);
        }catch (Exception e){
            log.error("【工作空间】更新失败",e);
        }
    }


}
