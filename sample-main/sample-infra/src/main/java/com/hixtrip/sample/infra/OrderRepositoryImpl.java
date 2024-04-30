package com.hixtrip.sample.infra;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.order.repository.OrderRepository;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import com.hixtrip.sample.infra.db.convertor.OrderDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import com.hixtrip.sample.infra.db.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {


    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Order selectById(Long orderId) {
        OrderDO orderDO = orderMapper.selectById(orderId);
        return OrderDOConvertor.INSTANCE.doToDomain(orderDO);

    }

    @Override
    public int updateById(CommandPay commandPay) {
        OrderDO dbOrderDO = orderMapper.selectById(commandPay.getOrderId());
        if (dbOrderDO == null) {
            return 0;
        }
        OrderDO newOrderDO = OrderDOConvertor.INSTANCE.of(dbOrderDO, commandPay);
        return orderMapper.updateById(newOrderDO);
    }

    @Override
    @Transactional
    public Order create(Order order) {
        OrderDO orderDO = OrderDOConvertor.INSTANCE.domainToDo(order);
        orderDO.setId(getOrderId());
        orderDO.setUpdateBy(order.getUserId());
        int result = orderMapper.insert(orderDO);
        if (result <= 0) {
            throw new RuntimeException("新增订单失败");
        }
        return OrderDOConvertor.INSTANCE.doToDomain(orderDO);
    }

    /**
     * 这里默认使用mybatis的雪花算法工具
     * 如果需要支持基因法的分库分表，可在此调整id生成规则
     *
     * @return
     */
    private Long getOrderId() {
        return IdWorker.getId();
    }
}
