package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.FriendDto;
import com.summit.chat.Result.Result;
import com.summit.chat.service.Friend.FriendApply;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/friend")
@Tag(name = "好友申请")
public class FriendApplyController {
    @Autowired
    FriendApply friendApply;
    @Operation(summary = "发送好友申请")
    @ShakeProtect(" #dto.applicantId + ':' + #dto.recipientId")
    @PostMapping("/apply")
    public Result sendApplication(@RequestBody @Validated FriendDto dto) {
        return friendApply.sendApplication(dto);
    }

    @Operation(summary = "接受好友请求")
    @PostMapping("/ack")
    @ShakeProtect("#dto.id")
    public Result ackApplication(@RequestBody @Validated FriendDto dto){
        return friendApply.ackApplication(dto);
    }


    @Operation(summary = "拒绝好友请求")
    @ShakeProtect("#dto.id")
    @PostMapping("/reject")
    public Result rejectApplication(@RequestBody @Validated FriendDto dto){
        return friendApply.rejectApplication(dto);
    }

    @Operation(summary = "查询好友请求")
    @GetMapping("/query")
    public Result queryApplication(Integer page,Integer pageSize){
        return friendApply.queryApplication(page,pageSize);
    }

}
