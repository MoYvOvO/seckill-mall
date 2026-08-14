package com.seckill.seckilluserservice.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.seckillcommon.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
  String LoginbyUserid(String Userid);

}
