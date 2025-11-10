package com.rs.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AspectUtils {

    private static final Pattern SPEL_TEMPLATE_PATTERN = Pattern.compile("#\\{([^}]*)}");


    /**
     * 获取被拦截方法对象
     * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     * 所以应该使用反射获取当前对象的方法对象
     */
    public static Method getMethod(ProceedingJoinPoint pjp) {
        //获取参数的类型
        Signature sig = pjp.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) sig;
            return methodSignature.getMethod();
        } else {
            throw new IllegalArgumentException("It's not method");
        }
    }

    /**
     * 在aop切面中SPEL表达式对formatter进行格式化，
     * 转换成指定的值
     *
     * @param formatter
     * @param method
     * @param args
     * @return
     */
    public static String parse(String formatter, Method method, Object[] args) throws Exception {
        if (formatter == null || !formatter.contains("#{")) {
            return formatter;
        }

        Parameter[] parameters = method.getParameters();
        String expr = extractExpression(formatter);
        if (expr == null) {
            return formatter;
        }

        String replacement = null;

        // 情况1: 简单参数引用，如 #{orderId}
        if (!expr.contains("(")) {
            // 可能是 orderId 或 order.id（但你目前不支持属性链，先按简单变量处理）
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getName().equals(expr)) {
                    Object value = args[i];
                    if (value != null && isAllowedType(value.getClass())) {
                        throw new IllegalArgumentException("参数 '" + expr + "' 类型不支持: " + value.getClass());
                    }
                    replacement = String.valueOf(value);
                    break;
                }
            }
        }
        // 情况2: 方法调用，如 #{order.getOrderId()}
        else if (expr.matches("[a-zA-Z_][a-zA-Z0-9_]*\\.([a-zA-Z_][a-zA-Z0-9_]*)\\(\\)")) {
            String[] parts = expr.split("\\.", 2);
            String paramName = parts[0];          // "order"
            String methodWithParen = parts[1];    // "getOrderId()"

            // 去掉括号，得到方法名
            String methodName = methodWithParen.substring(0, methodWithParen.length() - 2); // 移除 "()"

            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getName().equals(paramName)) {
                    Method methodToInvoke = args[i].getClass().getMethod(methodName);
                    Object result = methodToInvoke.invoke(args[i]);
                    if (result != null && isAllowedType(result.getClass())) {
                        throw new IllegalArgumentException("方法 '" + methodName + "' 返回类型不支持: " + result.getClass());
                    }
                    replacement = String.valueOf(result);
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("不支持的 SpEL 表达式格式: " + expr);
        }

        if (replacement == null) {
            throw new IllegalArgumentException("未找到匹配的参数或方法: " + expr);
        }

        return formatter.replace("#{" + expr + "}", replacement);
    }

    /**
     * 提取第一个 #{...} 中的表达式内容（不包含 #{ 和 }）
     */
    public static String extractExpression(String formatter) {
        Matcher matcher = SPEL_TEMPLATE_PATTERN.matcher(formatter);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * 判断参数类型是否允许
     *
     * @param clazz 参数类型
     * @return 是否允许
     */
    private static boolean isAllowedType(Class<?> clazz) {
        return clazz != String.class &&
                clazz != Long.class && clazz != long.class &&
                clazz != Integer.class && clazz != int.class &&
                clazz != Short.class && clazz != short.class &&
                clazz != Byte.class && clazz != byte.class &&
                clazz != Character.class && clazz != char.class &&
                clazz != Double.class && clazz != double.class &&
                clazz != Float.class && clazz != float.class;
    }
}
