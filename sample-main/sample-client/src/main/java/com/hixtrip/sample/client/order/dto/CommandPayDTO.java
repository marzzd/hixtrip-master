package com.hixtrip.sample.client.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付回调的入参
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommandPayDTO {

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

}
