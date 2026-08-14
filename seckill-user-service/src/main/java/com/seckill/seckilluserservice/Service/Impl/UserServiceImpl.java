package com.seckill.seckilluserservice.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seckill.seckillcommon.constant.JwtConstant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.User;
import com.seckill.seckilluserservice.Mapper.UserMapper;
import com.seckill.seckilluserservice.Service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public Map Login(String username, String psw) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        String token = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .claim("role",user.getRole())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes()))
                .compact();
        result.put("data",user);
        result.put("token",token);
        result.put("role",user.getRole());

       // System.out.println(result);
        log.info("result: {}", result);

        return result;
    }

    @Override
    public void Register(String user, String psw, String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",user);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        User setuser=new User();
        setuser.setUsername(user);
        setuser.setPassword(psw);
        setuser.setNickname(nickname);
        userMapper.insert(setuser);

    }

    @Override
    public User GetCurrentUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        user.setPassword(null);
        return user;
    }
}
