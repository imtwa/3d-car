package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarPostDTO;
import com.lihua.car.entity.CarPost;
import com.lihua.car.entity.CarPostCollection;
import com.lihua.car.entity.CarPostComment;
import com.lihua.car.entity.CarPostLike;
import com.lihua.car.entity.CarUser;
import com.lihua.car.mapper.CarPostCollectionMapper;
import com.lihua.car.mapper.CarPostCommentMapper;
import com.lihua.car.mapper.CarPostLikeMapper;
import com.lihua.car.mapper.CarPostMapper;
import com.lihua.car.mapper.CarUserMapper;
import com.lihua.car.service.CarPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 汽车论坛帖子表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Slf4j
@Service
public class CarPostServiceImpl extends ServiceImpl<CarPostMapper, CarPost> implements CarPostService {

    @Autowired
    private CarPostMapper postMapper;
    
    @Autowired
    private CarPostCommentMapper commentMapper;
    
    @Autowired
    private CarPostLikeMapper postLikeMapper;

    @Autowired
    private CarUserMapper userMapper;

    @Autowired
    private CarPostCollectionMapper collectionMapper;

    @Override
    public IPage<CarPost> queryPage(CarPostDTO dto) {


        IPage<CarPost> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<CarPost> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getStatus())) {
            queryWrapper.eq(CarPost::getStatus, dto.getStatus());
        } else {
            queryWrapper.eq(CarPost::getStatus, "0");
        }
        
        if (StringUtils.hasText(dto.getKeyword())) {
            queryWrapper.and(wrapper -> 
                wrapper.like(CarPost::getTitle, dto.getKeyword())
                .or()
                .like(CarPost::getContent, dto.getKeyword())
            );
        }
        
        // 未删除的帖子
        queryWrapper.eq(CarPost::getDelFlag, "0");
        
        // 排序：先按置顶状态，再按创建时间降序
        queryWrapper.orderByDesc(CarPost::getIsTop, CarPost::getCreateTime);
        
        // 查询结果
        IPage<CarPost> result = postMapper.selectPage(page, queryWrapper);

        // 如果有结果且有用户ID，填充用户信息、点赞状态和收藏状态
        if (result.getRecords().size() > 0) {
            fillUserInfo(result.getRecords());
            
            if (dto.getUserId() != null) {
                fillPostLikeStatus(result.getRecords(), dto.getUserId());
                fillPostCollectionStatus(result.getRecords(), dto.getUserId());
            }
        }
        
        return result;
    }

    @Override
    public List<CarPost> getLatestPosts(int limit) {
        LambdaQueryWrapper<CarPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPost::getStatus, "0")
            .eq(CarPost::getDelFlag, "0")
            .orderByDesc(CarPost::getIsTop, CarPost::getCreateTime)
            .last("LIMIT " + limit);
        
        List<CarPost> posts = postMapper.selectList(queryWrapper);
        
        if (CollectionUtils.isNotEmpty(posts)) {
            fillUserInfo(posts);
        }
        
        return posts;
    }

    @Override
    public List<CarPost> getHotPosts(int limit) {
        List<CarPost> posts = postMapper.selectHotPosts(limit);
        
        if (CollectionUtils.isNotEmpty(posts)) {
            fillUserInfo(posts);
        }
        
        return posts;
    }
    
    @Override
    public List<CarPost> getPinnedPosts(int limit, Long userId) {
        LambdaQueryWrapper<CarPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPost::getStatus, "0")
            .eq(CarPost::getDelFlag, "0")
            .eq(CarPost::getIsTop, 1)
            .orderByDesc(CarPost::getCreateTime)
            .last("LIMIT " + limit);
        
        List<CarPost> posts = postMapper.selectList(queryWrapper);
        
        if (CollectionUtils.isNotEmpty(posts)) {
            fillUserInfo(posts);
            
            if (userId != null) {
                fillPostLikeStatus(posts, userId);
                fillPostCollectionStatus(posts, userId);
            }
        }
        
        return posts;
    }

    @Override
    @Transactional
    public CarPost getPostById(Long id, Long userId) {
        // 增加浏览次数
        postMapper.incrementViewCount(id);
        
        // 查询帖子
        CarPost post = postMapper.selectById(id);
        if (post == null || "1".equals(post.getDelFlag()) || "1".equals(post.getStatus())) {
            return null;
        }
        
        // 填充用户信息
        if (post.getUserId() != null) {
            CarUser user = userMapper.selectById(post.getUserId());
            if (user != null) {
                // 保存原始用户信息
                post.setUser(user);
                
                // 创建前端期望的author对象
                Map<String, Object> author = new HashMap<>();
                author.put("id", user.getId());
                author.put("username", user.getUsername() != null ? user.getUsername() : user.getNickname());
                
                // 设置默认头像或使用用户头像
                String avatar = user.getAvatar();
                if (avatar == null || avatar.isEmpty()) {
                    // 使用默认头像
                    avatar = "/avatar/default.png";
                }
                author.put("avatar", avatar);
                
                // 设置author字段
                post.setAuthor(author);
                
                // 将isTop转换为isPinned
                post.setIsPinned(post.getIsTop() == 1);
                
                // 默认设置isCollected为false
                post.setIsCollected(false);
            }
        }
        
        // 填充点赞状态和收藏状态
        if (userId != null) {
            // 点赞状态
            boolean liked = postLikeMapper.checkUserLiked(id, userId) > 0;
            post.setHasLiked(liked);
            post.setIsLiked(liked);
            
            // 收藏状态
            LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CarPostCollection::getPostId, id)
                    .eq(CarPostCollection::getUserId, userId);
            boolean collected = collectionMapper.selectCount(queryWrapper) > 0;
            post.setIsCollected(collected);
        }
        
        // 查询评论
        loadComments(post, userId);
        
        return post;
    }

    @Override
    public List<CarPost> searchPosts(String keyword, Long userId) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        List<CarPost> posts = postMapper.searchPosts(keyword);
        
        if (CollectionUtils.isNotEmpty(posts)) {
            fillUserInfo(posts);
            
            if (userId != null) {
                fillPostLikeStatus(posts, userId);
                fillPostCollectionStatus(posts, userId);
            }
        }
        
        return posts;
    }

    @Override
    @Transactional
    public int insertPost(CarPost post) {
        post.setId(IdWorker.getId());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setIsTop(0);
        post.setStatus("0");
        post.setDelFlag("0");
        post.setCreateTime(LocalDateTime.now());
        
        return postMapper.insert(post);
    }

    @Override
    public int updatePost(CarPost post) {
        post.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(post);
    }

    @Override
    public int deletePost(Long id) {
        CarPost post = new CarPost();
        post.setId(id);
        post.setDelFlag("1");
        post.setUpdateTime(LocalDateTime.now());
        
        return postMapper.updateById(post);
    }

    @Override
    public int toggleTop(Long id) {
        CarPost post = postMapper.selectById(id);
        if (post == null) {
            return 0;
        }
        
        int isTop = post.getIsTop() == 1 ? 0 : 1;
        return postMapper.updateTopStatus(id, isTop);
    }

    @Override
    @Transactional
    public int likePost(Long postId, Long userId) {
        // 检查是否已点赞
        int likeCount = postLikeMapper.checkUserLiked(postId, userId);
        
        if (likeCount > 0) {
            // 已点赞，取消点赞
            LambdaQueryWrapper<CarPostLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CarPostLike::getPostId, postId)
                .eq(CarPostLike::getUserId, userId);
            postLikeMapper.delete(queryWrapper);
            
            // 减少点赞数
            postMapper.updateLikeCount(postId, -1);
            
            return -1; // 返回-1表示取消点赞
        } else {
            // 未点赞，添加点赞
            CarPostLike like = new CarPostLike();
            like.setId(IdWorker.getId());
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            postLikeMapper.insert(like);
            
            // 增加点赞数
            postMapper.updateLikeCount(postId, 1);
            
            return 1; // 返回1表示点赞成功
        }
    }

    @Override
    @Transactional
    public int addComment(CarPostComment comment) {
        comment.setId(IdWorker.getId());
        comment.setLikeCount(0);
        comment.setStatus("0");
        comment.setDelFlag("0");
        comment.setCreateTime(LocalDateTime.now());
        
        int result = commentMapper.insert(comment);
        
        // 增加评论数
        if (result > 0) {
            postMapper.updateCommentCount(comment.getPostId(), 1);
        }
        
        return result;
    }

    @Override
    @Transactional
    public int deleteComment(Long commentId, Long userId) {
        CarPostComment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            return 0;
        }
        
        // 标记删除
        comment.setDelFlag("1");
        comment.setUpdateTime(LocalDateTime.now());
        int result = commentMapper.updateById(comment);
        
        // 减少评论数
        if (result > 0) {
            postMapper.updateCommentCount(comment.getPostId(), -1);
            
            // 删除子评论
            LambdaQueryWrapper<CarPostComment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CarPostComment::getParentId, commentId);
            List<CarPostComment> children = commentMapper.selectList(queryWrapper);
            
            if (CollectionUtils.isNotEmpty(children)) {
                for (CarPostComment child : children) {
                    child.setDelFlag("1");
                    child.setUpdateTime(LocalDateTime.now());
                    commentMapper.updateById(child);
                    
                    // 每删除一个子评论，减少帖子评论数
                    postMapper.updateCommentCount(comment.getPostId(), -1);
                }
            }
        }
        
        return result;
    }

    /**
     * 填充用户信息
     */
    private void fillUserInfo(List<CarPost> posts) {
        // 收集所有用户ID
        List<Long> userIds = posts.stream()
            .map(CarPost::getUserId)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        
        // 查询用户信息
        LambdaQueryWrapper<CarUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CarUser::getId, userIds);
        List<CarUser> users = userMapper.selectList(queryWrapper);

        // 构建用户ID到用户对象的映射
        Map<Long, CarUser> userMap = new HashMap<>();
        for (CarUser user : users) {
            userMap.put(user.getId(), user);
        }
        
        // 填充用户信息
        for (CarPost post : posts) {
            if (post.getUserId() != null && userMap.containsKey(post.getUserId())) {
                // 保存原始用户信息
                post.setUser(userMap.get(post.getUserId()));
                
                // 创建前端期望的author对象
                Map<String, Object> author = new HashMap<>();
                CarUser user = userMap.get(post.getUserId());
                author.put("id", user.getId());
                author.put("username", user.getUsername() != null ? user.getUsername() : user.getNickname());
                
                // 设置author字段
                post.setAuthor(author);
                
                // 默认设置isCollected为false
                post.setIsCollected(false);
            }
        }
    }
    
    /**
     * 填充帖子点赞状态
     */
    private void fillPostLikeStatus(List<CarPost> posts, Long userId) {
        // 收集所有帖子ID
        List<Long> postIds = posts.stream()
            .map(CarPost::getId)
            .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(postIds)) {
            return;
        }
        
        // 查询用户的点赞记录
        LambdaQueryWrapper<CarPostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CarPostLike::getPostId, postIds)
            .eq(CarPostLike::getUserId, userId);
        List<CarPostLike> likes = postLikeMapper.selectList(queryWrapper);
        
        // 构建帖子ID到点赞状态的映射
        Map<Long, Boolean> likeMap = new HashMap<>();
        for (CarPostLike like : likes) {
            likeMap.put(like.getPostId(), true);
        }
        
        // 填充点赞状态
        for (CarPost post : posts) {
            boolean liked = likeMap.getOrDefault(post.getId(), false);
            post.setHasLiked(liked);
            // 同时设置isLiked字段，保持与hasLiked一致
            post.setIsLiked(liked);
        }
    }
    
    /**
     * 加载评论及回复
     */
    private void loadComments(CarPost post, Long userId) {
        // 查询一级评论
        List<CarPostComment> rootComments = commentMapper.selectRootCommentsByPostId(post.getId());
        if (CollectionUtils.isEmpty(rootComments)) {
            post.setComments(new ArrayList<>());
            return;
        }
        
        // 收集评论用户ID
        List<Long> commentUserIds = rootComments.stream()
            .map(CarPostComment::getUserId)
            .distinct()
            .collect(Collectors.toList());
        
        // 收集所有评论ID
        List<Long> commentIds = rootComments.stream()
            .map(CarPostComment::getId)
            .collect(Collectors.toList());
        
        // 查询子评论
        LambdaQueryWrapper<CarPostComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarPostComment::getDelFlag, "0")
            .eq(CarPostComment::getStatus, "0")
            .in(CarPostComment::getParentId, commentIds)
            .orderByAsc(CarPostComment::getCreateTime);
        
        List<CarPostComment> replies = commentMapper.selectList(queryWrapper);
        
        // 收集回复评论的用户ID
        if (CollectionUtils.isNotEmpty(replies)) {
            commentUserIds.addAll(
                replies.stream()
                    .map(CarPostComment::getUserId)
                    .distinct()
                    .collect(Collectors.toList())
            );
            
            // 收集父评论用户ID
            commentUserIds.addAll(
                replies.stream()
                    .map(CarPostComment::getParentId)
                    .distinct()
                    .collect(Collectors.toList())
            );
            
            // 将回复评论的ID也添加到评论ID列表，用于查询点赞状态
            commentIds.addAll(
                replies.stream()
                    .map(CarPostComment::getId)
                    .collect(Collectors.toList())
            );
        }
        
        // 查询评论用户信息
        Map<Long, CarUser> userMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(commentUserIds)) {
            LambdaQueryWrapper<CarUser> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.in(CarUser::getId, commentUserIds);
            List<CarUser> users = userMapper.selectList(userQueryWrapper);
            
            for (CarUser user : users) {
                userMap.put(user.getId(), user);
            }
        }

        // 构建评论ID到子评论的映射
        Map<Long, List<CarPostComment>> replyMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(replies)) {
            for (CarPostComment reply : replies) {
                if (!replyMap.containsKey(reply.getParentId())) {
                    replyMap.put(reply.getParentId(), new ArrayList<>());
                }
                
                // 设置用户信息
                if (userMap.containsKey(reply.getUserId())) {
                    reply.setUser(userMap.get(reply.getUserId()));
                }
                
                // 设置父评论用户信息
                if (userMap.containsKey(reply.getParentId())) {
                    reply.setParentUser(userMap.get(reply.getParentId()));
                }
                
                replyMap.get(reply.getParentId()).add(reply);
            }
        }
        
        // 填充一级评论的用户信息、点赞状态和回复
        for (CarPostComment comment : rootComments) {
            // 设置用户信息
            if (userMap.containsKey(comment.getUserId())) {
                comment.setUser(userMap.get(comment.getUserId()));
            }

            // 设置回复列表
            comment.setChildren(replyMap.getOrDefault(comment.getId(), new ArrayList<>()));
        }
        
        post.setComments(rootComments);
    }

    /**
     * 获取用户发布的帖子
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 帖子列表
     */
    @Override
    public IPage<CarPost> getUserPosts(Long userId, int pageNum, int pageSize) {
        IPage<CarPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarPost> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加用户ID查询条件
        queryWrapper.eq(CarPost::getUserId, userId);
        
        // 未删除的帖子
        queryWrapper.eq(CarPost::getDelFlag, "0");
        
        // 正常状态的帖子
        queryWrapper.eq(CarPost::getStatus, "0");
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarPost::getCreateTime);
        
        // 查询结果
        IPage<CarPost> result = postMapper.selectPage(page, queryWrapper);
        
        // 填充用户信息
        if (result.getRecords().size() > 0) {
            fillUserInfo(result.getRecords());
            fillPostLikeStatus(result.getRecords(), userId);
            fillPostCollectionStatus(result.getRecords(), userId);
        }
        
        return result;
    }
    
    /**
     * 获取用户评论过的帖子以及评论内容
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @Override
    public IPage<CarPostComment> getUserComments(Long userId, int pageNum, int pageSize) {
        IPage<CarPostComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CarPostComment> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加用户ID查询条件
        queryWrapper.eq(CarPostComment::getUserId, userId);
        
        // 未删除的评论
        queryWrapper.eq(CarPostComment::getDelFlag, "0");
        
        // 正常状态的评论
        queryWrapper.eq(CarPostComment::getStatus, "0");
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(CarPostComment::getCreateTime);
        
        // 查询结果
        IPage<CarPostComment> result = commentMapper.selectPage(page, queryWrapper);
        
        // 关联帖子和用户信息
        if (result.getRecords().size() > 0) {
            for (CarPostComment comment : result.getRecords()) {
                // 获取评论所属的帖子
                CarPost post = postMapper.selectById(comment.getPostId());
                if (post != null && "0".equals(post.getDelFlag()) && "0".equals(post.getStatus())) {
                    comment.setPost(post);
                }
                
                // 设置用户信息
                if (comment.getUserId() != null) {
                    CarUser user = userMapper.selectById(comment.getUserId());
                    if (user != null) {
                        comment.setUser(user);
                        
                        // 创建评论作者信息
                        Map<String, Object> author = new HashMap<>();
                        author.put("id", user.getId());
                        author.put("username", user.getUsername() != null ? user.getUsername() : user.getNickname());
                        
                        // 设置默认头像或使用用户头像
                        String avatar = user.getAvatar();
                        if (avatar == null || avatar.isEmpty()) {
                            // 使用默认头像
                            avatar = "/avatar/default.png";
                        }
                        author.put("avatar", avatar);
                        
                        // 设置author字段
                        comment.setAuthor(author);
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * 填充帖子收藏状态
     */
    private void fillPostCollectionStatus(List<CarPost> posts, Long userId) {
        // 收集所有帖子ID
        List<Long> postIds = posts.stream()
            .map(CarPost::getId)
            .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(postIds)) {
            return;
        }
        
        // 查询用户的收藏记录
        LambdaQueryWrapper<CarPostCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CarPostCollection::getPostId, postIds)
                .eq(CarPostCollection::getUserId, userId);
        List<CarPostCollection> collections = collectionMapper.selectList(queryWrapper);
        
        // 构建帖子ID到收藏状态的映射
        Map<Long, Boolean> collectionMap = new HashMap<>();
        for (CarPostCollection collection : collections) {
            collectionMap.put(collection.getPostId(), true);
        }
        
        // 填充收藏状态
        for (CarPost post : posts) {
            boolean collected = collectionMap.getOrDefault(post.getId(), false);
            post.setIsCollected(collected);
        }
    }
}