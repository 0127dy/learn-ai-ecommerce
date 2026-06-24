package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Page<Product> listProducts(int page, int pageSize, String category, String keyword) {
        Page<Product> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (StringUtils.isNotBlank(category)) {
            wrapper.eq(Product::getCategory, category);
        }

        // 关键词搜索（匹配名称和描述）
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(Product::getName, keyword)
                .or()
                .like(Product::getDescription, keyword)
            );
        }

        // 按销量降序排列
        wrapper.orderByDesc(Product::getSales);

        return productMapper.selectPage(p, wrapper);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Product addProduct(Product product) {
        product.setSales(0);       // 新商品销量默认为 0
        product.setRating(5.0);    // 新商品评分默认为 5.0
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product updateProduct(Product product) {
        productMapper.updateById(product);
        return productMapper.selectById(product.getId());
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
}
