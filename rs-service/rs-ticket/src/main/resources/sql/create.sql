create table station
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    name         varchar(255)                       not null comment '站名',
    code         varchar(64)                        not null comment '站码',
    address      varchar(512)                       null comment '地址',
    city         varchar(128)                       null comment '所属城市',
    province     varchar(128)                       null comment '所属省份',
    longitude    varchar(32)                        null comment '经度',
    latitude     varchar(32)                        null comment '纬度',
    status       int      default 1                 null comment '状态: 0-禁用, 1-启用',
    is_hot       int      default 0                 null comment '是否热门: 0-否, 1-是',
    description  text                               null comment '描述',
    phone        varchar(32)                        null comment '联系电话',
    created_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    created_by   bigint                             null comment '创建人ID',
    updated_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    updated_by   bigint                             null comment '更新人ID'
)
    comment '车站表';

create table line
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    name           varchar(255)                       not null comment '线路名',
    code           varchar(64)                        not null comment '线路码',
    total_length   bigint                             null comment '线路总长度(Km)',
    designed_speed bigint                             null comment '线路设计速度(Km/h)',
    status         int      default 1                 null comment '线路状态: 0-停用, 1-启用',
    start_station  bigint                             null comment '线路起始站ID',
    end_station    bigint                             null comment '线路终点站ID',
    created_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    created_by     bigint                             null comment '创建人ID',
    updated_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    updated_by     bigint                             null comment '更新人ID',
    constraint line_ibfk_1
        foreign key (start_station) references station (id)
            on delete set null,
    constraint line_ibfk_2
        foreign key (end_station) references station (id)
            on delete set null
)
    comment '线路表';

create index end_station
    on line (end_station);

create index start_station
    on line (start_station);

create table line_stations
(
    id         bigint auto_increment comment '主键'
        primary key,
    line_id    bigint   not null comment '线路ID',
    station_id bigint   not null comment '途径车站ID',
    sequence   int      not null comment '顺序',
    constraint ls_ibfk_1
        foreign key (line_id) references line (id),
    constraint ls_ibfk_2
        foreign key (station_id) references station (id)
)
    comment '线路途径站点表';

create index line_id_idx
    on line_stations (line_id);

create table train
(
    id                              bigint auto_increment comment '主键ID'
        primary key,
    name                            varchar(255)                       not null comment '列车名称',
    code                            varchar(64)                        not null comment '列车编号',
    type                            varchar(64)                        null comment '列车类型（如：G/D/C）',
    status                          int      default 1                 null comment '状态: 0-停运, 1-运行中',
    first_class_carriages_num       int                                null comment '一等座车厢数',
    first_class_carriages_list_num  int                                null comment '一等座车厢列表数',
    second_class_carriages_num      int                                null comment '二等座车厢数',
    second_class_carriages_list_num int                                null comment '二等座车厢列表数',
    created_time                    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    created_by                      bigint                             null comment '创建人ID',
    updated_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    updated_by                      bigint                             null comment '更新人ID'
)
    comment '列车表';

create table ticket
(
    id                 bigint auto_increment comment '车票ID'
        primary key,
    line_id            bigint                                not null comment '线路ID',
    train_id           bigint                                not null comment '火车ID',
    start_time         datetime                              not null comment '出发时间',
    end_time           datetime                              not null comment '到达时间',
    status             varchar(32) default '0'               null comment '状态: 0-未开售, 1-正在售票, 2-售空',
    is_hot             int         default 0                 null comment '是否热门（0- 否， 1-是）',
    first_class_price  double                                not null comment '一等座价格',
    second_class_price double                                not null comment '二等座价格',
    created_time       datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    created_by         bigint                                null comment '创建人ID',
    updated_time       datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    updated_by         bigint                                null comment '更新人ID',
    constraint ticket_ibfk_1
        foreign key (line_id) references line (id)
            on delete cascade,
    constraint ticket_ibfk_2
        foreign key (train_id) references train (id)
            on delete cascade
)
    comment '车票表';

create table seat
(
    id             bigint auto_increment comment '座位ID'
        primary key,
    ticket_id      bigint                             not null comment '关联车票ID',
    seat_type      int                                not null comment '座位类型: 0-商务座, 1-一等座, 2-二等座',
    carriage_no    int                                not null comment '车厢号',
    seat_no        varchar(8)                         not null comment '座位编号（如 A, B, C, D, E, F）',
    seat_row       int                                not null comment '座位排号（如第12排）',
    full_seat_code varchar(16) as (concat(lpad(`carriage_no`, 2, _utf8mb4'0'), _utf8mb4'-',
                                          lpad(`seat_row`, 2, _utf8mb4'0'), _utf8mb4'-',
                                          `seat_no`)) stored comment '完整座位编码，如 05-12-A',
    status         int      default 0                 null comment '座位状态: 0-可售, 1-已售',
    order_id       bigint                             null comment '出售订单',
    price          double                             not null,
    created_time   datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null comment '创建人',
    updated_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    updata_by      bigint                             null comment '更新人',
    constraint uk_ticket_carriage_row_seat
        unique (ticket_id, carriage_no, seat_row, seat_no),
    constraint seat_ibfk_1
        foreign key (ticket_id) references ticket (id)
            on delete cascade
)
    comment '座位明细表';

create table stop_stations
(
    id            bigint auto_increment comment '主键'
        primary key,
    ticket_id     bigint   not null comment '车票Id',
    station_id    bigint   not null comment '停靠车站Id',
    arrived_time  datetime null comment '到达时间',
    departTime    datetime null comment '离开时间',
    stop_duration int as (timestampdiff(MINUTE, `departTime`, `arrived_time`)) stored,
    constraint stop_ibfk_1
        foreign key (ticket_id) references ticket (id),
    constraint stop_ibfk_2
        foreign key (station_id) references station (id)
)
    comment '停靠车站表';

create index line_id
    on ticket (line_id);

create index train_id
    on ticket (train_id);

create definer = root@localhost trigger auto_create_seat_by_ticket
    after insert
    on ticket
    for each row
BEGIN
    -- 声明变量（类型需与 train 表字段一致）
    DECLARE v_first_class_carriages_num INT DEFAULT 0;
    DECLARE v_first_class_rows_per_carriage INT DEFAULT 0;
    DECLARE v_second_class_carriages_num INT DEFAULT 0;
    DECLARE v_second_class_rows_per_carriage INT DEFAULT 0;
    DECLARE v_first_class_price DOUBLE DEFAULT 0.0;
    DECLARE v_second_class_price DOUBLE DEFAULT 0.0;

    -- 从 train 表查询配置（使用 NEW.train_id）
    SELECT first_class_carriages_num,
           first_class_carriages_list_num,
           second_class_carriages_num,
           second_class_carriages_list_num
    INTO
        v_first_class_carriages_num,
        v_first_class_rows_per_carriage,
        v_second_class_carriages_num,
        v_second_class_rows_per_carriage
    FROM train
    WHERE id = NEW.train_id;

    SET v_first_class_price = NEW.first_class_price;
    SET v_second_class_price = NEW.second_class_price;

    -- 调用存储过程（传入变量）
    CALL create_seat(
            v_first_class_carriages_num,
            v_first_class_rows_per_carriage,
            v_second_class_carriages_num,
            v_second_class_rows_per_carriage,
            v_first_class_price,
            v_second_class_price,
            NEW.id -- 新 ticket 的 ID
         );

END;

create
    definer = root@localhost procedure create_seat(IN first_class_carriages_num int,
                                                   IN first_class_carriages_list_num int,
                                                   IN second_class_carriages_num int,
                                                   IN second_class_carriages_list_num int, IN first_class_price double,
                                                   IN second_class_price double, IN ticket bigint)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;

    -- 一等座
    WHILE i <= first_class_carriages_num
        DO
            SET j = 1;
            WHILE j <= first_class_carriages_list_num
                DO
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '1', i, 'A', j, 0, first_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '1', i, 'B', j, 0, first_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '1', i, 'E', j, 0, first_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '1', i, 'F', j, 0, first_class_price, NOW(), NOW());
                    SET j = j + 1;
                END WHILE;
            SET i = i + 1;
        END WHILE;

    -- 二等座
    WHILE i <= first_class_carriages_num + second_class_carriages_num
        DO
            SET j = 1;
            WHILE j <= second_class_carriages_list_num
                DO
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '2', i, 'A', j, 0, second_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '2', i, 'B', j, 0, second_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '2', i, 'C', j, 0, second_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '2', i, 'E', j, 0, second_class_price, NOW(), NOW());
                    INSERT INTO seat (ticket_id, seat_type, carriage_no, seat_no, seat_row, status, price, created_time,
                                      updated_time)
                    VALUES (ticket, '2', i, 'F', j, 0, second_class_price, NOW(), NOW());
                    SET j = j + 1;
                END WHILE;
            SET i = i + 1;
        END WHILE;

END;
