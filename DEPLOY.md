# 鲜花系统 Docker 部署指南

## 目录

1. [项目介绍](#项目介绍)
2. [部署环境要求](#部署环境要求)
3. [快速开始](#快速开始)
4. [配置说明](#配置说明)
5. [服务说明](#服务说明)
6. [访问地址](#访问地址)
7. [常见问题](#常见问题)
8. [开发调试](#开发调试)

---

## 项目介绍

本项目是一个鲜花购物商城系统，包含：
- **前端**: Vue 3 + Element Plus
- **后端**: Spring Boot 2.7.x + MyBatis Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis 7

---

## 部署环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| Docker | >= 20.10 | 容器运行时 |
| Docker Compose | >= 2.0 | 容器编排工具 |
| Git | >= 2.0 | 代码版本管理 |

---

## 快速开始

### 方式一：使用部署脚本（推荐）

```bash
# Windows
deploy.bat

# Linux/Mac
chmod +x deploy.sh
./deploy.sh
```

### 方式二：手动部署

```bash
# 1. 克隆项目
git clone https://github.com/Guoyu-Coder/flower-system.git
cd flower-system

# 2. 配置环境变量（可选）
# 编辑 .env 文件修改数据库密码等配置

# 3. 启动服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看日志
docker-compose logs -f backend
```

---

## 配置说明

### 环境变量配置

编辑 `.env` 文件配置以下参数：

```env
# MySQL 配置
MYSQL_ROOT_PASSWORD=your-mysql-root-password
MYSQL_DATABASE=flower_shop
MYSQL_USER=flower_user
MYSQL_PASSWORD=your-mysql-password

# JWT 密钥
JWT_SECRET=your-jwt-secret-key

# 服务端口
FRONTEND_PORT=80
BACKEND_PORT=8088
```

### Docker Compose 配置

| 服务 | 容器名 | 端口映射 | 配置文件 |
|------|--------|----------|----------|
| 前端 | flower-frontend | 80:80 | frontend/Dockerfile |
| 后端 | flower-backend | 8088:8088 | backend/Dockerfile |
| MySQL | flower-mysql | 3306:3306 | docker-compose.yml |
| Redis | flower-redis | 6379:6379 | docker-compose.yml |

---

## 服务说明

### 启动顺序

服务会按照以下顺序启动：

1. **MySQL** → 等待健康检查通过
2. **Redis** → 等待健康检查通过  
3. **后端** → 等待MySQL和Redis就绪
4. **前端** → 等待后端就绪

### 数据持久化

- **MySQL**: 数据存储在 `mysql_data` 卷中
- **Redis**: 数据存储在 `redis_data` 卷中

### 初始化脚本

MySQL启动时会自动执行 `backend/sql/init.sql` 初始化数据库。

---

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端页面 | http://localhost | 鲜花商城首页 |
| 后端API | http://localhost:8088/api | RESTful API |
| 管理后台 | http://localhost/admin | 管理员登录页 |

---

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f          # 查看所有日志
docker-compose logs -f backend  # 查看后端日志
docker-compose logs -f frontend # 查看前端日志

# 查看容器状态
docker-compose ps

# 进入容器
docker exec -it flower-backend bash
docker exec -it flower-mysql mysql -u root -p

# 构建镜像（不使用缓存）
docker-compose build --no-cache

# 清理所有资源
docker-compose down --rmi all -v
```

---

## 常见问题

### Q: 服务启动失败？

**A**: 查看日志定位问题：
```bash
docker-compose logs -f backend
```

### Q: MySQL连接失败？

**A**: 确保MySQL服务已启动并健康：
```bash
docker-compose ps mysql
docker-compose logs mysql
```

### Q: 前端无法访问后端API？

**A**: 检查后端服务是否正常启动：
```bash
curl http://localhost:8088/api/product/hot
```

### Q: 端口被占用？

**A**: 修改 `.env` 文件中的端口配置，或停止占用端口的服务。

### Q: 数据库初始化失败？

**A**: 检查 `backend/sql/init.sql` 文件是否存在且格式正确。

---

## 开发调试

### 本地开发模式

```bash
# 启动数据库和Redis
docker-compose up -d mysql redis

# 在本地启动后端
cd backend
mvn spring-boot:run

# 在本地启动前端
cd frontend
npm install
npm run dev
```

### 数据库连接信息

```
主机: localhost
端口: 3306
数据库: flower_shop
用户名: flower_user
密码: (请在 .env 文件中配置)
```

### Redis连接信息

```
主机: localhost
端口: 6379
密码: 无
```

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue | 3.4.x |
| UI组件 | Element Plus | 2.5.x |
| 路由 | Vue Router | 4.2.x |
| 状态管理 | Pinia | 2.1.x |
| 构建工具 | Vite | 5.1.x |
| 后端框架 | Spring Boot | 2.7.x |
| ORM | MyBatis Plus | 3.5.x |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.0 |
| 容器 | Docker | 20.10+ |

---

## 项目结构

```
flower-system/
├── backend/                    # 后端代码
│   ├── src/                   # Java源码
│   ├── sql/                   # 数据库脚本
│   └── Dockerfile             # 后端Docker配置
├── frontend/                  # 前端代码
│   ├── src/                   # Vue源码
│   ├── public/                # 静态资源
│   ├── Dockerfile             # 前端Docker配置
│   └── nginx.conf             # Nginx配置
├── docker-compose.yml         # Docker编排文件
├── .env                       # 环境变量配置
├── deploy.bat                 # Windows部署脚本
└── README.md                  # 项目说明
```

---

## 许可证

MIT License

---

**创建时间**: 2026年5月23日
**版本**: 1.0.0
