package com.hixtrip.sample.client.order.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 创建订单的请求 入参
 */
@Data
@Builder
public class OrderVO {
    /**
     * 订单号
     */
    private String id;
    /**
     * 商品规格id
     */
    private Long skuId;

    /**
     * 购买数量
     */
    private Long num;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 支付状态
     */
    private String payStatus;
    /**
     * 第三方支付流水号
     */
    private String serialNo;

    /**
     * 第三方支付回调
     */
    private String payUrl;
    /**
     * 订单状态 0待付款 1待发货 2待收货 3待评价 4已取消 5退款 6已完成
     */
    private String status;

}
