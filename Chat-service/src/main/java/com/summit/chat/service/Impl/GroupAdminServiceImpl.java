package com.summit.chat.service.Impl;

import com.summit.chat.Dto.PutGroupMemberDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.GroupAdminMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.service.Impl.Support.group.GroupMemberSupport.GroupMemberValidator;
import com.summit.chat.service.group.GroupAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class GroupAdminServiceImpl implements GroupAdminService {
    @Autowired
    private GroupAdminMapper groupAdminMapper;
    @Autowired
    private GroupMemberValidator groupMemberValidator;

    @Override
    public Result putGroupMember(PutGroupMemberDTO dto) {
        try {
            //查看是否是群主或者管理员
            groupMemberValidator.verifyOwnerOrAdmin(groupMemberValidator.getMember(dto.getGroupId(), Long.valueOf(Objects.requireNonNull(UserHolder.getUserID()))));
            groupAdminMapper.putGroupMember(dto);
            return Result.ok();
        }catch (BusinessException e){
            return Result.fail(e.getMessage());
        }catch (Exception e){
            log.error("【群管理】修改群成员状态失败: {}", e.getMessage(), e);
            throw e;
        }
    }


}
