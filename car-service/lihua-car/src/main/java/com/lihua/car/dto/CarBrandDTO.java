package com.lihua.car.dto;

import com.lihua.car.entity.CarModel;
import com.lihua.model.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车品牌数据传输对象
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarBrandDTO extends BaseDTO implements Serializable {

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 品牌下的车型列表
     */
    private List<CarModel> models;
}