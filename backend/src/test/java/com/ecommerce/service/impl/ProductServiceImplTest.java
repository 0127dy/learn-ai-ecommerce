package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductService 单元测试 —— Mock Mapper 层，只测 Service 业务逻辑
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试手机");
        product.setCategory("电子");
        product.setDescription("一款高性能测试手机");
        product.setPrice(new BigDecimal("2999.00"));
        product.setStock(100);
        product.setSales(50);
        product.setRating(4.5);
    }

    @Test
    @DisplayName("分页查询商品列表 —— 无筛选条件")
    void testListProductsWithoutFilter() {
        Page<Product> mockPage = new Page<>(1, 10, 3);
        mockPage.setRecords(List.of(product));

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<Product> result = productService.listProducts(1, 10, null, null);

        assertNotNull(result);
        assertEquals(3, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("测试手机", result.getRecords().get(0).getName());
        verify(productMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询商品列表 —— 按分类筛选")
    void testListProductsWithCategory() {
        Page<Product> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(List.of(product));

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<Product> result = productService.listProducts(1, 10, "电子", null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(productMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("分页查询商品列表 —— 按关键词搜索")
    void testListProductsWithKeyword() {
        Page<Product> mockPage = new Page<>(1, 10, 1);
        mockPage.setRecords(List.of(product));

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(mockPage);

        Page<Product> result = productService.listProducts(1, 10, null, "手机");

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(productMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("根据ID查询商品")
    void testGetById() {
        when(productMapper.selectById(1L)).thenReturn(product);

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("测试手机", result.getName());
        verify(productMapper).selectById(1L);
    }

    @Test
    @DisplayName("新增商品 —— 自动填充默认值")
    void testAddProduct() {
        Product newProduct = new Product();
        newProduct.setName("新商品");
        newProduct.setCategory("食品");
        newProduct.setPrice(new BigDecimal("10.00"));
        newProduct.setStock(500);

        productService.addProduct(newProduct);

        verify(productMapper).insert(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertEquals("新商品", saved.getName());
        assertEquals(0, saved.getSales());     // 默认销量 0
        assertEquals(5.0, saved.getRating());  // 默认评分 5.0
    }

    @Test
    @DisplayName("更新商品后返回最新数据")
    void testUpdateProduct() {
        when(productMapper.selectById(1L)).thenReturn(product);

        Product update = new Product();
        update.setId(1L);
        update.setName("升级版手机");
        update.setPrice(new BigDecimal("3999.00"));

        Product result = productService.updateProduct(update);

        assertNotNull(result);
        assertEquals("升级版手机", result.getName());
        verify(productMapper).updateById(any(Product.class));
        verify(productMapper, times(2)).selectById(1L); // update 内调了一次, 查回来又调一次
    }

    @Test
    @DisplayName("删除商品")
    void testDeleteProduct() {
        productService.deleteProduct(1L);
        verify(productMapper).deleteById(1L);
    }
}
