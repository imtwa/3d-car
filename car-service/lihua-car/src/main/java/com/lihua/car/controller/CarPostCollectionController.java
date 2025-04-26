package com.lihua.car.controller;

import com.lihua.car.service.CarPostCollectionService;
import com.lihua.enums.ResultCodeEnum;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 帖子收藏表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/car/post/collection")
public class CarPostCollectionController extends BaseController {

    @Autowired
    private CarPostCollectionService collectionService;

    /**
     * 获取用户收藏的帖子列表
     */
    @GetMapping("/user/{userId}")
    public String getUserCollections(@PathVariable("userId") String userId,
                                     @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 处理"undefined"用户ID情况，返回空数据
        if (userId == null || "undefined".equals(userId)) {
            return success("");
        }
        
        // 转换userId为Long类型
        try {
            Long userIdLong = Long.valueOf(userId);
            return success(collectionService.getUserCollectedPosts(userIdLong, pageNum, pageSize));
        } catch (NumberFormatException e) {
            return success("");
        }
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/collect")
    public String collectPost(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        // 处理输入参数
        if (postId == null || userId == null || "undefined".equals(postId) || "undefined".equals(userId)) {
            return error(ResultCodeEnum.PARAMS_MISSING, "参数不能为空");
        }
        
        try {
            Long postIdLong = Long.valueOf(postId);
            Long userIdLong = Long.valueOf(userId);
            return success(collectionService.collectPost(postIdLong, userIdLong));
        } catch (NumberFormatException e) {
            return error(ResultCodeEnum.PARAMS_ERROR, "参数格式错误");
        }
    }

    /**
     * 取消收藏帖子
     */
    @PostMapping("/uncollect")
    public String uncollectPost(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        // 处理输入参数
        if (postId == null || userId == null || "undefined".equals(postId) || "undefined".equals(userId)) {
            return error(ResultCodeEnum.PARAMS_MISSING, "参数不能为空");
        }
        
        try {
            Long postIdLong = Long.valueOf(postId);
            Long userIdLong = Long.valueOf(userId);
            return success(collectionService.uncollectPost(postIdLong, userIdLong));
        } catch (NumberFormatException e) {
            return error(ResultCodeEnum.PARAMS_ERROR, "参数格式错误");
        }
    }

    /**
     * 检查用户是否已收藏帖子
     */
    @GetMapping("/check")
    public String checkUserCollected(@RequestParam("postId") String postId, @RequestParam("userId") String userId) {
        // 处理输入参数
        if (postId == null || userId == null || "undefined".equals(postId) || "undefined".equals(userId)) {
            return success(false);
        }
        
        try {
            Long postIdLong = Long.valueOf(postId);
            Long userIdLong = Long.valueOf(userId);
            return success(collectionService.checkUserCollected(postIdLong, userIdLong));
        } catch (NumberFormatException e) {
            return success(false);
        }
    }
} 