package com.summit.chat.service.Impl.Support.Emoji;

import com.summit.chat.Constants.EmojiConstants;
import com.summit.chat.Dto.EmojiDto;
import com.summit.chat.Mapper.EmojiCategoryMapper;
import com.summit.chat.Utils.ImageUtil;
import com.summit.chat.model.vo.EmojiCategoryVO;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Component
public class EmojiValidator extends GlobalValidatorImpl<EmojiDto> {
    @Autowired
    private EmojiCategoryMapper mapper;

    public void queryByValidate(EmojiDto dto) {
        String content = dto.getContent();
        Long categoryId = dto.getCategoryId();
        Timestamp createTime = dto.getCreateTime();
        String creatorId = dto.getCreatorId();
        String id = dto.getId();
        Integer status = dto.getStatus();
        if (id == null && categoryId == null && creatorId == null && createTime == null && (content == null || content.isEmpty()) && status == null) {
            super.throwException(EmojiConstants.ILLEGAL_CHAR);
        }
    }

    @Override
    public boolean validate(EmojiDto dto) {
        return false;
    }

    public void insertValidate(EmojiDto emojiDto) {
        String url = emojiDto.getUrl();
        Long sort = emojiDto.getSort();
        Long categoryId = emojiDto.getCategoryId();
        String content = emojiDto.getContent();
        List<EmojiCategoryVO> emojiCategoryVOS = mapper.queryAllCategory(null);
        if (url == null || StringUtil.isBlank(url)) {
            super.throwException(EmojiConstants.URL_NULL);
        }
        if (sort == null || sort < 0 || categoryId == null) {
            super.throwException(EmojiConstants.ILLEGAL_CHAR);
        }
        if (content == null || StringUtil.isBlank(content)) {
            super.throwException(EmojiConstants.CONTENT_NULL);
        }
        for (EmojiCategoryVO emojiCategoryVO : emojiCategoryVOS) {
            if (emojiCategoryVO.getId() == categoryId) {
                return;
            }
        }
        super.throwException(EmojiConstants.CATEGORY_NOT_EXIST);
    }

    public void validateFile(MultipartFile file)  {
        try {
            // 将MultipartFile转换为字节数组
            byte[] bytes = file.getBytes();

            // 使用NativeImageUtil获取图片的宽高
            int width = ImageUtil.getImageWidth(bytes);
            int height = ImageUtil.getImageHeight(bytes);

            // 验证宽高是否符合要求（例如：宽高不能为0）
            if ((width <= 0 || height <= 0) && (width > EmojiConstants.DEFAULT_EMOJI_MAX_WIDTH || height > EmojiConstants.DEFAULT_EMOJI_MAX_HEIGHT)) {
                super.throwException(EmojiConstants.INVALID_IMAGE_DIMENSIONS);
            }
        } catch (IOException e) {
            log.error("【表情包】文件读取错误: {}", e.getMessage(), e);
            super.throwException(EmojiConstants.FILE_READ_ERROR);
        }
    }
}
