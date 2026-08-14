package com.shop.work.controller;

import com.shop.work.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
@RestController
@RequestMapping("api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth/login")
    public Map UserLogin(@RequestBody Map<String ,Object>map){
         String user=(String) map.get("username");
         String psw=(String) map.get("password");
         return userService.Login(user,psw);

    }
}
