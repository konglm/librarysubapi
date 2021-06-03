package com.jfnice.annotation;

import java.lang.annotation.*;

/**
 * 取消验证权限标签
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ShiroClear {

}
