package com.summit.chat.Controller.user;

import com.summit.chat.Dto.GroupApplicationDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.service.group.GroupApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group-apply")
@Tag(name = "群聊申请管理")
public class GroupApplicationController {

    @Autowired
    private GroupApplicationService groupApplicationService;

    @PostMapping("/apply")
    @Operation(summary = "申请加入群聊")
    public Result apply(@RequestBody GroupApplicationDTO dto) {
        String userId = UserHolder.getUserID();
        dto.setApplicantId(Long.valueOf(userId));
        return groupApplicationService.apply(dto);
    }

    @PostMapping("/approve")
    @Operation(summary = "同意入群申请")
    public Result approve(@RequestBody GroupApplicationDTO dto) {
        String userId = UserHolder.getUserID();
        dto.setProcessedBy(Long.valueOf(userId));
        return groupApplicationService.approve(dto);
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝入群申请")
    public Result reject(@RequestBody GroupApplicationDTO dto) {
        String userId = UserHolder.getUserID();
        dto.setProcessedBy(Long.valueOf(userId));
        return groupApplicationService.reject(dto);
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "获取群组的申请列表(管理员/群主)")
    public Result listByGroup(@PathVariable Long groupId) {
        String userId = UserHolder.getUserID();
        return groupApplicationService.listByGroup(groupId, Long.valueOf(userId));
    }

    @GetMapping("/user")
    @Operation(summary = "获取我的申请列表")
    public Result listByUser() {
        String userId = UserHolder.getUserID();
        return groupApplicationService.listByUser(Long.valueOf(userId));
    }
}
