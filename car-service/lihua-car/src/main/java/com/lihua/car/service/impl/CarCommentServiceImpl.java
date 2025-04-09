package com.lihua.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lihua.car.dto.CarCommentDTO;
import com.lihua.car.entity.CarComment;
import com.lihua.car.entity.CarModel;
import com.lihua.car.mapper.CarCommentMapper;
import com.lihua.car.mapper.CarModelMapper;
import com.lihua.car.service.CarCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车评论表 服务实现类
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
    private CarModelMapper modelMapper;

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

        // 添加车型ID查询条件
        if (dto.getModelId() != null) {
            queryWrapper.eq(CarComment::getModelId, dto.getModelId());
        }

        // 添加用户ID查询条件
        if (dto.getUserId() != null) {
            queryWrapper.eq(CarComment::getUserId, dto.getUserId());
        }

        // 添加评分查询条件
        if (dto.getRating() != null) {
            queryWrapper.eq(CarComment::getRating, dto.getRating());
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

        // 为每个评论关联车型名称
        if (resultPage.getRecords() != null && !resultPage.getRecords().isEmpty()) {
            for (CarComment comment : resultPage.getRecords()) {
                if (comment.getModelId() != null) {
                    CarModel model = modelMapper.selectById(comment.getModelId());
                    if (model != null) {
                        comment.setModelName(model.getName());
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
        return this.getById(id);
    }

    /**
     * 根据车型ID查询评论列表
     *
     * @param modelId 车型ID
     * @return 评论列表
     */
    @Override
    public List<CarComment> selectCommentsByModelId(Long modelId) {
        LambdaQueryWrapper<CarComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CarComment::getModelId, modelId)
                .eq(CarComment::getDelFlag, "0")
                .eq(CarComment::getStatus, "0")
                .orderByDesc(CarComment::getCreateTime);
        return this.list(queryWrapper);
    }

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    @Override
    public boolean insertComment(CarComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setDelFlag("0");
        comment.setStatus("0");
        return this.save(comment);
    }

    /**
     * 修改评论
     *
     * @param comment 评论信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateComment(CarComment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        return commentMapper.updateById(comment);
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 结果
     */
    @Override
    public int deleteCommentById(Long id) {
        CarComment comment = new CarComment();
        comment.setId(id);
        comment.setDelFlag("1");
        comment.setUpdateTime(LocalDateTime.now());
        return commentMapper.updateById(comment);
    }

    /**
     * 批量删除评论
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
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
    public int changeStatus(CarComment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        return commentMapper.updateById(comment);
    }
} 