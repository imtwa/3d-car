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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 汽车车型表
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarModel implements Serializable {

    @TableField(exist = false)
    private List<String> imageIds;
    
    @TableField(exist = false)
    private String coverImageId;

    private static final long serialVersionUID = 1L;

    /**
     * 车型ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 所属品牌ID
     */
    private Long brandId;
    
    /**
     * 车型名称
     */
    private String name;

    /**
     * 车型简介
     */
    private String description;

    /**
     * 售价范围
     */
    private String price;

    /**
     * 3D模型文件附件ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modelAttachmentId;

    /**
     * 汽车参数(JSON格式)
     */
    private String parameters;

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
     * 品牌对象（非数据库字段）
     */
    private transient CarBrand brand;
    
    /**
     * 品牌名称（非数据库字段）
     */
    @TableField(exist = false)
    private String brandName;
}