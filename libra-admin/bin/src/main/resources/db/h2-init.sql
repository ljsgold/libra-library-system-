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
CREATE TABLE IF NOT EXISTS `sys_user` (
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
    INDEX `idx_sys_user_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
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
    INDEX `idx_sys_role_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
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
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
CREATE TABLE IF NOT EXISTS `lib_book` (
    `id` BIGINT NOT NULL COMMENT '图书ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN',
    `title` VARCHAR(255) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) NOT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `pub_date` DATE DEFAULT NULL COMMENT '出版日期',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
    `summary` TEXT DEFAULT NULL COMMENT '简介',
    `total_count` INT DEFAULT 0 COMMENT '总藏书量',
    `stock_count` INT DEFAULT 0 COMMENT '在馆数量',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:上架, 0:下架)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_book_isbn` (`isbn`),
    INDEX `idx_book_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';
CREATE TABLE IF NOT EXISTS `lib_inventory` (
    `id` BIGINT NOT NULL COMMENT '库存ID',
    `tenant_id` BIGINT NOT NULL,
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `location` VARCHAR(50) DEFAULT NULL COMMENT '存放位置',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:在馆, 2:借出, 3:丢失, 4:损毁)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_inventory_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书库存表';
CREATE TABLE IF NOT EXISTS `lib_borrow_record` (
    `id` BIGINT NOT NULL COMMENT '记录ID',
    `tenant_id` BIGINT NOT NULL,
    `inventory_id` BIGINT NOT NULL COMMENT '库存ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID (冗余)',
    `user_id` BIGINT NOT NULL COMMENT '借阅人ID',
    `borrow_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借出时间',
    `due_time` DATETIME NOT NULL COMMENT '应还时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '归还时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态 (1:借阅中, 2:已归还, 3:已逾期)',
    `fine_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '罚款金额',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_record_user_id` (`user_id`),
    INDEX `idx_record_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';
CREATE TABLE IF NOT EXISTS `lib_reservation` (
    `id` BIGINT NOT NULL COMMENT '预约ID',
    `tenant_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL COMMENT '预约人',
    `book_id` BIGINT NOT NULL COMMENT '预约图书',
    `status` TINYINT DEFAULT 0 COMMENT '状态 (0:排队中, 1:待取书, 2:已取书, 3:已取消, 4:已过期)',
    `queue_no` INT DEFAULT 0 COMMENT '排队号',
    `expected_date` DATE DEFAULT NULL COMMENT '预计有书日期',
    `pickup_deadline` DATE DEFAULT NULL COMMENT '取书截止日期',
    `notified` TINYINT DEFAULT 0 COMMENT '是否已通知',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_reserve_book_user` (`book_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';
