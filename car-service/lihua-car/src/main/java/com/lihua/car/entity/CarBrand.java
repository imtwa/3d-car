package com.lihua.car.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 汽车品牌表
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌ID
     */
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌logo图片地址
     */
    private String logo;

    /**
     * 品牌描述
     */
    private String description;

    /**
     * 创立年份
     */
    private String foundYear;

    /**
     * 原产国
     */
    private String country;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}