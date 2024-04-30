package com.hixtrip.sample.domain.order;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单领域服务
 * todo 只需要实现创建订单即可
 */
@Component
public class OrderDomainService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * todo 需要实现
     * 创建待付款订单
     */
    public Order createOrder(Order order) {
        //需要你在infra实现, 自行定义出入参
        return orderRepository.create(order);
    }

    /**
     * todo 需要实现
     * 待付款订单支付成功
     */
    public Boolean orderPaySuccess(CommandPay commandPay) {
        //需要你在infra实现, 自行定义出入参
        return orderRepository.updateById(commandPay) > 0;
    }

    /**
     * todo 需要实现
     * 待付款订单支付失败
     */
    public Boolean orderPayFail(CommandPay commandPay) {
        //需要你在infra实现, 自行定义出入参
        return orderRepository.updateById(commandPay) > 0;
    }

    public Order getById(Long orderId) {
        return orderRepository.selectById(orderId);
    }
}
