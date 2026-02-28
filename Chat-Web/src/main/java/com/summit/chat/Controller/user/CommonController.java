package com.summit.chat.Controller.user;


import com.summit.chat.Annotation.IgnoreSecurity;
import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.MediaTokenDto;
import com.summit.chat.Result.Result;
import com.summit.chat.config.RsaKeyConfig;
import com.summit.chat.service.File.FileLoadService;
import com.summit.chat.service.Impl.MediaTokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping
@Tag(name = "通用控制")

public class CommonController {
    FileLoadService fileLoadService;
    MediaTokenServiceImpl mediaTokenService;
    RsaKeyConfig rsaKeyConfig;

    public CommonController(FileLoadService fileLoadService, MediaTokenServiceImpl mediaTokenService,RsaKeyConfig rsaKeyConfig) {
        this.fileLoadService = fileLoadService;
        this.mediaTokenService = mediaTokenService;
        this.rsaKeyConfig = rsaKeyConfig;
    }

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @ShakeProtect("T(com.summit.chat.Utils.UserHolder).getUserID()")
    public Result upload(MultipartFile file, @RequestParam(defaultValue = "common") String directory) {
        return fileLoadService.upload(file, directory);
    }


    @Operation(summary = "获取rsa公钥")
    @GetMapping("/sign")
    @IgnoreSecurity
    public Result getRsa() {
        return Result.ok(rsaKeyConfig.getPublicKey());
    }


    @PostMapping("/mediaToken")
    @Operation(summary = "获取livekit的token")
    public Result mediaToken( @Valid @RequestBody MediaTokenDto dto) {
        return mediaTokenService.generate(dto);
    }
}

