create table `order`
(
    id             bigint auto_increment comment '主键'
        primary key,
    order_id       varchar(100)  not null comment '订单Id',
    user_id        bigint        not null comment '下单用户',
    ticket_id      bigint        not null comment '下单车次',
    status         int default 0 null comment '订单状态：0- 待支付, 1- 已支付, 2- 已出票, 3- 已取消, 4- 已退票, 5- 已改签',
    has_change     int default 0 null comment '是否改签-0 否, 1是',
    payment_method int           null comment '支付方式：0- 支付宝, 1- 微信',
    voucher_id     bigint        null comment '优惠券id',
    amount         double        not null comment '费用',
    pay_time       datetime      null comment '付款时间',
    expire_time    datetime      null comment '过期时间',
    create_by      bigint        null comment '创建人',
    create_time    datetime      null comment '创建时间',
    update_by      bigint        null comment '更新人',
    update_time    datetime      null comment '更新时间',
    constraint order_id
        unique (order_id)
);

create table order_seat
(
    id           bigint auto_increment comment '主键'
        primary key,
    order_id     varchar(100) not null comment '订单Id',
    seat_id      bigint       not null comment '座位Id',
    passenger_id bigint       null comment '乘客id-对应rs-customer的contact表',
    constraint uk_order_seat
        unique (order_id, seat_id) comment '防止重复绑定',
    constraint fk_order_id
        foreign key (order_id) references `order` (order_id)
            on delete cascade
)
    comment '订单-座位对应表';

create table ticket_order_failed_message
(
    id           bigint unsigned auto_increment comment '主键ID'
        primary key,
    message_id   varchar(64)                        not null comment '消息唯一ID（建议使用业务全局ID或MQ Message ID）',
    order_id     varchar(64)                        null comment '关联的订单ID（若已生成）',
    passenger_id bigint                             null comment '乘客ID（便于定位用户）',
    seat_id      bigint                             null comment '座位ID（若已分配）',
    body         longtext                           not null comment '原始消息内容（JSON格式，包含下单请求完整上下文）',
    retry_count  int      default 0                 not null comment '消费者重试次数',
    fail_reason  text                               null comment '最终消费失败原因（异常堆栈或错误描述）',
    status       int      default 0                 not null comment '处理状态（0-未处理， 1-已处理）',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '最终失败时间',
    create_by    bigint                             null comment '创建人',
    update_time  datetime                           null comment '处理时间',
    update_by    bigint                             null comment '修改人',
    constraint uk_message_id
        unique (message_id)
)
    comment 'ticket_order 下单业务消费者重试失败消息表' collate = utf8mb4_unicode_ci;

create index idx_order_id
    on ticket_order_failed_message (order_id);

create index idx_passenger_id
    on ticket_order_failed_message (passenger_id);

create table undo_log
(
    id            bigint auto_increment
        primary key,
    branch_id     bigint       not null,
    xid           varchar(100) not null,
    context       varchar(128) not null,
    rollback_info longblob     not null,
    log_status    int          not null,
    log_created   datetime     not null,
    log_modified  datetime     not null,
    ext           varchar(100) null,
    constraint ux_undo_log
        unique (xid, branch_id)
)
    charset = utf8mb3;

