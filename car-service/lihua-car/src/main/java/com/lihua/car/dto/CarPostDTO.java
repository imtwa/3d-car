package com.lihua.car.dto;

import com.lihua.car.entity.CarPostComment;
import com.lihua.car.entity.CarUser;
import com.lihua.model.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 汽车论坛帖子数据传输对象
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CarPostDTO extends BaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 发帖用户ID
     */
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
     * 是否置顶(0否 1是)
     */
    private Integer isTop;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 发帖用户
     */
    private CarUser user;
    
    /**
     * 用户已点赞
     */
    private Boolean hasLiked;
    
    /**
     * 评论列表
     */
    private List<CarPostComment> comments;
    
    /**
     * 搜索关键词
     */
    private String keyword;
} 