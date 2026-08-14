package com.seckill.seckilluserservice.Service;

import com.seckill.seckillcommon.dto.Result;
import com.seckill.seckillcommon.entity.User;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
public interface UserService extends IService<User> {

    Map Login(String user, String psw);

    void Register(String user, String psw, String nickname);

    User GetCurrentUser(String authHeader);
}
