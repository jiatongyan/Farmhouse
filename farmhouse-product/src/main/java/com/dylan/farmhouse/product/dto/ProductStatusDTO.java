package com.dylan.farmhouse.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 服务上下架请求。
 */
@Data
public class ProductStatusDTO {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态取值 0~2")
    @Max(value = 2, message = "状态取值 0~2")
    private Integer status;
}
