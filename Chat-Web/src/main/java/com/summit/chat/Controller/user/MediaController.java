package com.summit.chat.Controller.user;

import com.summit.chat.Dto.MediaApplyDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.service.media.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
@Tag(name = "媒体控制")
public class MediaController {
    @Autowired
    private MediaService mediaService;
    @Operation(summary = "发起视频聊天")
    @PostMapping("/send")
    public Result send(@RequestBody MediaApplyDTO dto){
        return mediaService.send(dto);
    }

    @Operation(summary = "接受视频聊天")
    @GetMapping("/accept/{emitterId}")
    public Result accept(@PathVariable String emitterId){
        return mediaService.accept(emitterId);
    }

    @Operation(summary = "拒绝视频聊天")
    @GetMapping("/reject/{emitterId}")
    public Result reject(@PathVariable String emitterId){
        return mediaService.reject(emitterId);
    }

    @Operation(summary = "取消视频聊天")
    @GetMapping("/cancel/{receiverId}")
    public Result cancel(@PathVariable String receiverId){
        return mediaService.cancel(receiverId);
    }
}
