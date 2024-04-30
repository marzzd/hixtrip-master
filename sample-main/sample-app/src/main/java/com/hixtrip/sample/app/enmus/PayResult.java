package com.hixtrip.sample.app.enmus;

import lombok.Getter;

/**
 * 支付结果枚举
 */
@Getter
public enum PayResult {

    // 支付成功
    SUCCESS("0"),
    // 支付失败
    FAIL("100"),
    // 重复支付
    REPEAT("200"),
    // 不做处理
    NO_OPERATE("500"),
    // 通知运营人员介入处理
    NOTIFY("-1");

    private final String code;

    PayResult(String code) {
        this.code = code;
    }

    public static PayResult convert(String code) {
        for (PayResult status : PayResult.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        System.out.println("不支持的支付结果返回NO_SUPPORT");
        return NOTIFY;
    }
}
