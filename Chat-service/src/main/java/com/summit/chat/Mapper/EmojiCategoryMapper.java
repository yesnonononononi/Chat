package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.EmojiCategory;
import com.summit.chat.model.vo.EmojiCategoryVO;
import com.summit.chat.model.vo.EmojiVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmojiCategoryMapper{
    //查询所有分类
    List<EmojiCategoryVO> queryAllCategory(Integer status);
    //根据分类id查询所有表情包
    List<EmojiVO> queryEmojiByCategoryId(long categoryId,Integer status);
    //添加分类
    @AutoFill(type = OperationType.INSERT)
    Integer insertCategory(EmojiCategory emojiCategory);
    //修改分类
    @AutoFill(type = OperationType.UPDATE)
    Integer updateCategory(EmojiCategory emojiCategory);
    //删除分类
    Integer deleteCategory(long categoryId);
}
