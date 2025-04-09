package com.lihua.car.controller;

import com.lihua.annotation.Log;
import com.lihua.car.dto.CarCommentDTO;
import com.lihua.car.entity.CarComment;
import com.lihua.car.service.CarCommentService;
import com.lihua.enums.LogTypeEnum;
import com.lihua.model.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 评论表 前端控制器
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
     * 获取评论列表
     */
    @PostMapping("/page")
    public String selectCommentList(@RequestBody CarCommentDTO dto) {
        return success(carCommentService.queryPage(dto));
    }

    /**
     * ID查询评论
     */
    @GetMapping("/selectId/{id}")
    public String selectCommentById(@PathVariable("id") Long id) {
        return success(carCommentService.selectCommentById(id));
    }

    /**
     * 根据车型ID查询评论列表
     */
    @GetMapping("/model/{modelId}")
    public String selectCommentsByModelId(@PathVariable("modelId") Long modelId) {
        return success(carCommentService.selectCommentsByModelId(modelId));
    }

    /**
     * 新增评论
     */
    @PostMapping("/add")
    @Log(description = "保存评论数据", type = LogTypeEnum.SAVE)
    public String saveAdd(@RequestBody CarComment comment) {
        return success(carCommentService.save(comment));
    }

    /**
     * 修改评论
     */
    @PostMapping("/update")
    @Log(description = "更新评论数据", type = LogTypeEnum.SAVE)
    public String updateComment(@RequestBody CarComment comment) {
        return success(carCommentService.updateById(comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{id}")
    @Log(description = "删除评论数据", type = LogTypeEnum.DELETE)
    public String deleteCommentById(@PathVariable("id") Long id) {
        return success(carCommentService.removeById(id));
    }

    /**
     * 变更评论状态
     */
    @PostMapping("/changeStatus")
    @Log(description = "更新评论状态", type = LogTypeEnum.SAVE)
    public String changeStatus(@RequestBody CarComment comment) {
        return success(carCommentService.updateById(comment));
    }
} 