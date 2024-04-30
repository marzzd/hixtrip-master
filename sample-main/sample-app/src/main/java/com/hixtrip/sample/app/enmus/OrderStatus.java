package com.hixtrip.sample.app.enmus;

import lombok.Getter;

/**
 * 订单状态
 */
@Getter
public enum OrderStatus {


    // 待付款
    WAIT_PAY("0"),
    // 待发货
    WAIT_DELIVER("1"),
    // 待收货
    WAIT_RECEIVE("2"),
    // 待评价
    WAIT_EVALUATE("3"),
    // 已取消
    CANCEL("4"),
    // 已退款
    REFUND("5"),
    // 已完成
    DONE("6");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }
}
