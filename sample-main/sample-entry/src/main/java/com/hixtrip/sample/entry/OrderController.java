package com.hixtrip.sample.entry;

import com.hixtrip.sample.app.api.OrderService;
import com.hixtrip.sample.app.api.PayContextService;
import com.hixtrip.sample.client.ResultVO;
import com.hixtrip.sample.client.order.dto.CommandOderCreateDTO;
import com.hixtrip.sample.client.order.dto.CommandPayDTO;
import com.hixtrip.sample.client.order.vo.OrderVO;
import com.hixtrip.sample.infra.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * todo 这是你要实现的
 */
@Slf4j
@RestController
public class OrderController {


    @Autowired
    private PayContextService payContextService;
    @Autowired
    private OrderService orderService;


    // 秒杀活动缓存key，格式为"HIXTRIP:SEC_KILL:SKUID:111":{"STATUS":0|1|2,"ENDTIME":"2024-5-1 00:00:00"}
    // STATUS 0:未开始 1:进行中 2:已结束
    // ENDTIME 秒杀活动结束时间，到秒
    // 活动状态的变更可通过mq或DelayQueue等方式触发
    private static final String SEC_KILL_START_KEY = "HIXTRIP:SEC_KILL:SKUID:";
    // 活动默认结束间隔
    private static final Long SEC_KILL_END = 3600L;

    // 已秒杀的用户缓存key 格式"HIXTRIP:SEC_KILL:SKUID:111:USERID":{"11":1,"12":1}
    // 判断该活动已下单人员缓存，避免重复下单
    private static final String SEC_KILL_USER_KEY = ":USERID";


    /**
     * todo 这是你要实现的接口
     * 在秒杀场景下，前端拦截，动静态数据分离，nginx限流等方式从而减少服务器请求压力,
     * 一般只允许1个用户下一笔订单，所以可以根据用户id进行过滤
     * CommandOderCreateDTO 增加SpringValid基础参数校验，这里就不补充了
     *
     * @param createDTO 入参对象
     * @return 请修改出参对象
     */
    @PostMapping(path = "/command/order/create")
    public ResultVO order(@RequestBody /* @Valid */ CommandOderCreateDTO createDTO) {
        //登录信息可以在这里模拟 模拟可能存在单用户多次下单
        Random randomno = new Random();
        long userId = randomno.nextInt(100000);
        createDTO.setUserId(userId);
        ResultVO result = checkSeckill(createDTO.getSkuId(), createDTO.getUserId());
        if (!result.isSuccess()) {
            return result;
        }
        createDTO.setUserId(userId);
        try {
            OrderVO orderVO = orderService.placeOrder(createDTO);
            return ResultVO.success("下单成功", orderVO);
        } catch (Exception e) {
            log.error("下单失败，", e);
            //下单未成功需要回滚掉相应缓存
            CacheUtil.incrementMapValue(SEC_KILL_START_KEY + createDTO.getSkuId() + SEC_KILL_USER_KEY, createDTO.getUserId() + "",-1L);
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 秒杀场景过滤
     *
     * @return
     */
    private ResultVO checkSeckill(Long skuId, Long userId) {
        //可添加skuId的布隆过滤器，用于过滤非秒杀商品
        Integer status = CacheUtil.getCacheMapValue(SEC_KILL_START_KEY + skuId, "STATUS");
        // 秒杀活动还未开始
        if (status == null || status == 0) {
            return ResultVO.fail("秒杀活动还未开始");
        } else if (status > 1) {
            return ResultVO.fail("秒杀活动已结束");
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endTimeStr = CacheUtil.getCacheMapValue(SEC_KILL_START_KEY + skuId, "ENDTIME");
        // 临时调整
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(SEC_KILL_END);
        if(endTimeStr == null || endTimeStr.isBlank()){
            endTimeStr = endTime.format(df);
            CacheUtil.setMapValue(SEC_KILL_START_KEY + skuId, "ENDTIME", endTimeStr);
        }
        endTime = LocalDateTime.parse(endTimeStr, df);
        if (endTime.isBefore(LocalDateTime.now())) {
            CacheUtil.setMapValue(SEC_KILL_START_KEY + skuId, "STATUS", 2);
            return ResultVO.fail("秒杀活动已结束");
        }
        if (CacheUtil.incrementMapValue(SEC_KILL_START_KEY + skuId + SEC_KILL_USER_KEY, userId + "",1L) > 1) {
            return ResultVO.fail("请勿重复点击");
        }
        // 限制同一个用户重复访问
        return ResultVO.success();
    }

    /**
     * todo 这是模拟创建订单后，支付结果的回调通知
     * 【中、高级要求】需要使用策略模式处理至少三种场景：支付成功、支付失败、重复支付(自行设计回调报文进行重复判定)
     *
     * @param commandPayDTO 入参对象
     * @return 请修改出参对象
     */
    @PostMapping(path = "/command/order/pay/callback")
    public String payCallback(@RequestBody CommandPayDTO commandPayDTO) {
        if (!signatureCheck(commandPayDTO)) {
            return "FAIL";
        }
        ResultVO payVO = payContextService.process(commandPayDTO);
        if (payVO.isSuccess()) {
            return "SUCCESS";
        }
        return "FAIL";
    }

    /**
     * 验签在此补充
     *
     * @param commandPayDTO
     * @return
     */
    private boolean signatureCheck(CommandPayDTO commandPayDTO) {
        return true;
    }

}
