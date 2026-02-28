package com.summit.chat.Controller.admin;

import com.summit.chat.Annotation.RequireRole;
import com.summit.chat.Dto.admin.UserPageQueryDTO;
import com.summit.chat.Enum.UserRoleEnum;
import com.summit.chat.Result.Result;
import com.summit.chat.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "分页获取所有用户")
    @GetMapping("/users")
    public Result getAllUsers(
            UserPageQueryDTO userDTO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return adminService.getAllUsers(userDTO, page, pageSize);
    }



    @Operation(summary = "禁用用户")
    @PostMapping("/black")
    public Result blackUser(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.blackUser(userID);
        return Result.ok();
    }


    @Operation(summary = "解禁用户")
    @PostMapping("/unblack")
    public Result unblackUser(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.unblackUser(userID);
        return Result.ok();
    }

    @Operation(summary = "设置用户为管理员")
    @PostMapping("/setAdmin")
    @RequireRole(role = UserRoleEnum.SUPER_ADMIN)
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
    public Result queryGroupList(Integer page,Integer pageSize){
        return adminService.queryGroupList(page, pageSize);
    }
    @DeleteMapping("/group/ban")
    @Operation(summary = "封禁群聊")
    public Result banGroup(String groupId) {
        adminService.banGroup(groupId);
        return Result.ok();
    }

    @GetMapping("/group/unban")
    @Operation(summary = "解封群聊")
    public Result unbanGroup(String groupId) {
        return adminService.unBanGroup(groupId);
    }


    @PostMapping("/delAdmin")
    @RequireRole
    @Operation(summary = "删除管理员")
    public Result delAdmin(@RequestBody Map<String, String> requestBody) {
        String userID = requestBody.get("userID");
        adminService.delAdmin(userID);
        return Result.ok();
    }
}
