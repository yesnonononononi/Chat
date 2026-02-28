package com.summit.chat.service.message.GroupMessageResendCenter;

import com.summit.chat.GlobalHandle.SocketHandler.ClientManager;
import com.summit.chat.model.vo.GroupMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GroupMsgProcessor {
    private final GroupMsgValidator validator;
    private final GroupMsgWorker worker;
    private final GroupMsgResendAssessor assessor;
    private final GroupMsgResendSupport groupMsgResendSupport;
    private final ClientManager clientManager;

    public GroupMsgProcessor(GroupMsgResendAssessor assessor, GroupMsgResendSupport groupMsgResendSupport, GroupMsgValidator validator, GroupMsgWorker worker, ClientManager clientManager) {
        this.assessor = assessor;
        this.validator = validator;
        this.groupMsgResendSupport = groupMsgResendSupport;
        this.worker = worker;
        this.clientManager = clientManager;
    }

    public String start(GroupMessageVO vo){
            GroupMsgContext context = GroupMsgContext.builder()
                    .clientManager(clientManager)
                    .msg(vo)
                    .build();
            validator.conduct(context);
            worker.conduct(context);
            groupMsgResendSupport.conduct(context);
            assessor.conduct(context);
            return String.valueOf(vo.getMsgId());
    }
}
