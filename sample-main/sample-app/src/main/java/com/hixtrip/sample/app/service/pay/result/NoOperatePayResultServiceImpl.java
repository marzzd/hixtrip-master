package com.hixtrip.sample.app.service.pay.result;

import com.hixtrip.sample.app.anno.PayResultAnno;
import com.hixtrip.sample.app.api.PayResultService;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import org.springframework.stereotype.Component;

/**
 * 不做任何订单与库存之类的处理，返回成功
 */
@PayResultAnno(value = PayResult.NO_OPERATE)
@Component
public class NoOperatePayResultServiceImpl implements PayResultService {
    @Override
    public ResultVO process(CommandPayDTO payDTO, OrderVO orderVO) {
        return ResultVO.success();
    }
}
