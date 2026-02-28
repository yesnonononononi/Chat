package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.GroupNotice;
import com.summit.chat.model.vo.GroupNoticeVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupNoticeMapper {

    /**
     * 新增群公告
     */
    @AutoFill(type = OperationType.INSERT)
    void insert(GroupNotice groupNotice);

    /**
     * 根据ID查询群公告
     */
    @Select("SELECT gn.*, u.nick_name as publisherName, u.icon as publisherAvatar " +
            "FROM group_notice gn " +
            "LEFT JOIN user u ON gn.publisher_id = u.id " +
            "WHERE gn.id = #{id} AND gn.is_deleted = 0")
    GroupNoticeVO selectById(@Param("id") Long id);

    /**
     * 根据群ID查询群公告列表
     */
    @Select("SELECT gn.*, u.nick_name as publisherName, u.icon as publisherAvatar " +
            "FROM group_notice gn " +
            "LEFT JOIN user u ON gn.publisher_id = u.id " +
            "WHERE gn.group_id = #{groupId} AND gn.is_deleted = 0 " +
            "ORDER BY gn.create_time DESC")
    List<GroupNoticeVO> selectByGroupId(@Param("groupId") Long groupId);

    /**
     * 更新群公告
     */
    @AutoFill(type = OperationType.UPDATE)
    void update(GroupNotice groupNotice);

    /**
     * 删除群公告（逻辑删除）
     */
    @Delete("UPDATE group_notice SET is_deleted = 1 WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    /**
     * 批量删除群公告（逻辑删除）
     */
    void batchDelete(@Param("ids") List<Long> ids,Long groupId);

    /**
     * 统计群组公告数量
     */
    @Select("SELECT COUNT(*) FROM group_notice WHERE group_id = #{groupId} AND is_deleted = 0")
    Integer countByGroupId(@Param("groupId") Long groupId);
}