package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarCommentDTO;
import com.lihua.car.entity.CarComment;
import com.lihua.car.entity.CarPost;
import com.lihua.car.entity.CarUser;
import com.lihua.car.mapper.CarCommentMapper;
import com.lihua.car.mapper.CarPostMapper;
import com.lihua.car.mapper.CarUserMapper;
import com.lihua.car.service.CarCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 帖子评论表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
@Slf4j
@Service
public class CarCommentServiceImpl extends ServiceImpl<CarCommentMapper, CarComment> implements CarCommentService {

    @Autowired
    private CarCommentMapper commentMapper;

    @Autowired
    private CarPostMapper postMapper;
    
    @Autowired
    private CarUserMapper userMapper;

    /**
     * 查询评论列表
     *
     * @param dto 评论查询条件
     * @return 评论列表
     */
    @Override
    public IPage<CarComment> queryPage(CarCommentDTO dto) {
        IPage<CarComment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<CarComment> queryWrapper = new LambdaQueryWrapper<>();

        // 添加帖子ID查询条件
        if (dto.getPostId() != null) {
            queryWrapper.eq(CarComment::getPostId, dto.getPostId());
        }

        // 添加用户ID查询条件
        if (dto.getUserId() != null) {
            queryWrapper.eq(CarComment::getUserId, dto.getUserId());
        }
        
        // 添加评论内容查询条件
        if (StringUtils.hasText(dto.getContent())) {
            queryWrapper.like(CarComment::getContent, dto.getContent());
        }
        
        // 添加创建时间范围查询条件
        if (StringUtils.hasText(dto.getCreateTimeStart()) && StringUtils.hasText(dto.getCreateTimeEnd())) {
            queryWrapper.between(CarComment::getCreateTime, dto.getCreateTimeStart(), dto.getCreateTimeEnd());
        }

        // 添加状态查询条件
        if (StringUtils.hasText(dto.getStatus())) {
            queryWrapper.eq(CarComment::getStatus, dto.getStatus());
        }

        // 添加删除标志条件，只查询未删除的记录
        queryWrapper.eq(CarComment::getDelFlag, "0");

        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarComment::getCreateTime);

        // 查询评论列表
        IPage<CarComment> resultPage = this.page(page, queryWrapper);
        
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            // 收集所有用户ID和帖子ID
            List<Long> userIds = resultPage.getRecords().stream()
                    .map(CarComment::getUserId)
                    .collect(Collectors.toList());
            
            List<Long> postIds = resultPage.getRecords().stream()
                    .map(CarComment::getPostId)
                    .collect(Collectors.toList());
            
            List<Long> parentUserIds = resultPage.getRecords().stream()
                    .filter(comment -> comment.getParentId() != null && comment.getParentId() > 0)
                    .map(comment -> this.getById(comment.getParentId()).getUserId())
                    .collect(Collectors.toList());
            
            userIds.addAll(parentUserIds);
            
            // 批量查询用户信息
            List<CarUser> users = null;
            if (!userIds.isEmpty()) {
                LambdaQueryWrapper<CarUser> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.in(CarUser::getId, userIds);
                users = userMapper.selectList(userWrapper);
            }
            
            // 批量查询帖子信息
            List<CarPost> posts = null;
            if (!postIds.isEmpty()) {
                LambdaQueryWrapper<CarPost> postWrapper = new LambdaQueryWrapper<>();
                postWrapper.in(CarPost::getId, postIds);
                posts = postMapper.selectList(postWrapper);
            }
            
            // 转换为Map方便查询
            Map<Long, CarUser> userMap = users != null ? users.stream()
                    .collect(Collectors.toMap(CarUser::getId, user -> user)) : null;
            
            Map<Long, CarPost> postMap = posts != null ? posts.stream()
                    .collect(Collectors.toMap(CarPost::getId, post -> post)) : null;
                    
            // 填充评论关联信息
            for (CarComment comment : resultPage.getRecords()) {
                // 设置用户名称
                if (userMap != null && userMap.containsKey(comment.getUserId())) {
                    comment.setUserName(userMap.get(comment.getUserId()).getUsername());
                }
                
                // 设置帖子标题
                if (postMap != null && postMap.containsKey(comment.getPostId())) {
                    comment.setPostTitle(postMap.get(comment.getPostId()).getTitle());
                }
                
                // 设置父评论用户名
                if (comment.getParentId() != null && comment.getParentId() > 0) {
                    CarComment parentComment = this.getById(comment.getParentId());
                    if (parentComment != null && userMap != null && userMap.containsKey(parentComment.getUserId())) {
                        comment.setParentUserName(userMap.get(parentComment.getUserId()).getUsername());
                    }
                }
            }
        }

        return resultPage;
    }

    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论信息
     */
    @Override
    public CarComment selectCommentById(Long id) {
        CarComment comment = this.getById(id);
        if (comment != null) {
            // 查询关联信息
            CarUser user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUserName(user.getUsername());
            }
            
            CarPost post = postMapper.selectById(comment.getPostId());
            if (post != null) {
                comment.setPostTitle(post.getTitle());
            }
            
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                CarComment parentComment = this.getById(comment.getParentId());
                if (parentComment != null) {
                    CarUser parentUser = userMapper.selectById(parentComment.getUserId());
                    if (parentUser != null) {
                        comment.setParentUserName(parentUser.getUsername());
                    }
                }
            }
        }
        return comment;
    }

    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    @Override
    public List<CarComment> selectCommentsByPostId(Long postId) {
        LambdaQueryWrapper<CarComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarComment::getPostId, postId);
        queryWrapper.eq(CarComment::getDelFlag, "0");
        queryWrapper.eq(CarComment::getStatus, "0");
        queryWrapper.orderByDesc(CarComment::getCreateTime);
        
        List<CarComment> comments = this.list(queryWrapper);
        
        if (comments != null && !comments.isEmpty()) {
            // 收集所有用户ID
            List<Long> userIds = comments.stream()
                    .map(CarComment::getUserId)
                    .collect(Collectors.toList());
            
            List<Long> parentUserIds = comments.stream()
                    .filter(comment -> comment.getParentId() != null && comment.getParentId() > 0)
                    .map(comment -> this.getById(comment.getParentId()).getUserId())
                    .collect(Collectors.toList());
            
            userIds.addAll(parentUserIds);
            
            // 批量查询用户信息
            List<CarUser> users = null;
            if (!userIds.isEmpty()) {
                LambdaQueryWrapper<CarUser> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.in(CarUser::getId, userIds);
                users = userMapper.selectList(userWrapper);
            }
            
            // 转换为Map方便查询
            Map<Long, CarUser> userMap = users != null ? users.stream()
                    .collect(Collectors.toMap(CarUser::getId, user -> user)) : null;
                    
            // 填充评论关联信息
            for (CarComment comment : comments) {
                // 设置用户名称
                if (userMap != null && userMap.containsKey(comment.getUserId())) {
                    comment.setUserName(userMap.get(comment.getUserId()).getUsername());
                }
                
                // 设置父评论用户名
                if (comment.getParentId() != null && comment.getParentId() > 0) {
                    CarComment parentComment = this.getById(comment.getParentId());
                    if (parentComment != null && userMap != null && userMap.containsKey(parentComment.getUserId())) {
                        comment.setParentUserName(userMap.get(parentComment.getUserId()).getUsername());
                    }
                }
            }
        }
        
        return comments;
    }

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean insertComment(CarComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        return this.save(comment);
    }

    /**
     * 修改评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateComment(CarComment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        return this.updateById(comment) ? 1 : 0;
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCommentById(Long id) {
        // 直接使用SQL更新来绕过MyBatis-Plus的逻辑删除过滤
        return commentMapper.update(null, 
            new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CarComment>()
                .eq(CarComment::getId, id)
                .set(CarComment::getDelFlag, "1")
                .set(CarComment::getUpdateTime, LocalDateTime.now()));
    }

    /**
     * 批量删除评论
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCommentByIds(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            count += deleteCommentById(id);
        }
        return count;
    }

    /**
     * 变更评论状态
     *
     * @param comment 评论信息
     * @return 结果
     */
    @Override
    @Transactional
    public int changeStatus(CarComment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        return this.updateById(comment) ? 1 : 0;
    }
} 