package com.lihua.car.controller;

import com.lihua.annotation.Log;
import com.lihua.car.dto.CarCommentDTO;
import com.lihua.car.entity.CarComment;
import com.lihua.car.service.CarCommentService;
import com.lihua.enums.LogTypeEnum;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 帖子评论表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/car/comment")
public class CarCommentController extends BaseController {

    @Autowired
    private CarCommentService carCommentService;

    /**
     * 分页查询评论列表
     */
    @PostMapping("/page")
    public String selectCommentList(@RequestBody CarCommentDTO dto) {
        return success(carCommentService.queryPage(dto));
    }

    /**
     * ID查询评论
     */
    @GetMapping("/{id}")
    public String selectCommentById(@PathVariable("id") Long id) {
        return success(carCommentService.selectCommentById(id));
    }

    /**
     * 根据帖子ID查询评论列表
     */
    @PostMapping("/post/{postId}")
    public String selectCommentsByPostId(@PathVariable("postId") Long postId, @RequestBody(required = false) CarCommentDTO dto) {
        if (dto == null) {
            dto = new CarCommentDTO();
        }
        dto.setPostId(postId);
        return success(carCommentService.queryPage(dto));
    }

    /**
     * 新增评论
     */
    @PostMapping("/add")
    @Log(description = "保存评论数据", type = LogTypeEnum.SAVE)
    public String saveAdd(@RequestBody CarComment comment) {
        return success(carCommentService.insertComment(comment));
    }

    /**
     * 修改评论
     */
    @PostMapping("/update")
    @Log(description = "更新评论数据", type = LogTypeEnum.SAVE)
    public String updateComment(@RequestBody CarComment comment) {
        return success(carCommentService.updateComment(comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    @Log(description = "删除评论数据", type = LogTypeEnum.DELETE)
    public String deleteCommentById(@PathVariable("id") Long id) {
        return success(carCommentService.deleteCommentById(id));
    }
    
    /**
     * 批量删除评论
     */
    @DeleteMapping
    @Log(description = "批量删除评论数据", type = LogTypeEnum.DELETE)
    public String deleteCommentByIds(@RequestBody List<Long> ids) {
        Long[] idArray = ids.toArray(new Long[0]);
        return success(carCommentService.deleteCommentByIds(idArray));
    }

    /**
     * 变更评论状态
     */
    @PostMapping("/updateStatus/{id}/{status}")
    @Log(description = "更新评论状态", type = LogTypeEnum.SAVE)
    public String updateStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        CarComment comment = new CarComment();
        comment.setId(id);
        comment.setStatus(status);
        return success(carCommentService.changeStatus(comment));
    }
    
    /**
     * 导出评论数据
     */
    @PostMapping("/export")
    @Log(description = "导出评论数据", type = LogTypeEnum.EXPORT)
    public void exportExcel(@RequestBody CarCommentDTO dto) {
        // 实现导出逻辑
    }
} 