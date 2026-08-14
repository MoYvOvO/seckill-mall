package com.seckill.seckilluserservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.seckillcommon.entity.User;
import com.seckill.seckilluserservice.Mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SeckillUserServiceApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
    }
    @Test
    void register(){
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username","admin");
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User setuser=new User();
        setuser.setUsername("admin");
        setuser.setPassword("admin");
        setuser.setNickname("admin");
        userMapper.insert(setuser);
    }
}
