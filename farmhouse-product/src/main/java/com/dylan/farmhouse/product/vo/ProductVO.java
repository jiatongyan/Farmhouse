package com.dylan.farmhouse.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务信息（含店铺名称与店铺状态）。
 */
@Data
public class ProductVO {

    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String title;

    private String subtitle;

    private String description;

    private BigDecimal price;

    private String coverUrl;

    private Integer stock;

    private Integer status;

    private String shopName;

    private Integer shopStatus;
}
