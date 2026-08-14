package com.shop.work.mapper;

import com.shop.work.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
public interface UserMapper extends BaseMapper<User> {
  String LoginbyUserid(String Userid);

}
