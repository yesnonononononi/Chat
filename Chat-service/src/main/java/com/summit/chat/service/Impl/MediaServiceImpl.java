package com.summit.chat.service.Impl;

import cn.hutool.core.lang.UUID;
import com.corundumstudio.socketio.SocketIOClient;
import com.summit.chat.Constants.MediaConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.MediaApplyDTO;
import com.summit.chat.Enum.MediaState;
import com.summit.chat.Enum.UserCallStateEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.MediaMapper;
import com.summit.chat.Mapper.Mysql.UserMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.mysql.Media;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.service.Impl.Support.MediaSupport.MediaSocketSupport;
import com.summit.chat.service.Impl.Support.MediaSupport.MediaValidator;
import com.summit.chat.service.Lock.LockService;
import com.summit.chat.service.media.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static com.summit.chat.Constants.UserConstants.LOCK_USER_ACTIVE_CLEAR_VAL;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {
    @Autowired
    private MediaValidator mediaValidator;
    @Autowired
    private MediaSocketSupport mediaSocketSupport;
    @Autowired
    private LockService lockService;
    @Autowired
    private MediaMapper mediaMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result send(MediaApplyDTO dto) {
        String userID = null;
        String receiverId = dto.getReceiverId();
        try {
            userID = UserHolder.getUserID();
            //查看是否是好友关系
            mediaValidator.isFriend(userID, receiverId);
            //查询好友状态
            mediaValidator.validateState(receiverId);
            //推送给好友申请通知
            SocketIOClient client = mediaSocketSupport.getClient(receiverId);
            
            // 获取当前用户信息并填充到DTO
            User user = userMapper.getUserById(Long.valueOf(userID));
            if (user != null) {
                dto.setUserId(userID);
                dto.setNickName(user.getNickName());
                dto.setIcon(user.getIcon());
            }

            if (client != null) {
                mediaSocketSupport.send(client, dto);
            }

            Media media = new Media();
            media.setEmitterId(userID);
            media.setReceiveId(receiverId);
            media.setStatus(MediaState.WAIT.getState());
            media.setCreateTime(new Timestamp(System.currentTimeMillis()));
            mediaMapper.insert(media);
            if (client == null) return Result.fail(UserConstants.USER_OFFLINE);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【视频聊天】发送申请失败,接收者id:{},申请者id:{}, 错误:{}", receiverId, userID, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result accept(String emitterId) {
        String userID = null;
        RLock rlock = null;
        try {
            userID = UserHolder.getUserID();
            //查看是否有这条申请记录
            mediaValidator.isExistApply(emitterId, userID);
            //获取申请者客户端
            SocketIOClient client = mediaSocketSupport.getClient(emitterId);
            //推给申请者
            if (client != null) {
                mediaSocketSupport.call(client, userID);
            }
            rlock = lockService.tryLock(MediaConstants.MEDIA_VIDEO_PREFIX, emitterId, MediaConstants.LEASE_TIME, MediaConstants.WAIT_TIME, TimeUnit.MILLISECONDS);
            if (rlock != null) {
                mediaMapper.accept(emitterId, userID);
                userMapper.putUserState(UserCallStateEnum.CALLING.getState(), userID);
            }
            return Result.ok();  //返回房间号
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【视频聊天】接受申请失败,接收者id:{},申请者id:{}, 错误:{}", userID, emitterId, e.getMessage(), e);
            throw e;
        } finally {
            if (rlock != null) lockService.unLock(rlock);
        }
    }

    @Override
    public Result reject(String emitterId) {
        String userID = null;
        try {
            userID = UserHolder.getUserID();
            //查看是否有这条申请记录
            mediaValidator.isExistApply(emitterId, userID);

            // 更新状态为拒绝
            mediaMapper.endMedia(emitterId, userID);
            userMapper.putUserState(UserCallStateEnum.IDLE.getState(), userID);
            userMapper.putUserState(UserCallStateEnum.IDLE.getState(), emitterId);
            // 通知申请者
            SocketIOClient client = mediaSocketSupport.getClient(emitterId);
            if (client != null) {
                mediaSocketSupport.reject(client, userID);
            }
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【视频聊天】拒绝申请失败,接收者id:{},申请者id:{}, 错误:{}", userID, emitterId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancel(String receiverId) {
        String userID = null;
        try {
            userID = UserHolder.getUserID();
            // 更新数据库状态为取消
            mediaMapper.putMedia(MediaState.CANCEL, userID, receiverId);
            userMapper.putUserState(UserCallStateEnum.IDLE.getState(), userID);
            userMapper.putUserState(UserCallStateEnum.IDLE.getState(), receiverId);
            // 获取接收者客户端
            SocketIOClient client = mediaSocketSupport.getClient(receiverId);
            // 推送取消通知给接收者
            if (client != null) {
                mediaSocketSupport.cancel(client, userID);
            }
            return Result.ok();
        } catch (Exception e) {
            log.error("【视频聊天】取消申请失败,接收者id:{},申请者id:{}, 错误:{}", receiverId, userID, e.getMessage(), e);
            throw e;
        }
    }
}
