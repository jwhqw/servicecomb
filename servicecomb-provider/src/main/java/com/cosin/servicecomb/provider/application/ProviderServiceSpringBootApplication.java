package com.cosin.servicecomb.provider.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableServiceComb
@ComponentScan("com.cosin.servicecomb.provider")
@MapperScan({"com.cosin.servicecomb.provider.*.mapper"})
@Slf4j
public class ProviderServiceSpringBootApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProviderServiceSpringBootApplication.class,args);
        log.info("provider端服务启动成功。。。。。");
    }

}
