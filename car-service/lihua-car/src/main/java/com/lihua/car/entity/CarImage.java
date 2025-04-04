package com.lihua.car.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 汽车图片表
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    private Long id;

    /**
     * 所属车型ID
     */
    private Long modelId;
    
    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 图片类型（0首图 1详情图）
     */
    private String imageType;

    /**
     * 排序号
     */
    private Integer sort;

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