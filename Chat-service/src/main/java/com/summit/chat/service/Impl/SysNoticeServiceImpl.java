package com.summit.chat.service.Impl;

import com.summit.chat.Constants.SysNoticeConstants;
import com.summit.chat.Dto.SysNoticeDTO;
import com.summit.chat.Exception.BusinessException;
import com.summit.chat.Mapper.SysNoticeMapper;
import com.summit.chat.Result.Result;
import com.summit.chat.model.entity.SysNotice;
import com.summit.chat.model.vo.SysNoticeVO;
import com.summit.chat.service.Impl.Support.SysNoticeSupport.NoticeSupport;
import com.summit.chat.service.Impl.Support.SysNoticeSupport.SysNoticeValidator;
import com.summit.chat.service.SysNotice.SysNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class SysNoticeServiceImpl implements SysNoticeService {

    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    @Autowired
    private SysNoticeValidator sysNoticeValidator;

    @Autowired
    private NoticeSupport noticeSupport;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result publish(SysNoticeDTO dto) {
        try {
            // 1. 参数校验
            sysNoticeValidator.verifyPublish(dto);

            // 2. 实体转换与属性设置
            SysNotice sysNotice = new SysNotice();
            dto.setCreateTime(new Timestamp(System.currentTimeMillis()));
            BeanUtils.copyProperties(dto, sysNotice, "id");
            sysNotice.setIsDeleted(1L);
            // 3. 执行插入
            sysNoticeMapper.insert(sysNotice);
            dto.setId(sysNotice.getId());
            log.info("【系统公告】发布成功，公告ID: {}, 发布者ID: {}", sysNotice.getId(), dto.getPublisherId());

            //转发系统通知给所有在线用户
            noticeSupport.publishToClient(dto);

            return Result.ok(sysNotice.getId());
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【系统公告】发布失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Result getById(Long id) {
        if (id == null) {
            return Result.fail(SysNoticeConstants.NOTICE_ID_NULL);
        }
        SysNoticeVO vo = sysNoticeMapper.selectById(id);
        return vo != null ? Result.ok(vo) : Result.fail(SysNoticeConstants.NOTICE_NOT_EXIST);
    }

    @Override
    public Result list() {
        List<SysNoticeVO> notices = sysNoticeMapper.selectAll();
        return Result.ok(notices);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(SysNoticeDTO dto) {
        try {
            // 1. 校验
            sysNoticeValidator.verifyUpdate(dto);

            // 2. 执行更新
            SysNotice sysNotice = new SysNotice();
            BeanUtils.copyProperties(dto, sysNotice);
            sysNoticeMapper.update(sysNotice);

            log.info("【系统公告】更新成功，公告ID: {}", dto.getId());
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【系统公告】更新失败", e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(Long id) {
        try {
            // 1. 校验
            sysNoticeValidator.verifyExists(id);

            // 2. 执行逻辑删除
            sysNoticeMapper.deleteById(id);

            log.info("【系统公告】删除成功，公告ID: {}", id);
            return Result.ok();
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("【系统公告】删除失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
