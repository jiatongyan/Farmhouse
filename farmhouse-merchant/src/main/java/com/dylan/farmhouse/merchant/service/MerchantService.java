package com.dylan.farmhouse.merchant.service;

import com.dylan.farmhouse.merchant.vo.MerchantVO;

import java.util.List;

public interface MerchantService {

    MerchantVO getShop(Long userId);

    MerchantVO updateStatus(Long userId, Integer status);

    /** 获取（不存在则创建）当前用户对应的商户 ID */
    Long getOrCreateMerchantId(Long userId);

    /** 批量查询店铺，供其它服务（如 product）内部调用 */
    List<MerchantVO> listByIds(List<Long> ids);
}
