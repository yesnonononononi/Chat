package com.summit.chat.Controller.admin;

import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.mysql.WorkSpace;
import com.summit.chat.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/statics")
@Tag(name = "工作台接口", description = "工作台接口")
public class WorkSpaceController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "获取最新的工作台统计数据")
    @GetMapping("/workspace/latest")
    public Result<WorkSpace> getLatestWorkSpaceData() {
        WorkSpace data = adminService.getLatestWorkSpaceData();
        return Result.ok(data);
    }


    @Operation(summary = "获取指定时间段的工作台统计数据")
    @PostMapping("/workspace/list")
    public Result<List<WorkSpace>> getWorkSpaceDataByDate(@RequestBody Map<String, List<String>> dateList) {
        List<WorkSpace> data = adminService.getWorkSpaceDataByDate(dateList.get("dateList"));
        return Result.ok(data);
    }


    @Operation(summary = "统计用户活跃度")
    @GetMapping("/user/active")
    public Result getUserActive() {
        return adminService.getUserActive();
    }


    @Operation(summary = "统计用户区间活跃度")
    @PostMapping("/user/active/list")
    public Result getUserActiveByRange(@RequestBody Map<String, List<String>> dateList) {
        return adminService.getUserActiveByRange(dateList.get("dateList"));
    }
}
