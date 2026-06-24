# 🛒 AI 电商智能客服与商品推荐系统

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Python](https://img.shields.io/badge/Python-3.10-blue)
![Flask](https://img.shields.io/badge/Flask-2.3-lightgrey)
![License](https://img.shields.io/badge/License-MIT-green)

> 一个 AI 赋能的电商平台后端系统。包含完整的用户认证、商品管理、购物车、订单、评价模块，并集成了基于协同过滤的商品推荐引擎和智能客服对话系统。

---

## 📋 目录

- [系统架构](#系统架构)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [快速启动](#快速启动)
- [API 文档](#api-文档)
- [项目结构](#项目结构)

---

## 🏗 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                       客户端（前端）                          │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP / RESTful API
┌──────────────────────▼──────────────────────────────────────┐
│              Spring Boot 后端（端口 8080）                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │ 认证模块  │  │ 商品模块  │  │ 购物车模块│  │  订单模块   │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│  ┌──────────┐  ┌──────────┐                                  │
│  │ 评价模块  │  │ 推荐模块  │                                  │
│  └──────────┘  └────┬─────┘                                  │
└──────────────────────┼───────────────────────────────────────┘
                       │ RestTemplate 调用
┌──────────────────────▼───────────────────────────────────────┐
│              Python Flask AI 服务（端口 5002）                │
│  ┌────────────────┐  ┌──────────────────────────────────┐   │
│  │ 协同过滤推荐引擎 │  │   智能客服对话系统（意图识别）    │   │
│  └────────────────┘  └──────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

---

## 💻 技术栈

### 后端（Java）

| 技术 | 用途 |
|------|------|
| **Spring Boot 3.2** | 应用框架 |
| **Spring Security** | 权限认证 |
| **JWT (jjwt)** | 无状态 Token 认证 |
| **MyBatis-Plus** | ORM 持久层 |
| **MySQL** | 关系型数据库 |
| **Redis** | 缓存加速 |
| **RabbitMQ** | 异步消息队列 |
| **Spring AOP** | 统一日志切面 |
| **SpringDoc OpenAPI** | 接口文档自动生成 |
| **Maven** | 依赖管理 |
| **Docker Compose** | 容器化编排 |

### AI 服务（Python）

| 技术 | 用途 |
|------|------|
| **Flask** | Web 框架 |
| **协同过滤** | 商品推荐算法 |
| **关键词匹配 + 意图识别** | 智能客服对话管理 |

---

## ✨ 功能模块

### 🔐 用户认证
- 用户注册 / 登录
- JWT Token 签发与验证
- 角色权限控制（USER / ADMIN）

### 📦 商品管理
- 商品 CRUD（Admin 权限）
- 分页查询 + 分类筛选 + 关键词搜索
- 商品详情查看

### 🛒 购物车
- 添加 / 删除 / 修改购物车商品
- 购物车列表查看

### 📋 订单
- 创建订单
- 用户订单列表与详情查询

### ⭐ 评价
- 添加商品评价 + 评分
- 查看商品评价列表

### 🤖 AI 智能客服
- 自然语言意图识别（问候、商品咨询、价格查询、推荐、订单查询、投诉、售后）
- 多轮对话上下文管理
- 对话建议引导

### 🔥 AI 商品推荐
- 基于协同过滤的个性化推荐
- 热销商品推荐

---

## 🚀 快速启动

### 前置要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Docker & Docker Compose（可选）

### 1️⃣ 克隆项目

```bash
git clone https://github.com/0127dy/learn-ai-ecommerce.git
cd learn-ai-ecommerce
```

### 2️⃣ 配置数据库

创建 MySQL 数据库 `ecommerce`：

```sql
CREATE DATABASE ecommerce DEFAULT CHARACTER SET utf8mb4;
```

启动 Redis 和 RabbitMQ（可通过 Docker）：

```bash
docker-compose up -d redis rabbitmq mysql
```

### 3️⃣ 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`

### 4️⃣ 启动 AI 服务

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

AI 服务运行在 `http://localhost:5002`

### 5️⃣ 一键启动（使用 Docker Compose）

```bash
docker-compose up --build
```

---

## 📖 API 文档

启动后端后访问：`http://localhost:8080/swagger-ui.html`

### 主要接口一览

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录 | 公开 |
| GET | `/api/products` | 商品分页查询 | 公开 |
| GET | `/api/products/{id}` | 商品详情 | 公开 |
| POST | `/api/cart` | 添加购物车 | 需登录 |
| GET | `/api/cart` | 购物车列表 | 需登录 |
| POST | `/api/orders` | 创建订单 | 需登录 |
| GET | `/api/orders` | 订单列表 | 需登录 |
| POST | `/api/reviews` | 添加评价 | 需登录 |
| POST | `/api/ai/chat` | AI 客服对话 | 公开 |
| GET | `/api/recommend/hot` | 热销推荐 | 公开 |

---

## 📁 项目结构

```
learn-ai-ecommerce/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/ecommerce/
│   │   ├── aspect/             # AOP 切面（日志）
│   │   ├── common/             # 工具类（JWT、统一返回）
│   │   ├── config/             # 配置类（Security、Redis、MQ等）
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── mapper/             # MyBatis Mapper
│   │   └── service/            # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml     # 配置文件
│   └── pom.xml                 # Maven 依赖
├── ai-service/                 # Python AI 服务
│   ├── models/
│   │   ├── chatbot.py          # 智能客服
│   │   └── recommend.py        # 推荐引擎
│   ├── app.py                  # Flask 应用入口
│   └── requirements.txt        # Python 依赖
├── docs/                       # 文档
├── docker-compose.yml           # Docker 编排
└── README.md
```

---

## 📧 联系

项目作者：端木 · [GitHub](https://github.com/0127dy)
