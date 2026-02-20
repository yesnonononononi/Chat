package com.summit.chat.Mapper;

import com.summit.chat.Dto.GroupApplicationDTO;
import com.summit.chat.model.entity.GroupApplications;
import com.summit.chat.model.vo.GroupApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupApplicationMapper {

    /**
     * 新增群申请
     */
    void insert(GroupApplicationDTO dto);

    /**
     * 更新申请状态
     */
    void updateStatus(@Param("id") Long id, String status, @Param("processedBy") Long processedBy, @Param("rejectionReason") String rejectionReason);

    /**
     * 删除申请
     */
    void delete(@Param("id") Long id);

    /**
     * 根据ID查询
     */
    GroupApplications selectById(@Param("id") Long id);

    /**
     * 根据群ID查询申请列表
     */
    List<GroupApplicationVO> selectByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据用户ID查询申请列表 (用户申请的)
     */
    List<GroupApplicationVO> selectByApplicantId(@Param("applicantId") Long applicantId);

    /**
     * 统计未处理的申请数量 (用于防止重复申请等)
     */
    Integer countPendingApplication(@Param("groupId") Long groupId, @Param("applicantId") Long applicantId);
}
