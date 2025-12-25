create table chat_message
(
    id         int auto_increment comment '主键'
        primary key,
    session_id varchar(50) not null comment '对话id',
    content    text        not null comment '对话'
)
    comment '对话id内容表';

create table memory
(
    id          int auto_increment comment '主键'
        primary key,
    session_id  varchar(50) not null comment '对话id',
    content     text        not null comment '历史 ',
    type        int         null comment '对话类型 1- AI对话 2- 人工客服',
    user_id     int         null,
    agent_id    int         null,
    update_time datetime    null,
    create_time datetime    null
)
    comment '对话id内容表';

