package com.seckill.seckillproductservice.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.seckillcommon.entity.Product;
import com.seckill.seckillproductservice.service.ProductService;
import com.seckill.seckillproductservice.mapper.ProductMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author zyw
 * @since 2026-05-24
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
     private ProductMapper productMapper;


    @Override
    public Map getallproducts(){
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        result.put("data",productMapper.selectList(queryWrapper));
        return  result;
    }

    @Override
    public boolean save(Product product) {
        if (product.getId() == null || product.getId().isBlank()) {
            product.setId("p" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }
        productMapper.insert(product);
        return true;
    }

    @Override
    public boolean updateById(Product product) {
        productMapper.updateById(product);
        return true;
    }

    @Override
    public boolean removeById(Product product) {
       productMapper.deleteById(product);
       return true;
    }

    @Override
    public Product SelectOneProduct(String productId) {
        Product Product =  productMapper.selectById(productId);
        return  Product;

    }

    @Override
    public int deductStock(String productId, Integer quantity) {
        return productMapper.deductStock(productId, quantity);
    }
}
