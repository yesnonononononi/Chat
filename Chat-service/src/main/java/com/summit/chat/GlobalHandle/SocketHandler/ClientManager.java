package com.summit.chat.GlobalHandle.SocketHandler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.chat.Constants.ChatConstants;
import com.summit.chat.Enum.ChatEvent;
import com.summit.chat.model.entity.ClientSession;
import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 客户端会话管理器，负责管理客户端的连接状态和会话信息。
 */
@Component
@Slf4j
@Validated
public class ClientManager {
    private static final String SESSION_PREFIX = "session:id:";
    @Resource
    SocketIOServer socketIOServer;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;  // <client userid:uuid>
    private final ConcurrentMap<String, ClientSession> map = new ConcurrentHashMap<>();

    /**
     * 根据接收方ID获取对应的SocketIO客户端。
     *
     * @param receiveID 接收方ID
     * @return 对应的SocketIO客户端，如果不存在或不在线则返回null
     */
    public SocketIOClient getClient(@NotNull String receiveID) {
        try {
            SocketIOClient clientFromMap = getClientFromMap(receiveID); // 从map中获取会话id
            if (clientFromMap != null) return clientFromMap;
            
            ClientSession clientFromCache = getClientFromCache(receiveID);// 从缓存中获取会话id
            if (clientFromCache == null) {
                log.info("【会话管理】未获取到用户:{}会话", receiveID);
                return null;
            }   //判断缓存中是否有会话对象
            UUID sessionId = clientFromCache.getSessionId();
            if (sessionId == null) {
                log.info("【会话管理】已获取用户会话,但未存在会话id,用户id:{}", receiveID);
                return null;
            }  // 判断会话id是否为空
            SocketIOClient client = socketIOServer.getClient(sessionId);// 获取会话id对应的SocketIO客户端
            if (client == null || !client.isChannelOpen()) {
                log.info("【会话管理】用户不在线或连接不在本机,用户id:{}", receiveID);
                // 仅清理本地缓存，不轻易清理Redis
                map.remove(receiveID);
                return null;
            }  // 判断会话id对应的SocketIO客户端是否在线
            
            // 将从Redis获取到的有效Session加入本地缓存
            map.put(receiveID, clientFromCache);
            return client;
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】Redis连接异常,用户id:{}", receiveID, e);
            return null;
        } catch (Exception e) {
            log.error("【会话管理】获取用户会话异常,用户id:{}", receiveID, e);
            return null;
        }
    }


    public Collection<SocketIOClient> getAllClient(){

        return  this.socketIOServer.getAllClients();
    }

    /**
     * 根据用户ID移除客户端会话。
     *
     * @param userId 用户ID
     */
    public void removeClient(String userId) {
        try {
            if (userId == null) return;
            redisTemplate.delete(buildKey(userId));
            map.remove(userId);
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】Redis连接异常,用户id:{}", userId, e);
        } catch (Exception e) {
            log.error("【会话管理】移除用户会话异常,用户id:{},{}", userId,e.getMessage(), e);
        }
    }

    /**
     * 根据SocketIO客户端移除对应的用户会话。
     *
     * @param client SocketIO客户端
     */
    public void removeClient(@NotNull SocketIOClient client) {
        String userID = null;
        try {
            userID = client.get(ChatConstants.USER_INFO);
            if (userID == null) return;
            
            // 校验当前Session是否与Redis中一致，防止误删新连接的Session
            ClientSession session = getClientFromCache(userID);
            if (session != null && session.getSessionId().equals(client.getSessionId())) {
                redisTemplate.delete(buildKey(userID));
                log.info("【会话管理】成功清理用户:{}的会话缓存", userID); 
            } else {
                log.info("【会话管理】用户:{}的会话已变更或不存在，跳过Redis清理", userID);
            }
            // 无论如何都要清理本地缓存，确保本地状态最新
            map.remove(userID);
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】Redis连接异常,用户id:{}", userID, e);
        } catch (Exception e) {
            log.error("【会话管理】移除用户会话异常,用户id:{},错误:{}", userID,e.getMessage(), e);
        }
    }

    /**
     * 设置客户端会话信息。
     *
     * @param client SocketIO客户端
     */
    public void setClient(@NotNull SocketIOClient client) {
        String userID = null;
        try {
            userID = client.get(ChatConstants.USER_INFO);
            if (userID == null) {
                client.sendEvent(ChatEvent.UN_LOGIN.name(), ChatConstants.USER_NO_LOGIN);
                client.disconnect();
                return;
            }
            saveClient(userID, client);
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】Redis连接异常,用户id:{}", userID, e);
            client.disconnect();
        } catch (Exception e) {
            log.error("【会话管理】保存用户信息失败", e);
            client.disconnect();
        }
    }

    /**
     * 从缓存中获取客户端会话信息。
     *
     * @param receiveId 接收方ID
     * @return 客户端会话信息，如果不存在则返回null
     */
    private ClientSession getClientFromCache(String receiveId) {
        try {
            // 获取对象
            Object obj = redisTemplate.opsForValue().get(buildKey(receiveId));
            if (obj == null) {
                return null;
            }
            
            // 兼容性处理：如果反序列化结果是 LinkedHashMap（Jackson默认行为），则进行二次转换
            if (obj instanceof java.util.LinkedHashMap) {
                return new ObjectMapper()
                        .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .convertValue(obj, ClientSession.class);
            }
            
            // 正常情况
            return (ClientSession) obj;
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】从缓存获取用户会话失败,用户id: {}", receiveId, e);
            return null;
        } catch (Exception e) {
            log.error("【会话管理】从缓存获取用户会话时发生未知错误,用户id: {}", receiveId, e);
            return null;
        }
    }

    /**
     * 保存客户端会话信息到缓存。
     *
     * @param receiveId 接收方ID
     * @param client    SocketIO客户端
     */
    private void saveClient(String receiveId, SocketIOClient client) {
        try {
            ClientSession clientSession = ClientSession.builder()
                    .userId(receiveId)
                    .sessionId(client.getSessionId())
                    .linkTime(String.valueOf(System.currentTimeMillis()))
                    .clientIP(getClientIP(client))
                    .isDoNotDisturb(false)
                    .build();
            redisTemplate.opsForValue().set(buildKey(receiveId), clientSession, ChatConstants.EXPIRE_TIME, TimeUnit.HOURS);
            map.put(receiveId, clientSession);
        } catch (RedisConnectionException | RedisSystemException e) {
            log.error("【会话管理】Redis连接异常,用户id:{}", receiveId, e);
            client.disconnect();
        } catch (Exception e) {
            log.error("【会话管理】保存用户会话信息到缓存失败", e);
            client.disconnect();
        }

    }

    /**
     * 构建Redis键。
     *
     * @param userID 用户ID
     * @return Redis键
     */
    private String buildKey(String userID) {
        return SESSION_PREFIX + userID;
    }

    /**
     * 获取客户端IP地址。
     *
     * @param client SocketIO客户端
     * @return 客户端IP地址
     */
    private String getClientIP(SocketIOClient client) {
        SocketAddress remoteAddress = client.getRemoteAddress();
        if (remoteAddress != null) {
            String s = remoteAddress.toString();
            if (s.startsWith("/") && s.contains(":")) {
                return s.substring(1, s.indexOf(":"));
            }
            // 兼容特殊格式，返回处理后的字符串
            return s.replace("/", "");
        }
        return "未知";
    }
    
    /**
     * 从本地映射中获取客户端。
     *
     * @param receiveID 接收方ID
     * @return SocketIO客户端，如果不存在或不在线则返回null
     */
    private SocketIOClient getClientFromMap(String receiveID) {
        ClientSession clientSession = map.get(receiveID);
        UUID uuid = clientSession == null ? null : clientSession.getSessionId();
        if (clientSession != null && uuid != null) {
            SocketIOClient client = this.socketIOServer.getClient(clientSession.getSessionId());
            if (client != null && client.isChannelOpen()) return client;
            map.remove(receiveID);
            log.info("【会话管理】用户:{}本地会话已断开,已从会话管理中移除", receiveID);
        }
        // log.info("【会话管理】本地未获取到用户:{}会话,将从缓存获取", receiveID); // 降低日志级别或移除，避免刷屏
        return null;
    }

}
