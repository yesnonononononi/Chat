package com.summit.chat.service.Impl.Support.Ai;

import com.summit.chat.Constants.AiConstants;
import com.summit.chat.Dto.AiChatDto;
import com.summit.chat.Enum.ModelType;
import com.summit.chat.service.Impl.GlobalValidatorImpl;
import jodd.util.StringUtil;
import org.springframework.stereotype.Component;

@Component
public class AiValidator extends GlobalValidatorImpl<AiChatDto> {

    @Override
    public boolean validate(AiChatDto dto) {
        return false;
    }

    public void validateChat(AiChatDto dto){
        String modelName = dto.getModelName();
        if(StringUtil.isBlank(modelName)){
            super.throwException(AiConstants.MODEL_NOT_EXIST);
        }
        if(!modelName.equals(ModelType.QWEN_PLUS.getModelName())){
            super.throwException(AiConstants.MODEL_NOT_EXIST);
        }
    }
}
