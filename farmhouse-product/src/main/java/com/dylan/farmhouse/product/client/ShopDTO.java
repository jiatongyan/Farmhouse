package com.dylan.farmhouse.product.client;

import lombok.Data;

/**
 * 店铺信息（Feign 调用 merchant 服务返回）。
 */
@Data
public class ShopDTO {

    private Long id;

    private Long userId;

    private String shopName;

    private Integer status;
}
