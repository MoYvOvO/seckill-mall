package com.seckill.seckillorderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.Order;
import com.seckill.seckillcommon.entity.Product;
import com.seckill.seckillorderservice.Feign.ProductFeignClient;
import com.seckill.seckillorderservice.config.RabbitMQConfig;
import com.seckill.seckillorderservice.mapper.OrderMapper;
import com.seckill.seckillorderservice.service.OrderService;

import com.seckill.seckillorderservice.service.StockSyncService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.seckill.seckillcommon.constant.JwtConstant;
import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zyw
 * @since 2026-05-27
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    @Value("${idempotent.enabled:true}")
    private boolean idempotentEnabled;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StockSyncService stockSyncService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Map getinfo(String authHeader){
        // 从 Header 里提取 Token
        String token = authHeader.substring(7);
        // 解析 Token，拿到 userId 和 role
        Claims claims = parseToken(token);
       String userId = String.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if ("admin".equals(role)) {
           //admin 能看到所有订单
        } else {
            queryWrapper.eq("user_id", userId);
        }

        result.put("data",orderMapper.selectList(queryWrapper));
        return result;



    }



    public void SetStatus(String orderid, String status) {
        UpdateWrapper<Order> updateWrapper=new UpdateWrapper<>();
        System.out.println(status);
        updateWrapper.eq("id",orderid).set("status",status);
        orderMapper.update(null,updateWrapper);
    }

    @Override
    @Transactional
    public Map createOrder(String userId, String productId) {
        if (idempotentEnabled) {
            String orderKey = "order:user:" + userId + ":product:" + productId;
            Boolean hasOrder = redisTemplate.opsForValue().setIfAbsent(orderKey, "1", 3600, TimeUnit.SECONDS);

            if (hasOrder == null || !hasOrder) {
                log.warn("用户 {} 已购买过商品 {}，重复下单被拦截", userId, productId);
                throw new RuntimeException("您已购买过该商品，每人限购一件");
            }
            log.info("用户 {} 首次购买商品 {}，幂等校验通过", userId, productId);
        } else {
            log.info("幂等校验已关闭，当前为压测模式");
        }

        String stockKey = STOCK_KEY_PREFIX + productId;
        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);
        log.info("用户 {} 秒杀商品 {}，扣减后剩余库存：{}", userId, productId, remainingStock);

        if (remainingStock == null || remainingStock < 0) {
            if (remainingStock != null) {
                redisTemplate.opsForValue().increment(stockKey);
            }
            if (idempotentEnabled) {
                String orderKey = "order:user:" + userId + ":product:" + productId;
                redisTemplate.delete(orderKey);
            }
            log.warn("用户 {} 秒杀商品 {} 失败：库存不足", userId, productId);
            throw new RuntimeException("商品已售罄");
        }

        stockSyncService.syncStockToDB(productId, 1);
        Map<String, String> orderMessage = new HashMap<>();
        orderMessage.put("userId", userId);
        orderMessage.put("username", userId);
        orderMessage.put("productId", productId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SECKILL_EXCHANGE,
                RabbitMQConfig.SECKILL_ROUTING_KEY,
                orderMessage
        );

        log.info("用户 {} 秒杀商品 {}，订单消息已发送到 MQ", userId, productId);

        // 第四步：返回排队中的订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setStatus("pending");
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        return data;
    }
}
