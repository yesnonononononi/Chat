package com.summit.chat.Controller.admin;

import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.WorkSpace;
import com.summit.chat.service.User.UserService;
import com.summit.chat.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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




}
