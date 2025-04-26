package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.entity.CarPost;
import com.lihua.car.entity.CarPostCollection;
import com.lihua.car.entity.CarUser;
import com.lihua.car.mapper.CarPostCollectionMapper;
import com.lihua.car.mapper.CarPostMapper;
import com.lihua.car.mapper.CarUserMapper;
import com.lihua.car.service.CarPostCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 帖子收藏表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2024-01-10
 */
@Slf4j
@Service
public class CarPostCollectionServiceImpl extends ServiceImpl<CarPostCollectionMapper, CarPostCollection> implements CarPostCollectionService {

    @Autowired
    private CarPostMapper postMapper;

    @Autowired
    private CarUserMapper userMapper;

    /**
     * 获取用户收藏的帖子列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 收藏帖子列表
     */
    @Override
    public IPage<CarPostCollection> getUserCollectedPosts(Long userId, int pageNum, int pageSize) {
        IPage<CarPostCollection> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加用户ID查询条件
        queryWrapper.eq(CarPostCollection::getUserId, userId);
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarPostCollection::getCreateTime);
        
        // 查询收藏列表
        IPage<CarPostCollection> resultPage = this.page(page, queryWrapper);
        
        // 关联帖子详情
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            for (CarPostCollection collection : resultPage.getRecords()) {
                // 查询帖子信息
                CarPost post = postMapper.selectById(collection.getPostId());
                if (post != null && "0".equals(post.getDelFlag()) && "0".equals(post.getStatus())) {
                    // 填充用户信息
                    if (post.getUserId() != null) {
                        CarUser user = userMapper.selectById(post.getUserId());
                        if (user != null) {
                            post.setUser(user);
                        }
                    }
                    collection.setPost(post);
                }
            }
        }
        
        return resultPage;
    }

    /**
     * 收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean collectPost(Long postId, Long userId) {
        // 检查帖子是否存在
        CarPost post = postMapper.selectById(postId);
        if (post == null || "1".equals(post.getDelFlag()) || "1".equals(post.getStatus())) {
            return false;
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPostCollection::getPostId, postId)
                .eq(CarPostCollection::getUserId, userId);
        long count = this.count(queryWrapper);
        
        if (count > 0) {
            return true; // 已收藏，直接返回成功
        }
        
        // 创建收藏记录
        CarPostCollection collection = new CarPostCollection();
        collection.setPostId(postId);
        collection.setUserId(userId);
        collection.setCreateTime(LocalDateTime.now());
        
        return this.save(collection);
    }

    /**
     * 取消收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean uncollectPost(Long postId, Long userId) {
        LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPostCollection::getPostId, postId)
                .eq(CarPostCollection::getUserId, userId);
        
        return this.remove(queryWrapper);
    }

    /**
     * 检查用户是否已收藏帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    @Override
    public boolean checkUserCollected(Long postId, Long userId) {
        LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPostCollection::getPostId, postId)
                .eq(CarPostCollection::getUserId, userId);
        
        return this.count(queryWrapper) > 0;
    }
} 