package com.summit.chat.service.media;

import com.summit.chat.Dto.MediaApplyDTO;
import com.summit.chat.Result.Result;
import org.springframework.stereotype.Service;

@Service
public interface MediaService {
    public Result send(MediaApplyDTO dto);
    public Result accept(String receiverId );
    public Result reject(String receiverId );
    public Result cancel(String receiverId);
}
