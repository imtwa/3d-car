package com.lihua.car.controller;

import com.lihua.annotation.Log;
import com.lihua.car.dto.CarPostDTO;
import com.lihua.car.entity.CarPost;
import com.lihua.car.entity.CarPostComment;
import com.lihua.car.service.CarPostService;
import com.lihua.enums.LogTypeEnum;
import com.lihua.model.validation.MaxPageSizeLimit;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汽车论坛帖子表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@RestController
@RequestMapping("/car/post")
public class CarPostController extends BaseController {

    @Autowired
    private CarPostService postService;

    /**
     * 分页查询帖子列表
     */
    @PostMapping("/page")
    public String queryPage(@RequestBody @Validated(MaxPageSizeLimit.class) CarPostDTO dto) {
        return success(postService.queryPage(dto));
    }

    /**
     * 获取最新帖子列表
     */
    @GetMapping("/latest/{limit}")
    public String getLatestPosts(@PathVariable("limit") int limit) {
        return success(postService.getLatestPosts(limit));
    }

    /**
     * 获取热门帖子列表
     */
    @GetMapping("/hot/{limit}")
    public String getHotPosts(@PathVariable("limit") int limit) {
        return success(postService.getHotPosts(limit));
    }

    /**
     * 获取置顶帖子列表
     */
    @GetMapping("/pinned/{limit}")
    public String getPinnedPosts(@PathVariable("limit") int limit, @RequestParam(value = "userId", required = false) Long userId) {
        return success(postService.getPinnedPosts(limit, userId));
    }

    /**
     * 搜索帖子
     */
    @GetMapping("/search")
    public String searchPosts(@RequestParam("keyword") String keyword, @RequestParam(value = "userId", required = false) Long userId) {
        return success(postService.searchPosts(keyword, userId));
    }

    /**
     * 根据ID查询帖子详情
     */
    @GetMapping("/{id}")
    public String getPostById(@PathVariable("id") Long id, @RequestParam(value = "userId", required = false) Long userId) {
        return success(postService.getPostById(id, userId));
    }

    /**
     * 新增帖子
     */
    @PostMapping("/add")
    @Log(description = "发布帖子", type = LogTypeEnum.SAVE)
    public String addPost(@RequestBody CarPost post) {
        return success(postService.insertPost(post));
    }

    /**
     * 修改帖子
     */
    @PostMapping("/update")
    public String updatePost(@RequestBody CarPost post) {
        // 只允许修改标题和内容
        CarPost updatePost = new CarPost();
        updatePost.setId(post.getId());
        updatePost.setTitle(post.getTitle());
        updatePost.setContent(post.getContent());
        
        return success(postService.updatePost(updatePost));
    }

    /**
     * 删除帖子 (POST方法)
     */
    @PostMapping("/remove/{id}")
    public String removePost(@PathVariable("id") Long id, @RequestParam(value = "userId", required = false) Long userId) {
        // 验证用户是否有权限删除帖子（管理员或帖子作者）
        return success(postService.deletePost(id, userId));
    }

    /**
     * 置顶/取消置顶帖子
     */
    @PostMapping("/toggle-top/{id}")
    public String toggleTopPost(@PathVariable("id") Long id) {
        return success(postService.toggleTop(id));
    }

    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/like/{id}")
    public String likePost(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return success(postService.likePost(id, userId));
    }

    /**
     * 发表评论
     */
    @PostMapping("/comment/add")
    public String addComment(@RequestBody CarPostComment comment) {
        // 用户ID由前端传入，不再从SecurityUtils获取
        return success(postService.addComment(comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{id}")
    public String deleteComment(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        return success(postService.deleteComment(id, userId));
    }

    /**
     * 获取用户发布的帖子
     */
    @GetMapping("/user/posts/{userId}")
    public String getUserPosts(@PathVariable("userId") Long userId,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return success(postService.getUserPosts(userId, pageNum, pageSize));
    }

    /**
     * 获取用户评论
     */
    @GetMapping("/user/comments/{userId}")
    public String getUserComments(@PathVariable("userId") Long userId,
                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return success(postService.getUserComments(userId, pageNum, pageSize));
    }
}