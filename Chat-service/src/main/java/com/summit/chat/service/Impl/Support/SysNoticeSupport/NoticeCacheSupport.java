package com.summit.chat.service.Impl.Support.SysNoticeSupport;

import com.summit.chat.Constants.SysNoticeConstants;
import com.summit.chat.model.vo.NoticeLikeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NoticeCacheSupport {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean isLike(String userID, String id) {

        return redisTemplate.opsForSet().isMember(buildKey(id), userID);
    }

    public void cancelLike(String userID, String id) {
        try {
            redisTemplate.opsForSet().remove(buildKey(id), userID);
        } catch (Exception e) {
            log.error("【系统公告】取消点赞失败: {}", e.getMessage(), e);
            throw e;
        }
    }


    public String buildKey(String id) {
        return SysNoticeConstants.CACHE_LIKE_PREFIX + id;
    }

    public void like(String userID, String id) {
        try {

            redisTemplate.opsForSet().add(buildKey(id), userID);
        } catch (Exception e) {
            log.error("【系统公告】点赞失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    public List<NoticeLikeVO> getLikeList(List<String> list) {
        List<NoticeLikeVO> notices = new ArrayList<>();
        list.forEach(each -> {
            NoticeLikeVO notice = new NoticeLikeVO();
            notice.setNoticeId(Long.valueOf(each));
            notice.setLike(getLikeCount(each));
            notices.add(notice);
        });
        return notices;
    }

    public Integer getLikeCount(String id) {
        return Math.toIntExact(redisTemplate.opsForSet().size(buildKey(id)));
    }
}
