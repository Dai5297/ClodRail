package com.rs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * MySQL 共享配置属性类
 * 对应 rs.mysql 前缀的配置，用于 Nacos 共享配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "rs.mysql")
public class MysqlProperties {

    /**
     * MySQL 服务器主机地址
     */
    private String host = "localhost";

    /**
     * MySQL 服务器端口号
     */
    private Integer port = 3306;

    /**
     * 数据库名称
     */
    private String dbName = "rs-order";

    /**
     * 连接 MySQL 的用户名
     */
    private String username = "root";

    /**
     * 连接 MySQL 的密码
     */
    private String password;

    /**
     * HikariCP 连接池配置
     */
    @NestedConfigurationProperty
    private Hikari hikari = new Hikari();

    /**
     * 获取完整的 JDBC URL
     */
    public String getJdbcUrl() {
        return String.format(
                "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
                host, port, dbName
        );
    }

    /**
     * HikariCP 连接池配置
     */
    @Data
    public static class Hikari {
        /**
         * 连接超时时间（毫秒）
         */
        private Long connectionTimeout = 30000L;

        /**
         * 连接验证超时时间（毫秒）
         */
        private Long validationTimeout = 10000L;

        /**
         * 连接池最大连接数
         */
        private Integer maximumPoolSize = 20;

        /**
         * 连接池最小空闲连接数
         */
        private Integer minimumIdle = 5;

        /**
         * 连接空闲超时时间（毫秒）
         */
        private Long idleTimeout = 600000L;

        /**
         * 连接最大生命周期（毫秒）
         */
        private Long maxLifetime = 1800000L;
    }
}