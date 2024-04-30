package com.hixtrip.sample.domain.order.repository;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;

/**
 *
 */
public interface OrderRepository {

    Order selectById(Long orderId);

    int updateById(CommandPay commandPay);

    Order create(Order order);
}
