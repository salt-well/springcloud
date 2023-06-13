package com.yanjing.springcloud.service.impl;

import com.yanjing.springcloud.service.PaymentHystrixService;
import org.springframework.stereotype.Component;

/**
 * 类服务降级方法，和业务逻辑分开
 */

@Component //必须加 //必须加 //必须加
public class PaymentHystrixServiceFallback implements PaymentHystrixService
{

    @Override
    public String paymentInfo_OK(Integer id) {
        //return "服务调用失败，提示来自：cloud-consumer-feign-hystrix-order80";
        return "-----PaymentFallbackService fall back-paymentInfo_OK ,o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }
}
