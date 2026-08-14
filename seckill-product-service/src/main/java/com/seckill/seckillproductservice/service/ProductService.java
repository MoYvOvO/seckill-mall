package com.seckill.seckillproductservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.seckillcommon.entity.Product;

import java.util.Map;

/**
 * <p>
 * 秒杀商品表 服务类
 * </p>
 *
 * @author zyw
 * @since 2026-05-24
 */
public interface ProductService extends IService<Product> {

    Map getallproducts();
    boolean save(Product product);
    boolean updateById(Product product);
    boolean removeById(Product product);

    Product SelectOneProduct(String productId);

    int deductStock(String productId, Integer quantity);
}
