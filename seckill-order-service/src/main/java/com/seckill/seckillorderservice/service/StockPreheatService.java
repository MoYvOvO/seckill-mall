package com.seckill.seckillorderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.seckillcommon.entity.Order;

public interface StockPreheatService {
    void preheatStock();
}
