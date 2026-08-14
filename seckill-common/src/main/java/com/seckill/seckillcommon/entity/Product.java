package com.seckill.seckillcommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 秒杀商品表
 * </p>
 *
 * @author zyw
 * @since 2026-05-24
 */
@Getter
@Setter
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId("id")
    private String id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 原价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 秒杀价
     */
    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    /**
     * 库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 商品图片URL
     */
    @TableField("image")
    private String image;

    /**
     * 秒杀开始时间
     */
    @TableField("seckill_start")
    private LocalDateTime seckillStart;

    /**
     * 秒杀结束时间
     */
    @TableField("seckill_end")
    private LocalDateTime seckillEnd;

    /**
     * 状态: 0=已结束, 1=进行中, 2=未开始
     */
    @TableField("status")
    private Byte status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
