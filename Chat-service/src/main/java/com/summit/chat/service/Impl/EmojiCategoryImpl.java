package com.summit.chat.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Dto.EmojiCategoryDto;
import com.summit.chat.Enum.EmojiStatusEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.EmojiCategoryMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.mysql.EmojiCategory;
import com.summit.chat.service.emoji.EmojiCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmojiCategoryImpl implements EmojiCategoryService {
    @Autowired
    private EmojiCategoryMapper emojiCategoryMapper;
    @Override
    public Result queryAllCategory(Integer status) {
        try{
           return Result.ok( emojiCategoryMapper.queryAllCategory(status));
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【表情包】添加失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result queryEmojiByCategoryId(long categoryId,Integer status) {

        try{
            return Result.ok(emojiCategoryMapper.queryEmojiByCategoryId(categoryId,status));
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【表情包分类】查询失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result insertCategory(EmojiCategoryDto emojiCategory) {
        try{
            EmojiCategory emojiCategory1 = new EmojiCategory();
            BeanUtil.copyProperties(emojiCategory,emojiCategory1);
            if(emojiCategory1.getStatus() == null){
                emojiCategory1.setStatus(EmojiStatusEnum.UP.getCode());
            }
            emojiCategoryMapper.insertCategory(emojiCategory1);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【表情包分类】添加失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result updateCategory(EmojiCategoryDto emojiCategory) {
        try{
            EmojiCategory emojiCategory1 = new EmojiCategory();
            BeanUtil.copyProperties(emojiCategory,emojiCategory1);
            emojiCategoryMapper.updateCategory(emojiCategory1);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【表情包分类】更新失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result deleteCategory(Long categoryId) {
        try{
            emojiCategoryMapper.deleteCategory(categoryId);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【表情包分类】删除失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
