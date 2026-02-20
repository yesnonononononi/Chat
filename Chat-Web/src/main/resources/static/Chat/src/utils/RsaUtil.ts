import JSEncrypt from 'jsencrypt';
import CryptoJS from 'crypto-js';
import type { data } from "@/types/user";

export class RsaUtil {
    private static publicKey: string | null = null;
    private static readonly KEY_STORAGE_KEY = "SIGN";
    private static WHITELIST = ["/sign", "/upload"];  //接口白名单,存在即不验签
    /**
     * 获取公钥（带缓存和重试机制）
     * 独立使用 fetch 避免 axios 循环依赖
     */
    public static async getPublicKey(): Promise<string> {
        if (this.publicKey) return this.publicKey;
        
        let sign = localStorage.getItem(this.KEY_STORAGE_KEY);
        if (sign) {
             this.publicKey = sign;
             return sign;
        }

        let n = 2; // 重试次数
        while (n > 0) {
            try {
                const response = await fetch("/api/sign");
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                
                const res: data<string> = await response.json();
                if (res.code === 1 && res.data) {
                    sign = res.data;
                    this.publicKey = sign;
                    localStorage.setItem(this.KEY_STORAGE_KEY, sign);
                    return sign;
                }
            } catch (err) {
                console.error("获取公钥失败, 剩余重试次数:", n - 1, err);
            }
            n--;
        }
        
        throw new Error("无法获取公钥");
    }

    /**
     * RSA加密
     * @param data 待加密数据
     */
    public static async encrypt(data: string): Promise<string> {
        const key = await this.getPublicKey();
        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(key);
        const encrypted = encryptor.encrypt(data);
        if (!encrypted) {
            throw new Error("加密失败");
        }
        return encrypted;
    }

    /**
     * RSA解密 (通常前端不持有私钥，仅作预留或特定场景使用)
     * @param data 待解密数据
     * @param privateKey 私钥
     */
    public static decrypt(data: string, privateKey: string): string {
        const decryptor = new JSEncrypt();
        decryptor.setPrivateKey(privateKey);
        const decrypted = decryptor.decrypt(data);
        if (!decrypted) {
            throw new Error("解密失败");
        }
        return decrypted;
    }

    /**
     * 验签
     * @param data 后端返回的统一响应对象
     * @param url 请求的URL，用于判断是否在白名单中
     */
    public static async checkSign(data: data<any>, url: string = ""): Promise<void> {
        // 白名单接口，无需验签
        // /sign: 获取公钥接口
        // /upload: 文件上传接口（通常返回简单结构，视业务需要可加签）
    
        const isWhitelisted =this.WHITELIST.some(path => url.includes(path));

        // 如果没有签名
        if (!data.sign) {
            // 如果是白名单接口，允许无签名
            if (isWhitelisted) {
                return;
            }
            // 否则，非白名单接口必须有签名，防止降级攻击（攻击者删除sign字段绕过验签）
            throw new Error("响应缺少签名，可能被篡改");
        }

        const publicKey = await this.getPublicKey();
        const verify = new JSEncrypt();
        verify.setPublicKey(publicKey);

        // 复制数据并移除sign字段，构建待验签内容
        const temp = { ...data };
        delete temp.sign;
        // 保持与后端一致的序列化方式（注意：需确保前后端JSON序列化字段顺序一致）
        const content = JSON.stringify(temp);

        // 使用SHA256验签
        const verified = verify.verify(content, data.sign, (str: string) => {
            return CryptoJS.SHA256(str).toString();
        });

        if (!verified) {
            throw new Error("数据验签失败,可能被篡改");
        }
    }
}
