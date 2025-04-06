package com.lihua.car.dto;

import com.lihua.car.entity.CarBrand;
import com.lihua.car.entity.CarImage;
import com.lihua.model.BaseDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车车型数据传输对象
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
public class CarModelDTO extends BaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车型ID
     */
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
     * 汽车参数(JSON格式)
     */
    private String parameters;

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
     * 品牌信息
     */
    private CarBrand brand;
    
    /**
     * 首图列表
     */
    private List<CarImage> mainImages;
    
    /**
     * 详情图列表
     */
    private List<CarImage> detailImages;
}