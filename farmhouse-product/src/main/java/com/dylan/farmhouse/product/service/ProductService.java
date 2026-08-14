package com.dylan.farmhouse.product.service;

import com.dylan.farmhouse.product.dto.ProductSaveDTO;
import com.dylan.farmhouse.product.vo.PageResult;
import com.dylan.farmhouse.product.vo.ProductVO;

public interface ProductService {

    PageResult<ProductVO> list(int page, int size, String keyword, Long categoryId);

    ProductVO detail(Long id);

    Long save(Long userId, ProductSaveDTO dto);

    void update(Long userId, Long id, ProductSaveDTO dto);

    void updateStatus(Long userId, Long id, Integer status);
}
