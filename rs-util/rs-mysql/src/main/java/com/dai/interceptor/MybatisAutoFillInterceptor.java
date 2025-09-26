package com.dai.interceptor;

import com.dai.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisAutoFillInterceptor implements Interceptor {

    // 定义需要自动填充的字段名
    private static final Set<String> AUTO_FILL_FIELDS = Set.of(
            "createBy", "updateBy", "createTime", "updateTime", "isDeleted"
    );

    // 排除JDK内部类的包前缀
    private static final Set<String> EXCLUDED_PACKAGES = Set.of(
            "java.", "javax.", "sun.", "com.sun."
    );

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.debug("开始自动填充处理");

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];

        if (parameter == null) {
            return invocation.proceed();
        }

        Long loginId = UserContext.get();
        if (loginId == null) {
            return invocation.proceed();
        }

        if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
            replaceEntityProperty(parameter, loginId, sqlCommandType);
        }

        return invocation.proceed();
    }

    private void replaceEntityProperty(Object parameter, Long loginId, SqlCommandType sqlCommandType) {
        if (parameter instanceof Map) {
            replaceMap((Map<?, ?>) parameter, loginId, sqlCommandType);
        } else {
            replace(parameter, loginId, sqlCommandType);
        }
    }

    private void replaceMap(Map<?, ?> parameter, Long loginId, SqlCommandType sqlCommandType) {
        Collection<?> values = parameter.values();
        for (Object value : values) {
            if (value != null && !isJdkClass(value.getClass())) {
                replace(value, loginId, sqlCommandType);
            }
        }
    }

    private void replace(Object parameter, Long loginId, SqlCommandType sqlCommandType) {
        // 检查是否为JDK内部类，避免反射访问
        if (isJdkClass(parameter.getClass())) {
            return;
        }

        if (SqlCommandType.INSERT == sqlCommandType) {
            dealInsert(parameter, loginId);
        } else {
            dealUpdate(parameter, loginId);
        }
    }

    private void dealUpdate(Object parameter, Long loginId) {
        Field[] allFields = getAllFields(parameter);
        for (Field field : allFields) {
            // 跳过JDK内部类的字段
            if (isJdkClass(field.getDeclaringClass())) {
                continue;
            }

            // 只处理需要自动填充的字段
            if (!AUTO_FILL_FIELDS.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object currentValue = field.get(parameter);

                // 如果字段已有值，则跳过
                if (Objects.nonNull(currentValue)) {
                    field.setAccessible(false);
                    continue;
                }

                // 根据字段名设置值
                switch (field.getName()) {
                    case "updateBy":
                        field.set(parameter, loginId);
                        log.debug("自动填充 updateBy: {}", loginId);
                        break;
                    case "updateTime":
                        field.set(parameter, LocalDateTime.now());
                        log.debug("自动填充 updateTime: {}", LocalDateTime.now());
                        break;
                    default:
                        // 其他字段不处理
                        break;
                }
                field.setAccessible(false);

            } catch (Exception e) {
                log.warn("字段填充失败: {}.{} - {}",
                        parameter.getClass().getSimpleName(), field.getName(), e.getMessage());
            }
        }
    }

    private void dealInsert(Object parameter, Long loginId) {
        Field[] allFields = getAllFields(parameter);
        for (Field field : allFields) {
            // 跳过JDK内部类的字段
            if (isJdkClass(field.getDeclaringClass())) {
                continue;
            }

            // 只处理需要自动填充的字段
            if (!AUTO_FILL_FIELDS.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object currentValue = field.get(parameter);

                // 如果字段已有值，则跳过
                if (Objects.nonNull(currentValue)) {
                    field.setAccessible(false);
                    continue;
                }

                // 根据字段名设置值
                switch (field.getName()) {
                    case "createBy":
                    case "updateBy":
                        field.set(parameter, loginId);
                        log.debug("自动填充 {}: {}", field.getName(), loginId);
                        break;
                    case "createTime":
                    case "updateTime":
                        Object timeValue = field.getType() == LocalDateTime.class ?
                                LocalDateTime.now() : new Date();
                        field.set(parameter, timeValue);
                        log.debug("自动填充 {}: {}", field.getName(), timeValue);
                        break;
                    case "isDeleted":
                        // 如果是逻辑删除字段，默认设置为0
                        if (field.getType() == Integer.class || field.getType() == int.class) {
                            field.set(parameter, 0);
                        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                            field.set(parameter, false);
                        }
                        log.debug("自动填充 isDeleted: 默认值");
                        break;
                    default:
                        // 其他字段不处理
                        break;
                }
                field.setAccessible(false);

            } catch (Exception e) {
                log.warn("字段填充失败: {}.{} - {}",
                        parameter.getClass().getSimpleName(), field.getName(), e.getMessage());
            }
        }
    }

    private Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();

        while (clazz != null && !isJdkClass(clazz)) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return fieldList.toArray(new Field[0]);
    }

    /**
     * 检查是否为JDK内部类，避免反射访问
     */
    private boolean isJdkClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        String className = clazz.getName();
        for (String excludedPackage : EXCLUDED_PACKAGES) {
            if (className.startsWith(excludedPackage)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 不需要任何处理
    }
}