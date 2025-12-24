package com.rs.model.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CanalFlatMessage {
    private String database;                     // 库名，如 "seata"
    private String table;                        // 表名，如 "distributed_lock"
    private String type;                         // 事件类型: "INSERT"/"UPDATE"/"DELETE"
    private Long es;                             // binlog 执行时间戳（毫秒）
    private Long ts;                             // Canal 处理时间戳（毫秒）
    private Long id;                             // 消息 ID
    private Boolean isDdl;                       // 是否为 DDL
    private String gtid;                         // GTID（通常为空）
    private String sql;                          // 原始 SQL（通常为空）

    private List<Map<String, String>> data;      // 变更后的行数据（每行是一个 map）
    private List<Map<String, String>> old;       // 变更前的值（仅 UPDATE 且字段有变化时存在）

    // 以下字段一般可忽略，除非需要元信息
    private Map<String, String> mysqlType;       // MySQL 字段类型定义
    private Map<String, Integer> sqlType;        // JDBC SQL 类型
    private List<String> pkNames;                // 主键字段名列表
}
