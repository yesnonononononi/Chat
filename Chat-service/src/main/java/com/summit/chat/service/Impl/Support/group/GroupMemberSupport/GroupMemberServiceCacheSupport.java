package com.summit.chat.service.Impl.Support.group.GroupMemberSupport;

import com.summit.chat.Mapper.Mysql.GroupMemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class GroupMemberServiceCacheSupport {
    @Autowired
    GroupMemberMapper groupMemberMapper;
    /**
     * 获取群成员ID列表（缓存）
     */

    @Cacheable(value = "chat:group:member", key = "#groupId")
    public List<String> getGroupMemberIds(Long groupId) {
        return groupMemberMapper.queryUserIdsByGroupId(groupId);
    }

    /**
     * 清除群成员缓存
     */

    @CacheEvict(value = "chat:group:member", key = "#groupId")
    public void evictGroupMemberCache(Long groupId) {
        log.info("【缓存】清除群成员缓存, groupId: {}", groupId);
    }

}
