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
 * 支付回调订单支付状态为支付成功时的服务
 */
@Slf4j
@PayStrategyAnno(value = PayStatus.PAY_SUCCESS)
@Component
public class SuccessPayStrategyServiceImpl implements PayStrategyService {

    @Override
    public PayResult process(CommandPayDTO payDTO, OrderVO orderVO) {
        String orderPayStatusCode = orderVO.getPayStatus();
        if (payStatusIsFail(orderPayStatusCode)
                || payStatusIsWait(orderPayStatusCode)) {
            log.info("检查订单：" + orderVO.getId() + ",支付状态码：" + orderVO.getPayStatus() +
                    ",回调支付状态码：" + payDTO.getPayStatus() + ",回调支付成功，数据库订单为待支付或支付失败，现支付成功");
            return PayResult.SUCCESS;
        }
        // 这里判断重复支付暂时通过第三方流水号以及自身订单id进行验证
        if (payStatusIsSuccess(orderPayStatusCode) && orderVO.getId().equals(payDTO.getOrderId())
                && orderVO.getSerialNo() != null && !orderVO.getSerialNo().equals(payDTO.getSerialNo())) {
            log.error("检查订单：" + orderVO.getId() + ",支付状态码：" + orderVO.getPayStatus() +
                    ",回调支付状态码：" + payDTO.getPayStatus() + ",回调支付成功，数据库订单已支付，订单号一致，" +
                    "但流水号不一致，表示用户重复支付");
            return PayResult.REPEAT;
        }
        // 正常来说需要直接调用第三方账单查询接口进行确认，看是否是同一笔订单支付了2次，或者需要通知运营人员介入确认
        log.info("检查订单：" + orderVO.getId() + ",支付状态码：" + orderVO.getPayStatus() +
                ",回调支付状态码：" + payDTO.getPayStatus() + ",回调支付成功，数据库订单已支付，流水号一致，" +
                "有可能是重复支付，也可能是支付1次但是第三方重复发送消息");
        return PayResult.NOTIFY;
    }
}
