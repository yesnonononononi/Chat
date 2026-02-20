package com.summit.chat.service.admin;

import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.WorkSpace;

public interface AdminService {
    Result getAllUsers(UserPageQueryDTO userPageQueryDTO, Integer page, Integer pageSize);

    Result blackUser(String userID);

    Result unblackUser(String userID);

    Result setAdmin(String userID);

    WorkSpace getLatestWorkSpaceData();


    Result banGroup(String groupId);

    Result queryGroupList(Integer page, Integer pageSize);

    Result unBanGroup(String groupId);
}
