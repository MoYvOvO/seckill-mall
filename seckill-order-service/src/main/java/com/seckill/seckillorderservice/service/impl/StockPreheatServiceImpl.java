package com.seckill.seckillorderservice.service.impl;

import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillorderservice.Feign.ProductFeignClient;
import com.seckill.seckillorderservice.service.StockPreheatService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class StockPreheatServiceImpl implements StockPreheatService {
    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final long CACHE_EXPIRE_DAYS = 7L;
    private static final Logger log = LoggerFactory.getLogger(StockPreheatServiceImpl.class);

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @PostConstruct
    public void preheatStock() {
        log.info("========== 开始全量库存预热 ==========");
        try {
            Result<Map<String, Object>> result = productFeignClient.getAllProducts();
            if (result == null || result.getCode() != 200 || result.getData() == null) {
                log.error("预热失败：获取商品列表失败，原因：{}",
                        result == null ? "Feign调用失败" : result.getMessage());
                return;
            }

            Object payload = result.getData().get("data");
            if (!(payload instanceof List<?>)) {
                log.error("预热失败：商品列表格式异常");
                return;
            }

            int successCount = 0;
            List<?> products = (List<?>) payload;
            for (Object item : products) {
                try {
                    Map<String, Object> product = (Map<String, Object>) item;
                    String productId = String.valueOf(product.get("id"));
                    Object stockObj = product.get("stock");
                    if (productId == null || "null".equals(productId) || stockObj == null) {
                        log.warn("跳过无效商品数据：{}", product);
                        continue;
                    }
                    Integer stock = ((Number) stockObj).intValue();
                    String redisKey = STOCK_KEY_PREFIX + productId;
                    redisTemplate.opsForValue().set(redisKey, stock);
                    redisTemplate.expire(redisKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
                    log.info("预热成功：商品 {} 库存 {} 已存入 Redis", productId, stock);
                    successCount++;
                } catch (Exception e) {
                    log.error("预热异常：{}", e.getMessage(), e);
                }
            }
            log.info("========== 库存预热完成，成功 {} 个商品 ==========", successCount);
        } catch (Exception e) {
            log.error("预热失败：{}", e.getMessage(), e);
        }
    }
}