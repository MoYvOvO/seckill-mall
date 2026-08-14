package com.seckill.seckillorderservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.seckillcommon.entity.Order;
import com.seckill.seckillorderservice.mapper.OrderMapper;
import com.seckill.seckillorderservice.service.OrderService;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class SeckillOrderServiceApplicationTests {
    @Autowired
  private OrderService orderService;
    @Autowired
  private  OrderMapper orderMapper;
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
    @Test
    void contextLoads() {
    }
    @Test
    void setstasus(){

    }
  @Test
   void testRedis() {
    // 向 Redis 写入一个键值对
    redisTemplate.opsForValue().set("test:key", "Hello Redis!");
    // 从 Redis 读取刚才写入的值
    String value = (String) redisTemplate.opsForValue().get("test:key");
    // 返回结果，验证读写是否成功
    System.out.println( "Redis 读写成功！读取到的值：" + value);
  }
}
