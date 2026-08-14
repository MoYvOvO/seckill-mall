package com.seckill.seckilluserservice.Controller;


import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckilluserservice.Service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result UserLogin(@RequestBody Map<String ,Object>map){
         String user=(String) map.get("username");
         String psw=(String) map.get("password");
         return  Result.success(userService.Login(user,psw));

    }
    @PostMapping("/auth/register")
    public Result UserRegister(@RequestBody Map<String ,Object>map){
        System.out.println("user success");
        String user=(String) map.get("username");
        String psw=(String) map.get("password");
        String nickname=(String) map.get("nickname");
        userService.Register(user,psw,nickname);
        return Result.success(null);

    }
    @GetMapping("/auth/me")
    public Result getCurrentUser(@RequestHeader("Authorization") String authHeader){
      return  Result.success(userService.GetCurrentUser(authHeader));
    }
}
