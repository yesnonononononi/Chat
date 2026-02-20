package com.summit.chat.Utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA工具类
 * 提供RSA公钥私钥生成、加密、解密功能
 */
@Slf4j
public class RSAUtil {

    /**
     * 生成RSA密钥对
     *
     * @return Map<String, String> key: publicKey, privateKey (Base64编码)
     */
    public static Map<String, String> generateKeyPair() {
        // 生成RSA密钥对 (2048位)
        KeyPair pair = SecureUtil.generateKeyPair("RSA", 2048);
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // 获取Base64编码的字符串
        String privateKeyStr = Base64.encode(privateKey.getEncoded());
        String publicKeyStr = Base64.encode(publicKey.getEncoded());

        Map<String, String> map = new HashMap<>();
        map.put("privateKey", privateKeyStr);
        map.put("publicKey", publicKeyStr);
        
        log.info("RSA密钥对生成成功(2048位)");
        return map;
    }

    /**
     * 公钥加密
     *  逻辑:前端用公钥加密,后端用私钥解密,确保前端敏感数据基本安全
     * @param content   待加密内容
     * @param publicKey 公钥(Base64编码)
     * @return 加密后内容(Base64编码)
     */
    public static String encrypt(String content, String publicKey) {
        try {
            RSA rsa = new RSA(null, publicKey);
            return rsa.encryptBase64(content, KeyType.PublicKey);
        } catch (Exception e) {
            log.error("RSA加密失败", e);
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param content    待解密内容(Base64编码)
     * @param privateKey 私钥(Base64编码)
     * @return 解密后内容
     */
    public static String decrypt(String content, String privateKey) {
        try {
            RSA rsa = new RSA(privateKey, null);
            return rsa.decryptStr(content, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error("RSA解密失败", e);
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 签名 (SHA256withRSA)
     *逻辑: 后端签名后,前端用公钥验签,确保服务器响应不会被修改
     * @param content    待签名内容
     * @param privateKey 私钥(Base64编码)
     * @return 签名(Base64编码)
     */
    public static String sign(String content, String privateKey) {
        try {
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, privateKey, null);
            return Base64.encode(sign.sign(cn.hutool.core.util.StrUtil.bytes(content)));
        } catch (Exception e) {
            log.error("RSA签名失败", e);
            throw new RuntimeException("RSA签名失败", e);
        }
    }

    /**
     * 验签 (SHA256withRSA)
     *
     * @param content   待验签内容
     * @param sign      签名(Base64编码)
     * @param publicKey 公钥(Base64编码)
     * @return 验签结果
     */
    public static boolean verify(String content, String sign, String publicKey) {
        try {
            Sign verify = SecureUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKey);
            return verify.verify(cn.hutool.core.util.StrUtil.bytes(content), Base64.decode(sign));
        } catch (Exception e) {
            log.error("RSA验签失败", e);
            return false;
        }
    }
}
