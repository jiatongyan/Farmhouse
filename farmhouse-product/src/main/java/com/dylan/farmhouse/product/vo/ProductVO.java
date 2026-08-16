package com.dylan.farmhouse.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务信息（shopName/shopStatus 由 product 服务调用 merchant 服务补充）。
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

    private Integer status;

    private String shopName;

    private Integer shopStatus;
}
