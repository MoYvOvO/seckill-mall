package com.seckill.seckillorderservice.service.impl;
import com.seckill.seckillorderservice.Feign.ProductFeignClient;
import com.seckill.seckillorderservice.service.StockSyncService;
import com.seckill.seckillcommon.dto.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class StockSyncServiceImpl implements StockSyncService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Async
    @Override
    public void syncStockToDB(String productId, Integer quantity) {
        try {
            log.info("开始异步同步库存：商品 {}，扣减 {}", productId, quantity);
            Result<Void> result = productFeignClient.deductStock(productId, quantity);
            if (result == null || result.getCode() != 200) {
                log.error("异步同步库存失败：商品 {}，错误：{}", productId,
                        result == null ? "Feign调用失败" : result.getMessage());
            } else {
                log.info("异步同步库存成功：商品 {}", productId);
            }
        } catch (Exception e) {
            log.error("异步同步库存异常：商品 {}，错误：{}", productId, e.getMessage(), e);
        }
    }
}
