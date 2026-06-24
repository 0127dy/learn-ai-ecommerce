# Python AI 服务 Dockerfile
FROM python:3.11-slim

WORKDIR /app

# 安装依赖
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 复制源码
COPY app.py .
COPY models/ ./models/

# 暴露端口
EXPOSE 5002

# 启动服务（使用 gunicorn 生产级服务器）
CMD ["gunicorn", "--bind", "0.0.0.0:5002", "--workers", "2", "--timeout", "60", "app:app"]
