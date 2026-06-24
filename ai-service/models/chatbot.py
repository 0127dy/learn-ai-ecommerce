"""
智能客服聊天机器人
基于规则 + 关键词匹配的客服对话系统

⭐⭐ 面试考点：
1. 意图识别：关键词匹配 → NLP 分类 → LLM
2. 对话管理：上下文记忆，多轮对话
3. 知识库 RAG：检索增强生成
"""

import re
import random
from typing import Optional

# ==================== 对话上下文（内存存储） ====================
# 实际项目应使用 Redis 等外部存储
chat_context: dict = {}  # { user_id: {"last_intent": str, "history": list} }

# 上下文保留轮数
MAX_HISTORY = 5


# ==================== 意图识别规则 ====================

def detect_intent(message: str) -> str:
    """
    意图识别
    通过关键词匹配判断用户意图

    返回意图类型：
    - greeting:     问候/打招呼
    - product_inquiry:   商品咨询
    - price_inquiry:     价格查询
    - recommendation:    推荐请求
    - order_inquiry:     订单查询
    - complaint:         投诉/问题
    - after_sale:        售后
    - farewell:          告别
    - unknown:           未识别
    """
    message = message.lower().strip()

    # 问候
    if re.search(r'^(你好|您好|嗨|hi|hello|hey|在吗|在不在)', message):
        return 'greeting'

    # 告别
    if re.search(r'(再见|拜拜|bye|88|下次见|谢谢.*?再见)', message):
        return 'farewell'

    # 推荐
    if re.search(r'(推荐|有什么.*?(推荐|好的|新)|有没有.*?推荐|推荐.*?什么|买什么好)', message):
        return 'recommendation'

    # 价格
    if re.search(r'(价格|多少钱|多少钱|价位|报价|便宜|贵|预算)', message):
        return 'price_inquiry'

    # 商品咨询
    if re.search(r'(手机|电脑|笔记本|耳机|平板|商品|产品|型号|参数|配置|功能|怎么样|好不好)', message):
        return 'product_inquiry'

    # 订单
    if re.search(r'(订单|下单|买了|购买|送货|物流|发货|配送|快递)', message):
        return 'order_inquiry'

    # 投诉
    if re.search(r'(投诉|差评|不满意|问题|坏了|故障|不行|太差|质量|退货|退款|换货)', message):
        return 'complaint'

    # 售后
    if re.search(r'(售后|维修|保修|客服|人工|服务)', message):
        return 'after_sale'

    return 'unknown'


# ==================== 回复模板 ====================

REPLIES = {
    'greeting': [
        "您好！欢迎来到 AI 智能电商平台。我是您的智能客服助手，很高兴为您服务！😊\n\n请问有什么可以帮助您的？您可以咨询商品信息、了解促销活动、查询订单状态等。",
        "您好！欢迎光临！请问您想了解什么商品？或者需要我为您推荐什么吗？",
        "嗨！很高兴见到您！有什么我可以帮您的吗？比如找商品、查价格、了解活动等。",
    ],
    'product_inquiry': [
        "我们目前有手机、笔记本、耳机、平板等多类商品，每款都经过精心挑选。\n\n热门推荐：\n📱 iPhone 15 Pro Max — 旗舰性能，仅 ¥9,999\n📱 华为 Mate 60 Pro — 国产之光，仅 ¥7,999\n💻 MacBook Air M3 — 轻薄办公首选，¥10,999\n\n需要我为您详细介绍某款商品吗？",
        "我们平台商品都很受欢迎！具体来说：\n- 手机类：iPhone 15 Pro Max、华为 Mate 60 Pro、小米14 Ultra\n- 笔记本类：MacBook Air M3、ThinkPad X1 Carbon、戴尔 XPS 16\n- 耳机类：Sony WH-1000XM5、AirPods Pro 2\n\n您对哪一类商品感兴趣？",
    ],
    'price_inquiry': [
        "以下是部分热门商品价格：\n📱 iPhone 15 Pro Max — ¥9,999\n📱 华为 Mate 60 Pro — ¥7,999\n📱 小米14 Ultra — ¥5,999\n💻 MacBook Air M3 — ¥10,999\n💻 ThinkPad X1 Carbon — ¥9,999\n🎧 Sony WH-1000XM5 — ¥2,499\n🎧 AirPods Pro 2 — ¥1,899\n\n价格可能会有促销活动，建议您关注首页活动信息！",
        "我们平台所有商品都是明码标价，性价比很高！\n\n您可以查看商品列表页了解详细价格，或者直接搜索您想了解的商品。",
    ],
    'recommendation': [
        "根据当前热销情况，为您推荐：\n🔥 1. AirPods Pro 2 — 降噪耳机爆款，¥1,899\n🔥 2. iPhone 15 Pro Max — 旗舰手机之王，¥9,999\n🔥 3. Sony WH-1000XM5 — 降噪标杆，¥2,499\n🔥 4. 小米14 Ultra — 徕卡影像旗舰，¥5,999\n\n您对哪个感兴趣？我可以为您详细介绍！",
        "给您推荐几个热销商品：\n· 如果您喜欢听音乐，推荐 Sony WH-1000XM5 或 AirPods Pro 2\n· 如果您需要办公学习，推荐 MacBook Air M3 或 iPad Air M2\n· 如果您想换手机，iPhone 15 Pro Max 和华为 Mate 60 Pro 都是顶级选择",
    ],
    'order_inquiry': [
        "关于订单问题，您可以：\n1️⃣ 登录后在「我的订单」查看订单状态\n2️⃣ 查看物流信息了解配送进度\n3️⃣ 如有疑问请联系我们的人工客服\n\n请问您需要查询哪笔订单？",
        "订单相关的问题，建议您登录后在个人中心的「订单管理」中查看。如果有物流问题，我们会及时为您处理。",
    ],
    'complaint': [
        "非常抱歉给您带来了不好的体验！😔 请您将问题具体描述一下，我们会立即核实并处理。\n\n同时您可以：\n1. 在订单页面申请退货/退款\n2. 联系人工客服进一步处理\n3. 我们的客服电话：400-888-8888",
        "非常理解您的心情！请您不要着急，我们会尽快为您解决问题。请提供您的订单号和具体问题，我们会在 24 小时内给您回复。",
    ],
    'after_sale': [
        "售后服务相关信息：\n· 我们提供 7 天无理由退换货\n· 大部分商品享受 1 年官方保修\n· 如遇质量问题，平台承担退货运费\n· 售后热线：400-888-8888\n\n请问您需要办理什么售后业务？",
        "您好！售后问题我们非常重视。请您提供购买商品的订单号，我们会尽快为您处理退换货或维修事宜。",
    ],
    'farewell': [
        "感谢您的咨询！祝您购物愉快！😊 如果还有其他问题，随时来找我哦！",
        "不客气！很高兴为您服务！祝您生活愉快！🎉",
        "再见！期待再次为您服务！有需要随时找我~",
    ],
    'unknown': [
        "抱歉，我没有完全理解您的意思。您可以尝试以下方式：\n· 直接搜索商品名称\n· 说「推荐」获取商品推荐\n· 说「价格」查看商品价格\n· 说「订单」查询订单状态\n\n或者请您换一种方式描述您的问题？",
        "不好意思，我暂时无法理解这个问题。您可以试试这样说：\n· 「我想买手机」\n· 「推荐几款耳机」\n· 「我的订单到哪了」\n· 「价格是多少」\n\n我们的客服系统正在持续升级中，感谢您的理解！",
    ],
}

# 推荐商品时附加的建议
SUGGESTIONS = {
    'greeting': ['推荐商品', '查看价格', '查看活动'],
    'product_inquiry': ['iPhone 15', 'MacBook Air', 'Sony耳机'],
    'price_inquiry': ['iPhone价格', '推荐便宜商品', '促销活动'],
    'recommendation': ['iPhone详情', '华为Mate60', 'AirPods'],
    'order_inquiry': ['查看订单', '物流查询', '联系客服'],
    'complaint': ['申请退款', '联系人工', '客服电话'],
    'after_sale': ['退货流程', '保修查询', '联系售后'],
    'farewell': [],
    'unknown': ['推荐商品', '查看价格', '联系人工客服'],
}


# ==================== 客服回复逻辑 ====================

def chatbot_reply(user_id: Optional[int], message: str) -> dict:
    """
    生成客服回复

    参数:
        user_id: 用户 ID（可能为 None）
        message: 用户消息

    返回:
        {
            "reply": "回复内容",
            "intent": "识别的意图",
            "suggestions": ["推荐操作1", "推荐操作2", ...]
        }
    """
    # 意图识别
    intent = detect_intent(message)

    # 更新对话上下文
    if user_id:
        if user_id not in chat_context:
            chat_context[user_id] = {"last_intent": None, "history": []}
        ctx = chat_context[user_id]
        ctx["last_intent"] = intent
        ctx["history"].append({"role": "user", "message": message, "intent": intent})
        # 保留最近的 MAX_HISTORY 条记录
        if len(ctx["history"]) > MAX_HISTORY:
            ctx["history"] = ctx["history"][-MAX_HISTORY:]

    # 选择回复
    replies = REPLIES.get(intent, REPLIES['unknown'])
    reply = random.choice(replies)

    # 获取建议操作
    suggestions = SUGGESTIONS.get(intent, [])

    return {
        'reply': reply,
        'intent': intent,
        'suggestions': suggestions,
    }
