package com.summit.chat.service.Friend;

import com.summit.chat.Dto.FriendDto;
import com.summit.chat.Result.Result;

public interface FriendApply {
    Result sendApplication(FriendDto dto);

    Result ackApplication(FriendDto dto);
    Result rejectApplication(FriendDto dto);

    Result queryApplication(Integer page,Integer pageSize);
}
