package com.seckill.seckillorderservice.service;

public interface StockSyncService {
    void syncStockToDB(String productId, Integer quantity);
}
