package indi.flynn.mvc.framework.annotation;

import java.lang.annotation.*;

/**
 * FlynnController
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/9/12
 */

@Target(ElementType.TYPE) // 表示注解在类、接口上使用
@Retention(RetentionPolicy.RUNTIME) // 在运行时生效
@Documented
public @interface FlynnController {
    String value() default ""; // 设置注解的默认值
}
