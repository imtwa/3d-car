-- ----------------------------
-- Table structure for car_brand
-- ----------------------------
DROP TABLE IF EXISTS `car_brand`;
CREATE TABLE `car_brand` (
  `id` bigint NOT NULL COMMENT '品牌ID',
  `name` varchar(50) NOT NULL COMMENT '品牌名称',
  `logo` varchar(500) DEFAULT NULL COMMENT '品牌logo图片地址',
  `description` varchar(1000) DEFAULT NULL COMMENT '品牌描述',
  `found_year` varchar(20) DEFAULT NULL COMMENT '创立年份',
  `country` varchar(50) DEFAULT NULL COMMENT '原产国',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_car_brand_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='汽车品牌表';