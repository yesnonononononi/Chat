package com.summit.chat.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.summit.chat.Constants.BaseConstants;
import com.summit.chat.Constants.MsgConstants;
import com.summit.chat.Dto.ChatForPrivatePage;
import com.summit.chat.Enum.MsgEnum;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.Mysql.MsgMapper;
import com.summit.chat.Result.PageResult;
import com.summit.chat.Result.Result;
import com.summit.chat.Utils.GlobalIDWorker;
import com.summit.chat.Utils.UserHolder;
import com.summit.chat.model.vo.PrivateMessageVO;
import com.summit.chat.service.Impl.Support.MsgSupport.PrivateMsgSupport;
import com.summit.chat.service.msg.MsgService;
import com.summit.chat.service.validate.MsgValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class MsgImpl implements MsgService {
    @Autowired
    MsgValidator msgValidator;
    @Autowired
    MsgMapper msgMapper;
    @Autowired
    private PrivateMsgSupport privateMsgSupport;

    @Override
    public Result save(PrivateMessageVO dto) {
        try {
            String userID = UserHolder.getUserID();
            //保存消息,首先校验消息发送对象是否存在
            msgValidator.validateSend(dto, userID);
            //生成全局唯一id - 由前端生成，后端不再生成
            // String msgID = GlobalIDWorker.generateId();
            String msgID = dto.getMsgId();
            if (msgID == null || msgID.trim().isEmpty()) {
                // 兼容逻辑：如果前端没传，则后端兜底生成（可选）
                msgID = GlobalIDWorker.generateId();
                dto.setMsgId(msgID);
            }

            //消息时间戳生成 system.currentMills
            Timestamp time = new Timestamp(System.currentTimeMillis());
            //保存
            // dto.setMsgId(msgID);
            dto.setSendTime(time);
            dto.setStatus(MsgEnum.NOT_ONLINE.getStatus());
            //放入数据库
            msgMapper.save(dto);
            return Result.ok(msgID);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}保存消息:{}时发生错误", dto.getEmitterId(), dto.getMsg(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        } finally {
            UserHolder.remove();
        }


    }

    @Override
    public Result withdrawn(PrivateMessageVO dto) {
        try {
            //消息撤回,先校验dto的id是否存在,以及消息的发送时间是否存在,撤回时间是否超过最大限制
            msgValidator.validate(dto);
            //时间检测
            msgValidator.checkMsgTime(dto);
            //撤回
            msgMapper.withdrawn(dto);
            //通知接收用户
            privateMsgSupport.withdrawn(dto);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户{}撤回消息:{}时发生错误", dto.getEmitterId(), dto.getMsg(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        } finally {
            UserHolder.remove();
        }
    }

    @Override
    public Result queryById(String id) {
        //查询消息通过消息id,直接查即可
        try {
            PrivateMessageVO privateMessageVOs = msgMapper.queryById(id);
            return Result.ok(privateMessageVOs);
        } catch (Exception e) {
            log.error("用户: {}查询消息: {}时发生错误", UserHolder.getUserID(), id, e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        }

    }


    @Override
    public Result queryByContent(PrivateMessageVO dto) {
        //查询消息通过内容,需要做分页
        /*PageHelper.startPage()*/
        try {
            String userID = UserHolder.getUserID();
            msgValidator.baseValidate(dto, userID);
            return Result.ok(msgMapper.queryByContent(dto));
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户: {}查询消息: {}时发生错误", dto.getEmitterId(), dto.getMsg(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        } finally {
            UserHolder.remove();
        }
    }

    @Override
    public Result queryHistoryMsg(ChatForPrivatePage dto) {
        PrivateMessageVO dto1 = null;

        try {
            int page, pageSize;
            if (!msgValidator.pageValidate(dto)) {
                page = MsgConstants.DEFAULT_PAGE;
                pageSize = MsgConstants.DEFAULT_PAGESIZE;
            } else {
                page = dto.getPage();
                pageSize = dto.getPageSize();
            }
            dto1 = dto.getDto();
            Page<Object> page0 = PageHelper.startPage(page, pageSize);

            List<PrivateMessageVO> privateMessageVOs = msgMapper.queryHistoryMsg(dto1);


            return Result.ok(new PageResult(page0.getTotal(), privateMessageVOs));

        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("用户: {}查询历史消息: {}时发生错误", dto1.getEmitterId(), dto1.getMsg(), e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        } finally {
            UserHolder.remove();
        }
    }


    @Override
    public Result readMsg(String emitterId) {
        try {
            String userId = UserHolder.getUserID();
            if (userId == null) {
                return Result.fail(BaseConstants.UNCACHE_USERID);
            }
            if (emitterId == null) {
                return Result.fail(BaseConstants.ARGV_ERROR);
            }
            msgMapper.readMsgFromUser(emitterId, userId);
            privateMsgSupport.read(emitterId);

            return Result.ok();
        } catch (Exception e) {
            log.error("用户: {} 标记消息已读失败，发送者: {}", UserHolder.getUserID(), emitterId, e);
            return Result.fail(BaseConstants.SERVER_EXCEPTION);
        } finally {
            UserHolder.remove();
        }
    }


}

