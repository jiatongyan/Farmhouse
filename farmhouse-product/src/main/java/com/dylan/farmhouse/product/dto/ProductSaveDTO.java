package com.dylan.farmhouse.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务新增/编辑请求。
 */
@Data
public class ProductSaveDTO {

    @NotBlank(message = "服务名称不能为空")
    private String title;

    private String subtitle;

    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", message = "价格不能为负")
    private BigDecimal price;

    private Long categoryId;

    private String coverUrl;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负")
    private Integer stock;

    /** 状态：0 草稿，1 已上架，2 已下架；缺省为草稿 */
    private Integer status;
}
