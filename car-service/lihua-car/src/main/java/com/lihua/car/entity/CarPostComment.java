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
 * 帖子评论表
 * </p>
 *
 * @author lihua
 * @since 2023-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CarPostComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞次数
     */
    private Integer likeCount;

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
     * 评论用户（非数据库字段）
     */
    @TableField(exist = false)
    private CarUser user;
    
    /**
     * 用户已点赞（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean hasLiked;
    
    /**
     * 回复评论列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CarPostComment> children;
    
    /**
     * 父评论用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private CarUser parentUser;

    /**
     * 帖子对象（非数据库字段）
     */
    @TableField(exist = false)
    private CarPost post;
    
    /**
     * 评论作者信息（非数据库字段）
     */
    @TableField(exist = false)
    private Map<String, Object> author;
    
    /**
     * 获取帖子对象
     */
    public CarPost getPost() {
        return post;
    }
    
    /**
     * 设置帖子对象
     */
    public void setPost(CarPost post) {
        this.post = post;
    }
    
    /**
     * 获取评论作者信息
     */
    public Map<String, Object> getAuthor() {
        return author;
    }
    
    /**
     * 设置评论作者信息
     */
    public void setAuthor(Map<String, Object> author) {
        this.author = author;
    }
} 