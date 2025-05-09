package com.lihua.car.dto;

import com.lihua.model.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 前台用户DTO
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarUserDTO extends BaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 注册类型 0 管理员新增，1 用户自助注册
     */
    private String registerType;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 滑块验证标识
     */
    private String slideVerifyFlag;
    
    /**
     * 创建时间开始（用于日期范围查询）
     */
    private String createTimeStart;
    
    /**
     * 创建时间结束（用于日期范围查询）
     */
    private String createTimeEnd;
}