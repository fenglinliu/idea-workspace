package indi.flynn.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * FlynnAutowired
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/12
 */

@Target(ElementType.FIELD) // 表示注解在属性上使用
@Retention(RetentionPolicy.RUNTIME) // 在运行时生效
@Documented
public @interface FlynnAutowired {
    String value() default ""; // 设置注解的默认值
}
