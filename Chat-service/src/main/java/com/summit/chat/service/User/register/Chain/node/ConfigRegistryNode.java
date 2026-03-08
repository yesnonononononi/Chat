package com.summit.chat.service.User.register.Chain.node;


import cn.hutool.core.bean.BeanUtil;
import com.summit.chat.Constants.AiConstants;
import com.summit.chat.Constants.FileConstants;
import com.summit.chat.Dto.NormalRegisterLoginRequest;
import com.summit.chat.Dto.UserLinkDto;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.model.entity.mysql.User;
import com.summit.chat.service.User.register.Chain.Entity.RegisterChainContext;
import com.summit.chat.service.User.register.Chain.Entity.RegisterHandleChain;
import com.summit.chat.service.UserLink.UserLinkService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigRegistryNode implements RegisterHandleChain {
    RegisterHandleChain next;
    @Autowired
    private UserLinkService userLinkService;


    @Override
    public Result handle(RegisterChainContext chainContext) throws Exception {
        chainContext.validate(next);
        //自动添加ai为好友
        //自动设置用户名
        NormalRegisterLoginRequest request = chainContext.getRequest();

        try {
            String userID = GlobalIDWorker.generateId();
            //构建用户
            User user = buildUser(userID, request);
            //保存ai为好友
            saveLinkForAI(userID);
            chainContext.setUserId(userID);
            chainContext.setUser(user);
            return next.handle(chainContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


        @Override
        public void setNext (RegisterHandleChain next){
            this.next = next;
        }

        private String generateNickName() {
            Faker faker = new Faker();
            //随机中文名
            return faker.funnyName().name();

    }

    private void saveLinkForAI(String userId){
        UserLinkDto userLinkDto = new UserLinkDto();
        userLinkDto.setLinkID(AiConstants.AI_ID);
        userLinkDto.setUserID(userId);
        userLinkService.saveLink(userLinkDto);
    }

    private User buildUser(String userID, NormalRegisterLoginRequest request){
        User user = new User();
        BeanUtil.copyProperties(request, user);
        if (StringUtil.isBlank(user.getNickName())) user.setNickName(generateNickName());
        user.setId(userID);
        user.setIcon(FileConstants.DEFAULT_FILE);
        return user;
    }
}
