package indi.flynn.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * FlynnRequestParameter
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/12
 */

@Target(ElementType.PARAMETER) // 表示注解在方法参数上使用
@Retention(RetentionPolicy.RUNTIME) // 在运行时生效
@Documented
public @interface FlynnRequestParameter {
    String value() default ""; // 设置注解的默认值

    boolean required() default true; // 表示这个参数是不是必填
}
