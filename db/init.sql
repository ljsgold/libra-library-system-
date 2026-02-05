-- =================================================================================
-- 数据库初始化脚本 - 企业级 SaaS 图书管理系统
-- =================================================================================

CREATE DATABASE IF NOT EXISTS `libra_bms` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `libra_bms`;

-- 1. 租户管理表
CREATE TABLE `sys_tenant` (
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
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '所属租户',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
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
CREATE TABLE `sys_role` (
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
CREATE TABLE `sys_menu` (
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
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6. 用户角色关联表
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 7. 审计日志表
CREATE TABLE `sys_log` (
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
CREATE TABLE `lib_book` (
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
CREATE TABLE `lib_inventory` (
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
CREATE TABLE `lib_borrow_record` (
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
CREATE TABLE `lib_config` (
    `id` BIGINT NOT NULL,
    `tenant_id` BIGINT NOT NULL,
    `config_key` VARCHAR(50) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key_tenant` (`config_key`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统业务配置表';


-- 12. ?????
CREATE TABLE `lib_reservation` (
    `id` BIGINT NOT NULL COMMENT '??ID',
    `tenant_id` BIGINT NOT NULL COMMENT '??ID',
    `user_id` BIGINT NOT NULL COMMENT '??ID',
    `book_id` BIGINT NOT NULL COMMENT '??ID',
    `status` TINYINT DEFAULT 0 COMMENT '??(0:???,1:????,2:???)',
    `queue_no` INT DEFAULT NULL COMMENT '???',
    `expected_date` DATE DEFAULT NULL COMMENT '??????',
    `pickup_deadline` DATE DEFAULT NULL COMMENT '??????',
    `notified` TINYINT DEFAULT 0 COMMENT '????????',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='?????';
