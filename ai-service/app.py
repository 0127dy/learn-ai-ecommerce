"""
AI 电商智能客服与商品推荐系统 — Python AI 服务
Flask 应用，端口 5002

功能：
1. POST /api/recommend/collaborative — 协同过滤推荐
2. POST /api/recommend/hot          — 热销商品推荐
3. POST /api/chat                   — 智能客服
4. GET  /api/health                 — 健康检查
"""

from flask import Flask, request, jsonify
from models.recommend import get_collaborative_recommendations, get_hot_products
from models.chatbot import chatbot_reply

app = Flask(__name__)


# ==================== 健康检查 ====================

@app.route('/api/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        'status': 'UP',
        'service': 'ai-service',
        'version': '1.0.0'
    })


# ==================== 协同过滤推荐 ====================

@app.route('/api/recommend/collaborative', methods=['POST'])
def collaborative_recommendation():
    """
    基于物品的协同过滤推荐
    请求体: { "user_id": 1 }
    响应:   { "user_id": 1, "recommended_product_ids": [3, 7, 9, ...], "reason": "..." }
    """
    data = request.get_json(silent=True) or {}
    user_id = data.get('user_id')

    if user_id is None:
        return jsonify({'error': '缺少 user_id 参数'}), 400

    try:
        user_id = int(user_id)
    except (ValueError, TypeError):
        return jsonify({'error': 'user_id 必须是整数'}), 400

    result = get_collaborative_recommendations(user_id)
    return jsonify(result)


# ==================== 热销商品推荐 ====================

@app.route('/api/recommend/hot', methods=['POST'])
def hot_products():
    """
    热销商品推荐
    请求体: {} (无需参数)
    响应:   { "hot_product_ids": [1, 5, 6, ...], "reason": "..." }
    """
    result = get_hot_products()
    return jsonify(result)


# ==================== 智能客服 ====================

@app.route('/api/chat', methods=['POST'])
def chat():
    """
    智能客服对话接口
    请求体: { "user_id": 1, "message": "你好，我想买手机" }
    响应:   { "reply": "...", "intent": "product_inquiry", "suggestions": [...] }
    """
    data = request.get_json(silent=True) or {}
    user_id = data.get('user_id')
    message = data.get('message', '')

    if not message.strip():
        return jsonify({'error': '消息不能为空'}), 400

    result = chatbot_reply(user_id, message)
    return jsonify(result)


# ==================== 启动 ====================

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002, debug=True)
