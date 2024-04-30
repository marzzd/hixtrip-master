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
 * 支付成功后续处理
 */
@Slf4j
@PayResultAnno(value = PayResult.SUCCESS)
@Component
public class SuccessPayResultServiceImpl implements PayResultService {

    @Autowired
    private OrderService orderService;


    @Override
    public ResultVO process(CommandPayDTO payDTO, OrderVO orderVO) {
        try{
            if(!orderService.orderPaySuccess(payDTO)){
                //订单更新失败，发送告警
                sendNotify(payDTO, "支付成功,但修改订单状态失败");
            }
        }catch (Exception e){
            log.error("支付成功,但修改订单状态异常",e);
            sendNotify(payDTO, "支付成功,但修改订单状态异常");
        }
        return ResultVO.success();
    }


}
