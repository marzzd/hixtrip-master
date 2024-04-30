package com.hixtrip.sample.app.anno;

import com.hixtrip.sample.app.enmus.PayStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 支付状态策略注解
 */
@Target({ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface PayStrategyAnno {
    PayStatus value() default PayStatus.PAY_SUCCESS;

}
