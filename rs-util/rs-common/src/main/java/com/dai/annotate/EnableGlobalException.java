package com.dai.annotate;

import com.dai.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AopAutoConfiguration.class, GlobalExceptionHandler.class})
public @interface EnableGlobalException {
}
