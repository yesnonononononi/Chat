package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.GroupMessageVO;
import com.summit.chat.service.group.GroupMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/msg")
@Tag(name = "群消息管理")
public class GroupMessageController {

    @Autowired
    private GroupMessageService groupMessageService;

    @Operation(summary = "查询群历史消息")
    @GetMapping("/history/{groupId}")
    public Result queryGroupMsgById(@PathVariable Long groupId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return groupMessageService.queryGroupMsgById(groupId, page, pageSize);
    }

    @Operation(summary = "查询用户的群消息")
    @PostMapping("/user/history")
    public Result queryGroupMsgByUserId(@RequestBody GroupMessageDTO dto,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return groupMessageService.queryGroupMsgByUserId(dto, page, pageSize);
    }

    @Operation(summary = "撤回群消息")
    @ShakeProtect("#vo.msgId")
    @PostMapping("/withdrawn")
    public Result withdrawn(@RequestBody GroupMessageVO vo) {
        return groupMessageService.withdrawn(vo);
    }
}
