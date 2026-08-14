package com.dylan.farmhouse.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dylan.farmhouse.product.entity.Merchant;
import com.dylan.farmhouse.product.mapper.MerchantMapper;
import com.dylan.farmhouse.product.service.MerchantService;
import com.dylan.farmhouse.product.vo.MerchantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;

    @Override
    public MerchantVO getShop(Long userId) {
        return toVO(getOrCreate(userId));
    }

    @Override
    public MerchantVO updateStatus(Long userId, Integer status) {
        Merchant merchant = getOrCreate(userId);
        merchant.setStatus(status);
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.updateById(merchant);
        return toVO(merchant);
    }

    @Override
    public Long getOrCreateMerchantId(Long userId) {
        return getOrCreate(userId).getId();
    }

    private Merchant getOrCreate(Long userId) {
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
        if (merchant != null) {
            return merchant;
        }

        Merchant toInsert = new Merchant();
        toInsert.setUserId(userId);
        toInsert.setShopName("默认店铺");
        toInsert.setStatus(1);
        toInsert.setDescription("");
        toInsert.setCreatedAt(LocalDateTime.now());
        toInsert.setUpdatedAt(LocalDateTime.now());
        try {
            merchantMapper.insert(toInsert);
            return toInsert;
        } catch (DuplicateKeyException e) {
            // 并发下唯一约束兜底，重查一次
            return merchantMapper.selectOne(
                    new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
        }
    }

    private MerchantVO toVO(Merchant merchant) {
        return MerchantVO.builder()
                .id(merchant.getId())
                .userId(merchant.getUserId())
                .shopName(merchant.getShopName())
                .status(merchant.getStatus())
                .description(merchant.getDescription())
                .build();
    }
}
