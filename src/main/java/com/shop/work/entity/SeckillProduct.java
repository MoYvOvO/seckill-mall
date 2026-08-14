package com.shop.work.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

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
@TableName("seckill_product")
public class SeckillProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的商品ID（product表）
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 秒杀价
     */
    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 秒杀开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 每人限购数量
     */
    @TableField("limit_count")
    private Integer limitCount;

    /**
     * 乐观锁版本号（用于高并发优化）
     */
    @TableField("version")
    private Integer version;

    /**
     * 状态：0=已结束，1=进行中，2=未开始
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
