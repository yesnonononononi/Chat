package com.summit.chat.Controller.user;

import com.summit.chat.Dto.CursorPage;
import com.summit.chat.Dto.EmojiDto;
import com.summit.chat.Enum.EmojiStatusEnum;
import com.summit.chat.Result.Result;
import com.summit.chat.service.emoji.EmojiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.cache.annotation.CacheRemoveAll;
import java.util.List;

@RestController
@RequestMapping("/emoji")
@Tag(name = "EmojiController", description = "表情包管理(用户端)")
public class EmojiController {
    @Autowired
    private EmojiService emojiService;

    //查询表情包根据名称
    @Operation(summary = "查询表情包根据条件")
    @PostMapping("/by")
    public Result queryEmojiBy(@RequestBody EmojiDto dto){
        return emojiService.queryEmojiBy(dto);
    }
    //查询表情包列表
    @Operation(summary = "查询表情包列表")
    @GetMapping("/list")

    public Result   queryEmojiList(CursorPage cursorPage){
        cursorPage.setStatus(EmojiStatusEnum.UP.getCode()); // 强制用户只能查询正常状态的表情包
        return emojiService.queryEmojiList(cursorPage);
    }

    @Operation(summary = "查询表情包列表")
    @PostMapping("/listByIds")
    public Result queryEmojiByIds(@RequestBody List<String> idList){
        return emojiService.queryEmojiByIds(idList,EmojiStatusEnum.UP.getCode());
    }
}
