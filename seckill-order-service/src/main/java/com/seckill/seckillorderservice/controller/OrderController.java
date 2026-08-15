package com.seckill.seckillorderservice.controller;

import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.Order;
import com.seckill.seckillorderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author zyw
 * @since 2026-05-27
 */
@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @GetMapping ("orders")
    public Result orders(@RequestHeader("Authorization") String authHeader){

        return Result.success(orderService.getinfo(authHeader));
    }
    @PatchMapping("orders/{orderid}/status")
    public Result setstatus(@PathVariable("orderid")String orderid ,@RequestBody Map map){
        String status= (String) map.get("status");
        orderService.SetStatus(orderid,status);
        return  Result.success(null);
    }
    @PostMapping("seckill")
    public Result CreateseckillOrder(@RequestBody Map map) {
        String UserName= (String) map.get("username");
        String ProductId= (String) map.get("productId");

        log.info("UserName={}  ProductId={}",UserName,ProductId);
        return  Result.success( orderService.createOrder(UserName,ProductId));
    }
}
