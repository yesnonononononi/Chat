package com.summit.chat.service.message.PrivateMessageResendCenter;

import cn.hutool.core.util.IdUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Utils.MsgQueueUtil;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Slf4j
@Component
public class MsgWorker {
    @Autowired
    ClientManager clientManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MsgQueueUtil msgQueueUtil;

    public void conduct(MsgContext<PrivateMessageVO> context) {

        PrivateMessageVO msg = context.getMsg();

        SocketIOClient client = clientManager.getClient(msg.getReceiveId());

        if (client == null) {
            log.info("用户:{}不在线",msg.getReceiveId());
            context.setOnline(false);
        }
        if (msg.getStatus() == null) {
            msg.setStatus(MsgEnum.NOT_ONLINE.getStatus());
        }

        // 优先使用前端传入的 msgId
        if (msg.getMsgId() == null || msg.getMsgId().trim().isEmpty()) {
             String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
             msg.setMsgId(snowflakeNextIdStr);
        }
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        context.setReceiveClient(client);
        msg.setSendTime(timestamp);

        Long seq = getMsgSessionSeqFromCache(msg);
        msg.setSessionSeq(seq);
    }

    public Long getMsgSessionSeqFromCache(PrivateMessageVO msg){
        return msgQueueUtil.getSessionSeqFromCache(MsgQueueUtil.getSessionId(msg.getEmitterId(), msg.getReceiveId()));
    }
}

