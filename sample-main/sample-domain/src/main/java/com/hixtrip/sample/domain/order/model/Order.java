package com.hixtrip.sample.domain.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder(toBuilder = true)
public class Order {

    /**
     * 订单id
     */
    //主键的生成需要基于买家id的基因法生成
    private Long id;
    /**
     * 买家id
     */
    private Long userId;
    /**
     * 卖家id
     */
    private Long sellerId;
    /**
     * 下单的skuId
     */
    private Long skuId;
    /**
     * 买家名称
     */
    private String userName;

    /**
     * 商品标题
     */
    private String goodsTitle;
    /**
     * 商品规格内容
     */
    private String goodsSku;
    /**
     * 商品规格图片地址
     */
    private String goodsSkuPic;
    /**
     * 订单数量
     */
    private Long num;

    /**
     * sku单价
     */
    private BigDecimal skuPrice;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付状态 0未支付 1支付成功 2支付失败
     */
    private String payStatus;

    /**
     * 第三方支付回调
     */
    private String payUrl;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 支付方式 1微信,2支付宝
     */
    private String payMethod;

    /**
     * 第三方流水号
     */
    private String serialNo;
    /**
     * 订单状态 0待付款 1待发货 2待收货 3待评价 4已取消 5退款 6已完成
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收货人
     */
    private String consignee;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 地址
     */
    private String address;
    /**
     * 订单取消时间
     */
    private LocalDateTime cancelTime;
    /**
     * 订单取消原因
     */
    private String cancelReason;

    /**
     * 订单拒绝原因
     */
    private String rejectionReason;

    /**
     * 投诉状态 0无投诉 1已投诉
     */
    private String complaintStatus;
    /**
     * 投诉原因
     */
    private String complaintReason;
    /**
     * 送达时间
     */
    private LocalDateTime deliveryTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    /**
     * 最后更新人id
     */
    private Long updateBy;
}
