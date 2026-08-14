package com.shop.work;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.work.entity.Order;
import com.shop.work.entity.Product;
import com.shop.work.mapper.OrderMapper;
import com.shop.work.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class WorkApplicationTests {
	@Autowired
	public OrderMapper orderMapper;
	@Autowired
	public ProductMapper productMapper;
	@Test
	void contextLoads() {
	}

	@Test
	void getallorders() {

		 Map<String, Object> result = new HashMap<>();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
		result.put("data",orderMapper.selectList(queryWrapper));
		System.out.println(result);
	}
	@Test
	void getallproduct() {

		Map<String, Object> result = new HashMap<>();
		QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
		result.put("data",productMapper.selectList(queryWrapper));
		System.out.println(result);
	}
}
