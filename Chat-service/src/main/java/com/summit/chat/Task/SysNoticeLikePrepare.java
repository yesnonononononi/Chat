package com.summit.chat.Task;

import com.summit.chat.Constants.SysNoticeConstants;
import com.summit.chat.Mapper.Mysql.SysNoticeMapper;
import com.summit.chat.model.vo.SysNoticeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SysNoticeLikePrepare {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SysNoticeMapper sysNoticeMapper;



    @Scheduled(cron = "0 */10 * * * *")
    public void prepare() {
        try {
            log.info("【系统公告】点赞数预处理开始");
            long start = System.currentTimeMillis();
            List<SysNoticeVO> sysNoticeVOS = sysNoticeMapper.selectAll();
            sysNoticeVOS.forEach(each -> {
                        Long id = each.getId();
                        Long size = redisTemplate.opsForSet().size(SysNoticeConstants.CACHE_LIKE_PREFIX + id);
                        if (size != null) {
                            sysNoticeMapper.updateLike(id, size);
                        }
                    }
            );
            log.info("【系统公告】点赞数处理耗时: {}ms", (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("【系统公告】点赞数处理失败: {}", e.getMessage(), e);
        }
    }
}
