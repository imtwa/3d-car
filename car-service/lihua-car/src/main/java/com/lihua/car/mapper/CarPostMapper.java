package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 汽车论坛帖子表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarPostMapper extends BaseMapper<CarPost> {

    /**
     * 获取热门帖子列表
     *
     * @param limit 限制数量
     * @return 热门帖子列表
     */
    List<CarPost> selectHotPosts(@Param("limit") int limit);

    /**
     * 搜索帖子
     *
     * @param keyword 关键词
     * @return 帖子列表
     */
    List<CarPost> searchPosts(@Param("keyword") String keyword);
    
    /**
     * 增加浏览次数
     *
     * @param id 帖子ID
     * @return 结果
     */
    @Update("UPDATE car_post SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    /**
     * 更新评论数
     *
     * @param id 帖子ID
     * @param count 评论数量，可以为负数
     * @return 结果
     */
    @Update("UPDATE car_post SET comment_count = comment_count + #{count} WHERE id = #{id}")
    int updateCommentCount(@Param("id") Long id, @Param("count") int count);
    
    /**
     * 更新点赞数
     *
     * @param id 帖子ID
     * @param count 点赞数量，可以为负数
     * @return 结果
     */
    @Update("UPDATE car_post SET like_count = like_count + #{count} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("count") int count);
    
    /**
     * 置顶/取消置顶帖子
     *
     * @param id 帖子ID
     * @param isTop 是否置顶（0否 1是）
     * @return 结果
     */
    @Update("UPDATE car_post SET is_top = #{isTop} WHERE id = #{id}")
    int updateTopStatus(@Param("id") Long id, @Param("isTop") int isTop);
} 