package com.summit.chat.service.Impl;

import com.summit.chat.Constants.FileConstants;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.AliOssUtil;
import com.summit.chat.service.File.FileLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
@Service
@Slf4j
public class FileLoadServiceImpl implements FileLoadService {
   AliOssUtil aliOssUtil;

    public FileLoadServiceImpl(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @Override
    public Result upload(MultipartFile file, String directory) {
        String path;
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取后缀
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = directory + "/" + UUID.randomUUID() + extension;
            try {
                path = aliOssUtil.upload(file.getBytes(), newFileName);
            } catch (IOException e) {
                log.error("【文件服务】文件上传错误: {}", e.getMessage(), e);
                return Result.fail(FileConstants.FILE_UPLOAD_ERROR);
            }
        } else {
            return Result.fail("文件名为空");
        }
        return Result.ok(path);
    }
}
