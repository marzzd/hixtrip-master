package com.hixtrip.sample.app.service.pay.result;

import com.hixtrip.sample.app.anno.PayResultAnno;
import com.hixtrip.sample.app.api.PayResultService;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 重复支付后续处理
 * 这里的重复支付是针对订单号相同，但是第三方流水号不同进行判断的，
 * 也就是说用户针对同一笔订单支付了多笔金额，但是实际库存只会成功扣减1次
 * 所以只需退款即可，无需修改库存
 */
@Slf4j
@PayResultAnno(value = PayResult.REPEAT)
@Component
public class RepeatPayResultServiceImpl implements PayResultService {


    @Override
    public ResultVO process(CommandPayDTO payDTO, OrderVO orderVO) {
        // 重复支付可以直接退款，或者通知运营人员直接介入处理
        try {
            if (!refund(payDTO)) {
                // 退款失败通知运营人员介入处理
                sendNotify(payDTO, "重复支付，且退款失败");
            }
        } catch (Exception e) {
            log.error("重复支付，且退款异常", e);
            sendNotify(payDTO, "重复支付，且退款异常");
        }
        // 执行重复支付所需的任何其他操作
        return ResultVO.success();
    }

    /**
     * 调用第三方进行退款
     */
    private boolean refund(CommandPayDTO payDTO) {
        // 记录退款数据
        // 调用第三方退款，等待返回结果
        // 更新退款状态
        return true;
    }
}
