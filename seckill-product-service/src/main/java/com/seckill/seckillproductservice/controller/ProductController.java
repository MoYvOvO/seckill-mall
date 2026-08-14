package com.seckill.seckillproductservice.controller;

import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.Product;
import com.seckill.seckillproductservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 秒杀商品表 前端控制器
 * </p>
 *
 * @author zyw
 * @since 2026-05-24
 */
@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping("products")
    public Result getallproduct(){

        return Result.success(productService.getallproducts()) ;
    }
    @PostMapping("/products")
    public Result<Void> createProduct(@RequestBody Product product) {
        productService.save(product);
        return Result.success(null);
    }


    @PutMapping("/products/{productId}")
    public Result<Void> updateProduct(@PathVariable("productId") String productId,
                                      @RequestBody Product product) {
        product.setId(productId);
        productService.updateById(product);
        return Result.success(null);
    }

    @DeleteMapping("/products/{productId}")
    public Result<Void> deleteProduct(@PathVariable("productId") String productId) {
        productService.removeById(productId);
        return Result.success(null);
    }
    @GetMapping("/products/{productId}")
    public Result<Product> getProductById(@PathVariable("productId") String productId) {
        Product product=productService.SelectOneProduct(productId);
        return Result.success(product);
    }
    @PutMapping("/products/{productId}/deduct")
    public Result deductProductById(@PathVariable("productId") String productId, @RequestParam("quantity") Integer quantity) {
        int rows = productService.deductStock(productId, quantity);
        if (rows == 0) {
            return Result.error("库存不足");
        }
        return Result.success(null);
    }
    @GetMapping("/products/{productId}/stock")
    public Result<Integer> getStock(@PathVariable("productId") String productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product.getStock());
    }
}

