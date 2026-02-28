package com.summit.chat.service.Impl;

import com.corundumstudio.socketio.SocketIOClient;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.Mapper.GroupMapper;
import com.summit.chat.Mapper.WorkSpaceMapper;
import com.summit.chat.Mapper.admin.AdminMapper;
import com.summit.chat.Mapper.admin.UserActiveMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.entity.User;
import com.summit.chat.model.entity.WorkSpace;
import com.summit.chat.model.vo.UserActiveVO;
import com.summit.chat.service.Impl.Support.Admin.AdminBusinessSupport;
import com.summit.chat.service.Impl.Support.Admin.AdminUserCacheSupport;
import com.summit.chat.service.Impl.Support.Admin.AdminValidator;
import com.summit.chat.service.Impl.Support.group.GroupSupport.GroupServiceCacheSupport;
import com.summit.chat.service.admin.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

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
    @Autowired
    private AdminValidator adminValidator;


    @Autowired
    private AdminUserCacheSupport adminUserCacheSupport;
    @Autowired
    private ClientManager clientManager;
    @Autowired
    private UserActiveMapper userActiveMapper;

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
        //封禁对象不能是超管和其他管理员
        if (adminValidator.isSuperAdminOrAdmin(userID)) {
            return Result.fail(UserConstants.ILLEGAL_OPERATE);
        }
        adminMapper.blackUser(userID);
        //清除用户的令牌
        adminBusinessSupport.clearUserToken(userID);
        // 清除用户资料缓存
        adminUserCacheSupport.evictUserProfileCache(userID);
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
        try {
            if (userID == null) return Result.fail(UserConstants.ILLEGAL_CHAR);
            //如果解禁对象是管理员,则需要超管权限
            boolean admin = adminValidator.isAdmin(userID);
            if (!admin || adminValidator.isSuperAdmin(UserHolder.getUserID())) {
                adminMapper.unblackUser(userID);
                // 清除用户资料缓存
                adminUserCacheSupport.evictUserProfileCache(userID);
            } else {
                return Result.fail(UserConstants.ILLEGAL_OPERATE);
            }
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【管理员】解封用户失败: {}", e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Result setAdmin(String userID) {
        try {

            Integer i = adminMapper.setAdmin(userID);
            if (i == 0) {
                return Result.fail(UserConstants.SET_NOT_SUPER_ADMIN);  //1不存在 2已经是管理员
            }
            // 清除用户资料缓存
            adminUserCacheSupport.evictUserProfileCache(userID);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【管理员】设置管理员失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public WorkSpace getLatestWorkSpaceData() {
        WorkSpace workSpace = workSpaceMapper.queryAllData();
        Collection<SocketIOClient> allClient = clientManager.getAllClient();
        workSpace.setOnlineUser(allClient.size());
        return workSpace;
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

    @Override
    public Result delAdmin(String userID) {
        try {
            boolean superAdmin = adminValidator.isSuperAdmin(UserHolder.getUserID());
            if (!superAdmin) return Result.fail(UserConstants.ILLEGAL_OPERATE);
            Integer i = adminMapper.delAdmin(userID);
            if (i == 0) {
                return Result.fail(UserConstants.NOT_ADMIN);  //1不存在 2不是管理员
            }
            // 清除用户资料缓存
            adminUserCacheSupport.evictUserProfileCache(userID);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【管理员】删除管理员失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<WorkSpace> getWorkSpaceDataByDate(List<String> dateList) {
        List<Date> list = dateList
                .stream()
                .map(Date::valueOf)
                .toList();
        List<WorkSpace> workSpaces = workSpaceMapper.queryAllDataByDate(list);

        return workSpaces;
    }

    @Override
    public Result getUserActive() {
        try {
            List<UserActiveVO> list = adminUserCacheSupport.getUserActive();
            return Result.ok(list);
        } catch (Exception e) {
            log.error("【管理员】获取用户活跃数据失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result getUserActiveByRange(List<String> dateList) {
        try{
            List<Date> dates  = dateList.stream().map(Date::valueOf).toList();
            return Result.ok(userActiveMapper.queryUserActiveByRange(dates));
        }catch (ClassCastException e){
            return Result.fail(UserConstants.ILLEGAL_CHAR);
        } catch (Exception e) {
            log.error("【管理员】获取用户活跃数据失败: {}", e.getMessage(), e);
            throw e;
        }

    }
}
