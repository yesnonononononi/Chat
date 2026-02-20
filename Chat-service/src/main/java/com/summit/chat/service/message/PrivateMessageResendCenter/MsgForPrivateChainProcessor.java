package com.summit.chat.service.message.PrivateMessageResendCenter;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.model.vo.PrivateMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MsgForPrivateChainProcessor {
    @Autowired
    MsgToQueue msgToQueue;
    @Autowired
    MsgValidate msgValidate;
    @Autowired
    MsgReSend msgReSend;
    @Autowired
    MsgWorker msgWorker;


    /**
     * 私人转发服务,校验msg合法性-生成全局唯一id-转发给用户-送入消息队列,持久化保存
     *
     * @param emitterClient 发送信息的客户端
     * @param vo            发送的消息体
     * @param ackRequest    回执器
     * @return
     */
    public String start(SocketIOClient emitterClient, PrivateMessageVO vo,AckRequest ackRequest) {
        MsgContext<PrivateMessageVO> context = new MsgContext<>(emitterClient);
        context.setOnline(true);
        context.setMsg(vo);
        context.setAckRequest(ackRequest);
        msgValidate.conduct(context);
        msgWorker.conduct(context);
        msgReSend.conduct(context);
        msgToQueue.conduct(context);
        return "";
    }
}

