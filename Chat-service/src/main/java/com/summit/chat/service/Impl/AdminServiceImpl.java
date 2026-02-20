package com.summit.chat.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.GroupMapper;
import com.summit.chat.Mapper.WorkSpaceMapper;
import com.summit.chat.Mapper.admin.AdminMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.entity.WorkSpace;
import com.summit.chat.service.Impl.Support.Admin.AdminBusinessSupport;
import com.summit.chat.service.Impl.Support.Admin.AdminValidator;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupServiceCacheSupport;
import com.summit.chat.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private WorkSpaceMapper workSpaceMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupServiceCacheSupport groupServiceCacheSupport;
    @Autowired
    private AdminBusinessSupport adminBusinessSupport;


    @Override
    public Result getAllUsers(UserPageQueryDTO userPageQueryDTO, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<User> userPage = (Page<User>) adminMapper.getAllUsers(userPageQueryDTO);
        // 擦除密码
        userPage.getResult().forEach(user -> user.setPw(""));
        return Result.ok(new PageResult(userPage.getTotal(), userPage.getResult()));
    }

    @Override
    public Result blackUser(String userID) {
        adminMapper.blackUser(userID);
        //清除用户的令牌
        adminBusinessSupport.clearUserToken(userID);
        //强制下线
        adminBusinessSupport.kickUser(userID);
        return Result.ok();
    }

    @Override
    public Result queryGroupList(Integer page, Integer pageSize) {
        try {
            Page<Object> pageHelp = PageHelper.startPage(page, pageSize);
            return Result.ok(new PageResult(pageHelp.getTotal(), groupMapper.queryAllGroup()));
        } catch (BusinessException e) {
            log.error("【群聊】查询群聊列表失败: {}", e.getMessage(), e);
            return Result.fail(e.getMessage());
        }
    }

    @Override
    public Result unBanGroup(String groupId) {
        Integer i = groupMapper.unBanGroup(groupId);
        if (i == 1) {
            // 清除群聊信息缓存，确保解封状态生效
            groupServiceCacheSupport.evictGroupInfoCache(Long.valueOf(groupId));
            return Result.ok();
        } else {
            return Result.fail(GroupConstants.GROUP_NOT_EXIST);
        }
    }


    @Override
    public Result unblackUser(String userID) {
        adminMapper.unblackUser(userID);
        return Result.ok();
    }

    @Override
    public Result setAdmin(String userID) {
        adminMapper.setAdmin(userID);
        return Result.ok();
    }

    @Override
    public WorkSpace getLatestWorkSpaceData() {
        return workSpaceMapper.queryAllData();
    }

    @Override
    public Result banGroup(String groupId) {
        Integer i = groupMapper.banGroup(groupId);
        if (i == 1) {
            // 清除群聊信息缓存，确保封禁状态生效
            groupServiceCacheSupport.evictGroupInfoCache(Long.valueOf(groupId));
            return Result.ok();
        } else {
            return Result.fail(GroupConstants.GROUP_NOT_EXIST);
        }
    }
}
