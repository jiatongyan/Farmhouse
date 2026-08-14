package com.dylan.farmhouse.product.service;

import com.dylan.farmhouse.product.vo.MerchantVO;

public interface MerchantService {

    MerchantVO getShop(Long userId);

    MerchantVO updateStatus(Long userId, Integer status);

    /** 获取（不存在则创建）当前用户对应的商户 ID，供服务发布等使用 */
    Long getOrCreateMerchantId(Long userId);
}
