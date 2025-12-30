# 客服助手服务 (rs-assistant)

客服助手服务负责 AI 智能问答和实时客服通信。

## 服务信息

- **服务名**：assistant-service
- **HTTP 端口**：18084
- **WebSocket 端口**：18085
- **模块路径**：`RailwaySystem-Backend/rs-service/rs-assistant`

## 功能职责

- AI 智能问答（基于阿里通义大模型）
- WebSocket 实时通信
- 人工客服转接
- 会话记忆管理

## API 路由

| 路由前缀 | 说明 |
|----------|------|
| `/customer/assistant/**` | 用户端客服接口 |
| `/admin/memory/**` | 会话记忆管理 |

## WebSocket 路径

| 路径 | 说明 |
|------|------|
| `/ws/assistant/user` | 用户端 WebSocket |
| `/ws/assistant/agent` | 客服端 WebSocket |

## 技术要点

- Netty WebSocket 长连接
- LangChain4j + 阿里通义大模型
- RabbitMQ 会话消息持久化
