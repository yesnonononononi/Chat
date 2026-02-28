package com.summit.chat.service.emoji;

import com.summit.chat.Dto.EmojiCategoryDto;
import com.summit.chat.Result.Result;

public interface EmojiCategoryService {
    //查询所有分类
    Result queryAllCategory(Integer status);
    //根据分类id查询所有表情包
    Result queryEmojiByCategoryId(long categoryId,Integer status);
    //添加分类
    Result insertCategory(EmojiCategoryDto emojiCategory);
    //修改分类
    Result updateCategory(EmojiCategoryDto emojiCategory);
    //删除分类
    Result deleteCategory(Long categoryId);
}
