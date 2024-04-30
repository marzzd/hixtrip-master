package com.hixtrip.sample.app.service.pay.result;

import com.hixtrip.sample.app.anno.PayResultAnno;
import com.hixtrip.sample.app.api.PayResultService;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import org.springframework.stereotype.Component;

/**
 * 不做任何订单与库存之类的处理，会发送告警信息，，返回成功
 */
@PayResultAnno(value = PayResult.NOTIFY)
@Component("notifyPayResultService")
public class NotifyPayResultServiceImpl implements PayResultService {
    @Override
    public ResultVO process(CommandPayDTO payDTO, OrderVO orderVO) {
        // 不做处理的订单需要发送告警信息通知运营人员接入处理
        sendNotify(payDTO, "订单数据异常");
        return ResultVO.success();
    }
}
