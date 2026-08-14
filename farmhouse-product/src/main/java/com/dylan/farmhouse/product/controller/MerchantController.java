package com.dylan.farmhouse.product.controller;

import com.dylan.farmhouse.common.exception.BizException;
import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.product.dto.ProductSaveDTO;
import com.dylan.farmhouse.product.dto.ProductStatusDTO;
import com.dylan.farmhouse.product.dto.ShopStatusDTO;
import com.dylan.farmhouse.product.service.MerchantService;
import com.dylan.farmhouse.product.service.ProductService;
import com.dylan.farmhouse.product.vo.MerchantVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户端接口：店铺状态管理、服务发布/编辑/上下架。
 * 身份由网关鉴权后通过 X-User-Id / X-User-Role 透传，这里校验必须为商户(role=1)。
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final ProductService productService;

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

    @PostMapping("/product")
    public Result<Long> createProduct(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-User-Role", required = false) Integer role,
                                      @Valid @RequestBody ProductSaveDTO dto) {
        return Result.success(productService.save(requireMerchantUserId(userId, role), dto));
    }

    @PutMapping("/product/{id}")
    public Result<Void> updateProduct(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-User-Role", required = false) Integer role,
                                      @PathVariable Long id,
                                      @Valid @RequestBody ProductSaveDTO dto) {
        productService.update(requireMerchantUserId(userId, role), id, dto);
        return Result.success();
    }

    @PutMapping("/product/{id}/status")
    public Result<Void> updateProductStatus(@RequestHeader("X-User-Id") Long userId,
                                            @RequestHeader(value = "X-User-Role", required = false) Integer role,
                                            @PathVariable Long id,
                                            @Valid @RequestBody ProductStatusDTO dto) {
        productService.updateStatus(requireMerchantUserId(userId, role), id, dto.getStatus());
        return Result.success();
    }
}
