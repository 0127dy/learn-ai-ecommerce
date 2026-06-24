package com.ecommerce.dto;

import lombok.Data;

/**
 * 下单请求 DTO
 */
@Data
public class OrderRequest {
    private Long productId;
    private Integer quantity;
}
