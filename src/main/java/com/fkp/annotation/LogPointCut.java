package com.fkp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志切点注解，标注此方法或类中所有方法为日志切点
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogPointCut {

    /**
     * 操作编码
     * @return
     */
    String operationCode() default "";

    /**
     * 操作描述
     * @return
     */
    String operationDesc() default "";
}
