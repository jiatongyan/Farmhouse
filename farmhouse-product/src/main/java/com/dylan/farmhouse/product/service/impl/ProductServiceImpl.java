package com.dylan.farmhouse.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dylan.farmhouse.common.exception.BizException;
import com.dylan.farmhouse.common.result.ResultCode;
import com.dylan.farmhouse.product.client.MerchantClient;
import com.dylan.farmhouse.product.client.ShopDTO;
import com.dylan.farmhouse.product.dto.ProductSaveDTO;
import com.dylan.farmhouse.product.entity.Product;
import com.dylan.farmhouse.product.mapper.ProductMapper;
import com.dylan.farmhouse.product.service.ProductService;
import com.dylan.farmhouse.product.vo.PageResult;
import com.dylan.farmhouse.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final MerchantClient merchantClient;

    @Override
    public PageResult<ProductVO> list(int page, int size, String keyword, Long categoryId) {
        IPage<ProductVO> result = productMapper.selectProductPage(new Page<>(page, size), keyword, categoryId);
        List<ProductVO> records = result.getRecords();

        if (!records.isEmpty()) {
            List<Long> merchantIds = records.stream()
                    .map(ProductVO::getMerchantId).distinct().toList();
            Map<Long, ShopDTO> shopMap = merchantClient.listShops(merchantIds).stream()
                    .collect(Collectors.toMap(ShopDTO::getId, Function.identity()));
            records.forEach(p -> {
                ShopDTO shop = shopMap.get(p.getMerchantId());
                if (shop != null) {
                    p.setShopName(shop.getShopName());
                    p.setShopStatus(shop.getStatus());
                }
            });
        }

        return PageResult.<ProductVO>builder()
                .records(records)
                .total(result.getTotal())
                .page(page)
                .size(size)
                .build();
    }

    @Override
    public ProductVO detail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }
        ProductVO vo = toVO(product);

        List<ShopDTO> shops = merchantClient.listShops(List.of(product.getMerchantId()));
        if (!shops.isEmpty()) {
            vo.setShopName(shops.get(0).getShopName());
            vo.setShopStatus(shops.get(0).getStatus());
        }
        return vo;
    }

    @Override
    public Long save(Long userId, ProductSaveDTO dto) {
        Long merchantId = merchantClient.getOrCreateMerchantId(userId);

        Product product = new Product();
        product.setMerchantId(merchantId);
        applyDTO(product, dto);
        product.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return product.getId();
    }

    @Override
    public void update(Long userId, Long id, ProductSaveDTO dto) {
        Product product = getOwnedProduct(userId, id);
        applyDTO(product, dto);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long userId, Long id, Integer status) {
        Product product = getOwnedProduct(userId, id);
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }

    private Product getOwnedProduct(Long userId, Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }
        Long merchantId = merchantClient.getOrCreateMerchantId(userId);
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        return product;
    }

    private void applyDTO(Product product, ProductSaveDTO dto) {
        product.setCategoryId(dto.getCategoryId());
        product.setTitle(dto.getTitle());
        product.setSubtitle(dto.getSubtitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCoverUrl(dto.getCoverUrl());
    }

    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }
}
