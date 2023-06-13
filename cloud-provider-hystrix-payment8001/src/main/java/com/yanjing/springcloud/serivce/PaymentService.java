package com.yanjing.springcloud.serivce;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService
{
    /**
     * 正常访问，一切OK
     * @param id
     * @return
     */
    public String paymentInfo_OK(Integer id)
    {
        return "线程池:"+Thread.currentThread().getName()+"paymentInfo_OK,id: "+id+"\t"+"O(∩_∩)O";
    }

    /**
     * 1.超时访问，演示降级（服务降级）
     * 下面故意制造两个异常：
     *    1.  int age = 10/0; 计算异常
     *    2.  我们能接受3秒钟，它运行4秒钟，超时异常。
     *
     *    当前服务不可用了，做服务降级，兜底的方案都是paymentInfo_TimeOutHandler
     */
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    public String paymentInfo_TimeOut(Integer id)
    {
        //上面@HystrixProperty设置超时为3秒
        //这里以下设置实际运行时间为4秒
        int second = 2;
        try { TimeUnit.SECONDS.sleep(second); } catch (InterruptedException e) { e.printStackTrace(); }
        return "线程池:"+Thread.currentThread().getName()+"paymentInfo_TimeOut,id: "+id+"\t"+"O(∩_∩)O，耗费"+second+"秒";
    }

    public String paymentInfo_TimeOutHandler(Integer id){
        return "/(ㄒoㄒ)/调用支付接口超时或异常：\t"+ "\t当前线程池名字" + Thread.currentThread().getName() + "\t 线程超时!!";
    }

    /**
     * 2.服务熔断
     * 涉及到断路器的三个重要参数：快照时间窗、请求总数阀值、错误百分比阀值。
     * 1：快照时间窗：断路器确定是否打开需要统计一些请求和错误数据，而统计的时间范围就是快照时间窗，默认为最近的10秒。
     * 2：请求总数阀值：在快照时间窗内，必须满足请求总数阀值才有资格熔断。默认为20，意味着在10秒内，如果该hystrix命令的调用次数不足20次，即使所有的请求都超时或其他原因失败，断路器都不会打开。
     * 3：错误百分比阀值：当请求总数在快照时间窗内超过了阀值，比如发生了30次调用，如果在这30次调用中，有15次发生了超时异常，也就是超过50%的错误百分比，在默认设定50%阀值情况下，这时候就会将断路器打开
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",
    commandProperties = {
            //开启断路器
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),
            //请求阀值：10秒内10次
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),
            //快照时间窗,10秒内
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),
            //错误百分比阀值：10秒内10次请求有60%错误，熔断打开
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),
   })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id){
        if(id < 0 ){
            throw  new RuntimeException("*******id 不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName()+"/t"+"调用成功,流水号："+serialNumber;
    }


    public String paymentCircuitBreaker_fallback(@PathVariable("id") Integer id){
        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
    }
}

