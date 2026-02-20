package com.summit.chat.service.group;

import com.summit.chat.Dto.GroupMemberDTO;
import com.summit.chat.Result.Result;
import java.util.List;

public interface GroupMemberService {
    Result addMember(GroupMemberDTO dto);
    Result delMember(GroupMemberDTO dto);
    Result queryMemberByGroupId(Long groupId,Integer page,Integer pageSize);

}
