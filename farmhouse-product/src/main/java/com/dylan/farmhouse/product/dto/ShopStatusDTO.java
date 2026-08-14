package com.dylan.farmhouse.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店铺状态请求。
 */
@Data
public class ShopStatusDTO {

    @NotNull(message = "店铺状态不能为空")
    @Min(value = 1, message = "店铺状态取值 1~3")
    @Max(value = 3, message = "店铺状态取值 1~3")
    private Integer status;
}
