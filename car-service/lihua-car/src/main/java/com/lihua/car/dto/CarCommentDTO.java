package com.lihua.car.dto;

import com.lihua.model.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 汽车评论查询条件
 * </p>
 *
 * @author lihua
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CarCommentDTO extends BaseDTO {

    /**
     * 车型ID
     */
    private Long modelId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
} 