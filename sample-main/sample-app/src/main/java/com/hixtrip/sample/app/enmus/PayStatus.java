package com.hixtrip.sample.app.enmus;

import lombok.Getter;

/**
 * 支付状态
 */
@Getter
public enum PayStatus {

    //待支付
    WAIT_PAY("0"),
    //支付成功
    PAY_SUCCESS("1"),
    //支付失败
    PAY_FAIL("2");

    private final String code;

    PayStatus(String code) {
        this.code = code;
    }

    public static PayStatus convert(String code) {
        for (PayStatus status : PayStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        System.out.println("未存在的支付类型返回空");
        return null;
    }
}
