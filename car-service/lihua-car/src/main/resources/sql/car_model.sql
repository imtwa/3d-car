-- ----------------------------
-- Table structure for car_model
-- ----------------------------
DROP TABLE IF EXISTS `car_model`;
CREATE TABLE `car_model` (
  `id` bigint NOT NULL COMMENT '车型ID',
  `brand_id` bigint NOT NULL COMMENT '所属品牌ID',
  `name` varchar(100) NOT NULL COMMENT '车型名称',
  `description` varchar(2000) DEFAULT NULL COMMENT '车型简介',
  `price` varchar(50) DEFAULT NULL COMMENT '售价范围',
  `model_vue` varchar(500) DEFAULT NULL COMMENT 'three.js模型对应的前端文件',
  `model_attachment_id` bigint DEFAULT NULL COMMENT '3D模型文件附件ID',
  `parameters` text DEFAULT NULL COMMENT '汽车参数(JSON格式)',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_car_model_brand_id` (`brand_id`) USING BTREE,
  INDEX `idx_car_model_name` (`name`) USING BTREE,
  CONSTRAINT `fk_car_model_brand` FOREIGN KEY (`brand_id`) REFERENCES `car_brand` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='汽车车型表';