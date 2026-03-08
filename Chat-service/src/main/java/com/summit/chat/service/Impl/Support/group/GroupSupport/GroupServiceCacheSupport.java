package com.summit.chat.service.Impl.Support.group.GroupSupport;

import com.summit.chat.Mapper.Mysql.GroupMapper;
import com.summit.chat.model.vo.GroupChatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 群聊信息缓存支持
 */
@Slf4j
@Component
public class GroupServiceCacheSupport {

    @Autowired
    private GroupMapper groupMapper;

    /**
     * 获取群聊信息（缓存）
     */
    @Cacheable(value = "chat:group:info", key = "#groupId")
    public GroupChatVO getGroupInfo(Long groupId) {
        return groupMapper.queryGroupById(groupId);
    }

    /**
     * 清除群聊信息缓存
     */
    @CacheEvict(value = "chat:group:info", key = "#groupId")
    public void evictGroupInfoCache(Long groupId) {
        log.info("【缓存】清除群聊信息缓存, groupId: {}", groupId);
    }
}
