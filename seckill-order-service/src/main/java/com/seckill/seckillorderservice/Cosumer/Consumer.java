package com.seckill.seckillorderservice.Cosumer;
import com.seckill.seckillcommon.entity.Order;
import com.seckill.seckillcommon.entity.Product;
import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillorderservice.Feign.ProductFeignClient;
import com.seckill.seckillorderservice.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class Consumer {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;

    @RabbitListener(queues = "seckill.order.queue")
    public void handleSeckillOrder(Map<String, String> message) {
        try {
            String userId = message.get("userId");
            String productId = message.get("productId");
            String tempOrderId = message.get("tempOrderId");

            log.info("收到秒杀订单消息：userId={}, productId={}, tempOrderId={}",
                    userId, productId, tempOrderId);

            // 查询商品信息
            Result<Product> result = productFeignClient.getProductById(productId);
            if (result == null || result.getCode() != 200 || result.getData() == null) {
                log.error("商品信息查询失败，productId={}", productId);
                return;
            }
            Product product = result.getData();
            String orderNo = "o" + System.currentTimeMillis() + (int)(Math.random() * 1000);
            // 创建订单（使用临时ID作为订单号）
            Order order = new Order();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setAmount(product.getSeckillPrice() != null ? product.getSeckillPrice() : product.getPrice());
            order.setStatus("paid");
            order.setProductName(product.getName());
            order.setCreateTime(LocalDateTime.now());
            order.setId(orderNo);
            order.setUsername(userId);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
         orderMapper.insert(order);
            log.info("订单创建成功：orderId={}", orderNo);

        } catch (Exception e) {
            log.error("订单消费失败：{}", e.getMessage(), e);
        }
    }
    @RabbitListener(queues = "seckill.dlx.queue")
    public void handleTimeoutOrder(Map<String, String> message) {
        String userId = message.get("userId");
        String productId = message.get("productId");
        log.info("收到超时订单消息：userId={}, productId={}", userId, productId);
        String stockKey = "seckill:stock:" + productId;
        Long currentStock = redisTemplate.opsForValue().increment(stockKey);
        log.info("已恢复库存：productId={}, 当前库存={}", productId, currentStock);
        String orderKey = "order:user:" + userId + ":product:" + productId;
        redisTemplate.delete(orderKey);
        log.info("已删除幂等Key：{}", orderKey);
    }
}