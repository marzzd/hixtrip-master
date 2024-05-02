package com.hixtrip.sample.app.service.order;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.convertor.OrderConvertor;
import com.hixtrip.sample.app.convertor.PayConvertor;
import com.hixtrip.sample.app.enmus.OrderStatus;
import com.hixtrip.sample.app.enmus.PayStatus;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import com.hixtrip.sample.domain.inventory.InventoryDomainService;
import com.hixtrip.sample.domain.order.OrderDomainService;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

/**
 * app层负责处理request请求，调用领域服务
 */
@Slf4j
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDomainService orderDomainService;
    @Autowired
    private InventoryDomainService inventoryDomainService;

    @Override
    public OrderVO getById(Long orderId) {
        Order order = orderDomainService.getById(orderId);
        return OrderConvertor.INSTANCE.toOrderVO(order);
    }

    /**
     * 订单支付成功
     *
     * @param payDTO
     * @return
     */
    @Override
    public Boolean orderPaySuccess(CommandPayDTO payDTO) {
        OrderVO orderVO = getById(payDTO.getOrderId());
        if (orderVO == null) {
            log.error("无效订单:" + payDTO.getOrderId());
            return Boolean.FALSE;
        }
        if (inventoryDomainService.changeInventory(orderVO.getSkuId(), 0L, -orderVO.getNum(), orderVO.getNum())) {
            try {
                CommandPay payOrder = PayConvertor.INSTANCE.dtoToDomain(payDTO);
                payOrder.setPayStatus(PayStatus.PAY_SUCCESS.getCode());
                payOrder.setStatus(OrderStatus.WAIT_DELIVER.getCode());
                return orderDomainService.orderPaySuccess(payOrder);
            } catch (Exception e) {
                log.error("更新订单失败，", e);
                // 库存回滚
                inventoryDomainService.changeInventory(orderVO.getSkuId(), 0L, orderVO.getNum(), -orderVO.getNum());
                throw e;
            }
        }
        log.error("库存扣减失败");
        return Boolean.FALSE;
    }

    /**
     * 订单支付失败
     *
     * @param payDTO
     * @return
     */
    @Override
    public Boolean orderPayFail(CommandPayDTO payDTO) {
        OrderVO orderVO = getById(payDTO.getOrderId());
        if (orderVO == null) {
            log.error("无效订单:" + payDTO.getOrderId());
            return Boolean.FALSE;
        }
        CommandPay commandPay = PayConvertor.INSTANCE.dtoToDomain(payDTO);
        commandPay.setPayStatus(PayStatus.PAY_FAIL.getCode());
        commandPay.setStatus(orderVO.getStatus());
        return orderDomainService.orderPayFail(commandPay);
    }


    /**
     * 下单
     *
     * @param orderDTO
     * @return
     */
    @Override
    public OrderVO placeOrder(CommandOderCreateDTO orderDTO) {
        Long inventory = inventoryDomainService.getInventory(orderDTO.getSkuId());
        if (inventory < orderDTO.getNum()) {
            throw new RuntimeException("库存不足");
        }
        if (inventoryDomainService.changeInventory(orderDTO.getSkuId(), -orderDTO.getNum(), orderDTO.getNum(), 0L)) {
            try {
                Order order = OrderConvertor.INSTANCE.toDomain(orderDTO);
                /**
                 * 根据skuid获取对应商品信息与卖家信息，并填充至order对象,可从缓存获取，
                 * 金额计算并填充至order中，设置第三方支付回调地址，
                 * 订单创建成功后，可发送mq消息用于后续操作，如优惠卷赠送，会员积分等
                 */
                order.setUserName(order.getUserId() + "name");
                order.setSellerId((long) (new Random().nextInt(10)));
                order.setGoodsSku(order.getSkuId() + "setGoodsSku");
                order.setGoodsTitle(order.getSkuId() + "setGoodsTitle");
                order.setGoodsSkuPic(order.getSkuId() + "setGoodsSkuPic");
                order.setSkuPrice(new BigDecimal(100));
                order.setTotalAmount(new BigDecimal(100).multiply(new BigDecimal(order.getNum())));
                order.setUpdateBy(order.getUserId());
                order.setPayUrl("https://xxx.com/command/order/pay/callback");
                return OrderConvertor.INSTANCE.toOrderVO(orderDomainService.createOrder(order));
            } catch (Exception e) {
                log.error("下单失败，", e);
                // 库存回滚
                inventoryDomainService.changeInventory(orderDTO.getSkuId(), orderDTO.getNum(), -orderDTO.getNum(), 0L);
                throw e;
            }
        }
        throw new RuntimeException("库存扣减失败");
    }


}
