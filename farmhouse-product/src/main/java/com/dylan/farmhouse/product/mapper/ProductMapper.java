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
     * 分页查询已上架且店铺营业中的服务，支持关键词与分类筛选。
     */
    @Select("""
            <script>
            SELECT p.id, p.merchant_id, p.category_id, p.title, p.subtitle, p.description,
                   p.price, p.cover_url, p.stock, p.status, p.created_at, p.updated_at,
                   m.shop_name, m.status AS shop_status
            FROM product p
            JOIN merchant m ON p.merchant_id = m.id
            WHERE p.status = 1 AND m.status = 1
            <if test="keyword != null and keyword != ''">
                AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.subtitle LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
                AND p.category_id = #{categoryId}
            </if>
            ORDER BY p.updated_at DESC
            </script>
            """)
    IPage<ProductVO> selectProductPage(Page<ProductVO> page,
                                       @Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId);
}
