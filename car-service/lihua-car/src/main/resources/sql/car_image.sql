-- ----------------------------
-- Table structure for car_image
-- ----------------------------
DROP TABLE IF EXISTS `car_image`;
CREATE TABLE `car_image` (
  `id` bigint NOT NULL COMMENT '图片ID',
  `model_id` bigint NOT NULL COMMENT '所属车型ID',
  `image_type` char(1) NOT NULL COMMENT '图片类型（0首图 1详情图）',
  `attachment_id` bigint DEFAULT NULL COMMENT '原始文件ID，关联sys_attachment表',
  `sort` int DEFAULT 0 COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_car_image_model_id` (`model_id`) USING BTREE,
  CONSTRAINT `fk_car_image_model` FOREIGN KEY (`model_id`) REFERENCES `car_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='汽车图片表';