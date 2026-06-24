package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.ReviewMapper;
import com.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价服务实现
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review addReview(Review review) {
        // 插入评价
        reviewMapper.insert(review);

        // 更新商品评分（计算所有评价的平均分）
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, review.getProductId());
        List<Review> allReviews = reviewMapper.selectList(wrapper);

        double avgRating = allReviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(5.0);

        Product product = productMapper.selectById(review.getProductId());
        if (product != null) {
            product.setRating(Math.round(avgRating * 10) / 10.0); // 保留一位小数
            productMapper.updateById(product);
        }

        return review;
    }

    @Override
    public List<Review> getProductReviews(Long productId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId);
        wrapper.orderByDesc(Review::getCreateTime);
        return reviewMapper.selectList(wrapper);
    }

    @Override
    public List<Review> getUserReviews(Long userId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId);
        wrapper.orderByDesc(Review::getCreateTime);
        return reviewMapper.selectList(wrapper);
    }
}
