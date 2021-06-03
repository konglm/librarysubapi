package com.jfnice.annotation;

import com.jfnice.enums.Logical;
import com.jfnice.enums.OpCodeEnum;

import java.lang.annotation.*;

/**
 * 权限校验标签
 * @author jsy
 * @date 2020/09/02
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JsyPermissions {

    // 权限控制符
    OpCodeEnum[] value() default {};

    // 是否是获取conPara
    boolean condPara() default false;

    // 控制符之间是and还是or
    Logical logical() default Logical.AND;

    // 年级id的参数名
    String grdCodeParaName() default "ignore";

    // 班级id的参数名
    String clsCodeParaName() default "ignore";

    // 科目代码的参数名
    String subCodeParaName() default "ignore";

    // 学生id的参数名
    String stuCodeParaName() default "ignore";

    /**
     * 是否直接通过此校验
     * @return
     */
    boolean pass() default false;

}
