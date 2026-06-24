"""
商品推荐模型
实现基于物品的协同过滤推荐 + 热销商品推荐

⭐⭐ 面试考点：
1. 协同过滤：User-based（找相似用户） vs Item-based（找相似商品）
2. 冷启动问题：新用户/新商品没有历史数据时如何处理
3. 混合推荐：协同过滤 + 基于内容 + 热销榜组合
"""

# ==================== 模拟数据 ====================
# 实际项目中这些数据应从数据库或 Redis 缓存中读取

# 商品分类映射
PRODUCT_CATEGORIES = {
    1: {'name': 'iPhone 15 Pro Max',     'category': '手机',   'sales': 120, 'rating': 4.8},
    2: {'name': '华为 Mate 60 Pro',      'category': '手机',   'sales': 95,  'rating': 4.9},
    3: {'name': 'MacBook Air M3',        'category': '笔记本', 'sales': 80,  'rating': 4.7},
    4: {'name': 'ThinkPad X1 Carbon',    'category': '笔记本', 'sales': 60,  'rating': 4.6},
    5: {'name': 'Sony WH-1000XM5',       'category': '耳机',   'sales': 200, 'rating': 4.9},
    6: {'name': 'AirPods Pro 2',         'category': '耳机',   'sales': 300, 'rating': 4.8},
    7: {'name': 'iPad Air M2',           'category': '平板',   'sales': 70,  'rating': 4.7},
    8: {'name': '机械革命 旷世16',       'category': '笔记本', 'sales': 45,  'rating': 4.5},
    9: {'name': '小米14 Ultra',          'category': '手机',   'sales': 150, 'rating': 4.7},
    10:{'name': '戴尔 XPS 16',           'category': '笔记本', 'sales': 30,  'rating': 4.6},
}

# 用户历史行为（模拟）
# 格式: { user_id: [(product_id, action_score), ...] }
# action_score: 浏览=1, 加购=2, 购买=3, 评价=4
USER_HISTORY = {
    1: [(1, 4), (5, 3), (6, 4)],   # admin: 买了iPhone和耳机
    2: [(1, 4), (5, 4), (6, 3)],   # user1: 买了iPhone和耳机
    3: [(2, 4), (9, 4)],           # user2: 买了华为和小米
}

# 同分类商品映射（基于内容的推荐辅助）
CATEGORY_PRODUCTS = {
    '手机':   [1, 2, 9],
    '笔记本': [3, 4, 8, 10],
    '耳机':   [5, 6],
    '平板':   [7],
}


# ==================== 协同过滤推荐 ====================

def get_collaborative_recommendations(user_id: int) -> dict:
    """
    基于物品的协同过滤推荐
    流程：
    1. 获取目标用户的历史商品
    2. 对每个历史商品，找到同分类的其他商品
    3. 按销量和评分加权计算推荐得分
    4. 返回 Top-N 推荐

    如果是新用户（无历史数据），按热销推荐 + 各分类精选
    """
    user_history = USER_HISTORY.get(user_id, [])
    if not user_history:
        # 新用户冷启动：推荐各分类的热销商品
        return _cold_start_recommendation(user_id)

    # 获取用户历史商品 ID 集合
    history_product_ids = {item[0] for item in user_history}

    # 计算每个商品的推荐得分
    scores = {}  # { product_id: score }

    for product_id, action_score in user_history:
        product = PRODUCT_CATEGORIES.get(product_id)
        if not product:
            continue

        category = product['category']
        # 同分类商品
        same_category = CATEGORY_PRODUCTS.get(category, [])

        for candidate_id in same_category:
            if candidate_id in history_product_ids:
                continue  # 排除已买过的

            candidate = PRODUCT_CATEGORIES.get(candidate_id)
            if not candidate:
                continue

            # 推荐得分 = 行为权重 × 销量权重 × 评分权重
            behavior_weight = action_score / 4.0  # 归一化到 0-1
            sales_weight = min(candidate['sales'] / 300, 1.0)  # 销量归一化
            rating_weight = candidate['rating'] / 5.0  # 评分归一化

            score = behavior_weight * (sales_weight * 0.4 + rating_weight * 0.6)
            scores[candidate_id] = scores.get(candidate_id, 0) + score

    # 按得分排序，取 Top-6
    sorted_products = sorted(scores.items(), key=lambda x: x[1], reverse=True)
    recommended_ids = [pid for pid, _ in sorted_products[:6]]

    # 如果推荐不足 6 个，补充热销商品
    if len(recommended_ids) < 6:
        hot_ids = _get_top_selling_ids(6)
        for hid in hot_ids:
            if hid not in recommended_ids and hid not in history_product_ids:
                recommended_ids.append(hid)
            if len(recommended_ids) >= 6:
                break

    reason = "基于您的购买记录，为您推荐了同品类热门商品"
    return {
        'user_id': user_id,
        'recommended_product_ids': recommended_ids,
        'reason': reason,
    }


def _cold_start_recommendation(user_id: int) -> dict:
    """
    冷启动推荐：新用户没有历史数据时
    策略：从每个分类选取销量最高的商品
    """
    recommended_ids = []
    for cat, product_ids in CATEGORY_PRODUCTS.items():
        # 从每个分类取销量最高的
        sorted_by_sales = sorted(
            product_ids,
            key=lambda pid: PRODUCT_CATEGORIES[pid]['sales'],
            reverse=True,
        )
        if sorted_by_sales:
            recommended_ids.append(sorted_by_sales[0])

        if len(recommended_ids) >= 6:
            break

    # 如果不足 6 个，补充热销
    if len(recommended_ids) < 6:
        hot_ids = _get_top_selling_ids(6)
        for hid in hot_ids:
            if hid not in recommended_ids:
                recommended_ids.append(hid)
            if len(recommended_ids) >= 6:
                break

    reason = "欢迎新用户！为您推荐各分类的热门商品"
    return {
        'user_id': user_id,
        'recommended_product_ids': recommended_ids[:6],
        'reason': reason,
    }


# ==================== 热销商品推荐 ====================

def get_hot_products() -> dict:
    """
    热销商品推荐
    综合考虑销量和评分，返回 Top-6
    """
    hot_ids = _get_top_selling_ids(6)
    reason = "本周热销商品推荐，基于销量和用户评分综合排名"
    return {
        'hot_product_ids': hot_ids,
        'reason': reason,
    }


def _get_top_selling_ids(n: int = 6) -> list:
    """获取销量最高的 N 个商品 ID"""
    sorted_products = sorted(
        PRODUCT_CATEGORIES.items(),
        key=lambda x: x[1]['sales'] * 0.7 + x[1]['rating'] * 20,
        reverse=True,
    )
    return [pid for pid, _ in sorted_products[:n]]
