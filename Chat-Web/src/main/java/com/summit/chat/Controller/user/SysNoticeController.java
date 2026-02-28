package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.RequireRole;
import com.summit.chat.Constants.SysNoticeConstants;
import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.service.SysNotice.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys-notice")
@Tag(name = "系统公告管理")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    @PostMapping("/publish")
    @RequireRole
    @Operation(summary = "发布系统公告")
    @CacheEvict(value = SysNoticeConstants.NOTICE_CACHE_LIST, allEntries = true)
    public Result publish(@RequestBody SysNoticeDTO dto) {
        String userId = UserHolder.getUserID();
        if (userId != null) {
            dto.setPublisherId(Long.valueOf(userId));
        }
        return sysNoticeService.publish(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询系统公告")
    @Cacheable(value = SysNoticeConstants.NOTICE_CACHE_PREFIX, key = "#id", unless = "#result.code!=1")
    public Result getById(@PathVariable Long id) {
        return sysNoticeService.getById(id);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有有效的系统公告")
    @Cacheable(value = SysNoticeConstants.NOTICE_CACHE_LIST, unless = "#result.code!=1")
    public Result list() {
        return sysNoticeService.list();
    }

    @GetMapping("/queryLikeList/{ids}")
    @Operation(summary = "批量查询点赞列表根据系统公告id")
    public Result queryLikeList(@PathVariable String ids){
        return sysNoticeService.queryLikeList(ids);
    }


    @PutMapping("/update")
    @RequireRole
    @Operation(summary = "更新系统公告")
    @CacheEvict(value = SysNoticeConstants.NOTICE_CACHE_LIST, allEntries = true)
    @CachePut(value = SysNoticeConstants.NOTICE_CACHE_PREFIX, key = "#dto.id", unless = "#result.code!=1")
    public Result update(@RequestBody SysNoticeDTO dto) {
        return sysNoticeService.update(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "逻辑删除系统公告")
    @RequireRole
    @CacheEvict(value = {SysNoticeConstants.NOTICE_CACHE_LIST, SysNoticeConstants.NOTICE_CACHE_PREFIX}, allEntries = true)
    public Result delete(@PathVariable Long id) {
        return sysNoticeService.delete(id);
    }

    @GetMapping("/like/{id}")
    @Operation(summary = "点赞系统公告")
    @CacheEvict(value = SysNoticeConstants.NOTICE_CACHE_LIST, allEntries = true)
    public Result like(@PathVariable String id) {
        return sysNoticeService.like(id);
    }
}
