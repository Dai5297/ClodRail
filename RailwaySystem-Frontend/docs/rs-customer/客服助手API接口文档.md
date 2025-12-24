# 客服助手模块 API 接口文档

## 目录

- [1. 模块概述](#1-模块概述)
- [2. 接口列表](#2-接口列表)
  - [2.1 AI 智能问答](#21-ai智能问答)
  - [2.2 获取会话历史列表](#22-获取会话历史列表)
  - [2.3 获取会话消息历史](#23-获取会话消息历史)
  - [2.4 创建新会话](#24-创建新会话)
  - [2.5 删除会话](#25-删除会话)
  - [2.6 获取客服状态](#26-获取客服状态)
  - [2.7 申请转接人工客服](#27-申请转接人工客服)
- [3. WebSocket 接口](#3-websocket-接口)
  - [3.1 人工客服实时通信](#31-人工客服实时通信)
- [4. 数据模型](#4-数据模型)
- [5. 错误码说明](#5-错误码说明)

---

## 1. 模块概述

客服助手模块提供 AI 智能客服和人工客服两种服务模式：

### 功能特性

- **AI 智能客服**：基于 LangChain4j 实现的智能对话系统，支持流式响应
- **人工客服转接**：支持从 AI 客服转接到人工客服
- **会话管理**：支持多会话管理，保存历史记录
- **实时通信**：基于 WebSocket 实现的实时消息推送
- **会话超时控制**：人工客服会话 3 分钟自动超时

### 技术栈

- **后端服务**：Spring Boot + LangChain4j
- **实时通信**：WebSocket
- **前端框架**：Vue 3 + Element Plus
- **流式响应**：Fetch API Stream

---

## 2. 接口列表

### 2.1 AI 智能问答

#### 接口信息

- **接口路径**：`/user/assistant/chat`
- **请求方法**：`POST`
- **接口描述**：发送消息给 AI 助手，支持流式响应
- **认证方式**：Bearer Token

#### 请求参数

| 参数名    | 类型   | 必填 | 说明         |
| --------- | ------ | ---- | ------------ |
| message   | String | 是   | 用户消息内容 |
| sessionId | String | 是   | 会话 ID      |

#### 请求示例

```json
{
  "message": "如何查询车次信息？",
  "sessionId": "session_1699267200000"
}
```

#### 响应说明

该接口返回流式响应（Stream），通过 `text/event-stream` 格式实时推送 AI 回复内容。

**响应内容**：分块返回的文本片段

#### 前端调用示例

```javascript
const response = await fetch("/assistant/chat", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  },
  body: JSON.stringify({
    message: "如何查询车次信息？",
    sessionId: "session_1699267200000",
  }),
});

const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { done, value } = await reader.read();
  if (done) break;

  const chunk = decoder.decode(value, { stream: true });
  console.log("接收到片段:", chunk);
}
```

---

### 2.2 获取会话历史列表

#### 接口信息

- **接口路径**：`/user/assistant/sessions`
- **请求方法**：`GET`
- **接口描述**：获取当前用户的所有会话列表
- **认证方式**：Bearer Token

#### 请求参数

无

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |
| data    | Array   | 会话列表     |

#### 会话对象 (Session)

| 参数名       | 类型    | 说明                      |
| ------------ | ------- | ------------------------- |
| sessionId    | String  | 会话 ID                   |
| title        | String  | 会话标题                  |
| type         | String  | 会话类型：ai / human      |
| lastMessage  | String  | 最后一条消息              |
| updateTime   | String  | 更新时间（ISO 8601 格式） |
| messageCount | Integer | 消息数量                  |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "sessionId": "session_1699267200000",
      "title": "当前对话",
      "type": "ai",
      "lastMessage": "如何查询车次信息...",
      "updateTime": "2025-11-05T14:30:00Z",
      "messageCount": 8
    },
    {
      "sessionId": "session_1699180800000",
      "title": "昨日对话",
      "type": "ai",
      "lastMessage": "如何退票改签...",
      "updateTime": "2025-11-04T10:15:00Z",
      "messageCount": 12
    },
    {
      "sessionId": "session_1699094400000",
      "title": "人工客服",
      "type": "human",
      "lastMessage": "已解决问题...",
      "updateTime": "2025-11-03T16:45:00Z",
      "messageCount": 25
    }
  ]
}
```

---

### 2.3 获取会话消息历史

#### 接口信息

- **接口路径**：`/user/assistant/history/{sessionId}`
- **请求方法**：`GET`
- **接口描述**：获取指定会话的消息历史记录
- **认证方式**：Bearer Token

#### 路径参数

| 参数名    | 类型   | 必填 | 说明    |
| --------- | ------ | ---- | ------- |
| sessionId | String | 是   | 会话 ID |

#### 查询参数

| 参数名   | 类型    | 必填 | 默认值 | 说明     |
| -------- | ------- | ---- | ------ | -------- |
| page     | Integer | 否   | 1      | 页码     |
| pageSize | Integer | 否   | 50     | 每页数量 |

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |
| data    | Object  | 分页数据     |

#### 消息对象 (Message)

| 参数名    | 类型   | 说明                                 |
| --------- | ------ | ------------------------------------ |
| messageId | String | 消息 ID                              |
| type      | String | 消息类型：user / ai / human / system |
| content   | String | 消息内容                             |
| timestamp | String | 发送时间（ISO 8601 格式）            |
| sender    | String | 发送者名称（人工客服时有效）         |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 25,
    "page": 1,
    "pageSize": 50,
    "messages": [
      {
        "messageId": "msg_1699267201000",
        "type": "user",
        "content": "如何查询车次信息？",
        "timestamp": "2025-11-05T14:30:00Z"
      },
      {
        "messageId": "msg_1699267202000",
        "type": "ai",
        "content": "您可以通过以下方式查询车次信息...",
        "timestamp": "2025-11-05T14:30:05Z"
      },
      {
        "messageId": "msg_1699267210000",
        "type": "system",
        "content": "已成功转接人工客服",
        "timestamp": "2025-11-05T14:31:00Z"
      },
      {
        "messageId": "msg_1699267215000",
        "type": "human",
        "content": "您好！我是客服小王，很高兴为您服务...",
        "timestamp": "2025-11-05T14:31:05Z",
        "sender": "小王"
      }
    ]
  }
}
```

---

### 2.4 创建新会话

#### 接口信息

- **接口路径**：`/user/assistant/session`
- **请求方法**：`POST`
- **接口描述**：创建一个新的 AI 客服会话
- **认证方式**：Bearer Token

#### 请求参数

无（自动创建新会话）

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |
| data    | Object  | 会话对象     |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "创建成功",
  "data": {
    "sessionId": "session_1699267300000",
    "title": "新建对话",
    "type": "ai",
    "lastMessage": "",
    "updateTime": "2025-11-05T14:35:00Z",
    "messageCount": 0
  }
}
```

---

### 2.5 删除会话

#### 接口信息

- **接口路径**：`/user/assistant/session/{sessionId}`
- **请求方法**：`DELETE`
- **接口描述**：删除指定的会话及其所有消息记录
- **认证方式**：Bearer Token

#### 路径参数

| 参数名    | 类型   | 必填 | 说明    |
| --------- | ------ | ---- | ------- |
| sessionId | String | 是   | 会话 ID |

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "删除成功"
}
```

---

### 2.6 获取客服状态

#### 接口信息

- **接口路径**：`/user/assistant/service/status`
- **请求方法**：`GET`
- **接口描述**：获取当前人工客服的在线状态和排队情况
- **认证方式**：Bearer Token

#### 请求参数

无

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |
| data    | Object  | 客服状态信息 |

#### 客服状态对象 (ServiceStatus)

| 参数名          | 类型    | 说明               |
| --------------- | ------- | ------------------ |
| onlineCount     | Integer | 在线客服数量       |
| queueCount      | Integer | 当前排队人数       |
| averageWaitTime | Integer | 平均等待时间（秒） |
| isAvailable     | Boolean | 是否可用           |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "onlineCount": 12,
    "queueCount": 5,
    "averageWaitTime": 120,
    "isAvailable": true
  }
}
```

---

### 2.7 申请转接人工客服

#### 接口信息

- **接口路径**：`/user/assistant/service/transfer`
- **请求方法**：`POST`
- **接口描述**：将 AI 客服会话转接到人工客服
- **认证方式**：Bearer Token

#### 请求参数

| 参数名    | 类型   | 必填 | 说明             |
| --------- | ------ | ---- | ---------------- |
| sessionId | String | 是   | 当前会话 ID      |
| question  | String | 否   | 问题描述（可选） |

#### 请求示例

```json
{
  "sessionId": "session_1699267200000",
  "question": "订票遇到支付问题，需要人工帮助"
}
```

#### 响应参数

| 参数名  | 类型    | 说明         |
| ------- | ------- | ------------ |
| success | Boolean | 请求是否成功 |
| code    | Integer | 状态码       |
| message | String  | 响应消息     |
| data    | Object  | 转接信息     |

#### 转接信息对象 (TransferInfo)

| 参数名            | 类型    | 说明                              |
| ----------------- | ------- | --------------------------------- |
| serviceId         | String  | 客服 ID                           |
| serviceName       | String  | 客服名称                          |
| waitTime          | Integer | 实际等待时间（秒）                |
| connectionTimeout | Integer | 连接超时时间（毫秒，固定 3 分钟） |

#### 响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "转接成功",
  "data": {
    "serviceId": "service_001",
    "serviceName": "小王",
    "waitTime": 15,
    "connectionTimeout": 180000
  }
}
```

#### 错误响应示例

```json
{
  "success": false,
  "code": 503,
  "message": "当前客服繁忙，请稍后再试"
}
```

---

## 3. WebSocket 接口

### 3.1 人工客服实时通信

#### 连接信息

- **WebSocket URL**：`ws://localhost:18080/ws/assistant`
- **协议**：WebSocket
- **认证方式**：Query Parameter (token)

#### 连接参数

| 参数名    | 类型   | 必填 | 说明     |
| --------- | ------ | ---- | -------- |
| sessionId | String | 是   | 会话 ID  |
| token     | String | 是   | 认证令牌 |

#### 连接示例

```javascript
const token = localStorage.getItem("token");
const sessionId = "session_1699267200000_service_001";
const wsUrl = `ws://localhost:18080/ws/assistant?sessionId=${sessionId}&token=${token}`;

const ws = new WebSocket(wsUrl);
```

#### 连接特性

- **心跳机制**：每 30 秒发送一次心跳（ping/pong）
- **心跳超时**：10 秒未收到 pong 响应则断开连接
- **会话超时**：3 分钟无活动自动断开
- **自动重连**：最多重连 3 次，每次间隔 3 秒

#### 消息格式

##### 客户端发送消息

**业务消息**

```json
{
  "type": "message",
  "content": "我想咨询退票流程",
  "sessionId": "session_1699267200000"
}
```

**心跳消息**

```json
{
  "type": "ping"
}
```

##### 服务端推送消息

**业务消息**

```json
{
  "type": "message",
  "content": "退票流程如下...",
  "sender": "小王",
  "timestamp": "2025-11-05T14:30:00Z"
}
```

**心跳响应**

```json
{
  "type": "pong"
}
```

**状态更新**

```json
{
  "type": "status",
  "online": true
}
```

**系统通知**

```json
{
  "type": "system",
  "content": "客服小王已加入会话"
}
```

#### WebSocket 事件

##### 连接打开 (onopen)

```javascript
ws.onopen = () => {
  console.log("WebSocket 连接成功");
};
```

##### 接收消息 (onmessage)

```javascript
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  console.log("收到消息:", data);
};
```

##### 连接关闭 (onclose)

```javascript
ws.onclose = (event) => {
  console.log("WebSocket 连接关闭:", event.code, event.reason);
};
```

##### 连接错误 (onerror)

```javascript
ws.onerror = (error) => {
  console.error("WebSocket 错误:", error);
};
```

#### 关闭码说明

| 关闭码 | 说明                   |
| ------ | ---------------------- |
| 1000   | 正常关闭               |
| 1001   | 服务端主动关闭         |
| 1006   | 异常断开               |
| 1008   | 违反策略（如认证失败） |
| 1011   | 服务端错误             |
| 4000   | 会话超时（3 分钟）     |

---

## 4. 数据模型

### 4.1 会话模型 (Session)

```typescript
interface Session {
  sessionId: string; // 会话ID
  userId: string; // 用户ID
  title: string; // 会话标题
  type: "ai" | "human"; // 会话类型
  lastMessage: string; // 最后一条消息
  updateTime: string; // 更新时间（ISO 8601）
  createTime: string; // 创建时间（ISO 8601）
  messageCount: number; // 消息数量
  status: "active" | "closed"; // 会话状态
}
```

### 4.2 消息模型 (Message)

```typescript
interface Message {
  messageId: string; // 消息ID
  sessionId: string; // 会话ID
  type: "user" | "ai" | "human" | "system"; // 消息类型
  content: string; // 消息内容
  sender?: string; // 发送者（人工客服时有效）
  timestamp: string; // 发送时间（ISO 8601）
  streaming?: boolean; // 是否正在流式输出
}
```

### 4.3 客服状态模型 (ServiceStatus)

```typescript
interface ServiceStatus {
  onlineCount: number; // 在线客服数量
  queueCount: number; // 当前排队人数
  averageWaitTime: number; // 平均等待时间（秒）
  isAvailable: boolean; // 是否可用
}
```

### 4.4 转接信息模型 (TransferInfo)

```typescript
interface TransferInfo {
  serviceId: string; // 客服ID
  serviceName: string; // 客服名称
  waitTime: number; // 实际等待时间（秒）
  connectionTimeout: number; // 连接超时时间（毫秒）
}
```

---

## 5. 错误码说明

### HTTP 状态码

| 状态码 | 说明             |
| ------ | ---------------- |
| 200    | 请求成功         |
| 400    | 请求参数错误     |
| 401    | 未认证或令牌失效 |
| 403    | 无权限访问       |
| 404    | 资源不存在       |
| 500    | 服务器内部错误   |
| 503    | 服务不可用       |

### 业务错误码

| 错误码 | 说明                 |
| ------ | -------------------- |
| 10001  | 会话不存在           |
| 10002  | 会话已过期           |
| 10003  | 消息发送失败         |
| 10004  | AI 服务异常          |
| 10005  | WebSocket 连接失败   |
| 10006  | 客服繁忙，请稍后再试 |
| 10007  | 会话超时（3 分钟）   |
| 10008  | 无可用客服           |
| 10009  | 转接失败             |
| 10010  | 心跳超时             |

### 错误响应格式

```json
{
  "success": false,
  "code": 10006,
  "message": "客服繁忙，请稍后再试",
  "data": null,
  "timestamp": "2025-11-05T14:30:00Z"
}
```

---

## 6. 使用场景示例

### 6.1 完整的 AI 对话流程

```javascript
// 1. 创建新会话
const sessionResponse = await createChatSession();
const sessionId = sessionResponse.data.sessionId;

// 2. 发送消息给 AI
const message = "如何购买学生票？";
await sendAIMessage(message, sessionId, (chunk) => {
  // 实时显示 AI 回复片段
  console.log("AI 回复:", chunk);
});

// 3. 查看历史记录
const historyResponse = await getChatHistory(sessionId);
console.log("历史消息:", historyResponse.data.messages);
```

### 6.2 转接人工客服流程

```javascript
// 1. 查询客服状态
const statusResponse = await getCustomerServiceStatus();
console.log("在线客服:", statusResponse.data.onlineCount);
console.log("排队人数:", statusResponse.data.queueCount);

// 2. 申请转接
const transferResponse = await requestTransfer(sessionId, "订票遇到支付问题");
const { serviceId, serviceName } = transferResponse.data;

// 3. 建立 WebSocket 连接
const ws = new WebSocket(
  `ws://localhost:18080/ws/assistant?sessionId=${sessionId}_${serviceId}&token=${token}`
);

ws.onopen = () => {
  console.log("已连接到人工客服:", serviceName);
};

ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.type === "message") {
    console.log(`客服 ${data.sender}:`, data.content);
  }
};

// 4. 发送消息给人工客服
ws.send(
  JSON.stringify({
    type: "message",
    content: "我想退票，但是找不到退票按钮",
    sessionId: sessionId,
  })
);

// 5. 3分钟后自动超时断开
setTimeout(() => {
  console.log("会话超时，自动断开");
  ws.close();
}, 3 * 60 * 1000);
```

---

## 7. 注意事项

### 7.1 流式响应注意事项

1. AI 智能问答使用流式响应，需要使用 `Fetch API` 而非 `XMLHttpRequest`
2. 前端需要逐步解析响应流，不能等待完整响应
3. 响应过程中可能出现网络中断，需要做好错误处理

### 7.2 WebSocket 注意事项

1. WebSocket 连接建立后，需要实现心跳机制保持连接
2. 连接断开时应该尝试自动重连（最多 3 次）
3. 人工客服会话固定 3 分钟超时，超时后自动断开
4. 发送消息前需要检查连接状态

### 7.3 会话管理注意事项

1. 用户可以创建多个会话，每个会话独立管理
2. 会话历史记录需要定期清理（建议保留最近 30 天）
3. 切换会话时需要重新加载历史消息

### 7.4 安全注意事项

1. 所有接口都需要携带有效的 JWT Token
2. WebSocket 连接需要通过 Query Parameter 传递 Token
3. 敏感信息不应该在客户端存储，包括消息内容
4. 需要对用户输入进行过滤和校验，防止 XSS 攻击

---

## 8. 快捷问题列表

系统预设的常见问题：

1. 如何查询车次信息？
2. 网上订票流程是什么？
3. 如何退票和改签？
4. 学生票如何购买？
5. 忘记取票密码怎么办？
6. 携带儿童乘车的规定？
7. 如何申请发票？
8. 行李托运规定是什么？
9. 如何修改订单信息？
10. 积分规则说明

---

## 9. 更新日志

| 版本   | 日期       | 说明                       |
| ------ | ---------- | -------------------------- |
| v1.0.0 | 2025-11-05 | 初始版本，完成基础接口设计 |

---

**文档编写日期**：2025-11-05  
**文档版本**：v1.0.0  
**维护团队**：铁路票务系统开发团队
