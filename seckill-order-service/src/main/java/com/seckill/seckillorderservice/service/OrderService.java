package com.seckill.seckillorderservice.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.Order;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zyw
 * @since 2026-05-27
 */
public interface OrderService extends IService<Order> {
    void SetStatus(String orderid, String status);
    Result createOrder(String userId, String productId);
    Map getinfo(String authHeader);

}
