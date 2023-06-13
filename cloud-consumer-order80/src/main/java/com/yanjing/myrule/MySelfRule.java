package com.yanjing.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Configuration;

@Configuration
//3.调用自定义的算法规则
public class MySelfRule {
    public IRule iRule(){
        return new RandomRule();  //定义随机
    }
}
