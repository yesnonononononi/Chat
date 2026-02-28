package com.summit.chat.service.group;

import com.summit.chat.Dto.PutGroupMemberDTO;
import com.summit.chat.Result.Result;

public interface GroupAdminService {
    Result putGroupMember(PutGroupMemberDTO dto);

}
