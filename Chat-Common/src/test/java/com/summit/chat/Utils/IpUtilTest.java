package com.summit.chat.Utils;

import com.summit.chat.model.dto.IpAddressDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpUtilTest {

    @Test
    void getIpAddressInfo() {
        IpAddressDTO ipAddressInfo = IpUtil.getIpAddressInfo("39.101.68.215");
        if(ipAddressInfo ==null){
            System.out.println("获取失败");
            return;
        }
        System.out.println(ipAddressInfo.getAddress());
    }
}