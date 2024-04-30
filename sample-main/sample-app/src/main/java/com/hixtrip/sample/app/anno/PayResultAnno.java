package com.hixtrip.sample.app.anno;

import com.hixtrip.sample.app.enmus.PayResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 支付结果注解
 */
@Target({ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface PayResultAnno {
    PayResult value() default PayResult.SUCCESS;

}
