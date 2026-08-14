package com.dylan.farmhouse.product.controller;

import com.dylan.farmhouse.common.result.Result;
import com.dylan.farmhouse.product.service.ProductService;
import com.dylan.farmhouse.product.vo.PageResult;
import com.dylan.farmhouse.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消费者端接口：服务列表、服务详情（游客可读，网关已放行）。
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Result<PageResult<ProductVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(productService.list(page, size, keyword, categoryId));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.detail(id));
    }
}
