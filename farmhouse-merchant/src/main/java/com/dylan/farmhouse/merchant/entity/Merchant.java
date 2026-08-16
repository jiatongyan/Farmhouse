package com.dylan.farmhouse.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户/店铺表。
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String shopName;

    /** 营业状态：1 营业中，2 休息中，3 关闭 */
    private Integer status;

    /** 审核状态：0 待审核，1 通过，2 拒绝 */
    private Integer auditStatus;

    private String description;

    private String contactPhone;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
