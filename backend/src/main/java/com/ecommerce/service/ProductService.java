package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 分页查询商品
     * @param page     页码
     * @param pageSize 每页条数
     * @param category 分类筛选（可选）
     * @param keyword  关键词搜索（可选）
     */
    Page<Product> listProducts(int page, int pageSize, String category, String keyword);

    /**
     * 根据 ID 查询商品详情
     */
    Product getById(Long id);

    /**
     * 新增商品（Admin）
     */
    Product addProduct(Product product);

    /**
     * 更新商品（Admin）
     */
    Product updateProduct(Product product);

    /**
     * 删除商品（Admin）
     */
    void deleteProduct(Long id);
}
