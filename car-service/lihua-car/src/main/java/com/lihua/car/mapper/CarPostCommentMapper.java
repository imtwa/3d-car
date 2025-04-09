package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarPostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 帖子评论表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarPostCommentMapper extends BaseMapper<CarPostComment> {

    /**
     * 获取帖子的评论列表（一级评论）
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    @Select("SELECT * FROM car_post_comment WHERE post_id = #{postId} AND (parent_id IS NULL OR parent_id = 0) AND del_flag = '0' AND status = '0' ORDER BY create_time DESC")
    List<CarPostComment> selectRootCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * 获取评论的回复列表
     *
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @Select("SELECT * FROM car_post_comment WHERE parent_id = #{parentId} AND del_flag = '0' AND status = '0' ORDER BY create_time ASC")
    List<CarPostComment> selectCommentReplies(@Param("parentId") Long parentId);
    
    /**
     * 更新评论点赞数
     *
     * @param id 评论ID
     * @param count 点赞数量，可以为负数
     * @return 结果
     */
    @Update("UPDATE car_post_comment SET like_count = like_count + #{count} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("count") int count);
    
    /**
     * 获取帖子的所有评论数量
     *
     * @param postId 帖子ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM car_post_comment WHERE post_id = #{postId} AND del_flag = '0' AND status = '0'")
    int countCommentsByPostId(@Param("postId") Long postId);
} 