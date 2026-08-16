package com.dylan.farmhouse.product.controller;

import com.dylan.farmhouse.common.exception.BizException;
import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.product.dto.ProductSaveDTO;
import com.dylan.farmhouse.product.dto.ProductStatusDTO;
import com.dylan.farmhouse.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户端服务管理接口：发布/编辑/上下架。
 * 路径归属 product 服务，POST/PUT 需网关鉴权；身份经网关透传 X-User-Id / X-User-Role，这里校验必须为商户(role=1)。
 */
@RestController
@RequestMapping("/api/product/manage")
@RequiredArgsConstructor
public class ProductManageController {

    private final ProductService productService;

    private Long requireMerchantUserId(Long userId, Integer role) {
        if (role == null || role != 1) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        return userId;
    }

    @PostMapping
    public Result<Long> create(@RequestHeader("X-User-Id") Long userId,
                               @RequestHeader(value = "X-User-Role", required = false) Integer role,
                               @Valid @RequestBody ProductSaveDTO dto) {
        return Result.success(productService.save(requireMerchantUserId(userId, role), dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader("X-User-Id") Long userId,
                               @RequestHeader(value = "X-User-Role", required = false) Integer role,
                               @PathVariable Long id,
                               @Valid @RequestBody ProductSaveDTO dto) {
        productService.update(requireMerchantUserId(userId, role), id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader(value = "X-User-Role", required = false) Integer role,
                                     @PathVariable Long id,
                                     @Valid @RequestBody ProductStatusDTO dto) {
        productService.updateStatus(requireMerchantUserId(userId, role), id, dto.getStatus());
        return Result.success();
    }
}
