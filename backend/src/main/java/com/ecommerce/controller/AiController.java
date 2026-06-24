package com.ecommerce.controller;

import com.ecommerce.common.CurrentUserUtil;
import com.ecommerce.common.Result;
import com.ecommerce.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 客服控制器
 * POST /api/ai/chat — 调用 Python AI 客服服务
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * AI 客服对话
     * POST /api/ai/chat
     *
     * 请求体: { "message": "你好，我想买一台笔记本电脑" }
     * 响应:   { "reply": "您好！很高兴为您服务。请问您对笔记本电脑有什么具体要求吗？" }
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return Result.badRequest("消息不能为空");
        }

        Long userId = CurrentUserUtil.getCurrentUserId();
        Map<String, Object> response = aiService.chat(userId, message);
        return Result.success(response);
    }
}
