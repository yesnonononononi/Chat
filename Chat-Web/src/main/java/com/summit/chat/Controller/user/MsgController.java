package com.summit.chat.Controller.user;

import com.summit.chat.Annotation.ShakeProtect;
import com.summit.chat.Dto.ChatForPrivatePage;
import com.summit.chat.Result.Result;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.Impl.MsgImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "消息控制,缓存方案")
@RequestMapping("/msg")
public class MsgController {
    @Autowired
    MsgImpl msgService;

   @Operation(summary = "查询历史聊天信息")
   @PostMapping("/history")
   public Result queryHistoryMsg(@RequestBody @Valid ChatForPrivatePage dto){
       return msgService.queryHistoryMsg(dto);
   }

   @Operation(summary = "根据消息id查询消息")
   @GetMapping("/{id}")
   @Cacheable(cacheNames = "private:msg",key = "T(com.summit.chat.Utils.UserHolder).getUserID()+':'+ #id",unless = "#result.code!=1")
   public Result queryById(@PathVariable @NotBlank(message = "参数不能为空") @Valid String id){
       return msgService.queryById(id);
   }

   /**
    * 模糊查询消息,不查缓存
    * @param dto
    * @return
    */
   @Operation(summary = "根据消息模糊查询")
   @PostMapping
   public Result queryByContent(@RequestBody @Valid PrivateMessageVO dto){
       return msgService.queryByContent(dto);
   }

   @Operation(summary = "保存消息")
   @PostMapping("/save")
   @ShakeProtect("#dto.emitterId + ':' + #dto.receiveId + ':' + #dto.msg")
   public Result saveMsg(@RequestBody @Valid PrivateMessageVO dto){
       return msgService.save(dto);
   }

   @Operation(summary = "撤回消息")
   @PostMapping("/withdrawn")
   @ShakeProtect("#dto.msgId")
   @CacheEvict(cacheNames = "private:msg",key = "T(com.summit.chat.Utils.UserHolder).getUserID()+':'+ #dto.msgId")
   public Result withdrawn(@RequestBody @Valid PrivateMessageVO dto){
       return msgService.withdrawn(dto);
   }

    @Operation(summary = "标记消息为已读")
    @GetMapping("/read")
    public Result readMsg( @NotBlank(message = "发送者ID不能为空") String emitterId) {
        return msgService.readMsg(emitterId);
    }
}

