-- =================================================================================
-- 数据库初始化脚本 - 企业级 SaaS 图书管理系统
-- =================================================================================

CREATE DATABASE IF NOT EXISTS `libra_bms` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `libra_bms`;

-- 1. 租户管理表
CREATE TABLE IF NOT EXISTS `sys_tenant` (
    `id` BIGINT NOT NULL COMMENT '租户ID',
    `tenant_name` VARCHAR(100) NOT NULL COMMENT '租户名称',
    `contact_user` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (0:禁用, 1:启用)',
    `expire_time` DATETIME DEFAULT NULL COMMENT '到期时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- 2. 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `wechat_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (0:禁用, 1:启用)',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username_tenant` (`username`, `tenant_id`),
    INDEX `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 系统角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL COMMENT '角色ID',
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色权限标识',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据范围 (1:全部数据, 2:本部门及以下, 3:仅本人)',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 4. 权限菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '菜单ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `menu_type` CHAR(1) DEFAULT 'M' COMMENT '类型 (M:目录, C:菜单, F:按钮)',
    `icon` VARCHAR(50) DEFAULT '#' COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 5. 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6. 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 7. 审计日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id` BIGINT NOT NULL COMMENT '日志ID',
    `tenant_id` BIGINT DEFAULT NULL,
    `user_id` BIGINT DEFAULT NULL,
    `username` VARCHAR(50) DEFAULT NULL,
    `module` VARCHAR(100) DEFAULT NULL COMMENT '业务模块',
    `operation` VARCHAR(255) DEFAULT NULL COMMENT '操作内容',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `result` TEXT DEFAULT NULL COMMENT '返回结果',
    `status` TINYINT DEFAULT 1 COMMENT '执行状态 (1:成功, 0:失败)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `execution_time` BIGINT DEFAULT NULL COMMENT '执行耗时(ms)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 8. 图书主数据表
CREATE TABLE IF NOT EXISTS `lib_book` (
    `id` BIGINT NOT NULL COMMENT '图书ID',
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN',
    `title` VARCHAR(255) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `pub_date` DATE DEFAULT NULL COMMENT '出版日期',
    `price` DECIMAL(10, 2) DEFAULT NULL COMMENT '标价',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面地址',
    `summary` TEXT DEFAULT NULL COMMENT '简介',
    `total_count` INT DEFAULT 0 COMMENT '总册数',
    `stock_count` INT DEFAULT 0 COMMENT '在馆册数',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:上架, 0:下架)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn_tenant` (`isbn`, `tenant_id`),
    INDEX `idx_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书主数据表';

-- 9. 图书馆藏表 (多副本管理)
CREATE TABLE IF NOT EXISTS `lib_inventory` (
    `id` BIGINT NOT NULL COMMENT '馆藏ID (条码号)',
    `tenant_id` BIGINT NOT NULL,
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '存放架位',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:在馆, 2:借出, 3:损坏, 4:丢失, 5:报废)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书馆藏表';

-- 10. 借阅记录表
CREATE TABLE IF NOT EXISTS `lib_borrow_record` (
    `id` BIGINT NOT NULL COMMENT '流水ID',
    `tenant_id` BIGINT NOT NULL,
    `inventory_id` BIGINT NOT NULL COMMENT '馆藏ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `user_id` BIGINT NOT NULL COMMENT '借阅人ID',
    `borrow_time` DATETIME NOT NULL COMMENT '借阅时间',
    `due_time` DATETIME NOT NULL COMMENT '应还时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '归还时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:借阅中, 2:已归还, 3:已逾期, 4:已赔付)',
    `fine_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '罚金金额',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_inventory_id` (`inventory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 11. 业务配置表 (规则引擎)
CREATE TABLE IF NOT EXISTS `lib_config` (
    `id` BIGINT NOT NULL,
    `tenant_id` BIGINT NOT NULL,
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key_tenant` (`config_key`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统业务配置表';


-- 12. 图书分类表（基于中图法22大类）
CREATE TABLE IF NOT EXISTS `lib_category` (
    `id` BIGINT NOT NULL COMMENT '分类ID',
    `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID（0表示系统级分类）',
    `category_code` VARCHAR(10) NOT NULL COMMENT '分类代码（中图法）',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID（0表示顶级分类）',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_code` (`category_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 13. 预约表
CREATE TABLE IF NOT EXISTS `lib_reservation` (
    `id` BIGINT NOT NULL COMMENT '预约ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态(0:等待中,1:已通知,2:已取消)',
    `queue_no` INT DEFAULT NULL COMMENT '排队号',
    `expected_date` DATE DEFAULT NULL COMMENT '预计到馆日期',
    `pickup_deadline` DATE DEFAULT NULL COMMENT '取书截止日期',
    `notified` TINYINT DEFAULT 0 COMMENT '是否已通知',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 插入中图法22大类基础分类数据
INSERT IGNORE INTO `lib_category` (`id`, `tenant_id`, `category_code`, `category_name`, `parent_id`, `sort_order`, `description`) VALUES
(1, 0, 'A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 0, 1, '马克思主义经典著作、毛泽东思想、邓小平理论等'),
(2, 0, 'B', '哲学、宗教', 0, 2, '哲学理论、世界哲学、中国哲学、宗教等'),
(3, 0, 'C', '社会科学总论', 0, 3, '社会科学理论与方法论、社会学、人口学、管理学等'),
(4, 0, 'D', '政治、法律', 0, 4, '政治理论、中国共产党、各国政治、法律等'),
(5, 0, 'E', '军事', 0, 5, '军事理论、中国军事、各国军事、战略战术等'),
(6, 0, 'F', '经济', 0, 6, '经济学、世界各国经济、经济计划与管理、贸易经济、财政金融等'),
(7, 0, 'G', '文化、科学、教育、体育', 0, 7, '文化理论、信息与知识传播、科学、教育、体育等'),
(8, 0, 'H', '语言、文字', 0, 8, '语言学、汉语、中国少数民族语言、常用外国语等'),
(9, 0, 'I', '文学', 0, 9, '文学理论、世界文学、中国文学、各国文学等'),
(10, 0, 'J', '艺术', 0, 10, '艺术理论、绘画、书法、雕塑、摄影、音乐、舞蹈、戏剧、电影等'),
(11, 0, 'K', '历史、地理', 0, 11, '史学理论、世界史、中国史、传记、地理等'),
(12, 0, 'N', '自然科学总论', 0, 12, '自然科学理论与方法论、自然科学现状与发展等'),
(13, 0, 'O', '数理科学和化学', 0, 13, '数学、力学、物理学、化学等'),
(14, 0, 'P', '天文学、地球科学', 0, 14, '天文学、测绘学、地球物理学、气象学、地质学等'),
(15, 0, 'Q', '生物科学', 0, 15, '普通生物学、细胞学、遗传学、生理学、生物化学等'),
(16, 0, 'R', '医药、卫生', 0, 16, '预防医学、卫生学、中国医学、基础医学、临床医学等'),
(17, 0, 'S', '农业科学', 0, 17, '农业基础科学、农业工程、农学、林业、畜牧、水产等'),
(18, 0, 'T', '工业技术', 0, 18, '一般工业技术、矿业工程、石油天然气、冶金、机械、电子、计算机等'),
(19, 0, 'U', '交通运输', 0, 19, '综合运输、铁路运输、公路运输、水路运输、航空运输等'),
(20, 0, 'V', '航空、航天', 0, 20, '航空、航天技术、航天器、航天工程等'),
(21, 0, 'X', '环境科学、安全科学', 0, 21, '环境科学基础理论、环境工程、安全科学等'),
(22, 0, 'Z', '综合性图书', 0, 22, '丛书、百科全书、辞典、年鉴、期刊、连续性出版物等');
