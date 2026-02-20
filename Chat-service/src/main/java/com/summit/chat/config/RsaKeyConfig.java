package com.summit.chat.config;

import com.summit.chat.Utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RSA密钥配置类
 * 负责加载和管理RSA公钥私钥
 *
 * 安全措施说明：
 * 1. 优先从配置文件(application.yml)或环境变量中读取 rsa.public-key 和 rsa.private-key
 * 2. 如果未配置，则自动生成临时密钥对，并在日志中输出警告，提示运维人员配置
 * 3. 这种机制保证了开发环境的便利性（无需手动配置），同时提醒生产环境必须配置持久化的密钥
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyConfig {

    private String publicKey;
    private String privateKey;

    @PostConstruct
    public void init() {
        if (publicKey == null || privateKey == null || publicKey.isEmpty() || privateKey.isEmpty()) {
            log.warn("=================================================================================");
            log.warn("【安全警告】未检测到配置的RSA密钥(rsa.public-key/rsa.private-key)");
            log.warn("正在生成临时RSA密钥对...请注意：重启服务后密钥将丢失！");
            log.warn("生产环境请务必将生成的密钥配置到环境变量或配置文件中！");
            
            Map<String, String> keyPair = RSAUtil.generateKeyPair();
            this.publicKey = keyPair.get("publicKey");
            this.privateKey = keyPair.get("privateKey");
            
            log.info("临时公钥 (PublicKey): {}", publicKey);
            log.info("临时私钥 (PrivateKey): {}", privateKey);
            log.warn("=================================================================================");
        } else {
            log.info("RSA密钥已从配置中成功加载");
        }
    }
}
