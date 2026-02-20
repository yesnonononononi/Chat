package com.summit.chat.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.aliyun")
public class AliyunProperty {
    private String endpoint;
    private String signName;
    private String TemplateCode;
    private String TemplateParam;
    private long codeType;
    private long ValidTime;
    private long codeLength;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String endpointForOss;
}
