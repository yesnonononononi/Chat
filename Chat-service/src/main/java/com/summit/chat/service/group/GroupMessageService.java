package com.summit.chat.service.group;

import com.summit.chat.Dto.GroupMessageDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.GroupMessageVO;

public interface GroupMessageService {
    Result queryGroupMsgById(Long groupId,Integer page,Integer pageSize);
    Result queryGroupMsgByUserId(GroupMessageDTO dto, Integer page, Integer pageSize);
    Result withdrawn(GroupMessageVO vo);
    Result addGroupMsg(GroupMessageVO vo);
}
