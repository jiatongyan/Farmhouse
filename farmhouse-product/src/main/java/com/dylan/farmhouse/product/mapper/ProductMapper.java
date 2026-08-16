package com.dylan.farmhouse.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dylan.farmhouse.product.entity.Product;
import com.dylan.farmhouse.product.vo.ProductVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询已上架服务，支持关键词与分类筛选。
     * 店铺信息（shopName/shopStatus）由 service 层通过 Feign 调用 merchant 服务补充。
     */
    @Select("""
            <script>
            SELECT id, merchant_id, category_id, title, subtitle, description,
                   price, cover_url, status, created_at, updated_at
            FROM product
            WHERE status = 1
            <if test="keyword != null and keyword != ''">
                AND (title LIKE CONCAT('%', #{keyword}, '%') OR subtitle LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
                AND category_id = #{categoryId}
            </if>
            ORDER BY updated_at DESC
            </script>
            """)
    IPage<ProductVO> selectProductPage(Page<ProductVO> page,
                                       @Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId);
}
