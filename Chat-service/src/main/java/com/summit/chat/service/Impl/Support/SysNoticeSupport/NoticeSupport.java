package com.summit.chat.service.Impl.Support.SysNoticeSupport;

import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Mapper.SysNoticeMapper;
import com.summit.chat.model.vo.NoticeLikeVO;
import com.summit.chat.model.vo.SysNoticeVO;
import com.summit.chat.service.SysNotice.SysNoticeSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class NoticeSupport implements SysNoticeSupport {
    @Autowired
    private ClientManager clientManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private NoticeCacheSupport noticeCacheSupport;
    @Autowired
    private SysNoticeMapper sysNoticeMapper;


    @Override
    public void publishToClient(SysNoticeDTO sysNotice) {
        //发布者信息不透
        publisherSecurity(sysNotice);
        try {
            Collection<SocketIOClient> allClient = clientManager.getAllClient();
            allClient.forEach(client -> {
                if (client != null && client.isChannelOpen()) {
                    client.sendEvent(ChatEvent.SYS_NOTICE.getType(), sysNotice);
                }
            });
            log.info("【系统公告】成功发送系统通知给所有在线用户！");

        } catch (Exception e) {
            log.error("【系统公告】发送系统公告: {} 失败, 错误:{}", sysNotice.getMsg(), e.getMessage(), e);
        }
    }

    private void publisherSecurity(SysNoticeDTO dto) {
        dto.setPublisherId(null);
    }


    /**
     * 判断用户是否点赞
     *
     * @param userID
     * @param id
     * @return
     */
    public boolean isLike(String userID, String id) {
        return noticeCacheSupport.isLike(userID, id);
    }

    /**
     * 取消点赞
     * @param userID 用户id
     * @param id 消息id
     */
    public void cancelLike(String userID, String id) {
        noticeCacheSupport.cancelLike(userID,id);
    }

    public void like(String userID, String id) {
        noticeCacheSupport.like(userID,id);
    }

    public List<String> getIdList(String ids) {
        String[] split = ids.split(",");
        return Arrays.stream(split).toList();

    }

    public List<NoticeLikeVO> getLikeList(List<String> list) {
        //缓存中查询
        List<NoticeLikeVO> sl = noticeCacheSupport.getLikeList(list);
        if(sl.isEmpty()){
            //数据库中查询
             sl = sysNoticeMapper.queryLikeList(list);
        }
        return sl;
    }
}

