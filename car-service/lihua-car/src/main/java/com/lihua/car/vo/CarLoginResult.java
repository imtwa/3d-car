package com.lihua.car.vo;

import com.lihua.car.entity.CarUser;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 前台用户登录结果
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@Accessors(chain = true)
public class CarLoginResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户token
     */
    private String token;

    /**
     * 用户信息
     */
    private CarUser user;
}