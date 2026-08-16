package com.dylan.farmhouse.merchant.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 店铺信息。
 */
@Data
@Builder
public class MerchantVO {

    private Long id;

    private Long userId;

    private String shopName;

    private Integer status;

    private Integer auditStatus;

    private String description;

    private String contactPhone;

    private String address;
}
