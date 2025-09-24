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
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("开始自动填充处理");

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 获取sql的类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // 获取sql的参数对象
        Object parameter = invocation.getArgs()[1];
        // 如果参数对象为空，就直接放行
        if (parameter == null) {
            return invocation.proceed();
        }
        // 获取当前用户登录的id
        Long loginId = UserContext.get();
        if (loginId == null) {
            return invocation.proceed();
        }
        // 如果sql的类型是插入或者更新，对参数做替换，填充created_by、created_time、update_by、update_time、is_deleted
        if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
            replaceEntityProperty(parameter, loginId, sqlCommandType);
        }
        // 放行
        return invocation.proceed();
    }

    // 填充created_by、created_time、update_by、update_time
    private void replaceEntityProperty(Object parameter, Long loginId, SqlCommandType sqlCommandType) {
        if (parameter instanceof Map) {
            // Map类型，就相当于批量插入的那种情况
            replaceMap((Map) parameter, loginId, sqlCommandType);
        } else {
            // 普通的eneity类型
            replace(parameter, loginId, sqlCommandType);
        }
    }

    private void replaceMap(Map parameter, Long loginId, SqlCommandType sqlCommandType) {
        // 获取到所有的参数对象
        Collection values = parameter.values();
        for (Object value : values) {
            // 将每个参数对象都进行替换
            replace(value, loginId, sqlCommandType);
        }
    }

    /**
     * 填充数据的逻辑
     *
     * @param parameter
     * @param loginId
     * @param sqlCommandType
     */
    private void replace(Object parameter, Long loginId, SqlCommandType sqlCommandType) {
        // 如果是插入类型的sql
        if (SqlCommandType.INSERT == sqlCommandType) {
            dealInsert(parameter, loginId);
        } else {
            // 由于只有插入类型和更新类型的sql类型会走到这里，所以这里一定就是更新类型了
            dealUpdate(parameter, loginId);
        }
    }

    /**
     * 更新类型的sql，填充数据的方式，对update_by、update_time进行填充
     *
     * @param parameter
     * @param loginId
     */
    private void dealUpdate(Object parameter, Long loginId) {
        // 获取这个对象对应类的所有字段
        Field[] allFields = getAllFields(parameter);
        // 遍历字段
        for (Field field : allFields) {
            // 使用try-catch捕捉异常，使其不影响主程序的执行
            try {
                // 设置这个字段为可访问的
                field.setAccessible(true);
                // 得到字段的值，后面指定对象
                Object o = field.get(parameter);
                if (Objects.nonNull(o)) {
                    // 如果字段的值不是空的，将这个字段设置成不可访问的
                    field.setAccessible(false);
                    // 然后跳过这次循环，遍历下一个字段
                    continue;
                }
                // 如果字段的值是空的，则根据字段的名字来填充数据
                if ("updateBy".equals(field.getName())) {
                    // 设置为用户的loginId
                    field.set(parameter, loginId);
                    // 设置字段为不可访问
                    field.setAccessible(false);
                } else if ("updateTime".equals(field.getName())) {
                    // 设置为当前时间
                    field.set(parameter, LocalDateTime.now());
                    // 设置字段为不可访问
                    field.setAccessible(false);
                } else {
                    // 如果不是这三个字段，就直接将这个字段设置为不可访问，然后遍历下一个字段
                    field.setAccessible(false);
                }

            } catch (Exception e) {
                log.error("dealInsert.error:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 插入类型的sql，填充数据的方式，对created_by、created_time、is_deleted进行填充
     *
     * @param parameter
     * @param loginId
     */
    private void dealInsert(Object parameter, Long loginId) {
        // 获取这个对象对应类的所有字段
        Field[] allFields = getAllFields(parameter);
        // 遍历字段
        for (Field field : allFields) {
            // 使用try-catch捕捉异常，使其不影响主程序的执行
            try {
                // 设置这个字段为可访问的
                field.setAccessible(true);
                // 得到字段的值，后面指定对象
                Object o = field.get(parameter);
                if (Objects.nonNull(o)) {
                    // 如果字段的值不是空的，将这个字段设置成不可访问的
                    field.setAccessible(false);
                    // 然后跳过这次循环，遍历下一个字段
                    continue;
                }
                // 如果字段的值是空的，则根据字段的名字来填充数据
                switch (field.getName()) {
                    case "createBy", "updateBy" -> {
                        // 设置为用户的loginId
                        field.set(parameter, loginId);
                        // 设置字段为不可访问
                        field.setAccessible(false);
                    }
                    case "createTime", "updateTime" -> {
                        // 设置为当前时间
                        field.set(parameter, new Date());
                        // 设置字段为不可访问
                        field.setAccessible(false);
                    }
                    default ->
                        // 如果不是这三个字段，就直接将这个字段设置为不可访问，然后遍历下一个字段
                            field.setAccessible(false);
                }

            } catch (Exception e) {
                log.error("dealInsert.error:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 反射获取类的所有字段，并以字段数组的形式返回
     *
     * @param object
     * @return
     */
    private Field[] getAllFields(Object object) {
        // 获取当前对象对应类的Class对象
        Class<?> clazz = object.getClass();
        // 使用一个列表存储该类的所有字段信息
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            // 将字段信息放到字段列表中
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            // clazz指向父类
            clazz = clazz.getSuperclass();
        }
        // 循环完成后，就会将当前的对象对应的类以及父类的字段对象放到字段列表中
        // 创建一个Field类型的数组，大小跟字段列表相同
        Field[] fields = new Field[fieldList.size()];
        // 将字段列表的元素放到数组中
        fieldList.toArray(fields);
        return fields;
    }

    @Override
    public Object plugin(Object target) {
        // 返回一个代理对象，用于在方法调用时进行拦截处理
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 不需要任何处理
    }
}