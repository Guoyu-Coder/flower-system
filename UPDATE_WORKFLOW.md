# =====================================================
# 鲜花系统 - 代码更新和部署流程
# 位置: UPDATE_WORKFLOW.md
# 说明: 详细说明如何优化代码和部署更新
# =====================================================

## 📋 目录

1. [本地开发流程](#本地开发流程)
2. [GitHub代码管理](#github代码管理)
3. [Docker镜像更新](#docker镜像更新)
4. [生产环境部署](#生产环境部署)
5. [常用命令速查](#常用命令速查)

---

## 本地开发流程

### 1️⃣ 拉取最新代码

```bash
# 进入项目目录
cd flower-system

# 拉取GitHub最新代码
git pull origin main
```

### 2️⃣ 启动本地服务进行开发

```bash
# 方式一：使用原始本地服务（推荐开发时使用）
# 终端1：启动后端
cd backend
mvn spring-boot:run

# 终端2：启动前端（开发模式，有热更新）
cd frontend
npm run dev

# 方式二：使用Docker服务
docker-compose up -d
# 修改代码后需要重新构建镜像
docker-compose build
docker-compose up -d
```

### 3️⃣ 开发调试

访问地址：
- 前端开发服务器: http://localhost:5173
- 后端API文档: http://localhost:8088/swagger-ui.html
- MySQL数据库: localhost:3306
- Redis缓存: localhost:6379

---

## GitHub代码管理

### 1️⃣ 创建功能分支

```bash
# 创建新分支
git checkout -b feature/new-feature
# 或修复bug分支
git checkout -b fix/bug-description
```

### 2️⃣ 提交代码

```bash
# 添加修改的文件
git add .

# 提交代码
git commit -m "feat: 添加新功能描述"

# 推送到GitHub
git push origin feature/new-feature
```

### 3️⃣ 创建Pull Request

1. 访问 https://github.com/Guoyu-Coder/flower-system
2. 点击 "Compare & pull request"
3. 填写PR描述
4. 请求代码审查
5. 合并到main分支

### 4️⃣ 合并后更新

```bash
# 切换回main分支
git checkout main

# 拉取最新代码
git pull origin main

# 删除已合并的分支
git branch -d feature/new-feature
```

---

## Docker镜像更新

### 场景1：仅修改了后端代码

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 重新构建后端镜像
docker-compose build backend

# 3. 重启后端服务
docker-compose up -d backend

# 4. 查看日志确认
docker-compose logs -f backend
```

### 场景2：仅修改了前端代码

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 重新构建前端镜像
docker-compose build frontend

# 3. 重启前端服务
docker-compose up -d frontend

# 4. 查看日志确认
docker-compose logs -f frontend
```

### 场景3：修改了前端和后端

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 重新构建所有服务
docker-compose build

# 3. 重启所有服务
docker-compose up -d

# 4. 查看所有服务状态
docker-compose ps
```

### 场景4：修改了Docker配置或添加了新服务

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 停止旧服务
docker-compose down

# 3. 重新启动所有服务
docker-compose up -d

# 4. 查看日志
docker-compose logs -f
```

---

## 生产环境部署

### 方式一：SSH登录服务器部署

```bash
# 1. SSH登录服务器
ssh user@your-server.com

# 2. 进入项目目录
cd /opt/flower-system

# 3. 拉取最新代码
git pull origin main

# 4. 重新构建并启动
docker-compose build
docker-compose up -d

# 5. 查看服务状态
docker-compose ps
docker-compose logs -f
```

### 方式二：使用CI/CD自动化部署（推荐）

使用GitHub Actions实现自动部署：

```yaml
# .github/workflows/deploy.yml
name: Deploy to Server

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.HOST }}
          username: ${{ secrets.USERNAME }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /opt/flower-system
            git pull origin main
            docker-compose build
            docker-compose up -d
```

### 方式三：一键更新脚本

创建 `update.sh` 脚本：

```bash
#!/bin/bash
# update.sh - 一键更新脚本

echo "开始更新鲜花系统..."

# 1. 拉取最新代码
echo "1/4 拉取最新代码..."
git pull origin main

# 2. 重新构建镜像
echo "2/4 重新构建镜像..."
docker-compose build

# 3. 停止旧服务
echo "3/4 停止旧服务..."
docker-compose down

# 4. 启动新服务
echo "4/4 启动新服务..."
docker-compose up -d

echo "更新完成！"
echo "访问地址: http://localhost"
docker-compose ps
```

使用方法：
```bash
chmod +x update.sh
./update.sh
```

---

## 常用命令速查

### Git命令

```bash
# 查看当前状态
git status

# 查看修改内容
git diff

# 查看提交历史
git log --oneline

# 创建分支
git checkout -b branch-name

# 切换分支
git checkout branch-name

# 合并分支
git merge branch-name

# 删除分支
git branch -d branch-name
```

### Docker Compose命令

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启所有服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service-name]

# 重新构建镜像
docker-compose build [service-name]

# 查看容器资源使用
docker stats

# 进入容器
docker exec -it container-name /bin/bash
```

### 数据库命令

```bash
# 进入MySQL容器
docker exec -it flower-mysql mysql -u flower_user -p

# 执行SQL
docker exec -it flower-mysql mysql -u flower_user -p flower_shop < backup.sql
```

### 备份和恢复

```bash
# 备份数据库
docker exec flower-mysql mysqldump -u flower_user -p flower_shop > backup.sql

# 恢复数据库
docker exec -i flower-mysql mysql -u flower_user -p flower_shop < backup.sql

# 备份Redis数据
docker exec flower-redis redis-cli SAVE

# 备份所有数据卷
docker run --rm -v flower-system_mysql_data:/data -v $(pwd):/backup ubuntu tar cvf /backup/mysql_data.tar /data
```

---

## 🔧 最佳实践

### 1️⃣ 开发规范

- [ ] 每次开发新功能创建新分支
- [ ] 提交代码前先测试
- [ ] 编写清晰的提交信息
- [ ] 定期拉取main分支代码

### 2️⃣ 代码审查

- [ ] 使用Pull Request审查代码
- [ ] 至少一个审查者通过
- [ ] 通过CI/CD测试
- [ ] 再合并到main分支

### 3️⃣ 环境管理

```bash
# 开发环境: docker-compose.override.yml
# 测试环境: docker-compose.test.yml
# 生产环境: docker-compose.prod.yml

# 使用特定环境
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 4️⃣ 监控和日志

```bash
# 查看实时日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看错误日志
docker-compose logs --tail=100 | grep ERROR

# 查看资源使用
docker stats

# 查看网络状况
docker network ls
docker network inspect flower-system_flower-network
```

---

## 🚨 故障排除

### 服务启动失败

```bash
# 1. 查看所有日志
docker-compose logs

# 2. 检查端口占用
netstat -an | grep 8088

# 3. 重启所有服务
docker-compose restart

# 4. 完全清理并重建
docker-compose down -v
docker-compose up -d
```

### 数据库连接失败

```bash
# 1. 检查MySQL状态
docker-compose ps mysql

# 2. 查看MySQL日志
docker-compose logs mysql

# 3. 检查网络连接
docker network inspect flower-system_flower-network

# 4. 重新启动MySQL
docker-compose restart mysql
```

### 前端无法访问后端

```bash
# 1. 检查后端服务
curl http://localhost:8088/api/product/hot

# 2. 检查Nginx配置
docker exec flower-frontend cat /etc/nginx/conf.d/default.conf

# 3. 重启Nginx
docker-compose restart frontend
```

---

## 📞 技术支持

如遇问题：
1. 查看日志：`docker-compose logs -f`
2. 检查服务状态：`docker-compose ps`
3. 验证网络：`docker network inspect flower-system_flower-network`
4. 查看文档：[DEPLOY.md](file:///c:\Users\郭宇\Desktop\flower-system\DEPLOY.md)

---

**创建时间**: 2026年5月23日
**版本**: 1.0.0
