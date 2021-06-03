package com.school.library.kit;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JsyAddDelEdit {

    /**
     * 是否需要有主键
     * @return
     */
    boolean requirePrimary() default true;
    
    /**
     * 主键字段名
     * @return
     */
    String primaryParam() default "";

    /**
     * 是否对多条数据进行操作
     * @return
     */
    boolean multi() default false;
}
