CREATE DATABASE /*!32312 IF NOT EXISTS*/`sepp` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `sepp`;

set @@global.foreign_key_checks = 0;

/*Table structure for table `sepp_attachment` */
DROP TABLE IF EXISTS `sepp_attachment`;

CREATE TABLE `sepp_attachment` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `file_name` varchar(100) NOT NULL COMMENT '文件名',
  `url` varchar(500) DEFAULT NULL COMMENT '文件访问URL',
  `upload_user` int(10) NOT NULL COMMENT '上传用户',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_auto_results` */

DROP TABLE IF EXISTS `sepp_auto_results`;

CREATE TABLE `sepp_auto_results` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `system_name` varchar(32) NOT NULL COMMENT '系统名称',
  `job_name` varchar(50) NOT NULL COMMENT 'JOB名称',
  `job_type` varchar(2) NOT NULL COMMENT '01: Auto Case Results; 02: Interface Results; 03: Algorithm Results',
  `system_version` varchar(32) DEFAULT NULL COMMENT '系统版本',
  `run_env` varchar(8) DEFAULT NULL COMMENT '运行环境',
  `class_total` int(11) DEFAULT NULL COMMENT '运行类总数',
  `class_success_no` int(11) DEFAULT NULL COMMENT '类运行成功数',
  `class_fail_no` int(11) DEFAULT NULL COMMENT '类运行失败数',
  `class_success_rate` decimal(5,2) DEFAULT NULL COMMENT '类运行失败率',
  `method_total` int(11) DEFAULT NULL COMMENT '运行方法总数',
  `success_no` int(11) DEFAULT NULL COMMENT '运行成功案例数',
  `fail_no` int(11) DEFAULT NULL COMMENT '运行失败案例数',
  `success_rate` decimal(5,2) DEFAULT NULL COMMENT '运行成功率',
  `complete_date` datetime NOT NULL COMMENT '运行结束时间',
  `fcd` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '入库时间',
  `lcd` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '运行结束时间',
  UNIQUE KEY `INDEX_RUN` (`system_name`,`job_name`,`job_type`,`system_version`,`run_env`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_auto_type` */

DROP TABLE IF EXISTS `sepp_auto_type`;

CREATE TABLE `sepp_auto_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_build_files` */

DROP TABLE IF EXISTS `sepp_build_files`;

CREATE TABLE `sepp_build_files` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `note_id` int(10) NOT NULL COMMENT 'releaseNotes编号',
  `instance` varchar(100) NOT NULL COMMENT '实例名称',
  `param_key` varchar(100) DEFAULT NULL,
  `param_value` text DEFAULT NULL,
  `build_params` text DEFAULT NULL COMMENT '构建参数',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_build_history` */

DROP TABLE IF EXISTS `sepp_build_history`;

CREATE TABLE `sepp_build_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '开发任务ID',
  `job_name` varchar(100) DEFAULT NULL COMMENT 'jenkins项目名称',
  `build_type` varchar(100) DEFAULT NULL,
  `tag` varchar(100) DEFAULT NULL,
  `note_id` int(10) DEFAULT NULL COMMENT 'releaseNotes编号',
  `product_id` int(10) DEFAULT NULL COMMENT '项目号',
  `branch_id` int(10) DEFAULT NULL COMMENT '分支',
  `branch_name` varchar(100) DEFAULT NULL,
  `env_name` varchar(100) DEFAULT NULL,
  `env_type` int(10) DEFAULT NULL COMMENT '环境',
  `instance` varchar(200) DEFAULT NULL COMMENT '实体',
  `instance_type` varchar(100) DEFAULT NULL,
  `build_version` int(10) DEFAULT NULL COMMENT 'jenkins项目构建编号',
  `submitter` varchar(100) DEFAULT NULL COMMENT '提交人',
  `build_host` varchar(30) DEFAULT NULL COMMENT '提交终端',
  `build_status` varchar(10) DEFAULT NULL COMMENT '结果',
  `build_params` text DEFAULT NULL COMMENT '详情',
  `pipeline_step` text DEFAULT NULL,
  `build_interval` int(10) DEFAULT NULL COMMENT '构建耗时',
  `code_change` text DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `day` (`created_date`),
  KEY `PRODUCT_ID_INDEX` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_build_instance` */

DROP TABLE IF EXISTS `sepp_build_instance`;

CREATE TABLE `sepp_build_instance` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int(10) DEFAULT NULL,
  `instance` varchar(100) NOT NULL COMMENT '实例名称',
  `project_name` varchar(100) DEFAULT NULL,
  `repo_url` varchar(100) DEFAULT NULL COMMENT 'git仓库地址',
  `namespace` varchar(100) DEFAULT NULL COMMENT '命名空间',
  `params` varchar(400) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `department` varchar(100) DEFAULT NULL COMMENT '责任部门',
  `user` varchar(100) DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_build_status` */

DROP TABLE IF EXISTS `sepp_build_status`;

CREATE TABLE `sepp_build_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_case_info` */

DROP TABLE IF EXISTS `sepp_case_info`;

CREATE TABLE `sepp_case_info` (
  `case_id` int(10) NOT NULL COMMENT 'CASE ID',
  `status` int(10) DEFAULT NULL COMMENT '用例状态',
  `designer` int(10) DEFAULT NULL COMMENT '设计者',
  `priority` int(10) DEFAULT NULL COMMENT '优先级',
  `test_type` int(10) DEFAULT NULL COMMENT '测试类型，如功能、性能、安全性等',
  `test_period` int(10) DEFAULT NULL COMMENT '测试阶段，如系统测试',
  `prod_module` int(10) DEFAULT NULL COMMENT '测试手段，如自动化、手动',
  `regress_mark` varchar(1) DEFAULT 'N' COMMENT '回归标识',
  `auto_path` varchar(200) DEFAULT NULL COMMENT '自动化脚本路径',
  `auto_type` int(10) DEFAULT NULL COMMENT '自动化测试类型，如webui、接口等',
  `pre_condition` text DEFAULT NULL COMMENT '测试前置条件',
  `summary` text DEFAULT NULL COMMENT '测试用例描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  UNIQUE KEY `case_id` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_case_mind` */

DROP TABLE IF EXISTS `sepp_case_mind`;

CREATE TABLE `sepp_case_mind` (
  `case_id` int(10) NOT NULL COMMENT 'CASE ID',
  `mind_text` text DEFAULT NULL COMMENT '脑图测试用例文本内容',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  UNIQUE KEY `case_id` (`case_id`),
  CONSTRAINT `CHK_TEXT` CHECK (json_valid(`mind_text`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_case_related` */

DROP TABLE IF EXISTS `sepp_case_related`;

CREATE TABLE `sepp_case_related` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `case_id` int(10) NOT NULL COMMENT 'CASE ID',
  `relate_id` int(10) NOT NULL COMMENT '关联对象ID',
  `relate_type` int(10) NOT NULL COMMENT '关联对象类型',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_RELATED` (`case_id`,`relate_id`,`relate_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_case_step` */

DROP TABLE IF EXISTS `sepp_case_step`;

CREATE TABLE `sepp_case_step` (
  `case_id` int(10) NOT NULL COMMENT 'CASE ID',
  `step_id` int(10) NOT NULL COMMENT 'STEP ID',
  `operation` varchar(200) NOT NULL COMMENT '操作描述',
  `input_data` varchar(200) NOT NULL COMMENT '输入数据',
  `expect_result` varchar(200) NOT NULL COMMENT '预期结果',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  UNIQUE KEY `INDEX_STEP` (`case_id`,`step_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_case_tree` */

DROP TABLE IF EXISTS `sepp_case_tree`;

CREATE TABLE `sepp_case_tree` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` int(10) NOT NULL COMMENT '父文件夹ID',
  `product_id` int(10) NOT NULL COMMENT '所属产品ID',
  `type` varchar(10) NOT NULL COMMENT '节点类型：文件夹、用例',
  `name` varchar(50) NOT NULL COMMENT '节点名称',
  `creator` int(10) NOT NULL COMMENT '创建人',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_PARENT` (`id`,`parent_id`,`name`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_cm_status` */

DROP TABLE IF EXISTS `sepp_cm_status`;

CREATE TABLE `sepp_cm_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_code_mission` */

DROP TABLE IF EXISTS `sepp_code_mission`;

CREATE TABLE `sepp_code_mission` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '开发任务ID',
  `req_id` int(10) NOT NULL COMMENT '产品需求ID',
  `module_id` int(10) NOT NULL COMMENT '所属模块ID',
  `status` varchar(10) NOT NULL COMMENT '任务状态',
  `spliter` int(10) NOT NULL COMMENT '任务拆分人',
  `split_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '任务拆分日期',
  `responser` int(10) DEFAULT NULL COMMENT '任务负责人',
  `manpower` float DEFAULT NULL COMMENT '所需人力（人日）',
  `plan_begin` date DEFAULT NULL COMMENT '计划开始日期',
  `plan_to` date DEFAULT '1970-01-01' COMMENT '计划完成日期',
  `attachment` varchar(200) DEFAULT NULL COMMENT '附件',
  `summary` varchar(500) NOT NULL COMMENT '任务摘要',
  `detail` text DEFAULT NULL COMMENT '需求详情',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INX_SPLITED` (`split_date`),
  KEY `INX_MODULE` (`module_id`),
  KEY `INX_PLAN` (`plan_begin`,`plan_to`),
  KEY `INX_REQ` (`req_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_data_unit` */

DROP TABLE IF EXISTS `sepp_data_unit`;

CREATE TABLE `sepp_data_unit` (
  `unit_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `unit_value` varchar(10) NOT NULL COMMENT '单位值',
  `unit_desc` varchar(40) NOT NULL COMMENT '单位描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`unit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_influence` */

DROP TABLE IF EXISTS `sepp_defect_influence`;

CREATE TABLE `sepp_defect_influence` (
  `influence_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `influence_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`influence_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_period` */

DROP TABLE IF EXISTS `sepp_defect_period`;

CREATE TABLE `sepp_defect_period` (
  `period_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `period_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`period_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_priority` */

DROP TABLE IF EXISTS `sepp_defect_priority`;

CREATE TABLE `sepp_defect_priority` (
  `priority_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `priority_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`priority_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_refuse_reason` */

DROP TABLE IF EXISTS `sepp_defect_refuse_reason`;

CREATE TABLE `sepp_defect_refuse_reason` (
  `reason_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `reason_name` varchar(40) NOT NULL COMMENT '原因描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`reason_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_status` */

DROP TABLE IF EXISTS `sepp_defect_status`;

CREATE TABLE `sepp_defect_status` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_id` int(10) NOT NULL COMMENT '状态ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态名称',
  `new_status_id` int(10) NOT NULL COMMENT '允许的新状态ID',
  `new_status_name` varchar(40) NOT NULL COMMENT '允许的新状态名称',
  `status_tips` varchar(100) NOT NULL COMMENT '状态变迁说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_UNIQUE` (`status_id`,`new_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defect_type` */

DROP TABLE IF EXISTS `sepp_defect_type`;

CREATE TABLE `sepp_defect_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_defects` */

DROP TABLE IF EXISTS `sepp_defects`;

CREATE TABLE `sepp_defects` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '缺陷ID',
  `rel_id` int(10) DEFAULT NULL COMMENT '所属版本ID',
  `req_id` int(10) NOT NULL COMMENT '需求ID',
  `status` int(10) NOT NULL COMMENT '缺陷状态',
  `priority` int(10) NOT NULL COMMENT '优先级',
  `influence` int(10) NOT NULL COMMENT '影响程度',
  `submitter` int(10) NOT NULL COMMENT '发现者',
  `conciliator` int(10) NOT NULL COMMENT '缺陷协调人',
  `responser` int(10) DEFAULT NULL COMMENT '负责人',
  `defect_type` int(10) NOT NULL COMMENT '缺陷类型，如功能、性能、安全性等',
  `found_period` int(10) NOT NULL COMMENT '发现阶段，如系统测试',
  `defect_period` int(10) DEFAULT NULL COMMENT '实际应发现阶段，如单元测试',
  `found_means` int(10) NOT NULL COMMENT '发现手段，比如手动测试',
  `product_id` int(10) NOT NULL COMMENT '所属产品',
  `prod_module` int(10) DEFAULT NULL COMMENT '应用模块',
  `fix_times` int(10) DEFAULT 0 COMMENT '修复次数',
  `summary` varchar(50) NOT NULL COMMENT '缺陷摘要',
  `detail` text DEFAULT NULL COMMENT '详细信息',
  `found_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '发现时间',
  `response_time` datetime DEFAULT NULL COMMENT '响应时间',
  `fixed_time` datetime DEFAULT NULL COMMENT '解决时间',
  `deployed_time` datetime DEFAULT NULL COMMENT '部署时间',
  `closed_time` datetime DEFAULT NULL COMMENT '关闭时间',
  `response_cost` float DEFAULT NULL COMMENT '响应时长',
  `fix_cost` float DEFAULT NULL COMMENT '修复时长',
  `deploy_cost` float DEFAULT NULL COMMENT '部署时长',
  `verify_cost` float DEFAULT NULL COMMENT '验证时长',
  `refuse_reason` tinyint(2) DEFAULT NULL COMMENT '拒绝原因',
  `refuse_detail` varchar(500) DEFAULT NULL COMMENT '拒绝描述',
  `same_code_defect` int(11) DEFAULT NULL COMMENT '同源/重复缺陷号',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `index_found` (`found_time`),
  KEY `index_fixed` (`fixed_time`),
  KEY `index_deployed` (`deployed_time`),
  KEY `index_closed` (`closed_time`),
  KEY `index_release` (`rel_id`),
  KEY `index_req` (`req_id`),
  KEY `index_module` (`prod_module`),
  KEY `index_users` (`submitter`,`conciliator`,`responser`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_deployment_history` */

DROP TABLE IF EXISTS `sepp_deployment_history`;

CREATE TABLE `sepp_deployment_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `instance` varchar(100) NOT NULL COMMENT '实例名称',
  `job_name` varchar(100) DEFAULT NULL COMMENT 'job名称',
  `deploy_job_name` varchar(100) DEFAULT NULL,
  `env_type` int(10) DEFAULT NULL COMMENT '环境',
  `branch_id` int(10) DEFAULT NULL COMMENT '分支',
  `instance_type` varchar(100) DEFAULT NULL COMMENT '实例类型',
  `build_version` int(10) NOT NULL COMMENT '构建物版本',
  `deploy_version` int(10) DEFAULT NULL,
  `deploy_status` varchar(100) NOT NULL COMMENT '部署状态',
  `username` varchar(100) DEFAULT NULL COMMENT '部署人',
  `deploy_type` varchar(50) DEFAULT NULL COMMENT '部署类型',
  `pipeline_step` text DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_device` */

DROP TABLE IF EXISTS `sepp_device`;

CREATE TABLE `sepp_device` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `asset_id` varchar(20) DEFAULT NULL COMMENT '资产编码',
  `device_name` varchar(40) DEFAULT NULL COMMENT '设备名称',
  `brand` varchar(20) DEFAULT NULL COMMENT '设备品牌',
  `opr_sys` varchar(20) DEFAULT NULL COMMENT '系统类型',
  `model` varchar(40) DEFAULT NULL COMMENT '设备型号',
  `color` varchar(10) DEFAULT NULL COMMENT '颜色',
  `versions` varchar(20) DEFAULT NULL COMMENT '系统版本',
  `ram` int(10) DEFAULT NULL COMMENT '运行内存',
  `rom` int(10) DEFAULT NULL COMMENT '存储内存',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `user_name` varchar(20) DEFAULT NULL COMMENT '使用人',
  `controller` int(10) DEFAULT NULL,
  `rent_date` date DEFAULT NULL COMMENT '租借日期',
  `return_date` date DEFAULT NULL COMMENT '归还日期',
  `create_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_env_type` */

DROP TABLE IF EXISTS `sepp_env_type`;

CREATE TABLE `sepp_env_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_environment` */

DROP TABLE IF EXISTS `sepp_environment`;

CREATE TABLE `sepp_environment` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int(10) NOT NULL COMMENT '产品代码',
  `branch_id` varchar(50) DEFAULT NULL COMMENT '产品分支',
  `env_type` int(10) NOT NULL COMMENT '环境类型',
  `instance` varchar(50) DEFAULT NULL COMMENT '实例名称',
  `env_url` varchar(200) DEFAULT NULL COMMENT '环境URL',
  `job_name` varchar(500) DEFAULT NULL COMMENT '构建JOB的名称',
  `job_params` varchar(500) DEFAULT NULL COMMENT '构建JOB的参数名列表',
  `qr_code` int(10) DEFAULT NULL COMMENT '二维码文件ID',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_REPORT` (`product_id`,`branch_id`,`env_type`,`instance`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_found_means` */

DROP TABLE IF EXISTS `sepp_found_means`;

CREATE TABLE `sepp_found_means` (
  `means_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `means_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`means_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_found_period` */

DROP TABLE IF EXISTS `sepp_found_period`;

CREATE TABLE `sepp_found_period` (
  `period_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `period_name` varchar(40) NOT NULL COMMENT '状态描述',
  `period_alias` varchar(10) DEFAULT NULL COMMENT '别名',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`period_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_history` */

DROP TABLE IF EXISTS `sepp_history`;

CREATE TABLE `sepp_history` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `obj_type` int(10) NOT NULL COMMENT '对象类型',
  `obj_id` int(10) NOT NULL COMMENT '对象类型ID',
  `obj_key` varchar(20) DEFAULT NULL COMMENT '对象属性',
  `product_id` int(10) NOT NULL COMMENT '产品ID',
  `oper_user` int(10) NOT NULL COMMENT '操作用户ID',
  `oper_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '操作时间',
  `oper_type` int(10) NOT NULL COMMENT '操作类型，如新增、修改、删除',
  `refer_user` int(10) DEFAULT NULL COMMENT '涉及用户',
  `org_value` text DEFAULT NULL COMMENT '对象旧值',
  `new_value` text DEFAULT NULL COMMENT '对象新值',
  `oper_comment` text DEFAULT NULL COMMENT '操作说明',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_TIME` (`oper_time`),
  KEY `INDEX_OBJ` (`obj_type`,`obj_id`,`obj_key`),
  KEY `INDEX_OPER` (`oper_user`),
  KEY `INDEX_REFER` (`refer_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_menu` */

DROP TABLE IF EXISTS `sepp_menu`;

CREATE TABLE `sepp_menu` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `menu_icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `menu_index` varchar(50) NOT NULL COMMENT '路径或位置',
  `title` varchar(100) NOT NULL COMMENT '菜单名称',
  `role_ids` varchar(500) DEFAULT NULL COMMENT '角色',
  `parent_id` int(10) NOT NULL DEFAULT 0 COMMENT '菜单父id',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_message` */

DROP TABLE IF EXISTS `sepp_message`;

CREATE TABLE `sepp_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int(10) NOT NULL,
  `object_type` int(10) DEFAULT NULL COMMENT '消息对象类型',
  `object_id` int(10) DEFAULT NULL COMMENT '消息对象ID',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text DEFAULT NULL COMMENT '消息内容',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_message_flow` */

DROP TABLE IF EXISTS `sepp_message_flow`;

CREATE TABLE `sepp_message_flow` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` bigint(11) NOT NULL COMMENT '消息ID',
  `creator` int(11) NOT NULL COMMENT '创建人',
  `type` int(2) NOT NULL COMMENT '消息类型',
  `user_id` int(11) NOT NULL COMMENT '接收人',
  `is_sent` tinyint(1) NOT NULL DEFAULT 0,
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `created_date` timestamp NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NULL DEFAULT current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_MESSAGE` (`message_id`,`creator`,`type`,`user_id`),
  KEY `INDEX_SEND` (`user_id`,`is_read`,`creator`,`type`),
  KEY `INDEX_CREATED` (`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_message_gateway` */

DROP TABLE IF EXISTS `sepp_message_gateway`;

CREATE TABLE `sepp_message_gateway` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) NOT NULL COMMENT '消息类型名称',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_object_type` */

DROP TABLE IF EXISTS `sepp_object_type`;

CREATE TABLE `sepp_object_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `table_name` varchar(100) NOT NULL COMMENT '数据表名称',
  `router_to` varchar(50) DEFAULT NULL COMMENT '目标对象跳转链接',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_organization` */

DROP TABLE IF EXISTS `sepp_organization`;

CREATE TABLE `sepp_organization` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `parent_id` int(10) NOT NULL COMMENT '上级团队ID',
  `responser` int(10) NOT NULL COMMENT '团队负责人',
  `team_name` varchar(50) NOT NULL COMMENT '团队名称',
  `team_description` text DEFAULT NULL COMMENT '团队描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_pr_audit` */

DROP TABLE IF EXISTS `sepp_pr_audit`;

CREATE TABLE `sepp_pr_audit` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pr_id` int(10) NOT NULL COMMENT '产品需求ID',
  `formal_id` int(10) DEFAULT NULL COMMENT '审核完毕生成的正式需求ID',
  `submitter` int(10) NOT NULL COMMENT '送审人',
  `submit_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '送审时间',
  `audit_deadline` datetime NOT NULL DEFAULT current_timestamp() COMMENT '审核截止时间',
  `base_auditor` varchar(100) DEFAULT NULL COMMENT '基础审批人（列表）',
  `leader_auditor` varchar(100) DEFAULT NULL COMMENT '主管审批人（列表）',
  `chief_auditor` varchar(100) DEFAULT NULL COMMENT '高管审批人（列表）',
  `base_audit_result` varchar(2000) DEFAULT NULL COMMENT '基础审批信息',
  `leader_audit_result` varchar(2000) DEFAULT NULL COMMENT '主管审批信息',
  `chief_audit_result` varchar(2000) DEFAULT NULL COMMENT '高管审批信息',
  `complete_time` datetime DEFAULT current_timestamp() COMMENT '审核全部完成时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `INX_SUBMITTED` (`submit_time`),
  KEY `INX_COMPLETED` (`complete_time`),
  KEY `INX_PR_ID` (`pr_id`),
  KEY `INX_CREATED` (`created_date`),
  KEY `INX_UPDATED` (`updated_date`),
  KEY `INX_SUBMITTER` (`submitter`),
  KEY `INX_FORMAL` (`formal_id`),
  CONSTRAINT `BASE_CHECK` CHECK (json_valid(`base_audit_result`)),
  CONSTRAINT `LEADER_CHECK` CHECK (json_valid(`leader_audit_result`)),
  CONSTRAINT `CHIEF_CHECK` CHECK (json_valid(`chief_audit_result`))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_pr_status` */

DROP TABLE IF EXISTS `sepp_pr_status`;

CREATE TABLE `sepp_pr_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_privileges` */

DROP TABLE IF EXISTS `sepp_privileges`;

CREATE TABLE `sepp_privileges` (
  `priv_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(10) NOT NULL COMMENT 'USER ID',
  `role_id` int(10) DEFAULT NULL COMMENT '角色ID',
  `product_id` int(10) DEFAULT NULL COMMENT '产品ID',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`priv_id`),
  UNIQUE KEY `INDEX_PRODUCT` (`product_id`,`role_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_problem` */

DROP TABLE IF EXISTS `sepp_problem`;

CREATE TABLE `sepp_problem` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `status` int(10) NOT NULL COMMENT '问题状态',
  `priority` int(10) NOT NULL COMMENT '问题优先级',
  `influence` int(10) NOT NULL COMMENT '问题影响程度',
  `submitter` int(10) NOT NULL COMMENT '问题发现者',
  `responser` int(10) DEFAULT NULL COMMENT '问题负责人',
  `type_first` int(10) NOT NULL COMMENT '问题一级分类',
  `type_second` int(10) NOT NULL COMMENT '问题二级分类',
  `resolve_method` int(10) DEFAULT 1 COMMENT '问题处理方式',
  `trans_id` int(10) DEFAULT NULL COMMENT '问题转报对象ID',
  `product_id` int(10) NOT NULL COMMENT '问题所属产品',
  `module_id` int(10) DEFAULT NULL COMMENT '问题应用模块',
  `summary` varchar(50) NOT NULL COMMENT '问题摘要',
  `detail` text DEFAULT NULL COMMENT '问题详细信息',
  `attachment` varchar(200) DEFAULT NULL COMMENT '问题附件',
  `submit_time` datetime NOT NULL DEFAULT current_timestamp() COMMENT '问题发现时间',
  `expect_resolve_time` datetime NOT NULL COMMENT '期望解决时间',
  `resolve_time` datetime DEFAULT NULL COMMENT '问题解决时间',
  `close_time` datetime DEFAULT NULL COMMENT '问题关闭时间',
  `resolve_cost` float DEFAULT NULL COMMENT '问题解决时长',
  `improve_one` int(10) DEFAULT NULL COMMENT '改进措施一级分类',
  `improve_two` int(10) DEFAULT NULL COMMENT '改进措施二级分类',
  `improve_plan_to` date DEFAULT NULL COMMENT '改进措施计划完成日期',
  `improve_detail` text DEFAULT NULL COMMENT '改进措施详细说明',
  `refuse_reason` text DEFAULT NULL COMMENT '拒绝原因描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `inx_submit_time` (`submit_time`),
  KEY `inx_resolve_time` (`resolve_time`),
  KEY `inx_close_time` (`close_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_problem_improve` */

DROP TABLE IF EXISTS `sepp_problem_improve`;

CREATE TABLE `sepp_problem_improve` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int(10) NOT NULL COMMENT '问题类型ID',
  `type_desc` varchar(40) NOT NULL COMMENT '问题类型描述',
  `sub_type` int(10) DEFAULT NULL COMMENT '问题子类型ID',
  `sub_desc` varchar(40) DEFAULT NULL COMMENT '问题子类型描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_UNIQUE` (`type`,`sub_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_problem_resolve` */

DROP TABLE IF EXISTS `sepp_problem_resolve`;

CREATE TABLE `sepp_problem_resolve` (
  `method_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `method_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`method_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_problem_status` */

DROP TABLE IF EXISTS `sepp_problem_status`;

CREATE TABLE `sepp_problem_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_problem_type` */

DROP TABLE IF EXISTS `sepp_problem_type`;

CREATE TABLE `sepp_problem_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int(10) NOT NULL COMMENT '问题类型ID',
  `type_desc` varchar(40) NOT NULL COMMENT '问题类型描述',
  `sub_type` int(10) NOT NULL COMMENT '问题子类型ID',
  `sub_desc` varchar(40) NOT NULL COMMENT '问题子类型描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_UNIQUE` (`type`,`sub_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_prod_module` */

DROP TABLE IF EXISTS `sepp_prod_module`;

CREATE TABLE `sepp_prod_module` (
  `module_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '模块ID',
  `product_id` int(10) NOT NULL COMMENT '产品ID',
  `module_name` varchar(50) NOT NULL COMMENT '模块名称',
  `pd_responser` int(10) NOT NULL COMMENT '产品负责人',
  `dev_responser` int(10) NOT NULL COMMENT '开发负责人',
  `test_responser` int(10) NOT NULL COMMENT '测试负责人',
  `module_desc` text DEFAULT NULL COMMENT '模块描述',
  `code_package` text DEFAULT NULL COMMENT '代码块/package',
  `is_valid` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否有效',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_product` */

DROP TABLE IF EXISTS `sepp_product`;

CREATE TABLE `sepp_product` (
  `product_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_code` varchar(20) DEFAULT NULL COMMENT '产品代号',
  `owner` int(10) NOT NULL COMMENT '产品负责人',
  `product_name` varchar(40) NOT NULL COMMENT '产品名称',
  `product_desc` text DEFAULT NULL COMMENT '产品描述',
  `is_valid` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否有效',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `INDEX_NAME` (`product_name`),
  UNIQUE KEY `INDEX_CODE` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_product_branch` */

DROP TABLE IF EXISTS `sepp_product_branch`;

CREATE TABLE `sepp_product_branch` (
  `branch_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `branch_name` varchar(40) NOT NULL COMMENT '标记名称',
  `product_id` int(10) NOT NULL COMMENT '产品ID',
  `creator` int(10) NOT NULL COMMENT '创建人',
  `is_valid` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_product_config` */

DROP TABLE IF EXISTS `sepp_product_config`;

CREATE TABLE `sepp_product_config` (
  `product_id` int(10) NOT NULL COMMENT '产品ID',
  `member_config` varchar(1024) NOT NULL COMMENT '产品各负责人',
  `change_auditor` varchar(50) NOT NULL DEFAULT '1,2,3' COMMENT '变更确认角色',
  `gompertz_define` varchar(256) NOT NULL DEFAULT '{"minTestPeriod":3,"minDefectCount":5,"latestOffsetMonth":24}' COMMENT 'Gompertz模型筛选定义的采样版本配置',
  `gompertz_params` varchar(256) NOT NULL DEFAULT '{"k":4.822428016262498,"a":-6.215482818878401,"b":0.6164230084786425,"m":20.0}' COMMENT 'Gompertz模型的参数',
  `dre_target` float NOT NULL DEFAULT 99.95 COMMENT 'DRE-缺陷消除率目标值',
  `qa_warning` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否打开质量预警计算',
  `code_repository` varchar(500) DEFAULT NULL COMMENT '代码仓库URL，默认继承自系统配置',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  UNIQUE KEY `INX_PRODUCT_ID` (`product_id`),
  CONSTRAINT `MEMBER_CHECK` CHECK (json_valid(`member_config`)),
  CONSTRAINT `GOM_DEFINE` CHECK (json_valid(`gompertz_define`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_product_docs` */

DROP TABLE IF EXISTS `sepp_product_docs`;

CREATE TABLE `sepp_product_docs` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `module_id` int(11) NOT NULL COMMENT '模块ID',
  `label` varchar(200) NOT NULL COMMENT '展示名称',
  `type` varchar(10) NOT NULL COMMENT '类型：文档，目录',
  `version` varchar(50) DEFAULT NULL COMMENT '所属大版本',
  `parent_id` int(11) NOT NULL COMMENT '父目录ID',
  `attachment_id` int(11) DEFAULT NULL COMMENT '附件ID',
  `maintain_user` int(11) NOT NULL COMMENT '维护用户',
  `keyword` varchar(100) DEFAULT NULL COMMENT '关键字',
  `summary` text DEFAULT NULL COMMENT '文档摘要',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_PROD_MOD` (`product_id`,`module_id`),
  KEY `INDEX_PARENT` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_product_requirement` */

DROP TABLE IF EXISTS `sepp_product_requirement`;

CREATE TABLE `sepp_product_requirement` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '产品需求ID',
  `submit_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '提交日期',
  `expect_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '期望完成日期',
  `status` int(10) NOT NULL COMMENT '需求状态',
  `priority` int(1) NOT NULL DEFAULT 2 COMMENT '需求优先级',
  `module_id` int(10) DEFAULT NULL COMMENT '所属模块ID',
  `type` int(10) NOT NULL COMMENT '需求类型',
  `submitter` int(10) NOT NULL COMMENT '提交人',
  `product_id` int(10) NOT NULL COMMENT '所属产品',
  `attachment` varchar(1000) DEFAULT NULL COMMENT '负责人',
  `summary` varchar(500) NOT NULL COMMENT '产品需求摘要',
  `ui_resource` varchar(500) DEFAULT NULL COMMENT 'UED原型链接',
  `refuse_times` int(10) NOT NULL DEFAULT 0 COMMENT '审核拒绝次数',
  `detail` text DEFAULT NULL COMMENT '产品需求详情',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `INX_SUBMITTED` (`submit_date`),
  KEY `INX_EXPECTED` (`expect_date`),
  KEY `INX_CREATED` (`created_date`),
  KEY `INX_UPDATED` (`updated_date`),
  KEY `INX_SUBMITTER` (`submitter`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_blob_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_blob_triggers`;

CREATE TABLE `sepp_qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `BLOB_DATA` blob DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `sepp_qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `sepp_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_calendars` */

DROP TABLE IF EXISTS `sepp_qrtz_calendars`;

CREATE TABLE `sepp_qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(190) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_cron_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_cron_triggers`;

CREATE TABLE `sepp_qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `sepp_qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `sepp_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_fired_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_fired_triggers`;

CREATE TABLE `sepp_qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `INSTANCE_NAME` varchar(190) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(190) DEFAULT NULL,
  `JOB_GROUP` varchar(190) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_SQCS_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_SQCS_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_SQCS_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SQCS_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_SQCS_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SQCS_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_job_details` */

DROP TABLE IF EXISTS `sepp_qrtz_job_details`;

CREATE TABLE `sepp_qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(190) NOT NULL,
  `JOB_GROUP` varchar(190) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SQCS_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_SQCS_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_locks` */

DROP TABLE IF EXISTS `sepp_qrtz_locks`;

CREATE TABLE `sepp_qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_paused_trigger_grps` */

DROP TABLE IF EXISTS `sepp_qrtz_paused_trigger_grps`;

CREATE TABLE `sepp_qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_scheduler_state` */

DROP TABLE IF EXISTS `sepp_qrtz_scheduler_state`;

CREATE TABLE `sepp_qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(190) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_simple_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_simple_triggers`;

CREATE TABLE `sepp_qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `sepp_qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `sepp_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_simprop_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_simprop_triggers`;

CREATE TABLE `sepp_qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `sepp_qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `sepp_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_qrtz_triggers` */

DROP TABLE IF EXISTS `sepp_qrtz_triggers`;

CREATE TABLE `sepp_qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(190) NOT NULL,
  `TRIGGER_GROUP` varchar(190) NOT NULL,
  `JOB_NAME` varchar(190) NOT NULL,
  `JOB_GROUP` varchar(190) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SQCS_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_SQCS_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_SQCS_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_SQCS_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_SQCS_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_SQCS_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_SQCS_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_SQCS_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_SQCS_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_SQCS_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_SQCS_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_SQCS_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `sepp_qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `sepp_qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_rel_status` */

DROP TABLE IF EXISTS `sepp_rel_status`;

CREATE TABLE `sepp_rel_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_relate_type` */

DROP TABLE IF EXISTS `sepp_relate_type`;

CREATE TABLE `sepp_relate_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_release` */

DROP TABLE IF EXISTS `sepp_release`;

CREATE TABLE `sepp_release` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rel_code` varchar(50) NOT NULL COMMENT '版本号',
  `branch_id` int(10) DEFAULT NULL COMMENT '版本分支标记',
  `status` int(10) NOT NULL COMMENT '状态',
  `creator` int(10) NOT NULL COMMENT '创建人',
  `responser` int(10) NOT NULL COMMENT '负责人',
  `product_id` int(10) NOT NULL COMMENT '所属产品',
  `rel_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '发布时间',
  `req_confirm_date` date DEFAULT NULL COMMENT '需求确认时间',
  `sit_begin_date` date DEFAULT NULL COMMENT '系统测试开始时间',
  `uat_begin_date` date DEFAULT NULL COMMENT '产品验收开始时间',
  `ready_date` date DEFAULT NULL COMMENT '封版时间',
  `rel_date_act` date DEFAULT NULL COMMENT '实际发布时间',
  `environment` varchar(100) DEFAULT NULL COMMENT '版本说明',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INX_REL_CODE` (`rel_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_release_note` */

DROP TABLE IF EXISTS `sepp_release_note`;

CREATE TABLE `sepp_release_note` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `req_id` int(10) NOT NULL COMMENT '产品需求号',
  `status` int(10) NOT NULL COMMENT '构建状态',
  `description` text DEFAULT NULL COMMENT '发布描述',
  `submitter` int(11) NOT NULL COMMENT '发布提交人',
  `files` text DEFAULT NULL COMMENT '更新文件',
  `others` text DEFAULT NULL COMMENT '配置及备注',
  `attachment` varchar(100) DEFAULT NULL COMMENT '附件',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `INDEX_REQ` (`req_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_releasenote_status` */

DROP TABLE IF EXISTS `sepp_releasenote_status`;

CREATE TABLE `sepp_releasenote_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_report_type` */

DROP TABLE IF EXISTS `sepp_report_type`;

CREATE TABLE `sepp_report_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_req_change` */

DROP TABLE IF EXISTS `sepp_req_change`;

CREATE TABLE `sepp_req_change` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `req_id` int(10) NOT NULL COMMENT '产品需求ID',
  `change_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '变更提交日期',
  `change_status` int(10) NOT NULL COMMENT '变更状态',
  `change_user` int(10) NOT NULL COMMENT '变更提交人',
  `audit_user` varchar(500) DEFAULT NULL COMMENT '变更确认人',
  `auditted_user` varchar(500) DEFAULT NULL COMMENT '已确认人',
  `change_desc` varchar(500) NOT NULL COMMENT '变更描述',
  `change_detail` varchar(4000) NOT NULL COMMENT '变更详情',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INX_REQ_ID` (`req_id`),
  KEY `INX_CHANGE_DATE` (`change_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_req_close_style` */

DROP TABLE IF EXISTS `sepp_req_close_style`;

CREATE TABLE `sepp_req_close_style` (
  `style_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `style_name` varchar(40) NOT NULL COMMENT '方式名',
  `tips` varchar(100) NOT NULL COMMENT '描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`style_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_req_priority` */

DROP TABLE IF EXISTS `sepp_req_priority`;

CREATE TABLE `sepp_req_priority` (
  `priority_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `priority_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`priority_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_req_status` */

DROP TABLE IF EXISTS `sepp_req_status`;

CREATE TABLE `sepp_req_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_req_type` */

DROP TABLE IF EXISTS `sepp_req_type`;

CREATE TABLE `sepp_req_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_requirement` */

DROP TABLE IF EXISTS `sepp_requirement`;

CREATE TABLE `sepp_requirement` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '产品需求ID',
  `source_id` int(10) NOT NULL DEFAULT 0 COMMENT '需求源ID',
  `product_id` int(10) NOT NULL COMMENT '所属产品',
  `module_id` int(10) DEFAULT NULL COMMENT '所属模块ID',
  `rel_id` int(10) DEFAULT NULL COMMENT '纳入版本号',
  `type` int(10) NOT NULL COMMENT '需求类型',
  `status` int(10) NOT NULL COMMENT '需求状态',
  `priority` int(10) NOT NULL COMMENT '需求优先级',
  `submitter` int(10) NOT NULL COMMENT '提交人',
  `submit_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '提交日期',
  `expect_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '期望完成日期',
  `sit_date` date DEFAULT NULL COMMENT '计划SIT开始时间',
  `uat_date` date DEFAULT NULL COMMENT '计划UAT开始时间',
  `summary` varchar(500) NOT NULL COMMENT '产品需求摘要',
  `ui_resource` varchar(500) DEFAULT NULL COMMENT 'UED原型链接',
  `attachment` varchar(1000) DEFAULT NULL COMMENT '附件ID',
  `detail` text DEFAULT NULL COMMENT '产品需求详情',
  `close_style` tinyint(1) DEFAULT NULL COMMENT '关闭方式',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INX_SUBMITTED` (`submit_date`),
  KEY `INX_EXPECTED` (`expect_date`),
  KEY `INX_CREATED` (`updated_date`),
  KEY `INX_UPDATED` (`created_date`),
  KEY `INX_SUBMITTER` (`submitter`),
  KEY `INX_SOURCE` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_resource_config` */

DROP TABLE IF EXISTS `sepp_resource_config`;

CREATE TABLE `sepp_resource_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `resource_desc` varchar(100) NOT NULL COMMENT '资源描述',
  `component_name` varchar(20) NOT NULL COMMENT '前端-组件名',
  `auth_id` varchar(20) NOT NULL COMMENT '前端-权限组件id',
  `request_url` varchar(200) NOT NULL COMMENT '接口url',
  `user` varchar(20) DEFAULT NULL,
  `request_method` varchar(10) NOT NULL COMMENT '接口请求方式',
  `role_id` int(10) NOT NULL COMMENT '角色id',
  `product_id` int(10) DEFAULT NULL COMMENT '产品ID',
  `is_valid` tinyint(1) DEFAULT 1 COMMENT '是否有效',
  `access_frequency` tinyint(1) DEFAULT 0,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_COMPONENT_AUTH` (`component_name`,`auth_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_setting_config` */

DROP TABLE IF EXISTS `sepp_setting_config`;

CREATE TABLE `sepp_setting_config` (
  `id` int(2) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `setting_type` varchar(20) NOT NULL COMMENT '配置类型标识',
  `setting_name` varchar(20) NOT NULL COMMENT '配置类型展示名称',
  `setting_limit` int(2) NOT NULL COMMENT '配置项个数限制',
  `setting_keys` varchar(1024) NOT NULL COMMENT '配置项参数KEY',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_settings` */

DROP TABLE IF EXISTS `sepp_settings`;

CREATE TABLE `sepp_settings` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_id` int(2) NOT NULL COMMENT '配置类型ID',
  `setting_value` varchar(4096) NOT NULL COMMENT '配置内容，JSON格式',
  `create_user` int(10) NOT NULL COMMENT '操作用户',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INX_TYPE` (`config_id`),
  CONSTRAINT `JSON_CHECK` CHECK (json_valid(`setting_value`))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_sonar_result` */

DROP TABLE IF EXISTS `sepp_sonar_result`;

CREATE TABLE `sepp_sonar_result` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `project_key` varchar(50) NOT NULL COMMENT '项目key',
  `project_version` varchar(50) DEFAULT NULL COMMENT '项目sonar扫描版本',
  `scan_date` varchar(50) DEFAULT NULL COMMENT '时间',
  `analysis_id` varchar(50) DEFAULT NULL COMMENT 'analysisId',
  `analysis_status` varchar(10) DEFAULT NULL COMMENT '分析结论',
  `ncloc` int(10) DEFAULT NULL COMMENT '代码行数',
  `coverage` float DEFAULT NULL COMMENT '覆盖率',
  `hotspots` int(11) DEFAULT NULL COMMENT '命中热点',
  `duplicated_lines_density` float DEFAULT NULL COMMENT '重复率',
  `code_smells` int(10) DEFAULT NULL COMMENT '代码异味',
  `bugs` int(10) DEFAULT NULL COMMENT '缺陷',
  `vulnerabilities` int(10) DEFAULT NULL COMMENT '漏洞',
  `sqale_index` int(10) DEFAULT NULL COMMENT '技术债',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_sonar_scan` */

DROP TABLE IF EXISTS `sepp_sonar_scan`;

CREATE TABLE `sepp_sonar_scan` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `note_id` int(10) DEFAULT NULL COMMENT '构建id',
  `product_id` int(10) DEFAULT NULL COMMENT '产品编号',
  `submitter` int(10) DEFAULT NULL COMMENT '提交人',
  `instance` VARCHAR(40) DEFAULT NULL COMMENT '实例名称',
  `project_key` varchar(40) DEFAULT NULL COMMENT '项目Key',
  `git_branch` varchar(50) DEFAULT NULL COMMENT 'git分支',
  `project_version` varchar(40) DEFAULT NULL COMMENT '版本',
  `start_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '开始时间',
  `result_id` int(10) DEFAULT NULL COMMENT '结果集id',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_means` */

DROP TABLE IF EXISTS `sepp_test_means`;

CREATE TABLE `sepp_test_means` (
  `means_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `means_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`means_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_mission` */

DROP TABLE IF EXISTS `sepp_test_mission`;

CREATE TABLE `sepp_test_mission` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '测试任务ID',
  `req_id` int(10) NOT NULL COMMENT '所属需求ID',
  `plan_id` int(10) DEFAULT NULL COMMENT '测试计划ID',
  `type` int(10) NOT NULL COMMENT '测试任务类型',
  `status` int(10) NOT NULL COMMENT '任务状态',
  `spliter` int(10) NOT NULL COMMENT '任务拆分人',
  `split_date` date NOT NULL DEFAULT '1970-01-01' COMMENT '任务拆分日期',
  `responser` int(10) NOT NULL COMMENT '任务负责人',
  `assistant` varchar(100) DEFAULT NULL COMMENT '任务参与人',
  `manpower` float NOT NULL DEFAULT 0 COMMENT '所需人力（人日）',
  `plan_begin` date NOT NULL COMMENT '计划开始日期',
  `plan_to` date NOT NULL COMMENT '计划完成日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INX_SPLITED` (`split_date`),
  KEY `INX_REQ_ID` (`req_id`),
  KEY `INX_PLANDATE` (`plan_begin`,`plan_to`),
  KEY `INX_PLAN` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_period` */

DROP TABLE IF EXISTS `sepp_test_period`;

CREATE TABLE `sepp_test_period` (
  `period_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `period_name` varchar(40) NOT NULL COMMENT '产品名称',
  `alias` varchar(10) DEFAULT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`period_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_plan` */

DROP TABLE IF EXISTS `sepp_test_plan`;

CREATE TABLE `sepp_test_plan` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '测试计划 ID',
  `rel_id` int(10) NOT NULL COMMENT '版本号',
  `submitter` int(10) NOT NULL COMMENT '计划创建人',
  `responser` int(10) NOT NULL COMMENT '测试计划负责人',
  `plan_status` int(10) NOT NULL COMMENT '测试计划状态:1打开，0关闭',
  `plan_type` int(10) NOT NULL COMMENT '测试计划类型',
  `plan_begin` date DEFAULT NULL COMMENT '测试开始时间',
  `plan_end` date DEFAULT NULL COMMENT '测试结束时间',
  `report_dates` varchar(200) DEFAULT NULL COMMENT '测试进度报告时间点，datetime格式，以逗号分隔',
  `email_to` varchar(500) DEFAULT NULL COMMENT '邮件主送',
  `email_cc` varchar(500) DEFAULT NULL COMMENT '邮件抄送',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_REL_ID` (`rel_id`,`plan_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_priority` */

DROP TABLE IF EXISTS `sepp_test_priority`;

CREATE TABLE `sepp_test_priority` (
  `priority_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `priority_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`priority_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_report` */

DROP TABLE IF EXISTS `sepp_test_report`;

CREATE TABLE `sepp_test_report` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rel_id` int(10) NOT NULL COMMENT '版本号ID',
  `plan_id` int(10) DEFAULT NULL COMMENT '计划号ID',
  `report_type` int(10) NOT NULL COMMENT '报告类型',
  `report_date` timestamp NULL DEFAULT NULL COMMENT '报告时间',
  `title` varchar(100) NOT NULL COMMENT '报告标题',
  `emergency_plan` varchar(500) DEFAULT NULL COMMENT '应急预案',
  `sqa_suggestion` varchar(500) DEFAULT NULL COMMENT '分析建议',
  `url` varchar(100) DEFAULT NULL COMMENT '存储位置',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_REPORT` (`rel_id`,`report_type`,`report_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_result` */

DROP TABLE IF EXISTS `sepp_test_result`;

CREATE TABLE `sepp_test_result` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scenario_id` int(10) NOT NULL COMMENT '测试场景/集合ID',
  `run_id` int(10) NOT NULL COMMENT '测试运行ID',
  `case_id` int(10) NOT NULL COMMENT '测试用例ID',
  `step_id` int(10) DEFAULT NULL COMMENT '测试用例ID',
  `step_actual` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '步骤运行的实际结果',
  `run_date` datetime NOT NULL DEFAULT current_timestamp() COMMENT '运行时间',
  `result` int(10) DEFAULT NULL COMMENT '运行结论',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_RUN` (`run_id`),
  KEY `INDEX_SCEN` (`scenario_id`),
  KEY `INDEX_CASE` (`case_id`,`step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_result_status` */

DROP TABLE IF EXISTS `sepp_test_result_status`;

CREATE TABLE `sepp_test_result_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_run` */

DROP TABLE IF EXISTS `sepp_test_run`;

CREATE TABLE `sepp_test_run` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scenario_id` int(10) NOT NULL COMMENT '测试集/场景ID',
  `run_date_s` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '运行开始时间',
  `run_date_e` timestamp NULL DEFAULT NULL COMMENT '运行结束时间',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_SCENARIO` (`scenario_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_scenario` */

DROP TABLE IF EXISTS `sepp_test_scenario`;

CREATE TABLE `sepp_test_scenario` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '场景ID',
  `name` varchar(50) NOT NULL COMMENT '场景名称',
  `creator` int(10) NOT NULL COMMENT '创建人',
  `plan_id` int(10) NOT NULL COMMENT '测试计划ID',
  `cases` text DEFAULT NULL COMMENT '测试用例ID集合',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_CASE_INDEX` (`plan_id`),
  KEY `INDEX_CREATE` (`created_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_status` */

DROP TABLE IF EXISTS `sepp_test_status`;

CREATE TABLE `sepp_test_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_test_type` */

DROP TABLE IF EXISTS `sepp_test_type`;

CREATE TABLE `sepp_test_type` (
  `type_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_tm_status` */

DROP TABLE IF EXISTS `sepp_tm_status`;

CREATE TABLE `sepp_tm_status` (
  `status_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `status_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_user` */

DROP TABLE IF EXISTS `sepp_user`;

CREATE TABLE `sepp_user` (
  `user_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_account` varchar(50) NOT NULL COMMENT '用户账号',
  `password` varchar(200) DEFAULT NULL COMMENT '用户密码',
  `user_name` varchar(50) NOT NULL COMMENT '用户姓名',
  `user_email` varchar(100) NOT NULL COMMENT '用户电子邮件地址',
  `favicon_id` int(10) DEFAULT 543 COMMENT '头像文件ID',
  `team_id` int(10) DEFAULT NULL COMMENT '所属团队ID',
  `is_valid` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否有效',
  `is_vendor` varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否外包',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `INDEX_ACCOUNT` (`user_account`),
  UNIQUE KEY `INDEX_EMAIL` (`user_email`),
  UNIQUE KEY `INDEX_NAME` (`user_name`),
  KEY `INDEX_TEAM` (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_user_role` */

DROP TABLE IF EXISTS `sepp_user_role`;

CREATE TABLE `sepp_user_role` (
  `role_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(10) NOT NULL COMMENT '角色代码',
  `role_name` varchar(20) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `is_valid` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否有效',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `INDEX_ROLE` (`role_code`),
  UNIQUE KEY `INDEX_ROLE_NAME` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_user_setting` */

DROP TABLE IF EXISTS `sepp_user_setting`;

CREATE TABLE `sepp_user_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(10) NOT NULL COMMENT '用户ID',
  `message_on` tinyint(1) NOT NULL DEFAULT 1 COMMENT '打开消息提示',
  `message_subscribe` varchar(200) NOT NULL DEFAULT '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21' COMMENT '用户消息订阅',
  `email_subscribe` varchar(200) NOT NULL DEFAULT '1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21' COMMENT '用户邮件订阅',
  `portal_config` varchar(50) NOT NULL DEFAULT '1,3,4,5,6,7' COMMENT '工作台选项卡展示',
  `dialog_auto_close` tinyint(1) NOT NULL DEFAULT 1 COMMENT '点击空白处自动关闭对话框',
  `auto_login` tinyint(1) NOT NULL DEFAULT 0 COMMENT '自动登录上次的项目',
  `auto_refresh` tinyint(1) NOT NULL DEFAULT 1 COMMENT '侧边栏折叠和展开时echarts页面布局',
  `table_show_border` tinyint(1) NOT NULL DEFAULT 0 COMMENT '表格是否展示边框',
  `table_page_size` int(3) NOT NULL DEFAULT 20 COMMENT '表格每页最多展示记录数',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_USER` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning` */

DROP TABLE IF EXISTS `sepp_warning`;

CREATE TABLE `sepp_warning` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `batch_id` bigint(20) NOT NULL COMMENT '批次号，记录timestamp',
  `product_id` int(10) NOT NULL COMMENT '产品号',
  `type` int(1) NOT NULL COMMENT '告警类型，如缺陷修复异常、缺陷分布异常',
  `sub_type` int(1) NOT NULL COMMENT '告警子类型',
  `category` varchar(50) NOT NULL COMMENT '告警归属，如版本号、需求号等',
  `warning_date` date NOT NULL COMMENT '告警日期',
  `level` int(1) NOT NULL COMMENT '告警级别，如严重、一般等',
  `responser` int(10) DEFAULT NULL COMMENT '告警归属负责人',
  `summary` varchar(100) NOT NULL COMMENT '告警摘要',
  `content` text DEFAULT NULL COMMENT '告警详细说明',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_BATCH` (`batch_id`),
  KEY `INDEX_WARN` (`batch_id`,`type`,`sub_type`,`category`,`warning_date`),
  KEY `INDEX_DATE` (`warning_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning_batch` */

DROP TABLE IF EXISTS `sepp_warning_batch`;

CREATE TABLE `sepp_warning_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `batch_no` bigint(20) NOT NULL COMMENT '批次号，记录timestamp',
  `warning_date` date NOT NULL COMMENT '告警日期',
  `category` varchar(50) NOT NULL COMMENT '告警归属实例，如版本号',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `INDEX_BATCH` (`batch_no`,`warning_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning_level` */

DROP TABLE IF EXISTS `sepp_warning_level`;

CREATE TABLE `sepp_warning_level` (
  `level_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `level_name` varchar(40) NOT NULL COMMENT '状态描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning_notify` */

DROP TABLE IF EXISTS `sepp_warning_notify`;

CREATE TABLE `sepp_warning_notify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `warning_id` bigint(20) NOT NULL COMMENT '告警ID',
  `send_gateway` int(2) NOT NULL COMMENT '告警发送类型：邮件、即时通讯、实时推送',
  `to` int(10) NOT NULL COMMENT '接收人',
  `is_sent` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否已发送',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_MESSAGE` (`warning_id`,`send_gateway`,`to`),
  KEY `INDEX_TO_SEND` (`send_gateway`,`is_sent`,`to`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning_rules` */

DROP TABLE IF EXISTS `sepp_warning_rules`;

CREATE TABLE `sepp_warning_rules` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int(10) NOT NULL COMMENT '告警类型',
  `sub_type` int(10) NOT NULL COMMENT '告警子类型',
  `level` int(10) NOT NULL COMMENT '告警级别',
  `target_type` int(10) DEFAULT NULL COMMENT '告警目标对象类型',
  `title` varchar(500) DEFAULT NULL COMMENT '告警说明',
  `expression` text DEFAULT NULL COMMENT '告警规则表达式',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '更新日期',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sepp_warning_type` */

DROP TABLE IF EXISTS `sepp_warning_type`;

CREATE TABLE `sepp_warning_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int(10) NOT NULL COMMENT '告警类型ID',
  `type_desc` varchar(40) NOT NULL COMMENT '告警类型描述',
  `sub_type` int(10) NOT NULL COMMENT '告警子类型ID',
  `sub_desc` varchar(40) NOT NULL COMMENT '告警子类型描述',
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建日期',
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `INDEX_UNIQUE` (`type`,`sub_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

set @@global.foreign_key_checks = 1;