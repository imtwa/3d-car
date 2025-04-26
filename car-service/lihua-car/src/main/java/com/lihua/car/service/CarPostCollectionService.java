package com.lihua.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lihua.car.entity.CarPostCollection;

/**
 * <p>
 * 帖子收藏表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2024-01-10
 */
public interface CarPostCollectionService {

    /**
     * 获取用户收藏的帖子列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 收藏帖子列表
     */
    IPage<CarPostCollection> getUserCollectedPosts(Long userId, int pageNum, int pageSize);

    /**
     * 收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    boolean collectPost(Long postId, Long userId);

    /**
     * 取消收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    boolean uncollectPost(Long postId, Long userId);

    /**
     * 检查用户是否已收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean checkUserCollected(Long postId, Long userId);
} 