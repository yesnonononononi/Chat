package com.summit.chat.Controller.user;

import com.summit.chat.Dto.GroupChatDto;
import com.summit.chat.Result.Result;
import com.summit.chat.service.group.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@Tag(name = "群聊管理")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "创建群聊")
    @PostMapping("/add")
    public Result addGroup(@RequestBody GroupChatDto dto) {
        return groupService.addGroup(dto);
    }

    @Operation(summary = "删除群聊")
    @DeleteMapping("/del")
    public Result delGroup(@RequestParam Long groupId, @RequestParam String userId) {
        return groupService.delGroup(groupId, userId);
    }



    @Operation(summary = "更新群聊信息")
    @PutMapping("/update")
    public Result putGroup(@RequestBody GroupChatDto dto) {
        return groupService.putGroup(dto);
    }

    @Operation(summary = "根据ID查询群聊")
    @GetMapping("/{groupId}")
    public Result queryGroupById(@PathVariable Long groupId) {
        return groupService.queryGroupById(groupId);
    }



    @Operation(summary = "查询用户创建的群聊")
    @GetMapping("/user/{userId}")
    public Result queryUserGroupByUserId(@PathVariable String userId, 
                                         @RequestParam(defaultValue = "1") Integer page, 
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return groupService.queryUserGroupByUserId(userId, page, pageSize);
    }
    @Operation(summary = "根据群聊名字分页查询群聊")
    @GetMapping("/query")
    public Result queryGroupByName(@RequestParam(required = false) String name,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return groupService.queryGroupByName(name, page, pageSize);
    }

    @Operation(summary = "查询用户参加的所有群聊")
    @GetMapping("/user")
    public Result queryGroupByUserId(@RequestParam String userId) {
        return groupService.queryGroupByUserId(userId);
    }
}
