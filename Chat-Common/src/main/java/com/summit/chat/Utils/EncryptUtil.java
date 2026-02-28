package com.summit.chat.Utils;

import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EncryptUtil {


    public static String encrypt(String pw) {
        return BCrypt.hashpw(pw);
    }

    public static Boolean identity(String cur, String real) {
        return BCrypt.checkpw(cur, real);
    }


}