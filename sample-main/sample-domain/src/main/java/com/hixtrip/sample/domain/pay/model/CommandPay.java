package com.hixtrip.sample.domain.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandPay {


    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 支付方式 1微信,2支付宝
     */
    private String payMethod;
    /**
     * 支付流水号
     */
    private String serialNo;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 订单状态 0待付款 1待发货 2待收货 3待评价 4已取消 5退款 6已完成
     */
    private String status;
}