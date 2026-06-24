package com.ecommerce.service.impl;

import com.ecommerce.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI 服务实现
 * 通过 RestTemplate 调用 Python AI 服务（Flask）
 */
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final RestTemplate restTemplate;

    /** AI 服务基础 URL（从配置读取） */
    @Value("${ai.service.url:http://localhost:5002}")
    private String aiServiceUrl;

    @Override
    public List<Long> getCollaborativeRecommendation(Long userId) {
        try {
            // 调用 Python AI 服务的协同过滤推荐接口
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                aiServiceUrl + "/api/recommend/collaborative",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null && response.getBody().containsKey("recommended_product_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> ids = (List<Integer>) response.getBody().get("recommended_product_ids");
                return ids.stream().map(Long::valueOf).toList();
            }
        } catch (Exception e) {
            // AI 服务不可用时，返回空列表
            System.err.println("调用 AI 推荐服务失败: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Long> getHotProducts() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                aiServiceUrl + "/api/recommend/hot",
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null && response.getBody().containsKey("hot_product_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> ids = (List<Integer>) response.getBody().get("hot_product_ids");
                return ids.stream().map(Long::valueOf).toList();
            }
        } catch (Exception e) {
            System.err.println("调用热销推荐服务失败: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> chat(Long userId, String message) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("message", message);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                aiServiceUrl + "/api/chat",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("调用 AI 客服服务失败: " + e.getMessage());
            result.put("reply", "抱歉，AI 客服暂时不可用，请稍后再试。");
            result.put("error", e.getMessage());
        }
        return result;
    }
}
