package com.lihua.car.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lihua.car.dto.CarUserDTO;
import com.lihua.car.entity.CarUser;
import com.lihua.enums.ResultCodeEnum;

/**
 * <p>
 * 前台用户表 服务类
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
public interface CarUserService extends IService<CarUser> {

    /**
     * 用户注册
     *
     * @param carUserDTO 用户信息
     * @return 注册结果
     */
    String register(CarUserDTO carUserDTO);

    /**
     * 用户登录
     *
     * @param carUserDTO 登录信息
     * @return 登录结果
     */
    String login(CarUserDTO carUserDTO);

    /**
     * 验证用户名是否存在
     *
     * @param username 用户名
     * @return 验证结果
     */
    Boolean checkUsernameExist(String username);

    /**
     * 验证滑块验证码
     *
     * @param slideVerifyFlag 滑块验证标识
     * @return 验证结果
     */
    Boolean verifySlideCode(String slideVerifyFlag);
}