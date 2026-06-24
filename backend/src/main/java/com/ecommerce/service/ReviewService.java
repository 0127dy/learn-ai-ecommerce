package com.ecommerce.service;

import com.ecommerce.entity.Review;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {

    /**
     * 添加评价
     */
    Review addReview(Review review);

    /**
     * 获取商品的所有评价
     */
    List<Review> getProductReviews(Long productId);

    /**
     * 获取用户的评价列表
     */
    List<Review> getUserReviews(Long userId);
}
