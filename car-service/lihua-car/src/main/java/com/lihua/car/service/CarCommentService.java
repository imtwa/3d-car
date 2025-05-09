package com.lihua.car.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lihua.car.dto.CarCommentDTO;
import com.lihua.car.entity.CarComment;

import java.util.List;

/**
 * <p>
 * 帖子评论表 服务接口
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
public interface CarCommentService extends IService<CarComment> {

    /**
     * 查询评论列表
     *
     * @param dto 评论查询条件
     * @return 评论列表
     */
    IPage<CarComment> queryPage(CarCommentDTO dto);
    
    /**
     * 根据ID查询评论
     *
     * @param id 评论ID
     * @return 评论信息
     */
    CarComment selectCommentById(Long id);
    
    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CarComment> selectCommentsByPostId(Long postId);
    
    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    boolean insertComment(CarComment comment);
    
    /**
     * 修改评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    int updateComment(CarComment comment);
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 结果
     */
    int deleteCommentById(Long id);
    
    /**
     * 批量删除评论
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteCommentByIds(Long[] ids);
    
    /**
     * 变更评论状态
     *
     * @param comment 评论信息
     * @return 结果
     */
    int changeStatus(CarComment comment);
}