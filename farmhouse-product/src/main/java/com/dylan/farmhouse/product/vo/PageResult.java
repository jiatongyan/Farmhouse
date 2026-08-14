package com.dylan.farmhouse.product.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页返回体。
 */
@Data
@Builder
public class PageResult<T> {

    private List<T> records;

    private long total;

    private long page;

    private long size;
}
