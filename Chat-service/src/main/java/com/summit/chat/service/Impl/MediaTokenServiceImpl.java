package com.summit.chat.service.Impl;

import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Dto.MediaTokenDto;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Result.Result;
import io.livekit.server.*;
import livekit.LivekitRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Slf4j
@Service
public class MediaTokenServiceImpl {
    @Value("${livekit.secret}")
    private String SECRET;
    @Value("${livekit.apiKey}")
    private String APIKEY;

    public Result<String> generate(MediaTokenDto dto){
        log.info("secret:{}",SECRET);
        log.info("apiKey:{}",APIKEY);
        String userId = dto.getUserId();
        String userName = dto.getUserName();
        String roomName = dto.getRoomName();
        try {
            validate(userId, userName);
            AccessToken accessToken = new AccessToken(APIKEY, SECRET);
            setInfo(accessToken, userId, userName);
            setRoom(roomName, accessToken);
            return Result.ok(accessToken.toJwt());
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }
    }



    private void setInfo(AccessToken accessToken, String userId,String userName){
        //设置用户信息
        accessToken.setIdentity(userId);  //唯一标识

        accessToken.setName(userName);

        accessToken.setTtl(Duration.ofHours(3).toHours());
    }

    private void setRoom(String roomName,AccessToken accessToken){
      accessToken.addGrants(
              new RoomJoin(true),
              new RoomAdmin(true),
              new RoomName(roomName)
      );
    }
    private void validate(String userId,String userName){
        if(userId == null || userName == null){
            throw new BusinessException("参数不能为空");
        }
    }


}
