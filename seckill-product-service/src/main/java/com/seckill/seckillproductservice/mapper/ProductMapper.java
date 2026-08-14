package com.seckill.seckillproductservice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.seckillcommon.entity.Product;

/**
 * <p>
 * 秒杀商品表 Mapper 接口
 * </p>
 *
 * @author zyw
 * @since 2026-05-24
 */
public interface ProductMapper extends BaseMapper<Product> {

    int deductStock(String productId, Integer quantity);
}
