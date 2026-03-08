package com.summit.chat.Mapper.Mysql;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Dto.EmojiDto;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.mysql.Emoji;
import com.summit.chat.model.vo.EmojiVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmojiMapper {
    //添加表情包
    @AutoFill(type = OperationType.INSERT)
    Integer insertEmoji(Emoji emoji);
    //查询表情包根据名称
    List<EmojiVO> queryEmojiBy(EmojiDto dto);
    //查询表情包列表
    List<EmojiVO> queryEmojiList(@Param("cursor") LocalDateTime cursor, @Param("isDesc") Boolean isDesc, @Param("status") Integer status);
    List<EmojiVO> queryEmojiByIds(List<String> idList, Integer status);
    //更新表情包
    @AutoFill(type = OperationType.UPDATE)
    Integer updateEmoji(Emoji emoji);
    //删除表情包
    Integer deleteEmoji(String id);

}
