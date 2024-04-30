package com.hixtrip.sample.app.api;

import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;

/**
 * 订单的service层
 */
public interface OrderService {

    OrderVO getById(Long orderId);

    Boolean orderPaySuccess(CommandPayDTO payDTO);

    Boolean orderPayFail(CommandPayDTO payDTO);

    OrderVO placeOrder(CommandOderCreateDTO orderDTO);
}
