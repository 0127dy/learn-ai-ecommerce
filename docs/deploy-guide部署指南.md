# 部署指南 — AI 电商智能客服与商品推荐系统

## 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | Java 开发环境 |
| Maven | 3.8+ | Java 构建工具 |
| Python | 3.10+ | AI 服务运行环境 |
| Docker | 24+ | 容器化部署（可选） |
| Docker Compose | 2.20+ | 多容器编排（可选） |
| MySQL | 8.0+ | 数据库 |
| Redis | 7+ | 缓存 |
| RabbitMQ | 3.12+ | 消息队列 |

---

## 本地开发启动

### 第一步：启动基础设施

启动 MySQL、Redis、RabbitMQ（如果本地没有安装，可以使用 Docker）：

```bash
# 使用 Docker 启动基础设施
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=ecommerce \
  -p 3306:3306 \
  mysql:8.0

docker run -d --name redis \
  -p 6379:6379 \
  redis:7-alpine

docker run -d --name rabbitmq \
  -e RABBITMQ_DEFAULT_USER=ecommerce \
  -e RABBITMQ_DEFAULT_PASS=ecommerce123 \
  -p 5672:5672 \
  rabbitmq:3.12-management-alpine
```

### 第二步：初始化数据库

```bash
# 使用 MySQL 客户端执行初始化脚本
mysql -u root -proot123 ecommerce < backend/db/init.sql
```

### 第三步：启动 Python AI 服务

```bash
cd ai-service

# 创建虚拟环境（推荐）
python -m venv venv
source venv/bin/activate  # Linux/Mac
# 或 .\venv\Scripts\activate  # Windows

# 安装依赖
pip install -r requirements.txt

# 启动服务（端口 5002）
python app.py
```

### 第四步：启动 Spring Boot 后端

```bash
cd backend

# 编译打包
mvn clean package -DskipTests

# 启动（端口 8080）
java -jar target/ecommerce-backend-1.0.0.jar
```

或者使用 Maven 直接启动：

```bash
cd backend
mvn spring-boot:run
```

### 第五步：验证启动

```bash
# 检查后端
curl http://localhost:8080/api/health

# 检查 AI 服务
curl http://localhost:5002/api/health

# 检查数据库是否初始化
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"user123"}'
```

---

## Docker Compose 一键启动

推荐使用 Docker Compose 一键启动所有服务：

```bash
# 在项目根目录执行
docker-compose up --build
```

这将按以下顺序启动：
1. MySQL（数据库，自动执行 init.sql 初始化）
2. Redis（缓存）
3. RabbitMQ（消息队列）
4. AI Service（Python Flask，端口 5002）
5. Backend（Spring Boot，端口 8080）

> **注意：** 首次启动时，MySQL 需要几分钟初始化。可以通过 `docker-compose logs -f` 查看启动日志。

### 后台运行

```bash
docker-compose up --build -d
```

### 停止所有服务

```bash
docker-compose down
```

### 清除数据（重建数据库）

```bash
docker-compose down -v
docker-compose up --build
```

---

## API 测试示例

### 1. 用户注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "phone": "13900001111",
    "email": "test@example.com"
  }'
```

### 2. 用户登录（获取 Token）

```bash
# 管理员登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 普通用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"user123"}'
```

保存返回的 token 值。

### 3. 查询商品列表

```bash
# 所有商品
curl http://localhost:8080/api/products

# 按分类筛选
curl "http://localhost:8080/api/products?category=手机"

# 关键词搜索
curl "http://localhost:8080/api/products?keyword=iPhone"
```

### 4. 购物车操作（需 Token）

```bash
TOKEN="<登录返回的token>"

# 添加商品到购物车
curl -X POST http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":1}'

# 查看购物车
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"
```

### 5. 创建订单（需 Token）

```bash
TOKEN="<登录返回的token>"

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":1}'
```

### 6. 获取推荐（需 Token）

```bash
TOKEN="<登录返回的token>"

# 协同过滤推荐
curl http://localhost:8080/api/recommend/products/1 \
  -H "Authorization: Bearer $TOKEN"

# 热销推荐（公开）
curl http://localhost:8080/api/recommend/hot
```

### 7. AI 客服对话

```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"你好，我想买一台笔记本电脑"}'
```

---

## 常见问题排查

### 后端启动报数据库连接失败

检查：
1. MySQL 是否已启动：`docker ps | grep mysql`
2. 数据库是否已初始化：`mysql -u root -proot123 -e "use ecommerce; show tables;"`
3. `application.yml` 中的数据库连接配置是否正确

### AI 服务连接失败

检查：
1. AI 服务是否已启动：`curl http://localhost:5002/api/health`
2. `application.yml` 中的 `ai.service.url` 配置是否正确
3. Docker 环境中，后端通过 `ai-service:5002` 访问 AI 服务

### Token 过期

JWT Token 默认有效期为 24 小时。过期后需要重新登录获取新的 Token。
可以在 `application.yml` 中修改 `jwt.expiration` 值调整过期时间。
