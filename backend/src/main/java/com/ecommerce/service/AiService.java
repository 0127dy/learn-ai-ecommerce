package com.ecommerce.service;

import java.util.List;
import java.util.Map;

/**
 * AI 服务接口 — 调用 Python AI 服务
 */
public interface AiService {

    /**
     * 协同过滤推荐
     * @param userId 用户 ID
     * @return 推荐商品 ID 列表
     */
    List<Long> getCollaborativeRecommendation(Long userId);

    /**
     * 热销商品推荐
     * @return 热销商品 ID 列表
     */
    List<Long> getHotProducts();

    /**
     * AI 客服对话
     * @param userId  用户 ID（可选）
     * @param message 用户消息
     * @return 客服回复数据
     */
    Map<String, Object> chat(Long userId, String message);
}
