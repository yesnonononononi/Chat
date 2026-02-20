package com.summit.chat.GlobalHandle.SocketHandler;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.summit.chat.Constants.ChatConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Mapper.Cache.RedisProcessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ConnectionListener implements ConnectListener {
    @Resource
    ClientManager clientManager;

    @Autowired
    RedisProcessor redisProcessor;

    @Override
    public void onConnect(SocketIOClient socketIOClient) {
        //获取token
        HandshakeData handshakeData = socketIOClient.getHandshakeData();
        Map<String, List<String>> urlParams = handshakeData.getUrlParams();
        //提取token
        String s = extractToken(urlParams);

        //校验token
        if (s == null || !validateToken(s, socketIOClient)) {
            socketIOClient.sendEvent(ChatConstants.USER_NO_LOGIN);
            socketIOClient.disconnect();
            return;
        }
        log.info("【Socket连接】已连接到客户端:{}", socketIOClient.getSessionId());
        //保存用户信息
        clientManager.setClient(socketIOClient);

    }


    /**
     * 提取token
     *
     * @param urlParams
     * @return
     */
    public String extractToken(Map<String, List<String>> urlParams) {

        List<String> authorization = urlParams.get("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        String token = authorization.get(0);

        if (!token.startsWith("Bearer ")) {
            return null;
        }

        return token.substring(7);

    }

    /**
     * 校验token
     */
    public boolean validateToken(String token, SocketIOClient client) {
        String t = UserConstants.CACHE_USER_TOKEN + ":" + token;
        //查redis,如果存在用户信息,放行
        String userid = redisProcessor.get(t, ChatConstants.USER_INFO);
        if (userid == null) {
            log.warn("【Socket连接】token存在,但是信息为空");

            return false;
        }
        try {
            client.set("USERID", userid);
        } catch (ClassCastException cce) {
            log.error("【Socket连接】拦截器类型转化错误: {}", cce.getMessage());

            return false;
        }
        return true;

    }

}
