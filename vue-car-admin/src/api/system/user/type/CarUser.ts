export interface CarUser {
    /**
     * 主键
     */
    id?: string;

    /**
     * 用户名
     */
    username?: string;

    /**
     * 密码
     */
    password?: string;

    /**
     * 昵称
     */
    nickname?: string;

    /**
     * 头像
     */
    avatar?: string;

    /**
     * 性别
     */
    gender?: string;

    /**
     * 用户状态
     */
    status?: string;

    /**
     * 邮箱
     */
    email?: string;

    /**
     * 手机号码
     */
    phoneNumber?: string;

    /**
     * 备注
     */
    remark?: string;

    /**
     * 注册类型 0 管理员新增，1 用户自助注册
     */
    registerType?: string;

    /**
     * 创建时间
     */
    createTime?: string;

    /**
     * 更新时间
     */
    updateTime?: string;

    /**
     * 最后登录时间
     */
    lastLoginTime?: string;
}

export interface CarUserVO extends CarUser {
    statusIsNormal?: boolean;
    updateStatusLoading?: boolean;
}

export interface CarUserDTO extends CarUser {
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
    createTimeList?: string[];

    /**
     * 密码加密请求key
     */
    passwordRequestKey?: string;
} 