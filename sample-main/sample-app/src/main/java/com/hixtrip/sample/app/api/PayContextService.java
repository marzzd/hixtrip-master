package com.hixtrip.sample.app.api;

import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;

/**
 * 支付结果策略模式接口
 */
public interface PayContextService {
    ResultVO process(CommandPayDTO payDTO);
}
