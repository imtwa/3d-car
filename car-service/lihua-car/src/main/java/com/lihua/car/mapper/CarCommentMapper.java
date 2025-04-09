package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 汽车评论表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
@Mapper
public interface CarCommentMapper extends BaseMapper<CarComment> {
} 