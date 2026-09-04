package com.seckill.seckillorderservice.Feign;
import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/api/products/{productId}")
    Result<Product> getProductById(@PathVariable("productId") String productId);
    @PutMapping("/api/products/{productId}/deduct")
    Result<Void> deductStock(@PathVariable("productId") String productId, @RequestParam("quantity") Integer quantity);
    @GetMapping("/api/products/{productId}/stock")
    Result<Integer> getProductStock(@PathVariable("productId") String productId);
    @GetMapping("/api/products")
    Result<Map<String, Object>> getAllProducts();
}