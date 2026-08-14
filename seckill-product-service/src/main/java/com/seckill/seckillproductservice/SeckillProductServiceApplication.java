package com.seckill.seckillproductservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.seckill.seckillproductservice.mapper")
@EnableDiscoveryClient
public class SeckillProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillProductServiceApplication.class, args);
    }

}
