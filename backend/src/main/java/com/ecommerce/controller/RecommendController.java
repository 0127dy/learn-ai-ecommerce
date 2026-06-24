package com.ecommerce.controller;

import com.ecommerce.common.CurrentUserUtil;
import com.ecommerce.common.Result;
import com.ecommerce.entity.Product;
import com.ecommerce.service.AiService;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 * 调用 Python AI 服务获取推荐结果
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final AiService aiService;
    private final ProductService productService;

    /**
     * 协同过滤推荐（需登录）
     * 基于用户历史行为推荐相似商品
     * GET /api/recommend/products/{userId}
     */
    @GetMapping("/products/{userId}")
    public Result<List<Product>> recommendForUser(@PathVariable Long userId) {
        // 获取推荐商品 ID 列表
        List<Long> recommendedIds = aiService.getCollaborativeRecommendation(userId);

        // 查询商品详情
        List<Product> products = recommendedIds.stream()
            .map(productService::getById)
            .filter(p -> p != null)
            .toList();

        return Result.success(products);
    }

    /**
     * 热销商品推荐（公开）
     * GET /api/recommend/hot
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts() {
        List<Long> hotIds = aiService.getHotProducts();

        List<Product> products = hotIds.stream()
            .map(productService::getById)
            .filter(p -> p != null)
            .toList();

        return Result.success(products);
    }
}
