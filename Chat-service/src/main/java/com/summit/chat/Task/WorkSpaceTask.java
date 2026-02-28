package com.summit.chat.Task;

import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import com.summit.chat.Mapper.WorkSpaceMapper;
import com.summit.chat.Mapper.admin.UserActiveMapper;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.entity.WorkSpace;
import com.summit.chat.model.vo.UserActiveVO;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class WorkSpaceTask {
    @Autowired
    private WorkSpaceMapper workSpaceMapper;

    /**
     * 执行每日0点任务,更新工作空间数据
     *
     */
    @Scheduled(cron = "55 59 23 * * ? ")
    public void task() {
        try {
            WorkSpace workSpace = workSpaceMapper.queryAllData();
            workSpaceMapper.insert(workSpace);
        } catch (Exception e) {
            log.error("【工作空间】更新失败", e);
        }
    }



}
