package com.hixtrip.sample.app.service.pay.result;

import com.hixtrip.sample.app.anno.PayResultAnno;
import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.api.PayResultService;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付失败后续处理
 * 订单支付失败，更新订单状态与第三方支付相关字段
 * 暂不处理库存数据，只有当订单支付成功或者超时后再扣除实际库存
 */
@Slf4j
@PayResultAnno(value = PayResult.FAIL)
@Component
public class FailPayResultServiceImpl implements PayResultService {

    @Autowired
    private OrderService orderService;

    @Override
    public ResultVO process(CommandPayDTO payDTO, OrderVO orderVO) {
        // 更新订单状态与第三方流水号，更新成功则返回更新成功，更新失败则发送通知
        try {
            if (!orderService.orderPayFail(payDTO)) {
                sendNotify(payDTO, "支付失败,且修改订单状态失败");
            }
        } catch (Exception e) {
            log.error("支付失败,且修改订单状态异常", e);
            sendNotify(payDTO, "支付失败,且修改订单状态异常");
        }
        return ResultVO.success();
    }
}
