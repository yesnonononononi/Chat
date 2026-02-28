package com.summit.chat.service.Impl.Support.Ai;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiStreamingSupport {
    @Autowired
    private ClientManager clientManager;

    public void handlePartial(String res,String userID) {

        try{
            log.info("【AI】处理中,用户id:{}",userID);
            if(userID == null){
                log.error("【AI】用户id为空 " );
                return;
            }
            SocketIOClient client = clientManager.getClient(userID);
            if(client == null){
                log.error("【AI】用户不在线,用户id:{}",userID);
                return;
            };
            client.sendEvent(ChatEvent.AI_RES_STREAM.getType(),res);
            log.info("【AI】已发送给用户,用户id:{},回复:{}",userID,res);
        }catch (Exception e){
            log.error("【AI】处理错误,用户id:{}",userID, e);
        }
    }

    public void handleErr(Throwable throwable) {
        log.error("【AI】回复错误", throwable);
    }
}
