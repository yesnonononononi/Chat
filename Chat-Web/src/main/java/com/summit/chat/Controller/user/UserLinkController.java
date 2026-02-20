package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Result.Result;
import com.summit.chat.service.UserLink.UserLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户联系人")
@Slf4j
public class UserLinkController {
    @Autowired
    UserLinkService userLinkService;

    @Operation(summary = "根据用户id查询所有联系人")
    @GetMapping("/link-user")
    public Result getLinkById() {
        return userLinkService.getUserLinkById();
    }




    @Operation(summary = "根据用户id删除联系人")
    @PostMapping("/link-user/delete")
    @ShakeProtect("#dto.linkID + ':' + T(com.summit.chat.Utils.UserHolder).getUserID()")
    public Result delLink(@RequestBody @Validated UserLinkDto dto) {
        return userLinkService.delLink(dto);
    }
}