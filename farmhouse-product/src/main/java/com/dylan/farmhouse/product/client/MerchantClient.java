package com.dylan.farmhouse.product.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * merchant 服务内部接口的 Feign 客户端。
 */
@FeignClient(name = "farmhouse-merchant")
public interface MerchantClient {

    @GetMapping("/api/merchant/internal/merchant-id")
    Long getOrCreateMerchantId(@RequestParam("userId") Long userId);

    @GetMapping("/api/merchant/internal/shops")
    List<ShopDTO> listShops(@RequestParam("ids") List<Long> ids);
}
