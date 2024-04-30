package com.hixtrip.sample.app.api;

import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import org.springframework.util.Assert;

/**
 * 支付结果服务
 */
public interface PayResultService {

    ResultVO process(CommandPayDTO payDTO, OrderVO orderVO);

    default void sendNotify(CommandPayDTO payDTO, String msg) {
        //消息通知 payDTO可能为null，如需调用该参数，则需做判空验证
        Assert.notNull(payDTO, "payDTO is null");
        System.out.println("消息通知:" + msg);
    }
}
