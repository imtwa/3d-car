export interface CarPost {
    /**
     * 主键
     */
    id?: string;

    /**
     * 发帖用户ID
     */
    userId?: string;

    /**
     * 发帖用户名
     */
    username?: string;

    /**
     * 帖子标题
     */
    title?: string;

    /**
     * 帖子内容
     */
    content?: string;

    /**
     * 浏览次数
     */
    viewCount?: number;

    /**
     * 点赞次数
     */
    likeCount?: number;

    /**
     * 评论次数
     */
    commentCount?: number;

    /**
     * 是否置顶
     */
    isTop?: string;

    /**
     * 状态
     */
    status?: string;

    /**
     * 创建时间
     */
    createTime?: string;

    /**
     * 更新时间
     */
    updateTime?: string;

    /**
     * 用户信息
     */
    user?: {
        id?: string;
        username?: string;
        password?: string;
        nickname?: string;
        avatar?: string;
        gender?: string;
        status?: string;
        delFlag?: string;
        createTime?: string;
        updateTime?: string;
        lastLoginTime?: string;
        email?: string;
        phoneNumber?: string;
        remark?: string;
        registerType?: string;
    };

    /**
     * 是否已点赞
     */
    hasLiked?: boolean;

    /**
     * 评论列表
     */
    comments?: any[];

    /**
     * 作者信息
     */
    author?: {
        id?: number;
        username?: string;
    };

    /**
     * 是否已置顶
     */
    isPinned?: boolean;

    /**
     * 是否已收藏
     */
    isCollected?: boolean;

    /**
     * 是否已点赞
     */
    isLiked?: boolean;
}

export interface CarPostVO extends CarPost {
    statusIsNormal?: boolean;
    updateStatusLoading?: boolean;
    isTopFlag?: boolean;
    updateTopLoading?: boolean;
}

export interface CarPostDTO extends CarPost {
    /**
     * 当前页数
     */
    pageNum?: number;

    /**
     * 每页记录数
     */
    pageSize?: number;

    /**
     * 创建时间列表，用于日期范围查询
     */
    createTimeList?: any[];

    /**
     * 创建时间开始，格式化后的字符串
     */
    createTimeStart?: string;
    
    /**
     * 创建时间结束，格式化后的字符串
     */
    createTimeEnd?: string;
}

export interface CarPostComment {
    /**
     * 主键
     */
    id?: string;

    /**
     * 帖子ID
     */
    postId?: string;

    /**
     * 帖子标题
     */
    postTitle?: string;

    /**
     * 评论用户ID
     */
    userId?: string;

    /**
     * 评论用户名
     */
    username?: string;

    /**
     * 父评论ID
     */
    parentId?: string;

    /**
     * 父评论用户名
     */
    parentUsername?: string;

    /**
     * 评论内容
     */
    content?: string;

    /**
     * 点赞次数
     */
    likeCount?: number;

    /**
     * 状态
     */
    status?: string;

    /**
     * 创建时间
     */
    createTime?: string;

    /**
     * 更新时间
     */
    updateTime?: string;
}

export interface CarPostCommentVO extends CarPostComment {
    statusIsNormal?: boolean;
    updateStatusLoading?: boolean;
}

export interface CarPostCommentDTO extends CarPostComment {
    /**
     * 当前页数
     */
    pageNum?: number;

    /**
     * 每页记录数
     */
    pageSize?: number;

    /**
     * 创建时间列表，用于日期范围查询
     */
    createTimeList?: any[];

    /**
     * 创建时间开始，格式化后的字符串
     */
    createTimeStart?: string;
    
    /**
     * 创建时间结束，格式化后的字符串
     */
    createTimeEnd?: string;
} 