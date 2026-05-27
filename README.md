# 花语轩 AI 鲜花商城

基于 SpringBoot + Vue3 的全栈 AI 鲜花电商平台，集成 AI 智能导购、商品推荐、订单管理和数据可视化。

## 功能亮点

- **AI 智能导购**：右下角悬浮 AI 助手，支持商品推荐、订单查询、鲜花养护知识问答
- **推荐商品卡片**：AI 返回结果带商品卡片，可直接加入购物车或立即购买
- **搭配推荐**：购物篮分析 —— 买这束花的人还买了什么
- **完整电商流程**：商品浏览 → 购物车 → 下单 → 订单管理
- **管理后台**：ECharts 数据仪表盘 + 商品/用户/订单管理
- **节日 & 场景专题**：情人节、母亲节、表白、道歉等场景选购
- **JWT 认证 + 路由守卫**：axios 拦截器自动带 token，401 自动跳登录
- **RabbitMQ 订单异步通知**：下单后异步发短信/通知，不阻塞用户

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue3 + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts |
| 后端 | SpringBoot + MyBatis-Plus + MySQL + Redis + RabbitMQ + JWT |
| 部署 | Docker Compose（MySQL + Redis + 后端 + 前端 四容器编排） |

## 项目结构

```
flower-system/
├── frontend/src/
│   ├── views/
│   │   ├── Home.vue              # 首页（轮播+分类+热销）
│   │   ├── product/
│   │   │   ├── ProductList.vue   # 商品列表
│   │   │   └── ProductDetail.vue # 商品详情
│   │   ├── order/
│   │   │   ├── CreateOrder.vue   # 下单
│   │   │   ├── OrderList.vue     # 订单列表
│   │   │   └── OrderDetail.vue   # 订单详情
│   │   ├── Cart.vue              # 购物车
│   │   ├── Festival.vue          # 节日专题
│   │   ├── Gift.vue              # 送礼场景
│   │   ├── user/Profile.vue      # 个人中心
│   │   ├── admin/
│   │   │   ├── AdminLogin.vue    # 管理员登录
│   │   │   └── AdminDashboard.vue # 仪表盘（ECharts）
│   │   ├── Login.vue             # 用户登录
│   │   └── Register.vue          # 用户注册
│   ├── components/
│   │   ├── AiAssistant.vue       # AI 导购组件（核心）
│   │   └── AddressDialog.vue     # 地址选择
│   ├── api/index.js              # API 封装（10 模块 + 拦截器）
│   ├── stores/user.js            # Pinia 用户状态
│   └── router/index.js           # 路由 + 守卫
├── backend/src/main/java/com/flowershop/
│   ├── controller/               # 11 个 Controller
│   ├── service/                  # 业务逻辑层
│   ├── mapper/                   # MyBatis-Plus Mapper
│   ├── entity/                   # 实体类
│   ├── dto/                      # 数据传输对象
│   └── config/                   # Spring 配置
├── docker-compose.yml            # 四容器编排
└── backend/sql/init.sql          # 数据库初始化
```

## 快速启动

### Docker Compose 一键启动（推荐）

```bash
git clone https://github.com/Guoyu-Coder/flower-system.git
cd flower-system

# 配置环境变量
cp .env.example .env
# 编辑 .env，填入 MySQL 密码、JWT Secret 等

# 启动全部服务（MySQL + Redis + 后端 + 前端）
docker-compose up -d
```

访问：http://localhost

### 本地开发

**后端**：
```bash
cd backend
# 确保本地 MySQL 和 Redis 已启动
mvn spring-boot:run
```

**前端**：
```bash
cd frontend
npm install
npm run dev
```

## API 模块

| 模块 | 说明 |
|------|------|
| `/api/user/*` | 注册、登录、个人信息 |
| `/api/product/*` | 商品列表、详情、搜索 |
| `/api/cart/*` | 购物车 CRUD |
| `/api/order/*` | 下单、支付、取消、确认收货 |
| `/api/address/*` | 收货地址管理 |
| `/api/favorite/*` | 商品收藏 |
| `/api/ai/*` | AI 导购对话、推荐 |
| `/api/admin/*` | 管理后台 API |

## License

MIT
