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
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 汽车论坛帖子表
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarPost implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 发帖用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 是否置顶(0否 1是)
     */
    private Integer isTop;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 删除标志(0代表存在 1代表删除)
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
     * 发帖用户（非数据库字段）
     */
    @TableField(exist = false)
    private CarUser user;
    
    /**
     * 用户已点赞（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean hasLiked;
    
    /**
     * 评论列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CarPostComment> comments;
    
    /**
     * 作者信息（非数据库字段，前端展示用）
     */
    @TableField(exist = false)
    private Map<String, Object> author;
    
    /**
     * 是否置顶（非数据库字段，前端展示用）
     */
    @TableField(exist = false)
    private Boolean isPinned;
    
    /**
     * 是否已收藏（非数据库字段，前端展示用）
     */
    @TableField(exist = false)
    private Boolean isCollected;
    
    /**
     * 是否已点赞（非数据库字段，前端展示用，与hasLiked字段同义）
     */
    @TableField(exist = false)
    private Boolean isLiked;
}