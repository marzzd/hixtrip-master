package com.hixtrip.sample.infra.db.convertor;

import com.hixtrip.sample.domain.order.model.Order;
import com.hixtrip.sample.domain.pay.model.CommandPay;
import com.hixtrip.sample.infra.db.dataobject.OrderDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * DO对像 -> 领域对象转换器
 * todo 自由实现
 */
@Mapper
public interface OrderDOConvertor {
    OrderDOConvertor INSTANCE = Mappers.getMapper(OrderDOConvertor.class);

    Order doToDomain(OrderDO orderDO);

    OrderDO domainToDo(Order order);

    @Mappings({
            @Mapping(target = "id", source = "dbOrder.id"),
            @Mapping(target = "payStatus", source = "order.payStatus"),
            @Mapping(target = "status", source = "order.status"),
            @Mapping(target = "payMethod", source = "order.payMethod"),
            @Mapping(target = "serialNo", source = "order.serialNo"),
            @Mapping(target = "payAmount", source = "order.payAmount")
    })
    OrderDO of(OrderDO dbOrder, CommandPay order);

}
