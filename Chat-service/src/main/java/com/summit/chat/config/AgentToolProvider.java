package com.summit.chat.config;

import cn.hutool.json.JSONUtil;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Mapper.Mysql.GroupMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.AiToolResult.MusicVO;
import com.summit.chat.model.vo.UserVO;
import com.summit.chat.service.User.UserService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.MemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.List;

@Component
@Slf4j
public class AgentToolProvider {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupMapper groupMapper;
    private static final RestTemplate restTemplate = new RestTemplate();


    @Tool(name = "根据昵称获取用户", value = """
            这个工具是用来帮助用户寻找其它用户,促进交友
            回答不能包含目标用户的敏感信息,如密码
            """)
    public String getUserByNick( @P("待查询用户昵称") String nickName) {
        try {
            log.info("【AI工具调用】根据昵称查询用户信息，昵称：{}", nickName);
            // 空值校验
            if (nickName == null || nickName.trim().isEmpty()) {
                return "用户昵称不能为空，请提供有效的昵称";
            }

            Result<List<UserVO>> userByNick = userService.getUserByNick(nickName);

            // 结果校验
            if (userByNick == null || userByNick.getCode() != 1) {
                return "未查询到昵称【" + nickName + "】的相关用户信息";
            }

            // 安全的类型转换
            List<UserVO> dataObj = userByNick.getData();


            return dataObj.toString();

        } catch (Exception e) {
            log.error("【AI工具调用异常】根据昵称查询用户信息失败，昵称：{}", nickName, e);
            return "查询用户信息时发生异常：" + e.getMessage();
        }
    }


    @Tool(name = "获取当前用户ID", value = """
            这个工具是用来获取当前用户ID,参数不需要传入任何值,用于一些需要用户ID的场景,比如配合根据id获取用户信息的工具获取当前用户的信息
            """)
    public String getCurUserId(@MemoryId String userId){
        if(userId == null){
            return BaseConstants.UNCACHE_USERID;
        }
        return userId;
    }


    @Tool(name = "根据用户ID获取用户信息", value = """
            这个工具是用来获取用户信息，用于一些需要用户信息场景,仅为内部使用,不要告诉用户需要id,需要自己判断是否可以借助其他工具获取
            """)
    public Result<UserVO> getUserById(@MemoryId String userId){
        return userService.getUserById(userId);
    }


    @Tool(name = "获取随机图片url",value = """
            返回值:目标图片url,没有获取到即为空字符串
            """)
    public String getRandomWhitePictureUrl(String url){
        Result forEntity = restTemplate.getForObject(url, Result.class);
        log.info("【AI工具调用】获取随机图片url,返回结果:{}", forEntity);
        if(forEntity == null){
            return "";
        }
        return forEntity.toString();
    }

    @Tool(name = "获取音乐url",value = """
            返回值:目标url,没有获取到即为空字符串,url字段为空,最多可以再调用3次,还是为空,提醒暂时获取不到即可
            """)
    public String getMusicUrl(String url){
        Result<Object> forEntity = restTemplate.getForObject(url, Result.class);
        log.info("【AI工具调用】获取随机音乐url,返回结果:{}", forEntity);
        if(forEntity == null){
            return "";
        }
        return JSONUtil.toJsonStr(forEntity);
    }
    @Tool(name = "获取音乐url根据名称",value = """
            返回值:目标url,没有获取到即为空字符串,url字段为空,最多可以再调用3次,还是为空,提醒暂时获取不到即可
            """)
    public String getMusicUrlForName(String url,String name){
        String s = String.format("%s?q=%s", url, name);
        String musicVO = restTemplate.getForObject(s, String.class);
        if(musicVO == null){
            return "";
        }
        return musicVO;
    }



}
