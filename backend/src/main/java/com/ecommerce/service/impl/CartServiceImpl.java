package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.entity.CartItem;
import com.ecommerce.mapper.CartItemMapper;
import com.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;

    @Override
    public List<CartItem> getUserCart(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        return cartItemMapper.selectList(wrapper);
    }

    @Override
    public CartItem addItem(CartItem cartItem) {
        // 检查是否已存在该商品
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, cartItem.getUserId());
        wrapper.eq(CartItem::getProductId, cartItem.getProductId());
        CartItem existing = cartItemMapper.selectOne(wrapper);

        if (existing != null) {
            // 已存在则增加数量
            existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
            cartItemMapper.updateById(existing);
            return existing;
        }

        cartItemMapper.insert(cartItem);
        return cartItem;
    }

    @Override
    public CartItem updateItem(Long id, Long userId, Integer quantity) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getId, id);
        wrapper.eq(CartItem::getUserId, userId);
        CartItem item = cartItemMapper.selectOne(wrapper);

        if (item != null) {
            item.setQuantity(quantity);
            cartItemMapper.updateById(item);
        }
        return item;
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getId, id);
        wrapper.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(wrapper);
    }
}
