package com.summit.chat.Controller.user;

import com.summit.chat.Dto.EmojiCategoryDto;
import com.summit.chat.Enum.EmojiStatusEnum;
import com.summit.chat.Result.Result;
import com.summit.chat.service.emoji.EmojiCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "表情包分类管理", description = "表情包分类")
@RequestMapping("/emoji/category")
@RestController
public class EmojiCategoryController {
    @Autowired
    private EmojiCategoryService emojiCategoryService;
    //查询所有分类
    @Operation(summary = "查询所有分类")
    @GetMapping("/list")
    @Cacheable(cacheNames = "emoji:category",key = "'allCategor'")
    public Result queryAllCategory(){
        return emojiCategoryService.queryAllCategory(EmojiStatusEnum.UP.getCode()); // 用户只能查询正常状态(1)的分类
    }
    //根据分类id查询所有表情包
    @Operation(summary = "根据分类id查询所有表情包")
    @GetMapping("/{categoryId}")
    public Result queryEmojiByCategoryId(@PathVariable Long categoryId){
        return emojiCategoryService.queryEmojiByCategoryId(categoryId,EmojiStatusEnum.UP.getCode());
    }

}
