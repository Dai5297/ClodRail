-- 1. 车站表 (station)
CREATE TABLE station (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                         name VARCHAR(255) NOT NULL COMMENT '站名',
                         code VARCHAR(64) NOT NULL COMMENT '站码',
                         address VARCHAR(512) COMMENT '地址',
                         city VARCHAR(128) COMMENT '所属城市',
                         province VARCHAR(128) COMMENT '所属省份',
                         longitude VARCHAR(32) COMMENT '经度',
                         latitude VARCHAR(32) COMMENT '纬度',
                         status INT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
                         is_hot INT DEFAULT 0 COMMENT '是否热门: 0-否, 1-是',
                         description TEXT COMMENT '描述',
                         phone VARCHAR(32) COMMENT '联系电话',

                         created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         created_by BIGINT COMMENT '创建人ID',
                         updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         updated_by BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车站表';

-- 2. 线路表 (line)
CREATE TABLE line (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                      name VARCHAR(255) NOT NULL COMMENT '线路名',
                      code VARCHAR(64) NOT NULL COMMENT '线路码',
                      total_length BIGINT COMMENT '线路总长度(Km)',
                      designed_speed BIGINT COMMENT '线路设计速度(Km/h)',
                      status INT DEFAULT 1 COMMENT '线路状态: 0-停用, 1-启用',
                      start_station BIGINT COMMENT '线路起始站ID',
                      end_station BIGINT COMMENT '线路终点站ID',

                      created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      created_by BIGINT COMMENT '创建人ID',
                      updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      updated_by BIGINT COMMENT '更新人ID',

                      FOREIGN KEY (start_station) REFERENCES station(id) ON DELETE SET NULL,
                      FOREIGN KEY (end_station) REFERENCES station(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路表';

-- 3. 列车表 (train)
CREATE TABLE train (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                       name VARCHAR(255) NOT NULL COMMENT '列车名称',
                       code VARCHAR(64) NOT NULL COMMENT '列车编号',
                       type VARCHAR(64) COMMENT '列车类型（如：G/D/C）',
                       status INT DEFAULT 1 COMMENT '状态: 0-停运, 1-运行中',

                       created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       created_by BIGINT COMMENT '创建人ID',
                       updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       updated_by BIGINT COMMENT '更新人ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='列车表';

-- 4. 车票表 (ticket)
CREATE TABLE ticket (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '车票ID',
                        line_id BIGINT NOT NULL COMMENT '线路ID',
                        train_id BIGINT NOT NULL COMMENT '火车ID',
                        start_time DATETIME NOT NULL COMMENT '出发时间',
                        end_time DATETIME NOT NULL COMMENT '到达时间',
                        status VARCHAR(32) DEFAULT '0' COMMENT '状态: 0-未开售, 1-正在售票, 2-售空',

                        created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        created_by BIGINT COMMENT '创建人ID',
                        updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        updated_by BIGINT COMMENT '更新人ID',

                        FOREIGN KEY (line_id) REFERENCES line(id) ON DELETE CASCADE,
                        FOREIGN KEY (train_id) REFERENCES train(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车票表';

-- 5. 车票库存表 (ticket_inventory)
CREATE TABLE ticket_inventory (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
                                  ticket_id BIGINT NOT NULL COMMENT '关联的车票ID',
                                  seat_type VARCHAR(32) NOT NULL COMMENT '座位类型: 0-商务座, 1-一等座, 2-二等座',
                                  price DECIMAL(10,2) NOT NULL COMMENT '该类型座位价格',
                                  total_seats INT NOT NULL COMMENT '总座位数',
                                  remaining_seats INT NOT NULL COMMENT '剩余座位数',
                                  inventory_status VARCHAR(32) DEFAULT '0' COMMENT '库存状态: 0-充足, 1-紧张, 2-售空',

                                  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  created_by BIGINT COMMENT '创建人ID',
                                  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  updated_by BIGINT COMMENT '更新人ID',

                                  FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车票库存表';