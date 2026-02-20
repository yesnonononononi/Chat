package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Constants.GroupNoticeConstants;
import com.summit.chat.Dto.GroupNoticeDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.service.group.GroupNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group-notice")
@Tag(name = "群公告管理")
public class GroupNoticeController {

    @Autowired
    private GroupNoticeService groupNoticeService;

    @PostMapping("/publish")
    @ShakeProtect(value = "#dto.groupId" + ":" + "#dto.publisherId")
    @Operation(summary = "发布群公告")
    @CacheEvict(cacheNames = GroupNoticeConstants.NOTICE_CACHE_PREFIX_LIST, key = "#dto.groupId")
    public Result publish(@RequestBody GroupNoticeDTO dto) {
        String userId = UserHolder.getUserID();
        dto.setPublisherId(Long.valueOf(userId));
        return groupNoticeService.publish(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询群公告")
    @Cacheable(value = GroupNoticeConstants.NOTICE_CACHE_PREFIX, key = "#id", unless = "#result.code!=1")
    public Result getById(@PathVariable Long id) {
        return groupNoticeService.getById(id);
    }

    @GetMapping("/group/{groupId}")
    @Cacheable(value = GroupNoticeConstants.NOTICE_CACHE_PREFIX_LIST, key = "#groupId", unless = "#result.code!=1")
    @Operation(summary = "根据群ID查询群公告列表")
    public Result listByGroupId(@PathVariable Long groupId) {
        return groupNoticeService.listByGroupId(groupId);
    }

    @PutMapping("/update")
    @Operation(summary = "更新群公告")
    @CacheEvict(value = GroupNoticeConstants.NOTICE_CACHE_PREFIX_LIST, key = "#dto.groupId")
    @CachePut(cacheNames = GroupNoticeConstants.NOTICE_CACHE_PREFIX, key = "#dto.id")
    public Result update(@RequestBody GroupNoticeDTO dto) {
        return groupNoticeService.update(dto);
    }

    @PostMapping("/del/{groupId}/{id}")
    @Operation(summary = "删除群公告")
    @CacheEvict(cacheNames = GroupNoticeConstants.NOTICE_CACHE_PREFIX_LIST, key = "#groupId")
    public Result delete(@PathVariable Long groupId,@PathVariable Long id) {
        return groupNoticeService.delete(id,groupId);
    }

    @PostMapping("/{groupId}/batch")
    @Operation(summary = "批量删除群公告")
    @CacheEvict(cacheNames = GroupNoticeConstants.NOTICE_CACHE_PREFIX_LIST, key = "#groupId")
    public Result batchDelete(@RequestBody List<Long> ids, @PathVariable Long groupId) {
        return groupNoticeService.batchDelete(ids, groupId);
    }

    @GetMapping("/count/{groupId}")
    @Operation(summary = "统计群组公告数量")
    public Result countByGroupId(@PathVariable Long groupId) {
        return groupNoticeService.countByGroupId(groupId);
    }
}