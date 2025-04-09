package com.lihua.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lihua.car.dto.CarPostDTO;
import com.lihua.car.entity.CarPost;
import com.lihua.car.entity.CarPostComment;

import java.util.List;

/**
 * <p>
 * 汽车论坛帖子表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarPostService {

    /**
     * 分页查询帖子列表
     *
     * @param dto 帖子查询条件
     * @return 帖子列表
     */
    IPage<CarPost> queryPage(CarPostDTO dto);

    /**
     *
     * 获取最新帖子列表
     *
     * @param limit 限制数量
     * @return 最新帖子列表
     */
    List<CarPost> getLatestPosts(int limit);

    /**
     * 获取热门帖子列表
     *
     * @param limit 限制数量
     * @return 热门帖子列表
     */
    List<CarPost> getHotPosts(int limit);
    
    /**
     * 获取置顶帖子列表
     *
     * @param limit 限制数量
     * @param userId 当前用户ID，可以为null
     * @return 置顶帖子列表
     */
    List<CarPost> getPinnedPosts(int limit, Long userId);

    /**
     * 根据ID查询帖子
     *
     * @param id 帖子ID
     * @param userId 当前用户ID，可以为null
     * @return 帖子信息
     */
    CarPost getPostById(Long id, Long userId);

    /**
     * 搜索帖子
     *
     * @param keyword 关键词
     * @param userId 当前用户ID，可以为null
     * @return 帖子列表
     */
    List<CarPost> searchPosts(String keyword, Long userId);

    /**
     * 新增帖子
     *
     * @param post 帖子信息
     * @return 结果
     */
    int insertPost(CarPost post);

    /**
     * 修改帖子
     *
     * @param post 帖子信息
     * @return 结果
     */
    int updatePost(CarPost post);

    /**
     * 删除帖子
     *
     * @param id 帖子ID
     * @return 结果
     */
    int deletePost(Long id);
    
    /**
     * 置顶/取消置顶帖子
     *
     * @param id 帖子ID
     * @return 结果
     */
    int toggleTop(Long id);
    
    /**
     * 点赞/取消点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    int likePost(Long postId, Long userId);
    
    /**
     * 发表评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    int addComment(CarPostComment comment);
    
    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    int deleteComment(Long commentId, Long userId);

}