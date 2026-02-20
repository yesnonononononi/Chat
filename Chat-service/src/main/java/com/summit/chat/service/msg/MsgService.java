package com.summit.chat.service.msg;

import com.summit.chat.Dto.ChatForPrivatePage;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.PrivateMessageVO;

public interface MsgService {
    Result save(PrivateMessageVO dto);
    Result withdrawn(PrivateMessageVO dto);
    Result queryById(String id);
    Result queryByContent(PrivateMessageVO dto);
    Result queryHistoryMsg(ChatForPrivatePage dto);
    Result readMsg(String emitterId);
}

