package com.summit.chat.Utils;

import cn.hutool.core.util.StrUtil;
import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.summit.chat.Properties.AliyunProperty;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliyunUtil {
    String templateParam;
    String templateCode;
    String endpoint;
    String signName;
    long codeType;
    long validTime;
    long codeLength;

    AliyunProperty property;
    private Client client;

    public AliyunUtil(AliyunProperty property) {
        this.property = property;
    }

    @PostConstruct
    public void init() throws Exception {
        templateParam = property.getTemplateParam();
        templateCode = property.getTemplateCode();
        endpoint = property.getEndpoint();
        signName = property.getSignName();
        codeType = property.getCodeType();
        validTime = property.getValidTime();
        codeLength = property.getCodeLength();
        
        // 检查必要配置项是否已设置
        if (property.getAccessKeyId() == null || property.getAccessKeySecret() == null || endpoint == null) {
            log.error("【阿里云短信】阿里云配置缺失，请检查配置文件中的 accessKeyId、accessKeySecret 和 endpoint 是否已设置");
            throw new IllegalStateException("阿里云配置缺失，无法初始化客户端");
        }
        
        Config config = new Config();
        config.setAccessKeyId(property.getAccessKeyId()).setAccessKeySecret(property.getAccessKeySecret()).setEndpoint(endpoint);
        client = new Client(config);
    }

    private boolean validate(CheckSmsVerifyCodeResponse response) {
        try {
            if (response == null) {
                log.error("【阿里云短信】未收到校验返回信息");
                return false;
            }
            CheckSmsVerifyCodeResponseBody body = response.getBody();
            String errorMsg = body.getAccessDeniedDetail();
            if (StrUtil.isBlank(errorMsg)) {
                String code = body.getCode();
                Boolean success = body.getSuccess();
                if (StrUtil.equals(code, "OK") && success) {
                    return true;
                }
                log.error("【阿里云短信】校验错误状态码:{}", code);
                return false;
            }
            log.error("校验发生错误: {}", errorMsg);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validate(String phoneNumber, String verifyCode) throws Exception {
        try {
            CheckSmsVerifyCodeResponse response = client.checkSmsVerifyCode(
                    new CheckSmsVerifyCodeRequest()
                            .setPhoneNumber(phoneNumber)
                            .setVerifyCode(verifyCode)
            );
            return validate(response);
        }catch (Exception e){
            return false;
        }


    }


    private boolean send(SendSmsVerifyCodeResponse response) {
        if (response == null) {
            log.error("【阿里云短信】未获取到响应信息");
            return false;
        }
        try {
            SendSmsVerifyCodeResponseBody body = response.getBody();


            String errorMsg = body.getAccessDeniedDetail();

            if (StrUtil.isBlank(errorMsg)) {
                String code = body.getCode();
                Boolean success = body.getSuccess();
                if (StrUtil.equals(code, "OK") && success) {
                    return true;
                }
                log.error("收到错误发送状态码: {}", code);
                return false;
            }
            log.error("发送发生错误: {}", errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    @Async
    public void send(String phoneNumber) {
        // 检查客户端是否已正确初始化
        if (client == null) {
            log.error("【阿里云短信】阿里云客户端未正确初始化，请检查配置");
            return;
        }

        try {
            //通过客户端发送短信验证码请求，使用空的请求参数构建器创建请求对象
            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(
                    new SendSmsVerifyCodeRequest()
                            .setPhoneNumber(phoneNumber)
                            .setTemplateCode(templateCode)
                            .setTemplateParam(templateParam)
                            .setSignName(signName)
                            .setCodeType(codeType)
                            .setValidTime(validTime)
                            .setCodeLength(codeLength)
            );
            send(response);
        } catch (Exception e) {
            log.error("【阿里云短信】发送短信失败:{}", phoneNumber, e);
        }
    }
}