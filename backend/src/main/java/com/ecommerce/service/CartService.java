package com.ecommerce.service;

import com.ecommerce.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 获取用户的购物车列表
     */
    List<CartItem> getUserCart(Long userId);

    /**
     * 添加商品到购物车
     */
    CartItem addItem(CartItem cartItem);

    /**
     * 更新购物车项数量
     */
    CartItem updateItem(Long id, Long userId, Integer quantity);

    /**
     * 删除购物车项
     */
    void deleteItem(Long id, Long userId);

    /**
     * 清空用户购物车
     */
    void clearCart(Long userId);
}
