package com.hixtrip.sample.app.api;

import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.app.enmus.PayStatus;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;

/**
 * 支付状态策略服务
 */
public interface PayStrategyService {

    PayResult process(CommandPayDTO payDTO, OrderVO orderVO);

    default boolean payStatusIsFail(String payStatus){
        return PayStatus.PAY_FAIL.getCode().equals(payStatus);
    }
    default boolean payStatusIsSuccess(String payStatus){
        return PayStatus.PAY_SUCCESS.getCode().equals(payStatus);
    }
    default boolean payStatusIsWait(String payStatus){
        return PayStatus.WAIT_PAY.getCode().equals(payStatus);
    }
}
