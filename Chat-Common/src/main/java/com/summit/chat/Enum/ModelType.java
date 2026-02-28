package com.summit.chat.Enum;

import lombok.Getter;
@Getter
public enum ModelType {
    QWEN_PLUS("qwen-plus","千问模型");
    private final String modelName;
    private final String modelDesc;
    ModelType(String modelName, String modelDesc) {
        this.modelName = modelName;
        this.modelDesc = modelDesc;
    }

}
