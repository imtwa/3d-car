package com.lihua.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lihua.car.entity.CarUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 前台用户表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Mapper
public interface CarUserMapper extends BaseMapper<CarUser> {

}