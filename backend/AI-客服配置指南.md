# 花语轩鲜花商城 AI 客服功能配置指南

## 📋 目录
1. [环境配置](#环境配置)
2. [API Key 配置](#api-key-配置)
3. [Redis 配置](#redis-配置)
4. [功能测试](#功能测试)
5. [API 接口文档](#api-接口文档)
6. [常见问题](#常见问题)

---

## 🌍 环境配置

### 1. 后端配置

**文件位置：** `flower-shop-backend/.env`

请确保以下配置项正确填写：

```env
# DeepSeek API配置（已配置好）
DEEPSEEK_API_KEY=REMOVED_API_KEY
DEEPSEEK_API_URL=https://api.deepseek.com/v1/chat/completions
DEEPSEEK_MODEL=deepseek-chat

# MySQL数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=flower_shop
DB_USERNAME=root
DB_PASSWORD=REMOVED

# Redis配置（用于AI对话上下文记忆）
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT密钥配置
JWT_SECRET=REMOVED_JWT_SECRET

# 服务器端口
SERVER_PORT=8088
```

### 2. 修改 API Key（如果需要）

如果你的 DeepSeek API Key 不是上面那个，请在 `.env` 文件中修改：

```env
DEEPSEEK_API_KEY=你的真实API-Key
```

**⚠️ 重要：** 不要将 API Key 硬编码到代码中，始终使用环境变量！

### 3. 修改 MySQL 数据库密码

如果你的 MySQL 密码不是 `REMOVED`，请修改：

```env
DB_PASSWORD=你的MySQL密码
```

---

## 🔑 API Key 配置

### DeepSeek API Key 获取步骤

1. 访问 [DeepSeek 开放平台](https://platform.deepseek.com/)
2. 注册/登录账号
3. 进入「API Keys」页面
4. 点击「创建 API Key」
5. 复制生成的 Key（格式：`sk-xxxxxxxxxxxxxxxx`）
6. 将 Key 填入 `.env` 文件的 `DEEPSEEK_API_KEY` 字段

### 配置检查清单

- [x] API Key 已配置在 `.env` 文件中
- [x] API Key 未硬编码在代码中
- [x] `.env` 文件已添加到 `.gitignore`
- [x] 已创建 `.env.example` 作为模板

---

## 🗄️ Redis 配置

AI 客服的上下文记忆功能依赖 Redis。如果你的 Redis 有密码，请修改配置：

```env
REDIS_PASSWORD=你的Redis密码
```

### Redis 安装和启动（Windows）

1. 下载 Redis for Windows: https://github.com/microsoftarchive/redis/releases
2. 解压到指定目录
3. 启动 Redis 服务：
   ```bash
   redis-server.exe redis.windows.conf
   ```

### 验证 Redis 是否正常运行

```bash
redis-cli ping
# 如果返回 PONG，说明 Redis 运行正常
```

---

## 🧪 功能测试

### 方式一：使用 Postman 测试

#### 1. 测试 AI 对话接口（已登录用户）

**请求信息：**
- **URL:** `POST http://localhost:8088/api/ai/chat`
- **Headers:**
  - `Authorization`: `Bearer <用户Token>`
  - `Content-Type`: `application/json`
- **Body:**
```json
{
  "message": "情人节送什么花好？"
}
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "reply": "为你推荐适合情人节的鲜花~ 💐\n\n• **红玫瑰11朵** | ￥128\n• **红玫瑰99朵** | ￥368\n\n回复【一键加购】...",
    "hasRecommend": true
  }
}
```

#### 2. 测试 AI 对话接口（游客）

**请求信息：**
- **URL:** `POST http://localhost:8088/api/ai/chat`
- **Headers:**
  - `Content-Type`: `application/json`
- **Body:**
```json
{
  "message": "推荐一些鲜花",
  "sessionId": "guest_1234567890_abc123"
}
```

#### 3. 测试订单查询功能（需要先有订单）

**请求信息：**
- **URL:** `POST http://localhost:8088/api/ai/chat`
- **Headers:**
  - `Authorization`: `Bearer <用户Token>`
  - `Content-Type`: `application/json`
- **Body:**
```json
{
  "message": "查一下我的订单什么时候到？"
}
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "reply": "您的最近订单：\n- 订单号：FL20240101121212ABC123\n  状态：配送中\n  金额：￥168.00\n  时间：2024-01-01T12:12:12\n\n根据订单信息，您的鲜花正在配送中，预计今天送达~ 🌸",
    "hasRecommend": false
  }
}
```

#### 4. 测试对话历史

**请求信息：**
- **URL:** `GET http://localhost:8088/api/ai/history`
- **Headers:**
  - `Authorization`: `Bearer <用户Token>`

#### 5. 测试清空对话历史

**请求信息：**
- **URL:** `DELETE http://localhost:8088/api/ai/history`
- **Headers:**
  - `Authorization`: `Bearer <用户Token>`

---

### 方式二：前端界面测试

#### 启动前端项目

```bash
cd flower-shop-frontend
npm install
npm run dev
```

#### 测试步骤

1. **打开商城首页**
   - 访问 http://localhost:5173 （或前端配置的端口）
   - 在页面右下角应该看到粉色「AI导购」按钮 🤖

2. **点击打开聊天窗口**
   - 点击按钮展开聊天窗口
   - 顶部显示「花语AI助手」和在线状态

3. **测试对话功能**
   - 输入：`情人节送什么花好？`
   - 预期：AI 推荐情人节鲜花并说明花语

4. **测试多轮对话**
   - 继续输入：`预算200元以内呢？`
   - 预期：AI 根据上下文调整推荐

5. **测试订单查询**（需要登录）
   - 先登录商城账号
   - 创建至少一个订单
   - 在 AI 对话中输入：`查一下我的订单`
   - 预期：AI 显示你的订单状态

6. **测试历史记录**
   - 刷新页面
   - 重新打开聊天窗口
   - 预期：之前的对话记录还在

---

## 📡 API 接口文档

### 1. AI 对话接口

**端点：** `POST /api/ai/chat`

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| message | String | ✅ | 用户消息内容 |
| sessionId | String | ⚠️ | 游客必填，用户可不填 |

**请求示例：**
```json
{
  "message": "推荐生日鲜花",
  "sessionId": "guest_123456789"
}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "reply": "为你推荐适合生日的鲜花~ 🌸...",
    "hasRecommend": true
  }
}
```

### 2. 获取对话历史

**端点：** `GET /api/ai/history`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | ⚠️ | 游客必填，用户可不填 |

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "role": "user",
      "content": "情人节送什么花？",
      "timestamp": 1704067200000
    },
    {
      "role": "assistant",
      "content": "为你推荐...🌸",
      "timestamp": 1704067205000
    }
  ]
}
```

### 3. 清空对话历史

**端点：** `DELETE /api/ai/history`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | ⚠️ | 游客必填，用户可不填 |

**响应示例：**
```json
{
  "code": 200,
  "msg": "success",
  "data": "对话历史已清空"
}
```

---

## ❓ 常见问题

### Q1: AI 助手不回复或回复很慢？

**可能原因：**
1. DeepSeek API Key 配置错误或已过期
2. 网络连接问题
3. API 调用频率限制

**解决方案：**
- 检查 `.env` 文件中的 API Key 是否正确
- 检查网络连接
- 查看后端日志中的错误信息

### Q2: 游客无法使用 AI 客服？

**可能原因：**
- 未提供 sessionId 参数

**解决方案：**
- 确保前端传递了 sessionId
- 检查 Redis 服务是否正常运行

### Q3: 订单查询功能不工作？

**可能原因：**
1. 用户未登录
2. 用户没有订单
3. 订单查询逻辑未触发

**解决方案：**
- 确保用户已登录
- 确认用户有订单记录
- 检查消息中是否包含"订单"关键词

### Q4: 对话历史没有保存？

**可能原因：**
1. Redis 服务未启动
2. Redis 配置错误
3. 浏览器 localStorage 被禁用

**解决方案：**
- 启动 Redis 服务
- 检查 Redis 配置
- 清除浏览器缓存后重试

### Q5: 前端没有显示 AI 客服按钮？

**可能原因：**
1. AiAssistant 组件未正确引入
2. 组件未渲染

**解决方案：**
- 检查 `App.vue` 中是否正确引入了 AiAssistant 组件
- 清除浏览器缓存
- 重新启动前端项目

---

## 🔧 故障排查命令

### 检查后端服务状态

```bash
# 进入后端目录
cd flower-shop-backend

# 启动后端（开发环境）
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/flower-shop-backend-1.0.0.jar
```

### 检查 Redis 连接

```bash
# 连接 Redis
redis-cli

# 测试连接
ping
# 应该返回 PONG

# 查看 AI 对话记录
keys chat:*
```

### 查看后端日志

启动后端后，关注以下日志信息：
- `Started FlowerShopApplication` - 应用启动成功
- `DeepSeek API connected successfully` - API 连接成功
- 错误日志中的异常信息

### 检查前端网络请求

1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 发送 AI 对话请求
4. 检查请求和响应详情

---

## 📝 功能说明

### AI 客服支持的功能

1. **🌸 鲜花推荐**
   - 根据节日、场合、预算推荐鲜花
   - 说明花语和寓意
   - 提供价格参考

2. **📦 订单查询**
   - 查询订单状态（待支付/待发货/配送中/已完成）
   - 显示订单时间和金额
   - 提供物流信息

3. **💡 花语知识**
   - 解答各种鲜花的花语
   - 提供鲜花养护建议
   - 介绍送花礼仪

4. **💬 上下文记忆**
   - 支持多轮对话
   - 记住对话上下文
   - 游客和用户分别存储历史

5. **🛒 购物辅助**
   - 推荐相关商品
   - 提供一键加购功能

---

## 📞 技术支持

如果遇到其他问题，请检查：

1. 后端日志文件
2. 浏览器控制台错误
3. 网络请求详情
4. 数据库和 Redis 连接状态

---

**祝您使用愉快！🌸💐**
