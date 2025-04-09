package com.lihua.car.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 汽车评论表
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 车型ID
     */
    private Long modelId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分(1-5)
     */
    private Integer rating;

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

    /**
     * 用户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 车型名称（非数据库字段）
     */
    @TableField(exist = false)
    private String modelName;
} 