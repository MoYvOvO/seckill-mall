package com.seckill.seckillcommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zyw
 * @since 2026-05-25
 */
@Getter
@Setter
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    @TableField("nickname")
    private String nickname;
    /**
     * 密码(加密)
     */
    @TableField("password")
    private String password;

    /**
     * 状态(0禁用 1正常)
     */
    @TableField("status")
    private Byte status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除(0正常 1删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;
    @TableField(value = "role",insertStrategy = FieldStrategy.NEVER)
    private String role;
}
