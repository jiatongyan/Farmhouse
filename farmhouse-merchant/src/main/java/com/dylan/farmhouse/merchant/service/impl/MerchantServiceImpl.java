package com.dylan.farmhouse.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dylan.farmhouse.merchant.entity.Merchant;
import com.dylan.farmhouse.merchant.mapper.MerchantMapper;
import com.dylan.farmhouse.merchant.service.MerchantService;
import com.dylan.farmhouse.merchant.vo.MerchantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<MerchantVO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return merchantMapper.selectBatchIds(ids).stream().map(this::toVO).toList();
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
        toInsert.setAuditStatus(1); // 演示环境默认审核通过
        toInsert.setDescription("");
        toInsert.setCreatedAt(LocalDateTime.now());
        toInsert.setUpdatedAt(LocalDateTime.now());
        try {
            merchantMapper.insert(toInsert);
            return toInsert;
        } catch (DuplicateKeyException e) {
            // 并发下唯一约束兜底，重查
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
                .auditStatus(merchant.getAuditStatus())
                .description(merchant.getDescription())
                .contactPhone(merchant.getContactPhone())
                .address(merchant.getAddress())
                .build();
    }
}
