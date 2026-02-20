package com.summit.chat.service.UserLink;

import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Result.Result;

public interface UserLinkService {
    Result getUserLinkById();

    Result saveLink(UserLinkDto dto);


    Result delLink(UserLinkDto dto);
}
