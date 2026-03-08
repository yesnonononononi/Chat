package com.summit.chat.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentToolProviderTest {

    @Test
    void getRandomWhitePictureUrl() {
        AgentToolProvider agentToolProvider = new AgentToolProvider();
        String randomWhitePictureUrl = agentToolProvider.getMusicUrl("http://shanhe.kim/api/music/wyyjx.php?msg=当你");
        System.out.println(randomWhitePictureUrl);

    }

    @Test
    void getMusicUrlForName() {
        AgentToolProvider agentToolProvider = new AgentToolProvider();
        String randomWhitePictureUrl = agentToolProvider.getMusicUrlForName("https://zj.v.api.aa1.cn/api/qqmusic/demo.php","勋章");
        System.out.println(randomWhitePictureUrl);
    }
}