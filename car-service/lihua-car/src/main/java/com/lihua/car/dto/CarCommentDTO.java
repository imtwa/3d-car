package com.lihua.car.dto;

import com.lihua.model.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 帖子评论查询条件
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
     * 帖子ID
     */
    private Long postId;

    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 帖子标题
     */
    private String postTitle;
    
    /**
     * 用户名
     */
    private String username;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
    
    /**
     * 创建时间开始
     */
    private String createTimeStart;
    
    /**
     * 创建时间结束
     */
    private String createTimeEnd;
    
    /**
     * 创建时间列表（前端传入）
     */
    private LocalDateTime[] createTimeList;
} 