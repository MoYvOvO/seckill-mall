package com.seckill.seckilluserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.seckill.seckilluserservice.Mapper")
@EnableDiscoveryClient
public class SeckillUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillUserServiceApplication.class, args);
    }

}
