package com.summit.chat.service.File;

import com.summit.chat.Result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface FileLoadService {
    Result upload(MultipartFile file, String directory);
}
