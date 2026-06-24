package com.ecommerce.controller;

import com.ecommerce.common.CurrentUserUtil;
import com.ecommerce.common.Result;
import com.ecommerce.entity.Review;
import com.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 添加评价（需登录）
     * POST /api/reviews
     */
    @PostMapping
    public Result<Review> addReview(@RequestBody Review review) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        review.setUserId(userId);
        return Result.success("评价成功", reviewService.addReview(review));
    }

    /**
     * 获取商品的评价列表（公开）
     * GET /api/reviews/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public Result<List<Review>> getProductReviews(@PathVariable Long productId) {
        return Result.success(reviewService.getProductReviews(productId));
    }
}
