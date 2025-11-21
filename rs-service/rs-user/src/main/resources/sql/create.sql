CREATE TABLE `user` (
                        `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '密码',
                        `gender` varchar(2)NULL comment '性别',
                        `icon` VARCHAR(255) NULL COMMENT '头像',
                        `email` VARCHAR(100) NULL COMMENT '邮箱',
                        `phone` VARCHAR(20) NULL COMMENT '手机号',
                        `real_name` VARCHAR(50) NULL COMMENT '真名',
                        `id_card` VARCHAR(18) NULL COMMENT '身份证号',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_email` (`email`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';