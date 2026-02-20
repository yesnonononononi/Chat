package com.summit.chat.Controller.admin;

import com.summit.chat.Annotation.RequireRole;
import com.summit.chat.Constants.GroupConstants;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Controller.user.UserController;
import com.summit.chat.Dto.UserDTO;
import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.Result.Result;
import java.util.Map;

import com.summit.chat.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "分页获取所有用户")
    @GetMapping("/users")
    @RequireRole
    public Result getAllUsers(
            UserPageQueryDTO userDTO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminService.getAllUsers(userDTO, page, pageSize);
    }



    @Operation(summary = "禁用用户")
    @PostMapping("/black")
    @CacheEvict(cacheNames = UserConstants.CACHE_USER_PROFILE_HASH,key = "#requestBody['userID']")
    @RequireRole
    public Result blackUser(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.blackUser(userID);
        return Result.ok();
    }


    @Operation(summary = "解禁用户")
    @PostMapping("/unblack")
    @CacheEvict(value = UserConstants.CACHE_USER_PROFILE_HASH,key = "#requestBody['userID']")
    @RequireRole
    public Result unblackUser(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.unblackUser(userID);
        return Result.ok();
    }

    @Operation(summary = "设置用户为管理员")
    @PostMapping("/setAdmin")
    @RequireRole
    @CacheEvict(value = UserConstants.CACHE_USER_PROFILE_HASH,key = "#requestBody['userID']")
    public Result setAdmin(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.setAdmin(userID);
        return Result.ok();
    }

    @GetMapping("/auth")
    public Result auth(){
        return Result.ok(true);
    }

    @Operation(summary = "查询群聊列表")
    @GetMapping("/group/list")
    @RequireRole
    public Result queryGroupList(Integer page,Integer pageSize){
        return adminService.queryGroupList(page, pageSize);
    }
    @DeleteMapping("/group/ban")
    @Operation(summary = "封禁群聊")
    @RequireRole
    public Result banGroup(String groupId) {
        adminService.banGroup(groupId);
        return Result.ok();
    }

    @GetMapping("/group/unban")
    @Operation(summary = "解封群聊")
    @RequireRole
    public Result unbanGroup(String groupId) {
        return adminService.unBanGroup(groupId);
    }
}
