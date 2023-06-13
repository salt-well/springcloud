package com.yanjing.springcloud.controller;

import com.yanjing.springcloud.entities.CommonResult;
import com.yanjing.springcloud.entities.Payment;
import com.yanjing.springcloud.service.PaymentFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OrderFeignController
{
    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping(value = "/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id)
    {
        return paymentFeignService.getPaymentById(id);
    }

    /**
     * 默认Feign客户端只等待一秒钟，但是服务端处理需要超过1秒钟(设置服务端8001、8002超时时间为3秒)，导致Feign客户端不想等待了，直接返回报错。
     * 为了避免这样的情况，有时候我们需要设置Feign客户端的超时控制。
     * yml文件中开启配置
     * #设置feign客户端超时时间(OpenFeign默认支持ribbon)
     */
    @GetMapping(value = "/consumer/payment/feign/timeout")
    public String paymentTimeOut(){
        return paymentFeignService.paymentFeignTimeOut();
    }
}
