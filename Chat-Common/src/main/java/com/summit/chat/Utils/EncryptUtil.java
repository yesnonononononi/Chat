package com.summit.chat.Utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.log.Log;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;


@Slf4j
public class EncryptUtil {


    public static String encrypt(String pw) {
        return BCrypt.hashpw(pw);
    }

    public static Boolean identity(String cur, String real) {
        return BCrypt.checkpw(cur, real);
    }


}