CREATE TABLE `sepp_warning` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int(10) NOT NULL,
  `type` int(1) NOT NULL COMMENT '告警类型，如缺陷修复异常、缺陷分布异常',
  `sub_type` int(1) NOT NULL,
  `category` varchar(50) NOT NULL COMMENT '告警归属，如版本号、需求号等',
  `warning_date` date NOT NULL COMMENT '告警日期',
  `level` int(1) NOT NULL COMMENT '告警级别，如严重、一般等',
  `responser` int(10) DEFAULT NULL COMMENT '告警归属负责人',
  `summary` varchar(100) NOT NULL COMMENT '告警摘要',
  `content` text DEFAULT NULL COMMENT '告警详细说明',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;
