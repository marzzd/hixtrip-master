package com.hixtrip.sample.app.service.pay.strategy;

import com.hixtrip.sample.app.anno.PayStrategyAnno;
import com.hixtrip.sample.app.api.PayStrategyService;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.app.enmus.PayStatus;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付回调订单支付状态为待支付时的服务
 */
@Slf4j
@PayStrategyAnno(value = PayStatus.WAIT_PAY)
@Component
public class WaitPayStrategyServiceImpl implements PayStrategyService {

    @Override
    public PayResult process(CommandPayDTO payDTO, OrderVO orderVO) {
        String orderPayStatusCode = orderVO.getPayStatus();
        if (payStatusIsFail(orderPayStatusCode) || payStatusIsSuccess(orderPayStatusCode)) {
            log.warn("检查订单：" + orderVO.getId() + ",支付状态码：" + orderVO.getPayStatus() + ",回调支付状态码：" + payDTO.getPayStatus() + ",回调支付失败，数据库订单为支付失败或支付成功，通知运营人员介入处理");
            return PayResult.NOTIFY;
        }
        log.info("检查订单：" + orderVO.getId() + ",支付状态码：" + orderVO.getPayStatus() + ",回调支付状态码：" + payDTO.getPayStatus() + ",回调待支付，数据库订单为待支付，不做处理");
        return PayResult.NO_OPERATE;
    }
}
