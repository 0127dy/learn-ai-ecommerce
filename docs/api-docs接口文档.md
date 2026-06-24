# API 文档 — AI 电商智能客服与商品推荐系统

> 基地址：`http://localhost:8080`（后端） / `http://localhost:5002`（AI 服务）
>
> 认证方式：JWT Token（Bearer Token）
> - 登录后获得 token，在后续请求的 Header 中携带 `Authorization: Bearer <token>`

---

## 目录

1. [认证 API](#1-认证-api)
2. [商品 API](#2-商品-api)
3. [购物车 API](#3-购物车-api)
4. [订单 API](#4-订单-api)
5. [评价 API](#5-评价-api)
6. [推荐 API](#6-推荐-api)
7. [AI 客服 API](#7-ai-客服-api)
8. [健康检查](#8-健康检查)

---

## 1. 认证 API

### 1.1 用户注册

注册新用户，默认角色为 `USER`。

```
POST /api/auth/register
```

**请求体：**

```json
{
    "username": "newuser",
    "password": "password123",
    "phone": "13800001111",
    "email": "newuser@example.com"
}
```

**成功响应（200）：**

```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "id": 4,
        "username": "newuser",
        "password": null,
        "role": "USER",
        "phone": "13800001111",
        "email": "newuser@example.com",
        "createTime": "2026-06-22T21:00:00"
    }
}
```

**错误响应（400）：**

```json
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

### 1.2 用户登录

登录后获取 JWT Token。

```
POST /api/auth/login
```

**请求体：**

```json
{
    "username": "user1",
    "password": "user123"
}
```

**成功响应（200）：**

```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsInVzZXJJZCI6Miwi...（省略）"
    }
}
```

**错误响应（400）：**

```json
{
    "code": 400,
    "message": "用户名或密码错误",
    "data": null
}
```

---

## 2. 商品 API

### 2.1 分页查询商品列表

```
GET /api/products?page=1&pageSize=10&category=手机&keyword=华为
```

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| pageSize | int | 否 | 每页条数，默认 10 |
| category | string | 否 | 分类筛选 |
| keyword | string | 否 | 关键词搜索（匹配名称和描述） |

**成功响应（200）：**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "page": 1,
        "pageSize": 10,
        "total": 3,
        "pages": 1,
        "records": [
            {
                "id": 2,
                "name": "华为 Mate 60 Pro",
                "category": "手机",
                "description": "华为旗舰手机...",
                "price": 7999.00,
                "stock": 30,
                "imageUrl": "https://...",
                "sales": 95,
                "rating": 4.9,
                "createTime": "2026-06-22T21:00:00"
            }
        ]
    }
}
```

### 2.2 查询商品详情

```
GET /api/products/{id}
```

**成功响应：**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "iPhone 15 Pro Max",
        "price": 9999.00,
        "stock": 50,
        "sales": 120,
        "rating": 4.8,
        ...
    }
}
```

### 2.3 新增商品（Admin 专用）

```
POST /api/products
Authorization: Bearer <admin-token>
```

**请求体：**

```json
{
    "name": "新商品",
    "category": "手机",
    "description": "描述",
    "price": 3999.00,
    "stock": 100,
    "imageUrl": "https://..."
}
```

### 2.4 更新商品（Admin 专用）

```
PUT /api/products/{id}
Authorization: Bearer <admin-token>
```

### 2.5 删除商品（Admin 专用）

```
DELETE /api/products/{id}
Authorization: Bearer <admin-token>
```

---

## 3. 购物车 API

> 所有接口需要登录（需携带 JWT Token）

### 3.1 获取购物车列表

```
GET /api/cart
Authorization: Bearer <token>
```

### 3.2 添加商品到购物车

```
POST /api/cart
Authorization: Bearer <token>
```

```json
{
    "productId": 1,
    "quantity": 1
}
```

### 3.3 更新购物车项数量

```
PUT /api/cart/{id}?quantity=3
Authorization: Bearer <token>
```

### 3.4 删除购物车项

```
DELETE /api/cart/{id}
Authorization: Bearer <token>
```

---

## 4. 订单 API

> 所有接口需要登录（需携带 JWT Token）

### 4.1 创建订单

```
POST /api/orders
Authorization: Bearer <token>
```

```json
{
    "productId": 1,
    "quantity": 1
}
```

### 4.2 查询用户订单列表

```
GET /api/orders
Authorization: Bearer <token>
```

### 4.3 查询订单详情

```
GET /api/orders/{id}
Authorization: Bearer <token>
```

---

## 5. 评价 API

### 5.1 添加评价（需登录）

```
POST /api/reviews
Authorization: Bearer <token>
```

```json
{
    "productId": 1,
    "content": "商品非常好！",
    "rating": 5
}
```

### 5.2 获取商品评价列表（公开）

```
GET /api/reviews/product/{productId}
```

---

## 6. 推荐 API

### 6.1 协同过滤推荐（需登录）

```
GET /api/recommend/products/{userId}
Authorization: Bearer <token>
```

返回推荐的商品列表。

### 6.2 热销商品推荐（公开）

```
GET /api/recommend/hot
```

返回热销商品列表。

---

## 7. AI 客服 API

### 7.1 AI 客服对话

```
POST /api/ai/chat
```

**请求体：**

```json
{
    "message": "你好，我想买一台笔记本电脑"
}
```

**成功响应：**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "reply": "给您推荐几个热销商品...",
        "intent": "product_inquiry",
        "suggestions": ["iPhone 15", "MacBook Air", "Sony耳机"]
    }
}
```

---

## 8. 健康检查

```
GET /api/health
```

**响应：**

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "status": "UP",
        "service": "ecommerce-backend",
        "time": "2026-06-22T21:00:00"
    }
}
```
