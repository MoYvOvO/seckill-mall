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
        System.out.println("解析出的 role: " + role);
        System.out.println("解析出的 userId: " + userId);
        if ("admin".equals(role)) {

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
        String stockKey = STOCK_KEY_PREFIX + productId;
        // DECR 是原子操作，返回值是扣减后的库存数量
        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);
        log.info("用户 {} 秒杀商品 {}，扣减后剩余库存：{}", userId, productId, remainingStock);

        // ========== 第二步：判断是否库存不足 ==========
        if (remainingStock == null || remainingStock < 0) {
            // 库存不足，回滚 Redis（加回库存）
            if (remainingStock != null) {
                redisTemplate.opsForValue().increment(stockKey);
            }
            log.warn("用户 {} 秒杀商品 {} 失败：库存不足", userId, productId);
            throw new RuntimeException("商品已售罄");
        }

        // ========== 第三步：扣减成功后，异步同步到 MySQL ==========
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

        // 4. 返回排队中的订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setStatus("pending");
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        return data;
//        // ========== 第四步：调用 product-service 查询商品信息 ==========
//        Result<Product> productResult = productFeignClient.getProductById(productId);
//        if (productResult == null || productResult.getCode() != 200 || productResult.getData() == null) {
//            // 如果查询失败，回滚 Redis 库存
//            redisTemplate.opsForValue().increment(stockKey);
//            log.error("用户 {} 秒杀商品 {} 失败：商品信息查询失败", userId, productId);
//            throw new RuntimeException("商品信息不存在");
//        }
//        Product product = productResult.getData();
//        // 3. 生成订单号
//        String orderNo = "o" + System.currentTimeMillis() + (int)(Math.random() * 1000);
//        // 4. 创建订单对象
//        Order order = new Order();
//        order.setUserId(userId);
//        order.setProductId(productId);
//        order.setAmount(product.getSeckillPrice() != null ? product.getSeckillPrice() : product.getPrice());
//        order.setStatus("paid");
//        order.setProductName(product.getName());
//        order.setCreateTime(LocalDateTime.now());
//        order.setId(orderNo);
//        order.setUsername(userId);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setUpdatedAt(LocalDateTime.now());
//        orderMapper.insert(order);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("order", order);
//        data.put("product", product);
//        return data;
    }
}
