package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarPostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 帖子点赞表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarPostLikeMapper extends BaseMapper<CarPostLike> {

    /**
     * 检查用户是否已点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录数
     */
    @Select("SELECT COUNT(*) FROM car_post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int checkUserLiked(@Param("postId") Long postId, @Param("userId") Long userId);
} 