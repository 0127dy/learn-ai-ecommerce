package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.OrderRequest;
import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import com.ecommerce.common.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 所有接口需要登录
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * POST /api/orders
     */
    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderRequest request) {
        try {
            Long userId = CurrentUserUtil.getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            Order order = orderService.createOrder(userId, request);
            return Result.success("下单成功", order);
        } catch (RuntimeException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 查询当前用户订单列表
     * GET /api/orders
     */
    @GetMapping
    public Result<List<Order>> getUserOrders() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        List<Order> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }

    /**
     * 查询订单详情
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        Order order = orderService.getOrderById(id, userId);
        if (order == null) {
            return Result.badRequest("订单不存在");
        }
        return Result.success(order);
    }
}
