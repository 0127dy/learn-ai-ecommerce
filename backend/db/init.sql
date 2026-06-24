-- =============================================
-- AI 电商智能客服与商品推荐系统 — 数据库初始化脚本
-- 数据库: ecommerce (MySQL 8.0+, utf8mb4)
-- =============================================

-- ==================== 用户表 ====================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL               COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL               COMMENT '密码（BCrypt加密）',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER / ADMIN',
    `phone`       VARCHAR(20)  DEFAULT NULL           COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL           COMMENT '邮箱',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 商品表 ====================
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`        VARCHAR(200)  NOT NULL                COMMENT '商品名称',
    `category`    VARCHAR(50)   DEFAULT NULL            COMMENT '商品分类',
    `description` TEXT          DEFAULT NULL            COMMENT '商品描述',
    `price`       DECIMAL(10,2) NOT NULL                COMMENT '价格',
    `stock`       INT           NOT NULL DEFAULT 0      COMMENT '库存数量',
    `image_url`   VARCHAR(500)  DEFAULT NULL            COMMENT '商品图片URL',
    `sales`       INT           NOT NULL DEFAULT 0      COMMENT '销量',
    `rating`      DOUBLE        NOT NULL DEFAULT 5.0    COMMENT '评分（1-5）',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_sales` (`sales` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ==================== 订单表 ====================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id`     BIGINT        NOT NULL                COMMENT '用户ID',
    `product_id`  BIGINT        NOT NULL                COMMENT '商品ID',
    `quantity`    INT           NOT NULL                COMMENT '购买数量',
    `total_price` DECIMAL(10,2) NOT NULL                COMMENT '总价',
    `status`      VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PAID/SHIPPED/COMPLETED/CANCELLED',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ==================== 购物车表 ====================
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
    `id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id`    BIGINT NOT NULL                COMMENT '用户ID',
    `product_id` BIGINT NOT NULL                COMMENT '商品ID',
    `quantity`   INT    NOT NULL DEFAULT 1      COMMENT '数量',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ==================== 评价表 ====================
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
    `product_id`  BIGINT       NOT NULL                COMMENT '商品ID',
    `content`     TEXT         DEFAULT NULL            COMMENT '评价内容',
    `rating`      INT          NOT NULL DEFAULT 5      COMMENT '评分（1-5）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- =============================================
-- 演示数据（商品/订单/购物车/评价）
-- 用户由 DataInitializer.java 自动创建
-- =============================================

-- 商品数据（10 种商品，4 个分类）
INSERT INTO `products` (`name`, `category`, `description`, `price`, `stock`, `image_url`, `sales`, `rating`) VALUES
('iPhone 15 Pro Max', '手机', 'Apple 最新旗舰手机，A17 Pro 芯片，钛金属设计', 9999.00, 50, 'https://via.placeholder.com/400x400?text=iPhone15', 120, 4.8),
('华为 Mate 60 Pro', '手机', '华为旗舰手机，麒麟芯片，卫星通话', 7999.00, 30, 'https://via.placeholder.com/400x400?text=Mate60', 95, 4.9),
('MacBook Air M3', '笔记本', 'Apple M3 芯片，15.3英寸 Liquid Retina 显示屏', 10999.00, 25, 'https://via.placeholder.com/400x400?text=MacBookAir', 80, 4.7),
('ThinkPad X1 Carbon', '笔记本', '联想商用旗舰，14英寸 2.8K OLED 屏，仅重1.12kg', 9999.00, 20, 'https://via.placeholder.com/400x400?text=ThinkPadX1', 60, 4.6),
('Sony WH-1000XM5', '耳机', '索尼旗舰降噪耳机，30小时续航，顶级降噪', 2499.00, 80, 'https://via.placeholder.com/400x400?text=WH1000XM5', 200, 4.9),
('AirPods Pro 2', '耳机', 'Apple 主动降噪耳机，自适应音频，USB-C', 1899.00, 100, 'https://via.placeholder.com/400x400?text=AirPodsPro2', 300, 4.8),
('iPad Air M2', '平板', 'Apple M2 芯片，11英寸 Liquid Retina 显示屏', 4799.00, 40, 'https://via.placeholder.com/400x400?text=iPadAir', 70, 4.7),
('机械革命 旷世16', '笔记本', '14代 i9 + RTX 4070，2.5K 240Hz 电竞屏', 8499.00, 15, 'https://via.placeholder.com/400x400?text=Jixie', 45, 4.5),
('小米14 Ultra', '手机', '骁龙8Gen3，徕卡光学 Summilux 镜头', 5999.00, 60, 'https://via.placeholder.com/400x400?text=Mi14Ultra', 150, 4.7),
('戴尔 XPS 16', '笔记本', 'Intel Core Ultra，RTX 4070，4K OLED 触控屏', 15999.00, 10, 'https://via.placeholder.com/400x400?text=DellXPS16', 30, 4.6);

-- 注意：用户的 ID 由数据库自动生成，DataInitializer 会创建 admin(id=1), user1(id=2), user2(id=3)
-- 以下订单/购物车/评价数据假设 user1=id:2, user2=id:3

-- 订单数据
INSERT INTO `orders` (`user_id`, `product_id`, `quantity`, `total_price`, `status`) VALUES
(2, 1, 1, 9999.00, 'COMPLETED'),
(2, 5, 2, 4998.00, 'COMPLETED'),
(2, 6, 1, 1899.00, 'PAID'),
(3, 2, 1, 7999.00, 'COMPLETED'),
(3, 9, 1, 5999.00, 'PAID');

-- 购物车数据
INSERT INTO `cart_items` (`user_id`, `product_id`, `quantity`) VALUES
(2, 3, 1),
(2, 7, 1),
(3, 4, 1),
(3, 10, 1);

-- 评价数据
INSERT INTO `reviews` (`user_id`, `product_id`, `content`, `rating`) VALUES
(2, 1, '非常棒的手机！A17 Pro 性能太强了，拍照效果也很好。', 5),
(2, 5, '降噪效果一流，戴上去世界都安静了。', 5),
(2, 6, '音质很棒，降噪效果也很好。', 4),
(3, 2, '国产之光！信号超强，卫星通话太实用了。', 5),
(3, 9, '徕卡相机确实厉害，拍照效果惊艳！', 5);
