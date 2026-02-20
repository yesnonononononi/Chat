package com.summit.chat.Utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RSAUtilTest {

    @Test
    void generateKeyPair() {
        Map<String, String> stringStringMap = RSAUtil.generateKeyPair();
       stringStringMap.forEach((key,val)->{
           System.out.println(key +":"+val);
       });
    }
}