package com.yanjing.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yanjing.springcloud.service.PaymentHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod",commandProperties = {
        @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
})
public class OrderHystirxController
{

    @Resource
    private PaymentHystrixService paymentHystrixService;

    /**
     * 此时服务端provider已经down了，但是我们做了服务降级处理， 让客户端在服务端不可用时也会获得提示信息而不会挂起耗死服务器
     */

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id)
    {
        String result = paymentHystrixService.paymentInfo_OK(id);
        return result;
    }

    /**
     * 默认Feign客户端只等待一秒钟，但是服务端处理需要超过1秒钟(设置服务端hystrix8001超时时间为3秒)，导致Feign客户端不想等待了，直接返回报错。
     * 为了避免这样的情况，有时候我们需要设置Feign客户端的超时控制。
     * yml文件中开启配置
     * #设置feign客户端超时时间(OpenFeign默认支持ribbon)
     */
    /*//(1)加这个，用自己的服务降级
    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1500")
    })
    //value="1500":1.5秒支付模块hystrix-payment8001没响应服务降级调用paymentTimeOutFallbackMethod*/
    //(2)加了@DefaultProperties属性注解，并且没有写具体方法名字，就用统一全局的
    @HystrixCommand
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id)
    {
        String result = paymentHystrixService.paymentInfo_TimeOut(id);
        return result;
    }

    public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id)
    {
        return "我是hystrix消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }

    public String payment_Global_FallbackMethod()
    {
        return "Global异常处理信息，请稍后再试，/(ㄒoㄒ)/~~";
    }

}
