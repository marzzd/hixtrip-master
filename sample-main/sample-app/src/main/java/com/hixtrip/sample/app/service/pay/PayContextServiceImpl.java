package com.hixtrip.sample.app.service.pay;

import com.hixtrip.sample.app.anno.PayResultAnno;
import com.hixtrip.sample.app.anno.PayStrategyAnno;
import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.api.PayContextService;
import com.hixtrip.sample.app.api.PayResultService;
import com.hixtrip.sample.app.api.PayStrategyService;
import com.hixtrip.sample.app.convertor.PayConvertor;
import com.hixtrip.sample.app.enmus.PayResult;
import com.hixtrip.sample.app.enmus.PayStatus;
import com.hixtrip.sample.app.service.pay.result.NotifyPayResultServiceImpl;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import com.hixtrip.sample.domain.pay.PayDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支付上下文
 * 通过上下文获取要调用的支付状态实现类与支付结果服务实现类
 */
@Slf4j
@Component
public class PayContextServiceImpl implements PayContextService {

    /**
     * 自动注入所有具体实现类
     */
    @Autowired
    private List<PayResultService> payResultServiceList;
    @Autowired
    private List<PayStrategyService> payStrategyServiceList;

    /**
     * 额外定义需通知
     */
    @Autowired
    @Qualifier("notifyPayResultService")
    private PayResultService notifyService;

    @Autowired
    private PayDomainService payDomainService;

    @Autowired
    private OrderService orderService;

    @Override
    public ResultVO process(CommandPayDTO payDTO) {
        try {
            OrderVO orderVO = paramCheck(payDTO);
            if (orderVO == null) {
                return notifyService.process(payDTO, null);
            }
            //根据支付回调返回的支付状态适配对应支付状态策略
            PayStrategyService payStrategyService = payStrategyServiceList.stream()
                    .filter(f -> f.getClass().getAnnotation(PayStrategyAnno.class).value().getCode().equals(payDTO.getPayStatus()))
                    .findFirst()
                    .orElse(null);
            if (payStrategyService == null) {
                log.error("检查订单，缺少支付策略服务实现，需通知运营人员介入确认");
                return notifyService.process(payDTO, orderVO);
            }
            PayResult payStatus = payStrategyService.process(payDTO, orderVO);

            // 用于适配支付结果后续操作所对应实现类
            PayResultService payResultService = payResultServiceList.stream()
                    .filter(f -> payStatus.equals(f.getClass().getAnnotation(PayResultAnno.class).value()))
                    .findFirst()
                    .orElse(notifyService);

            return payResultService.process(payDTO, orderVO);
        } finally {
            // 记录支付回调结果 最好包括回调执行完毕的操作结果
            payDomainService.payRecord(PayConvertor.INSTANCE.dtoToDomain(payDTO));
        }
    }

    /**
     * 参数前置检查
     *
     * @param payDTO
     * @return
     */
    private OrderVO paramCheck(CommandPayDTO payDTO) {
        // 这里校验可通过SpringValidator进行校验过滤
        if (payDTO == null || payDTO.getOrderId() == null || payDTO.getPayStatus() == null) {
            log.warn("检查订单，支付回调的入参为空，但需通知运营人员介入确认");
            return null;
        }
        if (PayStatus.convert(payDTO.getPayStatus()) == null) {
            log.info("检查订单：" + payDTO.getOrderId() + ",回调支付状态码：" + payDTO.getPayStatus() + ",回调支付状态异常,需通知运营人员介入确认");
            return null;
        }
        OrderVO order = orderService.getById(payDTO.getOrderId());
        if (order == null) {
            log.warn("检查订单：" + payDTO.getOrderId() + ",回调支付状态码：" + payDTO.getPayStatus() + ",数据库订单不存在，订单不做处理，但需通知运营人员介入确认");
            return null;
        }
        if (PayStatus.convert(order.getPayStatus()) == null) {
            log.info("检查订单：" + order.getId() + ",支付状态码：" + order.getPayStatus() + ",回调支付状态码：" + payDTO.getPayStatus() + ",数据库订单支付状态异常,需通知运营人员介入确认");
            return null;
        }
        return order;
    }


}