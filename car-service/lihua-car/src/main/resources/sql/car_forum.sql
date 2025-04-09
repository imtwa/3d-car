-- ----------------------------
-- Table structure for car_post
-- ----------------------------
DROP TABLE IF EXISTS `car_post`;
CREATE TABLE `car_post` (
  `id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '发帖用户ID',
  `title` varchar(100) NOT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `comment_count` int DEFAULT '0' COMMENT '评论次数',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶(0否 1是)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 1代表删除)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_top` (`is_top`),
  FULLTEXT KEY `idx_content` (`title`,`content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='汽车论坛帖子表';

-- ----------------------------
-- Table structure for car_post_comment
-- ----------------------------
DROP TABLE IF EXISTS `car_post_comment`;
CREATE TABLE `car_post_comment` (
  `id` bigint NOT NULL COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `content` text NOT NULL COMMENT '评论内容',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 1代表删除)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `car_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子评论表';

-- ----------------------------
-- Table structure for car_post_like
-- ----------------------------
DROP TABLE IF EXISTS `car_post_like`;
CREATE TABLE `car_post_like` (
  `id` bigint NOT NULL COMMENT '点赞ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  CONSTRAINT `fk_like_post` FOREIGN KEY (`post_id`) REFERENCES `car_post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子点赞表';