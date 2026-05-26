# 🌸 鲜花商城系统 - 完整项目总结

## 📋 文档信息

| 项目 | 内容 |
|------|------|
| 项目名称 | 鲜花商城系统 (Flower Shop System) |
| 项目地址 | https://github.com/Guoyu-Coder/flower-system |
| 创建日期 | 2026年5月23日 |
| 文档版本 | 1.0.0 |
| 文档作者 | AI Assistant |

---

## 🎯 项目概述

### 1.1 项目简介

鲜花商城系统是一个现代化的B2C电商平台，专门用于鲜花的在线销售。系统提供了完整的用户购物体验，包括商品浏览、购物车管理、订单处理、支付结算等核心电商功能。

### 1.2 项目定位

- **用户群体**：普通消费者鲜花购买者
- **应用场景**：在线鲜花零售
- **系统类型**：前后端分离的Web应用
- **部署方式**：Docker容器化部署 + 本地开发环境

### 1.3 核心价值

| 价值点 | 说明 |
|--------|------|
| **用户体验优先** | 响应式设计，流畅的交互体验 |
| **功能完整性** | 覆盖电商全链路，从浏览到支付 |
| **技术先进性** | 采用主流技术栈，性能优异 |
| **可维护性强** | 代码结构清晰，易于扩展 |
| **部署便捷** | Docker一键部署，快速上线 |

---

## 🛠️ 技术栈详解

### 2.1 前端技术栈

#### 核心技术

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **Vue.js** | 3.4.x | 渐进式JavaScript框架 | https://vuejs.org |
| **Vite** | 5.1.x | 新一代前端构建工具 | https://vitejs.cn |
| **Vue Router** | 4.2.x | Vue官方路由管理器 | https://router.vuejs.org |
| **Pinia** | 2.1.x | Vue状态管理库 | https://pinia.vuejs.org |
| **Axios** | 1.6.x | HTTP请求库 | https://axios-http.cn |

#### UI组件库

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **Element Plus** | 2.5.x | Vue3 UI组件库 | https://element-plus.org |
| **@element-plus/icons-vue** | - | Element Plus图标库 | - |

#### 开发工具

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **npm** | 10.x | Node.js包管理器 | https://npmjs.com |
| **Node.js** | 20.x | JavaScript运行时 | https://nodejs.org |

### 2.2 后端技术栈

#### 核心框架

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **Spring Boot** | 2.7.18 | Spring快速开发框架 | https://spring.io/projects/spring-boot |
| **Spring MVC** | - | Web层MVC框架 | - |
| **Spring Security** | - | 安全认证框架 | https://spring.io/projects/spring-security |

#### 数据层

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **MyBatis Plus** | 3.5.x | MyBatis增强工具 | https://baomidou.com |
| **MySQL** | 8.0 | 关系型数据库 | https://www.mysql.com |
| **Redis** | 5.0.14.1 | NoSQL缓存数据库 | https://redis.io |

#### 构建工具

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **Maven** | 3.9.x | Java项目构建工具 | https://maven.apache.org |
| **Java** | 17 | 编程语言 | https://adoptium.net |

#### 工具库

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **JWT** | 0.11.x | JSON Web Token认证 | https://jwt.io |
| **Lombok** | - | Java注解工具 | https://projectlombok.org |
| **Apache Commons** | - | 通用工具库 | https://commons.apache.org |
| **FastJSON** | 2.0.x | JSON处理库 | https://github.com/alibaba/fastjson |

### 2.3 AI集成

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **DeepSeek API** | - | AI大模型接口 | https://platform.deepseek.com |

### 2.4 DevOps工具

| 技术 | 版本 | 说明 | 官网 |
|------|------|------|------|
| **Docker** | 29.4.3 | 容器化平台 | https://www.docker.com |
| **Docker Compose** | - | 容器编排工具 | https://docs.docker.com/compose |
| **Nginx** | Alpine | 反向代理服务器 | https://nginx.org |

---

## 🏗️ 系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户浏览器                           │
│                    (Chrome, Firefox等)                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────────────┐
│                      Nginx 反向代理                          │
│                      (端口: 80)                              │
│                   反向代理 + 静态资源服务                      │
└─────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ↓                   ↓
        ┌───────────────────┐   ┌───────────────────┐
        │    Vue 3 前端      │   │   Spring Boot    │
        │   (端口: 5173)     │   │   后端服务        │
        │                    │   │   (端口: 8088)   │
        │  ┌─────────────┐  │   │                  │
        │  │  Pinia状态  │  │   │  ┌────────────┐ │
        │  │  管理       │  │   │  │ Controller │ │
        │  └─────────────┘  │   │  └────────────┘ │
        │  ┌─────────────┐  │   │         ↓       │
        │  │  路由管理   │  │   │  ┌────────────┐ │
        │  │  Vue Router │  │   │  │  Service   │ │
        │  └─────────────┘  │   │  └────────────┘ │
        │                    │   │         ↓       │
        │  ┌─────────────┐  │   │  ┌────────────┐ │
        │  │  UI组件    │  │   │  │  Mapper    │ │
        │  │Element Plus│  │   │  └────────────┘ │
        │  └─────────────┘  │   │         ↓       │
        └───────────────────┘   │  ┌────────────┐ │
                                 │  │  MySQL    │ │
                                 │  │ Redis缓存 │ │
                                 │  └────────────┘ │
                                 └───────────────────┘
```

### 3.2 技术架构分层

```
┌────────────────────────────────────────┐
│          Presentation Layer            │
│              (表现层)                   │
│  ┌──────────────────────────────────┐  │
│  │  Vue 3 + Element Plus + Vite     │  │
│  │  HTML/CSS/JavaScript             │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────┐
│           Controller Layer             │
│              (控制层)                   │
│  ┌──────────────────────────────────┐  │
│  │  @RestController                 │  │
│  │  @RequestMapping                 │  │
│  │  参数校验 @Valid                  │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────┐
│            Service Layer              │
│              (业务层)                   │
│  ┌──────────────────────────────────┐  │
│  │  @Service                        │  │
│  │  业务逻辑处理                     │  │
│  │  事务管理 @Transactional         │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────┐
│           Data Access Layer            │
│              (数据层)                   │
│  ┌──────────────────────────────────┐  │
│  │  MyBatis Plus Mapper            │  │
│  │  MySQL 8.0 数据库               │  │
│  │  Redis 缓存                     │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
```

### 3.3 Docker容器架构

```
┌─────────────────────────────────────────────────────────────┐
│                  flower-system 网络                        │
│                                                             │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐ │
│  │   MySQL       │  │    Redis      │  │   Backend     │ │
│  │   容器         │  │    容器        │  │   容器         │ │
│  │               │  │               │  │               │ │
│  │  端口: 3306   │  │  端口: 6379   │  │  端口: 8088   │ │
│  │               │  │               │  │               │ │
│  │  存储: MySQL  │  │  存储: Redis  │  │  JAR包运行    │ │
│  │  数据卷       │  │  数据卷       │  │               │ │
│  └───────────────┘  └───────────────┘  └───────────────┘ │
│                                                    ↑       │
│                                          ┌─────────┴─────┐ │
│                                          │   Nginx +     │ │
│                                          │   Vue 3       │ │
│                                          │   前端容器     │ │
│                                          │               │ │
│                                          │  端口: 80     │ │
│                                          └───────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              ↑
                        用户访问
                         端口80
```

---

## 📊 数据库设计

### 4.1 数据库概述

| 项目 | 内容 |
|------|------|
| 数据库名称 | flower_shop |
| 数据库类型 | MySQL 8.0 |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_general_ci |

### 4.2 数据表结构

#### 用户相关表

**1. 用户表 (user)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 用户ID | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | NOT NULL, UNIQUE |
| password | VARCHAR(100) | 密码（加密存储） | NOT NULL |
| phone | VARCHAR(20) | 手机号 | NOT NULL |
| email | VARCHAR(100) | 邮箱 | - |
| nickname | VARCHAR(50) | 昵称 | - |
| avatar | VARCHAR(255) | 头像URL | - |
| gender | TINYINT | 性别：0-未知，1-男，2-女 | DEFAULT 0 |
| status | TINYINT | 状态：0-禁用，1-正常 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**2. 收货地址表 (address)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 地址ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| name | VARCHAR(50) | 收货人姓名 | NOT NULL |
| phone | VARCHAR(20) | 联系电话 | NOT NULL |
| province | VARCHAR(50) | 省份 | - |
| city | VARCHAR(50) | 城市 | - |
| district | VARCHAR(50) | 区县 | - |
| detail | VARCHAR(255) | 详细地址 | - |
| is_default | TINYINT | 是否默认：0-否，1-是 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | - |
| update_time | DATETIME | 更新时间 | - |

#### 商品相关表

**3. 商品分类表 (category)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 分类ID | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(50) | 分类名称 | NOT NULL |
| icon | VARCHAR(255) | 分类图标 | - |
| sort | INT | 排序号 | DEFAULT 0 |
| parent_id | BIGINT | 父分类ID | - |
| create_time | DATETIME | 创建时间 | - |

**4. 商品表 (product)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 商品ID | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | 商品名称 | NOT NULL |
| price | DECIMAL(10,2) | 商品价格 | NOT NULL |
| stock | INT | 库存数量 | DEFAULT 0 |
| image | VARCHAR(255) | 主图URL | - |
| images | TEXT | 图片列表（JSON格式） | - |
| description | TEXT | 商品描述 | - |
| category_id | BIGINT | 分类ID | FOREIGN KEY |
| sales | INT | 销量 | DEFAULT 0 |
| is_hot | TINYINT | 是否热门：0-否，1-是 | DEFAULT 0 |
| status | TINYINT | 状态：0-下架，1-上架 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | - |
| update_time | DATETIME | 更新时间 | - |

#### 订单相关表

**5. 订单表 (order)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 订单ID | PRIMARY KEY, AUTO_INCREMENT |
| order_no | VARCHAR(50) | 订单号 | NOT NULL, UNIQUE |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| total_price | DECIMAL(10,2) | 订单总价 | NOT NULL |
| freight_price | DECIMAL(10,2) | 运费 | DEFAULT 0.00 |
| pay_price | DECIMAL(10,2) | 实际支付金额 | NOT NULL |
| pay_type | TINYINT | 支付方式：1-微信，2-支付宝，3-余额 | - |
| order_status | TINYINT | 订单状态 | - |
| pay_time | DATETIME | 支付时间 | - |
| address_id | BIGINT | 收货地址ID | FOREIGN KEY |
| remark | VARCHAR(500) | 订单备注 | - |
| create_time | DATETIME | 创建时间 | - |
| update_time | DATETIME | 更新时间 | - |

**订单状态说明**

| 状态码 | 状态名 | 说明 |
|--------|--------|------|
| 0 | PENDING_PAYMENT | 待支付 |
| 1 | PAID | 已支付 |
| 2 | SHIPPED | 已发货 |
| 3 | CONFIRMED | 已收货 |
| 4 | CANCELLED | 已取消 |
| 5 | REFUNDING | 退款中 |
| 6 | REFUNDED | 已退款 |

**6. 订单商品表 (order_item)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 订单项ID | PRIMARY KEY, AUTO_INCREMENT |
| order_id | BIGINT | 订单ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| product_name | VARCHAR(100) | 商品名称（冗余） | - |
| product_image | VARCHAR(255) | 商品图片（冗余） | - |
| price | DECIMAL(10,2) | 单价（冗余） | - |
| quantity | INT | 购买数量 | NOT NULL |
| total_price | DECIMAL(10,2) | 小计金额 | - |

#### 购物车相关表

**7. 购物车表 (cart)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 购物车ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| quantity | INT | 商品数量 | NOT NULL |
| selected | TINYINT | 是否选中：0-否，1-是 | DEFAULT 1 |
| create_time | DATETIME | 添加时间 | - |
| update_time | DATETIME | 更新时间 | - |

#### AI相关表

**8. 祝福语表 (blessing)**

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 祝福语ID | PRIMARY KEY, AUTO_INCREMENT |
| occasion | VARCHAR(50) | 场合 | NOT NULL |
| content | TEXT | 祝福语内容 | NOT NULL |
| usage_count | INT | 使用次数 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | - |

### 4.3 数据库关系图

```
┌─────────────┐         ┌─────────────┐
│    User     │         │   Address   │
│   (用户)    │1──────N│  (地址)     │
└─────────────┘         └─────────────┘
       │
       │ 1:N
       ↓
┌─────────────┐         ┌─────────────┐
│    Cart     │         │   Order     │
│  (购物车)   │         │  (订单)     │
└─────────────┘         └─────────────┘
       │                       │
       │ N:N                   │ 1:N
       ↓                       ↓
┌─────────────┐         ┌─────────────┐
│   Product   │         │ OrderItem   │
│  (商品)     │         │(订单项)     │
└─────────────┘         └─────────────┘
       │
       │ N:1
       ↓
┌─────────────┐
│  Category   │
│  (分类)     │
└─────────────┘
```

---

## 🎨 功能模块详解

### 5.1 用户模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 用户注册 | 支持手机号注册 | ✅ 已完成 |
| 用户登录 | 支持手机号+密码登录 | ✅ 已完成 |
| JWT认证 | 无状态身份认证 | ✅ 已完成 |
| 个人信息管理 | 查看和修改个人信息 | ✅ 已完成 |
| 修改密码 | 修改登录密码 | ✅ 已完成 |
| 头像上传 | 上传用户头像 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @PostMapping("/register")      // 用户注册
    @PostMapping("/login")         // 用户登录
    @GetMapping("/info")           // 获取用户信息
    @PutMapping("/update")         // 更新用户信息
    @PutMapping("/password")       // 修改密码
    @PostMapping("/avatar")        // 上传头像
}
```

### 5.2 商品模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 商品分类 | 商品分类浏览和管理 | ✅ 已完成 |
| 商品列表 | 分页展示商品列表 | ✅ 已完成 |
| 商品详情 | 商品详细信息展示 | ✅ 已完成 |
| 热门商品 | 热门商品推荐 | ✅ 已完成 |
| 商品搜索 | 按名称搜索商品 | ✅ 已完成 |
| 商品分类浏览 | 按分类筛选商品 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @GetMapping("/list")           // 商品列表（分页）
    @GetMapping("/detail/{id}")   // 商品详情
    @GetMapping("/hot")           // 热门商品
    @GetMapping("/search")        // 商品搜索
}

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @GetMapping("/list")          // 分类列表
    @GetMapping("/{id}/products") // 分类下的商品
}
```

### 5.3 购物车模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 添加购物车 | 添加商品到购物车 | ✅ 已完成 |
| 查看购物车 | 查看购物车商品列表 | ✅ 已完成 |
| 修改数量 | 修改商品购买数量 | ✅ 已完成 |
| 删除商品 | 从购物车删除商品 | ✅ 已完成 |
| 全选/取消全选 | 批量选择购物车商品 | ✅ 已完成 |
| 选中结算 | 结算选中的商品 | ✅ 已完成 |
| 清空购物车 | 清空整个购物车 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @GetMapping("/list")           // 购物车列表
    @PostMapping("/add")           // 添加购物车
    @PutMapping("/update/{id}")   // 修改数量
    @DeleteMapping("/delete/{id}") // 删除商品
    @PutMapping("/select/{id}")    // 选中/取消选中
    @PutMapping("/selectAll")     // 全选/取消全选
    @DeleteMapping("/clear")      // 清空购物车
}
```

### 5.4 订单模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 确认订单 | 确认订单信息 | ✅ 已完成 |
| 创建订单 | 提交订单 | ✅ 已完成 |
| 订单支付 | 支持多种支付方式 | ✅ 已完成 |
| 订单列表 | 查看用户订单列表 | ✅ 已完成 |
| 订单详情 | 查看订单详细信息 | ✅ 已完成 |
| 取消订单 | 取消未支付订单 | ✅ 已完成 |
| 确认收货 | 确认已收到商品 | ✅ 已完成 |
| 申请退款 | 申请退款 | ✅ 已完成 |

#### 支付方式

| 支付方式 | 说明 | 状态 |
|----------|------|------|
| 微信支付 | 微信扫码支付 | ✅ 已完成 |
| 支付宝 | 支付宝扫码支付 | ✅ 已完成 |
| 余额支付 | 用户账户余额支付 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    @PostMapping("/confirm")          // 确认订单
    @PostMapping("/create")           // 创建订单
    @PostMapping("/pay/{id}")         // 支付订单
    @GetMapping("/list")              // 订单列表
    @GetMapping("/detail/{id}")       // 订单详情
    @PutMapping("/cancel/{id}")       // 取消订单
    @PutMapping("/confirm/{id}")      // 确认收货
    @PutMapping("/refund/{id}")      // 申请退款
}
```

### 5.5 地址模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 地址列表 | 查看用户收货地址列表 | ✅ 已完成 |
| 添加地址 | 新增收货地址 | ✅ 已完成 |
| 修改地址 | 编辑收货地址 | ✅ 已完成 |
| 删除地址 | 删除收货地址 | ✅ 已完成 |
| 设置默认 | 设置默认收货地址 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/address")
public class AddressController {
    
    @GetMapping("/list")              // 地址列表
    @PostMapping("/add")              // 添加地址
    @PutMapping("/update/{id}")       // 修改地址
    @DeleteMapping("/delete/{id}")    // 删除地址
    @PutMapping("/default/{id}")      // 设置默认地址
}
```

### 5.6 AI智能助手模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 智能客服 | AI客服对话 | ✅ 已完成 |
| 商品推荐 | AI智能商品推荐 | ✅ 已完成 |
| 祝福语生成 | AI生成祝福语 | ✅ 已完成 |
| 聊天记录 | 保存对话历史 | ✅ 已完成 |
| 快捷问题 | 预设常见问题 | ✅ 已完成 |

#### 技术实现

```java
@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    @PostMapping("/chat")               // 智能对话
    @PostMapping("/recommend")          // 商品推荐
    @PostMapping("/blessing")           // 祝福语生成
}
```

#### DeepSeek API集成

```java
@Service
public class DeepSeekService {
    
    // 调用DeepSeek API生成智能回复
    public String chat(String message);
    
    // 生成商品推荐
    public String recommend(String occasion);
    
    // 生成祝福语
    public String generateBlessing(String occasion);
}
```

### 5.7 首页模块

#### 功能列表

| 功能 | 说明 | 状态 |
|------|------|------|
| 热门商品 | 展示热门商品 | ✅ 已完成 |
| 商品分类 | 展示商品分类 | ✅ 已完成 |
| 促销活动 | 展示促销活动 | ✅ 已完成 |
| 搜索功能 | 关键词搜索商品 | ✅ 已完成 |

---

## 🔐 安全机制

### 6.1 认证授权

#### JWT认证流程

```
用户登录
    ↓
提交用户名+密码
    ↓
后端验证凭证
    ↓
生成JWT Token
    ↓
返回Token给前端
    ↓
前端存储Token
    ↓
后续请求携带Token
    ↓
后端验证Token
    ↓
处理请求
```

#### Token配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| 过期时间 | 30分钟 | Token有效时长 |
| 加密算法 | HS256 | JWT签名算法 |
| 存储位置 | LocalStorage | 前端存储位置 |
| 请求头 | Authorization | 请求头名称 |

### 6.2 接口安全

| 安全措施 | 说明 | 实现 |
|----------|------|------|
| 参数校验 | 使用@Valid注解校验参数 | ✅ 已实现 |
| SQL注入防护 | 使用MyBatis参数化查询 | ✅ 已实现 |
| XSS防护 | 前端输入转义 | ✅ 已实现 |
| CORS配置 | 跨域资源共享配置 | ✅ 已实现 |
| 密码加密 | BCrypt加密存储 | ✅ 已实现 |

---

## 🚀 部署架构

### 7.1 Docker部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户访问                              │
│                     (http://localhost)                       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   flower-frontend 容器                       │
│                        (Nginx)                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  Vue 3 静态资源                      │    │
│  │                 /usr/share/nginx/html               │    │
│  └─────────────────────────────────────────────────────┘    │
│                              ↓                              │
│                   反向代理到后端服务                          │
│                   http://backend:8088                       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   flower-backend 容器                        │
│                  (Spring Boot JAR)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  Controller  │  │   Service    │  │    Mapper    │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
└─────────────────────────────────────────────────────────────┘
                    ↓                    ↓
        ┌──────────────────┐  ┌──────────────────┐
        │  flower-mysql    │  │   flower-redis   │
        │    (MySQL)       │  │    (Redis)       │
        │   端口: 3306     │  │   端口: 6379     │
        │  数据卷持久化     │  │   数据卷持久化   │
        └──────────────────┘  └──────────────────┘
```

### 7.2 环境配置

#### 开发环境

| 服务 | 地址 | 端口 |
|------|------|------|
| 前端开发服务器 | http://localhost:5173 | 5173 |
| 后端API服务 | http://localhost:8088 | 8088 |
| MySQL数据库 | localhost | 3306 |
| Redis缓存 | localhost | 6379 |

#### 生产环境（Docker）

| 服务 | 容器名 | 外部端口 | 内部端口 |
|------|--------|----------|----------|
| Nginx前端 | flower-frontend | 80 | 80 |
| Spring Boot后端 | flower-backend | 8088 | 8088 |
| MySQL数据库 | flower-mysql | 3306 | 3306 |
| Redis缓存 | flower-redis | 6379 | 6379 |

### 7.3 数据持久化

| 数据类型 | 存储位置 | 备份方式 |
|----------|----------|----------|
| MySQL数据 | Docker Volume: flower-system_mysql_data | 定期备份SQL文件 |
| Redis缓存 | Docker Volume: flower-system_redis_data | 无需备份（可重建） |
| 用户上传文件 | 容器内部存储 | 需要配置外部存储 |

---

## 📁 项目文件结构

### 8.1 前端项目结构

```
flower-shop-frontend/
├── public/                      # 公共静态资源
│   └── vite.svg                # Vite图标
├── src/                        # 源代码目录
│   ├── assets/                 # 资源文件
│   │   └── css/               # 样式文件
│   │       ├── base.css       # 基础样式
│   │       └── main.css       # 主样式
│   ├── components/            # 公共组件
│   │   ├── Header.vue         # 头部组件
│   │   ├── Footer.vue         # 底部组件
│   │   ├── ProductCard.vue    # 商品卡片组件
│   │   ├── CartItem.vue       # 购物车项组件
│   │   └── AddressDialog.vue  # 地址对话框
│   ├── views/                 # 页面视图
│   │   ├── Home.vue           # 首页
│   │   ├── ProductList.vue    # 商品列表
│   │   ├── ProductDetail.vue  # 商品详情
│   │   ├── Cart.vue           # 购物车
│   │   ├── Order.vue          # 订单确认
│   │   ├── Pay.vue            # 支付页面
│   │   ├── Login.vue          # 登录页面
│   │   ├── Register.vue       # 注册页面
│   │   ├── User.vue           # 用户中心
│   │   ├── Address.vue        # 地址管理
│   │   ├── OrderList.vue      # 订单列表
│   │   ├── OrderDetail.vue    # 订单详情
│   │   └── Layout.vue         # 布局组件
│   ├── router/                # 路由配置
│   │   └── index.js           # 路由定义
│   ├── stores/                # Pinia状态管理
│   │   ├── user.js            # 用户状态
│   │   ├── cart.js            # 购物车状态
│   │   └── product.js        # 商品状态
│   ├── utils/                 # 工具函数
│   │   └── request.js         # Axios封装
│   ├── App.vue                # 根组件
│   └── main.js                # 入口文件
├── index.html                 # HTML模板
├── vite.config.js             # Vite配置
├── package.json               # 项目依赖
└── README.md                  # 项目说明
```

### 8.2 后端项目结构

```
flower-shop-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/flowershop/
│   │   │       ├── FlowerShopApplication.java  # 启动类
│   │   │       ├── config/                      # 配置类
│   │   │       │   ├── CorsConfig.java         # 跨域配置
│   │   │       │   ├── RedisConfig.java        # Redis配置
│   │   │       │   └── WebConfig.java         # Web配置
│   │   │       ├── controller/                  # 控制器层
│   │   │       │   ├── UserController.java     # 用户控制器
│   │   │       │   ├── ProductController.java  # 商品控制器
│   │   │       │   ├── CartController.java     # 购物车控制器
│   │   │       │   ├── OrderController.java    # 订单控制器
│   │   │       │   ├── AddressController.java # 地址控制器
│   │   │       │   ├── CategoryController.java# 分类控制器
│   │   │       │   └── AIController.java      # AI控制器
│   │   │       ├── service/                    # 服务层
│   │   │       │   ├── impl/                  # 服务实现
│   │   │       │   │   ├── UserServiceImpl.java
│   │   │       │   │   ├── ProductServiceImpl.java
│   │   │       │   │   ├── CartServiceImpl.java
│   │   │       │   │   ├── OrderServiceImpl.java
│   │   │       │   │   ├── AddressServiceImpl.java
│   │   │       │   │   ├── CategoryServiceImpl.java
│   │   │       │   │   ├── DeepSeekServiceImpl.java
│   │   │       │   │   └── RedisChatServiceImpl.java
│   │   │       │   └── (接口文件)
│   │   │       ├── mapper/                    # 数据层
│   │   │       │   ├── UserMapper.java
│   │   │       │   ├── ProductMapper.java
│   │   │       │   ├── CartMapper.java
│   │   │       │   ├── OrderMapper.java
│   │   │       │   ├── OrderItemMapper.java
│   │   │       │   ├── AddressMapper.java
│   │   │       │   └── CategoryMapper.java
│   │   │       ├── entity/                     # 实体类
│   │   │       │   ├── User.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── Cart.java
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   ├── Address.java
│   │   │       │   └── Category.java
│   │   │       ├── dto/                        # 数据传输对象
│   │   │       │   ├── LoginDTO.java
│   │   │       │   ├── RegisterDTO.java
│   │   │       │   ├── OrderDTO.java
│   │   │       │   └── (其他DTO)
│   │   │       ├── vo/                         # 视图对象
│   │   │       │   ├── ResultVO.java
│   │   │       │   ├── UserVO.java
│   │   │       │   └── (其他VO)
│   │   │       ├── common/                     # 公共类
│   │   │       │   ├── Constants.java
│   │   │       │   ├── JwtUtils.java
│   │   │       │   └── (工具类)
│   │   │       └── exception/                  # 异常处理
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── (自定义异常)
│   │   └── resources/
│   │       ├── application.yml                # 应用配置
│   │       └── mapper/                       # MyBatis映射文件
│   │           ├── UserMapper.xml
│   │           ├── ProductMapper.xml
│   │           ├── CartMapper.xml
│   │           ├── OrderMapper.xml
│   │           └── AddressMapper.xml
│   └── test/                                  # 测试目录
├── sql/                                      # SQL脚本
│   └── init.sql                              # 初始化脚本
├── pom.xml                                   # Maven配置
└── Dockerfile                               # Docker配置
```

### 8.3 Docker配置文件结构

```
flower-system/
├── docker-compose.yml          # Docker Compose编排文件
├── .env                        # 环境变量配置
├── deploy.bat                  # Windows部署脚本
├── deploy.sh                   # Linux/Mac部署脚本
├── DEPLOY.md                   # 部署详细指南
├── UPDATE_WORKFLOW.md          # 代码更新流程
├── PROJECT_COMPLETE_SUMMARY.md # 项目完整总结
├── backend/                    # 后端代码
│   ├── src/                   # Java源代码
│   ├── sql/                   # 数据库脚本
│   ├── Dockerfile            # 后端Dockerfile
│   └── pom.xml               # Maven配置
├── frontend/                  # 前端代码
│   ├── src/                  # Vue源代码
│   ├── public/              # 静态资源
│   ├── Dockerfile            # 前端Dockerfile
│   ├── nginx.conf           # Nginx配置
│   ├── vite.config.js       # Vite配置
│   └── package.json         # npm配置
└── README.md                # 项目说明文档
```

---

## ✅ 已完成功能清单

### 9.1 用户模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 用户注册 | 100% | 支持手机号注册 |
| 用户登录 | 100% | 支持JWT认证 |
| 用户登出 | 100% | 清除登录状态 |
| 个人信息查看 | 100% | 查看用户信息 |
| 个人信息修改 | 100% | 修改昵称、头像等 |
| 修改密码 | 100% | 修改登录密码 |

### 9.2 商品模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 分类列表 | 100% | 展示所有分类 |
| 商品列表 | 100% | 分页展示 |
| 商品详情 | 100% | 完整商品信息 |
| 热门推荐 | 100% | 热门商品展示 |
| 商品搜索 | 100% | 关键词搜索 |
| 分类筛选 | 100% | 按分类过滤 |

### 9.3 购物车模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 添加购物车 | 100% | 添加商品 |
| 查看购物车 | 100% | 列表展示 |
| 修改数量 | 100% | 加减数量 |
| 删除商品 | 100% | 单个删除 |
| 选中状态 | 100% | 勾选商品 |
| 全选功能 | 100% | 批量选择 |
| 清空购物车 | 100% | 全部删除 |

### 9.4 订单模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 确认订单 | 100% | 订单预览 |
| 创建订单 | 100% | 提交订单 |
| 微信支付 | 100% | 模拟支付 |
| 支付宝 | 100% | 模拟支付 |
| 余额支付 | 100% | 真实余额 |
| 订单列表 | 100% | 历史订单 |
| 订单详情 | 100% | 详细信息 |
| 取消订单 | 100% | 取消未支付 |
| 确认收货 | 100% | 确认收件 |
| 申请退款 | 100% | 退款申请 |

### 9.5 地址模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 地址列表 | 100% | 所有地址 |
| 新增地址 | 100% | 添加地址 |
| 编辑地址 | 100% | 修改地址 |
| 删除地址 | 100% | 删除地址 |
| 设置默认 | 100% | 默认地址 |

### 9.6 AI智能助手 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 智能对话 | 100% | DeepSeek API |
| 商品推荐 | 100% | 场合推荐 |
| 祝福语生成 | 100% | AI生成 |
| 聊天记录 | 100% | Redis缓存 |
| 快捷问题 | 100% | 预设问答 |

### 9.7 首页模块 ✅

| 功能 | 完成度 | 说明 |
|------|--------|------|
| 热门商品 | 100% | 热门推荐 |
| 分类导航 | 100% | 分类展示 |
| 促销活动 | 100% | 活动展示 |
| 搜索功能 | 100% | 关键词搜索 |

---

## 📊 项目统计

### 10.1 代码量统计

| 模块 | 语言 | 文件数 | 代码行数 |
|------|------|--------|----------|
| 前端 | JavaScript/Vue | ~50 | ~5000+ |
| 后端 | Java | ~80 | ~8000+ |
| SQL | SQL | 1 | ~500+ |
| 配置 | YAML/XML | ~10 | ~500+ |
| **总计** | - | **~140** | **~14000+** |

### 10.2 数据库统计

| 项目 | 数量 |
|------|------|
| 数据表 | 8 |
| 字段总数 | ~80 |
| 索引数 | ~20 |

### 10.3 API接口统计

| 模块 | 接口数 |
|------|--------|
| 用户模块 | 6 |
| 商品模块 | 5 |
| 分类模块 | 2 |
| 购物车模块 | 7 |
| 订单模块 | 8 |
| 地址模块 | 5 |
| AI模块 | 3 |
| **总计** | **36+** |

---

## 🛠️ 开发工具和环境

### 11.1 开发工具

#### 前端开发

| 工具 | 用途 |
|------|------|
| VS Code | 代码编辑器 |
| Vue DevTools | Vue调试工具 |
| Chrome DevTools | 浏览器调试 |
| Postman/Apifox | API测试 |

#### 后端开发

| 工具 | 用途 |
|------|------|
| IntelliJ IDEA | Java IDE |
| Navicat | 数据库管理 |
| Postman/Apifox | API测试 |
| Maven | 项目构建 |

### 11.2 开发环境

| 软件 | 版本 | 用途 |
|------|------|------|
| Node.js | 20.x | 前端运行时 |
| npm | 10.x | 包管理 |
| Java | 17 | 后端运行时 |
| Maven | 3.9.x | 项目构建 |
| MySQL | 8.0 | 数据库 |
| Redis | 5.0.14.1 | 缓存 |
| Docker | 29.4.3 | 容器化 |

---

## 📖 使用指南

### 12.1 本地开发

#### 环境准备

```bash
# 1. 安装Node.js (前端)
node -v  # >= 20.x

# 2. 安装Java (后端)
java -version  # 17

# 3. 安装Maven (后端)
mvn -v  # >= 3.9.x

# 4. 安装MySQL
mysql --version  # 8.0

# 5. 安装Redis
redis-cli ping  # PONG
```

#### 启动项目

```bash
# 终端1：启动后端
cd flower-shop-backend
mvn spring-boot:run

# 终端2：启动前端
cd flower-shop-frontend
npm install
npm run dev
```

#### 访问应用

- 前端地址：http://localhost:5173
- 后端API：http://localhost:8088/api

### 12.2 Docker部署

#### 环境准备

```bash
# 安装Docker Desktop
docker --version
docker-compose --version
```

#### 部署应用

```bash
# 克隆项目
git clone https://github.com/Guoyu-Coder/flower-system.git
cd flower-system

# 启动服务
./deploy.bat  # Windows
# 或
chmod +x deploy.sh && ./deploy.sh  # Linux/Mac

# 查看状态
docker-compose ps
```

#### 访问应用

- 前端地址：http://localhost
- 后端API：http://localhost:8088/api

---

## 🔄 代码更新流程

### 13.1 日常开发流程

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 创建功能分支
git checkout -b feature/new-feature

# 3. 开发新功能
# ... 修改代码 ...

# 4. 提交代码
git add .
git commit -m "feat: 新功能描述"
git push origin feature/new-feature

# 5. 合并到main分支
git checkout main
git merge feature/new-feature
git push origin main

# 6. 更新服务器
ssh user@服务器IP
cd /opt/flower-system
git pull origin main
docker-compose build
docker-compose up -d
```

### 13.2 Docker镜像更新

```bash
# 修改后端代码
git pull origin main
docker-compose build backend
docker-compose up -d backend

# 修改前端代码
git pull origin main
docker-compose build frontend
docker-compose up -d frontend

# 修改所有代码
git pull origin main
docker-compose build
docker-compose up -d
```

---

## 🎓 学习价值

### 14.1 技术栈覆盖

完成本项目后，你将掌握：

#### 前端技能

- ✅ Vue 3 核心语法和 Composition API
- ✅ Vue Router 路由管理
- ✅ Pinia 状态管理
- ✅ Element Plus 组件库使用
- ✅ Vite 构建工具
- ✅ HTTP 请求和 API 调用
- ✅ 前端工程化实践

#### 后端技能

- ✅ Spring Boot 快速开发
- ✅ Spring MVC 架构设计
- ✅ MyBatis Plus ORM框架
- ✅ JWT 无状态认证
- ✅ RESTful API 设计
- ✅ 业务逻辑分层设计
- ✅ 异常处理和统一响应

#### 数据库技能

- ✅ MySQL 数据库设计
- ✅ Redis 缓存使用
- ✅ 数据库性能优化
- ✅ 数据备份恢复

#### DevOps技能

- ✅ Docker 容器化部署
- ✅ Docker Compose 服务编排
- ✅ Nginx 反向代理配置
- ✅ CI/CD 持续集成部署
- ✅ 生产环境运维监控

### 14.2 项目亮点

| 亮点 | 说明 |
|------|------|
| **前后端分离** | 清晰的分层架构，易于维护和扩展 |
| **JWT认证** | 无状态认证，支持分布式部署 |
| **AI集成** | 集成DeepSeek AI，提供智能服务 |
| **Docker部署** | 一键部署，快速上线 |
| **响应式设计** | 适配多种设备，提升用户体验 |
| **组件化开发** | Vue组件化，提高代码复用性 |

---

## 🚀 未来扩展方向

### 15.1 功能扩展

| 功能 | 优先级 | 说明 |
|------|--------|------|
| 优惠券系统 | 高 | 满减券、折扣券 |
| 积分系统 | 高 | 积分获取和兑换 |
| 评论系统 | 中 | 商品评论和晒图 |
| 物流追踪 | 中 | 订单物流信息 |
| 拼团功能 | 中 | 团购活动 |
| 秒杀功能 | 中 | 限时秒杀活动 |
| 短信通知 | 低 | 订单状态短信通知 |
| 邮件通知 | 低 | 订单状态邮件通知 |

### 15.2 技术扩展

| 技术 | 优先级 | 说明 |
|------|--------|------|
| Redis集群 | 高 | 提高缓存可用性 |
| 数据库读写分离 | 中 | 提高数据库性能 |
| Elasticsearch | 中 | 商品搜索优化 |
| RabbitMQ | 中 | 异步消息队列 |
| Kubernetes | 低 | 容器编排（大规模部署） |
| CI/CD自动化 | 低 | 持续集成持续部署 |

### 15.3 性能优化

| 优化项 | 优先级 | 说明 |
|------|--------|------|
| 前端CDN加速 | 高 | 静态资源CDN |
| 图片压缩 | 高 | 图片优化加载 |
| 前端懒加载 | 高 | 路由组件懒加载 |
| 数据缓存 | 中 | 接口数据缓存 |
| 数据库索引 | 中 | 查询性能优化 |
| 接口合并 | 中 | 减少请求次数 |

---

## 📞 技术支持

### 16.1 在线资源

| 资源 | 链接 |
|------|------|
| 项目GitHub | https://github.com/Guoyu-Coder/flower-system |
| 前端文档 | Vue官方文档 |
| 后端文档 | Spring Boot官方文档 |
| Docker文档 | Docker官方文档 |

### 16.2 常见问题

#### Q: Docker镜像下载失败？

**A:** 配置Docker镜像加速器
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```

#### Q: 数据库连接失败？

**A:** 检查MySQL服务状态和配置
```bash
docker-compose ps mysql
docker-compose logs mysql
```

#### Q: 前端无法访问后端API？

**A:** 检查Nginx反向代理配置
```bash
docker exec flower-frontend cat /etc/nginx/conf.d/default.conf
```

---

## 🎉 项目总结

### 核心成就

1. **完整的电商系统**：覆盖用户、商品、订单、支付等核心功能
2. **现代化的技术栈**：Vue 3 + Spring Boot + MySQL + Redis
3. **AI智能集成**：DeepSeek AI 提供智能客服和推荐
4. **容器化部署**：Docker 一键部署，快速上线
5. **代码质量高**：结构清晰，注释完善，易于维护
6. **文档齐全**：部署指南、更新流程等文档完善

### 项目价值

- 🎓 **学习价值**：掌握完整的电商项目开发流程
- 💼 **实战经验**：积累真实项目经验
- 🚀 **就业竞争力**：提升技术实力和项目经验
- 🌟 **技术广度**：覆盖前端、后端、数据库、DevOps

---

## 📝 附录

### A. Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式
refactor: 重构
perf: 性能优化
test: 测试相关
chore: 构建/工具相关
```

### B. Git分支管理

```
main          - 主分支（生产环境）
├── develop   - 开发分支
├── feature/* - 功能分支
├── fix/*     - 修复分支
└── release/* - 发布分支
```

### C. 环境变量说明

| 变量名 | 说明 | 示例 |
|--------|------|------|
| MYSQL_PASSWORD | MySQL密码 | your-password |
| JWT_SECRET | JWT密钥 | your-secret-key |
| DEEPSEEK_API_KEY | DeepSeek API密钥 | your-api-key |

---

**文档版本**: 1.0.0
**最后更新**: 2026年5月23日
**作者**: AI Assistant
**许可证**: MIT License

---

**🌸 祝你的鲜花商城系统大获成功！**
