package com.summit.chat.Controller.user;


import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Constants.UserConstants;
import com.summit.chat.Dto.*;
import com.summit.chat.Result.Result;
import com.summit.chat.service.User.Login.LoginService;
import com.summit.chat.service.User.UserService;
import com.summit.chat.service.User.register.RegisterService;
import dev.langchain4j.agent.tool.Tool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "用户认证接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    LoginService loginService;
    @Autowired
    RegisterService registerService;


    /**
     * 手机号+密码
     */
    @Operation(summary = "根据手机号加密码登录")
    @PostMapping("/password-login")
    public Result login(@Validated @RequestBody NormalLoginRequest request) throws Exception {
        return loginService.loginByPw(request);
    }

    /**
     * 注册
     *
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@Validated @RequestBody NormalRegisterLoginRequest request) throws Exception {
        return registerService.execute(request);
    }

    /**
     * 发送验证码
     */
    @Operation(summary = "发送验证码")
    @GetMapping("/send-sms-code")
    public Result login(@NotBlank(message = "手机号为空") String phoneNumber) {
        return loginService.sendCode(phoneNumber);
    }

    /**
     * 手机号+验证码
     */
    @Operation(summary = "验证验证码并登录or注册")
    @PostMapping("/sms-login")
    public Result login(@Validated @RequestBody VerifyCodeLoginRequest request) throws Exception {
        return loginService.loginByCode(request);
    }

    @Operation(summary = "登出功能")
    @GetMapping("/loginout")
    @ShakeProtect("#token")
    public Result loginOut(String token) {
        return loginService.loginOut(token);
    }

    @Operation(summary = "根据昵称获取用户")

    @GetMapping("/nickName")
    public Result getUserByNick(String nickName) {
        return userService.getUserByNick(nickName);
    }

    @Operation(summary = "根据手机号获取用户信息")
    @GetMapping
    @Tool("根据手机号获取单个用户的详情")
    public Result getUserByPhone(String phoneNumber) {
        return userService.getUserByPhone(phoneNumber);
    }

    @Operation(summary = "删除用户")
    @PutMapping("/del/{userID}")
    @ShakeProtect("#userID")
    @CacheEvict(cacheNames = UserConstants.CACHE_USER_PROFILE_HASH, key = "#userID")
    public Result logOff(@PathVariable String userID) {
        return userService.delUser(userID);
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/edit")
    @ShakeProtect("#dto.id")
    @CacheEvict(cacheNames = UserConstants.CACHE_USER_PROFILE_HASH, key = "#dto.id")
    public Result putUser(@RequestBody UserDTO dto) {
        return userService.putUser(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据用户id获取信息")
    @Cacheable(cacheNames = UserConstants.CACHE_USER_PROFILE_HASH, key = "#id", unless = "#result.code!=1")
    public Result getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }


    @Operation(summary = "更换用户头像")
    @PostMapping("/avatar")
    @CachePut(cacheNames = UserConstants.CACHE_USER_PROFILE_HASH, key = "T(com.summit.chat.Utils.UserHolder).getUserID()")
    public Result changeAvatar(MultipartFile file) {
        return userService.putUserIcon(file);
    }


    @Operation(summary = "修改密码")
    @PostMapping("/pw")
    @ShakeProtect("#dto.id")
    public Result putPw(@RequestBody UserPwPutDto dto) {
        return userService.putPw(dto);
    }


    @Operation(summary = "忘记密码,找回")
    @PostMapping("/forget")
    @ShakeProtect("#dto.id")
    public Result forget(@RequestBody UserPwPutDto dto) {
        return userService.forgetPw(dto);
    }


}

