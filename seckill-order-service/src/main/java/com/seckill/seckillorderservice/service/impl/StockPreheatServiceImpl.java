package com.seckill.seckillorderservice.service.impl;

import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillorderservice.Feign.ProductFeignClient;
import com.seckill.seckillorderservice.service.StockPreheatService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class StockPreheatServiceImpl implements StockPreheatService {
    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final long CACHE_EXPIRE_DAYS = 7L;
    private static final List<String> SECKILL_PRODUCT_IDS = Arrays.asList("p1", "p2", "p3");
    private static final Logger log = LoggerFactory.getLogger(StockPreheatServiceImpl.class);
    @Autowired
    ProductFeignClient productFeignClient ;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Override
    @PostConstruct
    public void preheatStock() {
           // System.out.println(("========== 开始库存预热 =========="));
           log.info("========== 开始库存预热 ==========");
            for (String productId : SECKILL_PRODUCT_IDS) {
                try {
                    Result<Integer> result = productFeignClient.getProductStock(productId);
                    if (result == null || result.getCode() != 200 || result.getData() == null) {
                       // System.err.println("预热失败，商品ID：" + productId + "，原因：" + (result == null ? "Feign调用失败" : result.getMessage()));
                        log.error("预热失败，商品ID：{}，原因：{}", productId, result == null ? "Feign调用失败" : result.getMessage());
                        continue;
                    }
                    Integer stock = result.getData();
                    String redisKey = STOCK_KEY_PREFIX + productId;
                    redisTemplate.opsForValue().set(redisKey, stock);
                    redisTemplate.expire(redisKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
                  //  System.out.println("预热成功：商品 " + productId + "，库存 " + stock + " 已存入 Redis");
                    log.info("预热成功：商品{} 库存{}已存入 Redis",productId,stock);
                } catch (Exception e) {
                    e.printStackTrace();  // 临时加上
                  //  System.err.println("预热异常，商品ID：" + productId + "，错误：" + e.getMessage());
                    log.error("预热异常，商品ID：{}  错误：{}",productId,e.getMessage());
                }
            }
           // System.out.println("========== 库存预热完成 ==========");
            log.info("========== 库存预热完成 ==========");
        }
    }

