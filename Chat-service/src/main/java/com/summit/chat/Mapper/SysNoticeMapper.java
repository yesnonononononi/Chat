package com.summit.chat.Mapper;

import com.summit.chat.Annotation.AutoFill;
import com.summit.chat.Enum.OperationType;
import com.summit.chat.model.entity.SysNotice;
import com.summit.chat.model.vo.NoticeLikeVO;
import com.summit.chat.model.vo.SysNoticeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysNoticeMapper {

    /**
     * 新增系统公告
     */

    void insert(SysNotice sysNotice);

    /**
     * 根据ID查询系统公告
     */
    @Select("SELECT sn.id, msg, sn.create_time, end_time, version, is_deleted, publisher_id, `like`, u.nick_name as publisherName FROM sys_notice sn " +
            "LEFT JOIN user u ON sn.publisher_id = u.id " +
            "WHERE sn.id = #{id} AND sn.is_deleted = 1")
    SysNoticeVO selectById(@Param("id") Long id);

    /**
     * 查询所有有效的系统公告
     */
    @Select("SELECT sn.id, msg, sn.create_time, end_time, version, is_deleted, publisher_id, `like`, u.nick_name as publisherName FROM sys_notice sn " +
            "LEFT JOIN user u ON sn.publisher_id = u.id " +
            "WHERE sn.is_deleted = 1 ORDER BY sn.create_time DESC")
    List<SysNoticeVO> selectAll();

    /**
     * 更新系统公告
     */
    @AutoFill(type = OperationType.UPDATE)
    void update(SysNotice sysNotice);

    /**
     * 逻辑删除系统公告
     */
    @Update("UPDATE sys_notice SET is_deleted = 0 WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    /**
     * 更新点赞数
     */
    @Update("UPDATE sys_notice SET `like` = #{like} WHERE id = #{id}")
   Integer updateLike(@Param("id") Long id, @Param("like") Long like);



    List<NoticeLikeVO> queryLikeList(List<String> list);
}
