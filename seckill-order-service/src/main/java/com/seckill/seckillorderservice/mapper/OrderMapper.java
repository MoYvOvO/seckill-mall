package com.seckill.seckillorderservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.seckillcommon.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author zyw
 * @since 2026-05-27
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    void update(Order order);
    int insert(Order order);
}
