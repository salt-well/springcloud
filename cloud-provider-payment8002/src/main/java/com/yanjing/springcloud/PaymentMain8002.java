package com.yanjing.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableEurekaClient //客户端注册服务中心
@EnableDiscoveryClient //服务发现
public class PaymentMain8002
{
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PaymentMain8002.class, args);
        System.out.println("启动springboot");

        ConfigurableEnvironment environment = run.getEnvironment();
        System.out.println("端口号："+environment.getProperty("server.port"));
    }
}
