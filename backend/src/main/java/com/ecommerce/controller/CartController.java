package com.ecommerce.controller;

import com.ecommerce.common.CurrentUserUtil;
import com.ecommerce.common.Result;
import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 * 所有接口需要登录
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取当前用户的购物车列表
     * GET /api/cart
     */
    @GetMapping
    public Result<List<CartItem>> getCart() {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        return Result.success(cartService.getUserCart(userId));
    }

    /**
     * 添加商品到购物车
     * POST /api/cart
     */
    @PostMapping
    public Result<CartItem> addItem(@RequestBody CartItem cartItem) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        cartItem.setUserId(userId);
        return Result.success("添加成功", cartService.addItem(cartItem));
    }

    /**
     * 更新购物车项数量
     * PUT /api/cart/{id}
     */
    @PutMapping("/{id}")
    public Result<CartItem> updateItem(@PathVariable Long id,
                                       @RequestParam Integer quantity) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        CartItem item = cartService.updateItem(id, userId, quantity);
        if (item == null) {
            return Result.badRequest("购物车项不存在");
        }
        return Result.success(item);
    }

    /**
     * 删除购物车项
     * DELETE /api/cart/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        cartService.deleteItem(id, userId);
        return Result.success();
    }
}
