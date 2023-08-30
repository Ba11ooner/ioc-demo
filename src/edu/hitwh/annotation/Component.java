package edu.hitwh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识当前类为 Bean
 */
@Target(ElementType.TYPE) // 作用域为 类
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    //Bean 的名称，默认为空串
    //若不指定，则在后续处理时以类名作名称
    String value() default "";
}
