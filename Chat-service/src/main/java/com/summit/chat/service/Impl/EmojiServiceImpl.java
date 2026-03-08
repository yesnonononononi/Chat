package com.summit.chat.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.EmojiConstants;
import com.summit.chat.Constants.PaginationConstants;
import com.summit.chat.Dto.CursorPage;
import com.summit.chat.Dto.EmojiDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.EmojiMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.mysql.Emoji;
import com.summit.chat.model.vo.EmojiVO;
import com.summit.chat.service.Impl.Support.Emoji.EmojiValidator;
import com.summit.chat.service.emoji.EmojiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmojiServiceImpl implements EmojiService {
    @Autowired
    private EmojiMapper emojiMapper;
    @Autowired
    private EmojiValidator emojiValidator;
    @Autowired
    private FileLoadServiceImpl fileLoadServiceImpl;

    @Override
    public Result insertEmoji(EmojiDto emojiDto) {
        try {
            emojiValidator.insertValidate(emojiDto);
            Emoji emoji = new Emoji();
            BeanUtil.copyProperties(emojiDto, emoji);
            emoji.setCreatorId(UserHolder.getUserID());
            emojiMapper.insertEmoji(emoji);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】添加失败: {}", e.getMessage(), e);
            throw e;
        }


    }

    @Override
    public Result queryEmojiBy(EmojiDto emojiDto) {
        try {
            emojiValidator.queryByValidate(emojiDto);
            List<EmojiVO> emoji = emojiMapper.queryEmojiBy(emojiDto);
            return Result.ok(emoji);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】查询失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result queryEmojiList(CursorPage cursorPage) {
        try {
            PageHelper.startPage(1, cursorPage.getPageSize(), false);
            Object cursor = cursorPage.getCursor();
            Boolean desc = cursorPage.getIsDesc();
            desc = desc == null || desc;
            try {
                cursor = LocalDateTime.parse(cursor.toString(), PaginationConstants.DATETIME_FORMATTER);
            } catch (Exception e) {
                cursor = desc ? PaginationConstants.INIT_CURSOR_DESC : PaginationConstants.INIT_CURSOR_ASC;
            }
            LocalDateTime cur = (LocalDateTime) cursor;
            List<EmojiVO> emojiVOS = emojiMapper.queryEmojiList(cur, desc, cursorPage.getStatus());
            return Result.ok(emojiVOS);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】查询所有表情包失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result updateEmoji(EmojiDto emojiDto) {
        try {
            Emoji emoji = new Emoji();
            BeanUtil.copyProperties(emojiDto, emoji);
            Integer i = emojiMapper.updateEmoji(emoji);
            if (i <= 0) {
                return Result.fail(EmojiConstants.EMOJI_NO_EXIST);
            }
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】更新失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result deleteEmoji(String id) {
        try {
            Integer i = emojiMapper.deleteEmoji(id);
            if (i <= 0) {
                return Result.fail(EmojiConstants.EMOJI_NO_EXIST);
            }
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】删除失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result uploadEmoji(MultipartFile file, String directory) {
        try {
            emojiValidator.validateFile(file);
            return fileLoadServiceImpl.upload(file, directory);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【表情包】上传失败: {}", e.getMessage(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }

    @Override
    public Result queryEmojiByIds(List<String> idList,Integer status) {
        return Result.ok(emojiMapper.queryEmojiByIds(idList,status));
    }
}
