package com.summit.chat.Controller.user;

import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.Dto.PutGroupMemberDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.service.group.GroupAdminService;
import com.summit.chat.service.group.GroupMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/member")
@Tag(name = "群成员管理")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private GroupAdminService groupAdminService;

    @Operation(summary = "邀请/添加群成员")
    @PostMapping("/add")
    public Result addMember(@RequestBody GroupMemberDTO dto) {
        return groupMemberService.addMember(dto);
    }

    @Operation(summary = "移除群成员")
    @PostMapping("/del")
    public Result delMember(@RequestBody GroupMemberDTO dto) {
        return groupMemberService.delMember(dto);
    }

    @Operation(summary = "查询群成员列表")
    @GetMapping("/list/{groupId}")
    public Result queryMemberByGroupId(@PathVariable Long groupId,
                                       Integer page,
                                       Integer pageSize) {
        return groupMemberService.queryMemberByGroupId(groupId, page, pageSize);
    }

    @Operation(summary = "修改群成员状态")
    @PutMapping("/set")
    public Result blackMember(@RequestBody @Validated PutGroupMemberDTO dto) {
        return groupAdminService.putGroupMember(dto);
    }

}
