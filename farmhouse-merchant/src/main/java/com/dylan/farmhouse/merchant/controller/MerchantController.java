package com.dylan.farmhouse.merchant.controller;

import com.dylan.farmhouse.common.exception.BizException;
import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.merchant.dto.ShopStatusDTO;
import com.dylan.farmhouse.merchant.service.MerchantService;
import com.dylan.farmhouse.merchant.vo.MerchantVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商户端接口：店铺状态管理。
 * 身份由网关鉴权后通过 X-User-Id / X-User-Role 透传，这里校验必须为商户(role=1)。
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    private Long requireMerchantUserId(Long userId, Integer role) {
        if (role == null || role != 1) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        return userId;
    }

    @GetMapping("/shop")
    public Result<MerchantVO> getShop(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-User-Role", required = false) Integer role) {
        return Result.success(merchantService.getShop(requireMerchantUserId(userId, role)));
    }

    @PutMapping("/shop/status")
    public Result<MerchantVO> updateShopStatus(@RequestHeader("X-User-Id") Long userId,
                                               @RequestHeader(value = "X-User-Role", required = false) Integer role,
                                               @Valid @RequestBody ShopStatusDTO dto) {
        return Result.success(merchantService.updateStatus(requireMerchantUserId(userId, role), dto.getStatus()));
    }

    // ---- 内部接口：供其它服务通过 Feign 直连调用，不经过网关 ----

    @GetMapping("/internal/merchant-id")
    public Long getOrCreateMerchantId(@RequestParam Long userId) {
        return merchantService.getOrCreateMerchantId(userId);
    }

    @GetMapping("/internal/shops")
    public List<MerchantVO> listShops(@RequestParam List<Long> ids) {
        return merchantService.listByIds(ids);
    }
}
