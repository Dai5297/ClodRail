package com.dai.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class DurationTypeUtil extends BaseTypeHandler<Duration> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Duration parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getSeconds());
    }

    @Override
    public Duration getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long seconds = rs.getLong(columnName);
        return rs.wasNull() ? null : Duration.ofSeconds(seconds);
    }

    @Override
    public Duration getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long seconds = rs.getLong(columnIndex);
        return rs.wasNull() ? null : Duration.ofSeconds(seconds);
    }

    @Override
    public Duration getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long seconds = cs.getLong(columnIndex);
        return cs.wasNull() ? null : Duration.ofSeconds(seconds);
    }

}
