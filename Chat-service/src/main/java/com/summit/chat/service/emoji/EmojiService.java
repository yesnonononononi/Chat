package com.summit.chat.service.emoji;

import com.summit.chat.Dto.CursorPage;
import com.summit.chat.Dto.EmojiDto;
import com.summit.chat.Result.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmojiService {
    //添加表情包
    Result insertEmoji(EmojiDto dto);
    //查询表情包根据id
    Result queryEmojiBy(EmojiDto emojiDto);
    //查询表情包列表
    Result   queryEmojiList(CursorPage cursorPage);
    //更新表情包
    Result updateEmoji(EmojiDto dto);
    //删除表情包
    Result deleteEmoji(String id);

    Result uploadEmoji(MultipartFile file, String directory);

    Result queryEmojiByIds(List<String> idList,Integer status);
}
