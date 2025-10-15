package com.rs.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法工具类
 */
public class SnowflakeUtil {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    private SnowflakeUtil() {
        // 工具类，防止实例化
    }

    /**
     * 生成Long类型ID
     */
    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 生成String类型ID
     */
    public static String nextIdStr() {
        return SNOWFLAKE.nextIdStr();
    }

    /**
     * 生成带前缀的ID
     */
    public static String nextIdWithPrefix(String prefix) {
        return prefix + SNOWFLAKE.nextId();
    }
}
