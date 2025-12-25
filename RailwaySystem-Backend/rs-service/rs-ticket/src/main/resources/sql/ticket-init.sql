create table admin
(
    id          bigint auto_increment comment '主键'
        primary key,
    username    varchar(100)  not null comment '用户名',
    password    varchar(200)  not null comment '密码',
    real_name   varchar(20)   not null comment '真名',
    id_card     varchar(18)   null,
    icon        varchar(200)  null comment '头像',
    role        bigint        null comment '角色ID',
    role_name   varchar(50) as ((case `role`
                                     when 100 then _utf8mb4'å®¢æ'
                                     when 101 then _utf8mb4'è´¢å¡ç®¡çå'
                                     when 102 then _utf8mb4'è¿è¥ç®¡çå'
                                     when 103 then _utf8mb4'ç³»ç»ç®¡çå'
                                     when 104 then _utf8mb4'è¶çº§ç®¡çå'
                                     else NULL end)) comment '角色名称（自动生成）',
    status      int default 0 not null comment '账号状态 0- 禁用 1- 启用',
    create_time datetime      null comment '创建时间',
    create_by   bigint        null comment '创建人',
    update_time datetime      null comment '修改时间',
    update_by   bigint        null comment '修改人'
)
    comment '管理员表';

create table admin_permission
(
    id          bigint auto_increment comment '主键'
        primary key,
    permission  varchar(199) not null comment '权限标识',
    type        int          not null comment '权限类型 1- 路由 2- 接口',
    role        int          not null comment '角色ID',
    description varchar(199) null comment '描述',
    role_name   varchar(50) as ((case `role`
                                     WHEN 100 THEN '客服'
                                     WHEN 101 THEN '财务管理員'
                                     WHEN 102 THEN '运营管理員'
                                     WHEN 103 THEN '系统管理员'
                                     WHEN 104 THEN '超级管理员'
                                     else NULL end)) comment '角色名称（自动生成）'
)
    comment '管理员权限表';

create table contact
(
    id             bigint unsigned auto_increment comment '主键ID'
        primary key,
    user_id        bigint                                   not null comment '所属用户ID',
    name           varchar(100)                             not null comment '联系人姓名',
    id_card        varchar(18)                              null comment '身份证号',
    phone          varchar(20)                              not null comment '手机号码',
    email          varchar(100)                             null comment '邮箱地址（可选）',
    passenger_type tinyint                                  not null comment '乘客类型：1-成人，2-儿童，3-学生，4-老人',
    is_default     tinyint     default 0                    not null comment '是否为默认联系人：0-否，1-是',
    status         tinyint     default 1                    not null comment '状态：0-禁用，1-启用',
    remark         varchar(500)                             null comment '备注信息',
    is_deleted     tinyint     default 0                    not null comment '逻辑删除标识：0-未删除，1-已删除',
    create_by      bigint                                   null comment '创建人ID',
    create_time    datetime(3) default CURRENT_TIMESTAMP(3) null comment '创建时间',
    update_by      bigint                                   null comment '更新人ID',
    update_time    datetime(3) default CURRENT_TIMESTAMP(3) null on update CURRENT_TIMESTAMP(3) comment '更新时间'
)
    comment '联系人表' collate = utf8mb4_unicode_ci;

create index idx_user_id
    on contact (user_id);

create table user
(
    id           bigint auto_increment comment '用户id'
        primary key,
    username     varchar(50)                        not null comment '用户名',
    password     varchar(255)                       not null comment '密码',
    gender       varchar(10)                        null comment '性别',
    birthday     datetime                           null comment '生日',
    address      varchar(255)                       null comment '地址',
    introduction varchar(500)                       null comment '个性签名',
    icon         varchar(255)                       null comment '头像',
    email        varchar(100)                       null comment '邮箱',
    phone        varchar(20)                        null comment '手机号',
    real_name    varchar(50)                        null comment '真名',
    id_card      varchar(18)                        null comment '身份证号',
    create_by    bigint                             null,
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    bigint                             null,
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_email
        unique (email),
    constraint uk_id_card
        unique (id_card),
    constraint uk_phone
        unique (phone),
    constraint uk_username
        unique (username)
)
    comment '用户表' collate = utf8mb4_unicode_ci;

