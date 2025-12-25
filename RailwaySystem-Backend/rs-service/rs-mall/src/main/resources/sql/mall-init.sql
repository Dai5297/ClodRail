create table item
(
    id            bigint auto_increment comment '商品id'
        primary key,
    name          varchar(200)                         not null comment 'SKU名称',
    price         int        default 0                 not null comment '价格（分）',
    stock         int unsigned                         not null comment '库存数量',
    image         varchar(200)                         null comment '商品图片',
    category      varchar(200)                         null comment '类目名称',
    brand         varchar(100)                         null comment '品牌名称',
    spec          varchar(200)                         null comment '规格',
    sold          int        default 0                 null comment '销量',
    comment_count int        default 0                 null comment '评论数',
    is_ad         tinyint(1) default 0                 null comment '是否是推广广告，true/false',
    status        int        default 2                 null comment '商品状态 1-正常，2-下架，3-删除',
    create_time   datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by     bigint                               null comment '创建人',
    update_by     bigint                               null comment '修改人'
)
    comment '商品表' charset = utf8mb3
                     row_format = COMPACT;

create index category
    on item (category);

create index status
    on item (status);

create index updated
    on item (update_time);

create table mall_order
(
    id                bigint auto_increment comment '订单ID'
        primary key,
    order_number      varchar(50)                        not null comment '订单号(唯一标识)',
    user_id           bigint                             not null comment '用户ID',
    item_id           bigint                             not null comment '商品ID',
    item_name         varchar(200)                       not null comment '商品名称(冗余)',
    item_image        varchar(500)                       null comment '商品图片(冗余)',
    quantity          int      default 1                 not null comment '兑换数量',
    unit_price        int                                not null comment '单价(积分)',
    total_points      int                                not null comment '总积分',
    recipient_name    varchar(50)                        not null comment '收货人姓名',
    recipient_phone   varchar(20)                        not null comment '联系电话',
    recipient_address varchar(500)                       not null comment '收货地址',
    status            tinyint  default 0                 not null comment '订单状态: 0-待发货, 1-已发货, 2-已完成, 3-已取消',
    ship_time         datetime                           null comment '发货时间',
    complete_time     datetime                           null comment '完成时间',
    cancel_time       datetime                           null comment '取消时间',
    remark            varchar(500)                       null comment '备注',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_by         bigint                             null comment '创建人',
    update_by         bigint                             null comment '修改人',
    constraint uk_order_number
        unique (order_number)
)
    comment '积分商城订单表';

create index idx_create_time
    on mall_order (create_time);

create index idx_item_id
    on mall_order (item_id);

create index idx_status
    on mall_order (status);

create index idx_user_id
    on mall_order (user_id);

create table `point-detail`
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint      not null comment '用户id',
    type        tinyint     not null comment '0: 扣减, 1: 新增',
    point       int         not null comment '积分',
    comment     varchar(50) null comment '说明',
    create_time datetime    null comment '创建时间',
    create_by   bigint      null comment '创建人',
    update_time datetime    null comment '更新时间',
    update_by   bigint      null comment '更新人'
)
    comment '积分明细表';

create table point_balance
(
    id             bigint auto_increment comment '主键'
        primary key,
    user_id        bigint           not null comment '用户id',
    current_points bigint default 0 not null comment '当前可用积分',
    total_earned   bigint default 0 not null comment '累计获得积分',
    total_spent    bigint default 0 not null comment '累计消费积分',
    create_by      bigint           null comment '创建人',
    create_time    datetime         null comment '创建时间',
    update_by      bigint           null comment '更新人',
    update_time    datetime         null comment '更新时间'
)
    comment '积分表';

