package com.ecommerce.service;

import com.ecommerce.dto.OrderRequest;
import com.ecommerce.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     * @param userId  用户 ID
     * @param request 下单请求
     * @return 创建的订单
     */
    Order createOrder(Long userId, OrderRequest request);

    /**
     * 查询用户的所有订单
     */
    List<Order> getUserOrders(Long userId);

    /**
     * 查询订单详情
     */
    Order getOrderById(Long id, Long userId);

    /**
     * 更新订单状态
     */
    Order updateStatus(Long id, String status);
}
