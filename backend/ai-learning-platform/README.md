# 智学星途 - AI Agent智能学习平台

<div align="center">
  <h1>🌟 Smart Learning Journey - AI Learning Platform 🌟</h1>
  <p><strong>基于DeepSeek大模型的企业级AI Agent智能学习平台</strong></p>
  
  ![Java](https://img.shields.io/badge/Java-22-blue)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
  ![Vue](https://img.shields.io/badge/Vue-3.4-green)
  ![AI](https://img.shields.io/badge/AI-DeepSeek-orange)
</div>

---

## 📚 项目概述

智学星途是一个具备**独立思考、任务规划、用户感知、长期记忆、自主决策能力**的学习专属AI Agent智能体平台。不是简单调用大模型API，而是搭建真正的智能学习系统。

### ✨ 核心特性

- 🤖 **AI Agent驱动**: 区别于简单API调用，实现真正的智能学习Agent
- 🎯 **智能出题**: AI自动生成高质量练习题和试卷
- ✍️ **智能批改**: 客观题秒批，主观题深度批改
- 📊 **学情分析**: 个性化学习报告和薄弱点诊断
- 🎓 **错题本**: AI智能错因分析和举一反三训练
- 💬 **智能答疑**: 结合知识点的精准答疑服务

---

## 🏗️ 技术架构

### 后端技术栈
- Java 22 + SpringBoot 3.2.5
- MyBatis-Plus 3.5.7
- MySQL 8.0 + Redis 7.x
- JWT Authentication
- DeepSeek API + 自研Agent

### 前端技术栈
- Vue 3 + Composition API
- Vite 5 + Pinia
- Element Plus + ECharts 5
- Axios + Vue Router 4

---

## 📦 功能模块

### 🎓 学习端功能
- ✅ 用户注册/登录 (JWT认证)
- ✅ 智能刷题 (多种练习模式)
- ✅ 在线考试 (限时作答)
- ✅ AI错题本 (自动收录+AI诊断)
- ✅ 学情分析 (可视化报告)
- ✅ AI答疑广场 (智能问答)

### 👨‍🏫 教师端功能
- ✅ 题库管理 (CRUD+导入导出)
- ✅ 试卷管理 (手动/AI自动)
- ✅ 题目审核
- ✅ 学习数据查看

### 🔧 管理端功能
- ✅ 用户管理
- ✅ 学科知识管理
- ✅ 系统配置
- ✅ 公告管理
- ✅ AI配置

---

## 🚀 快速开始

### 环境要求
- JDK 22+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis 7.x

### 启动步骤

1. **数据库初始化**
```bash
mysql -u root -p < sql/init.sql
```

2. **配置数据库连接**
编辑 `application.yml` 中的数据库配置

3. **配置DeepSeek API**
```bash
export DEEPSEEK_API_KEY=your_api_key
```

4. **启动后端**
```bash
mvn spring-boot:run
```

5. **访问服务**
- API服务: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html

---

## 📂 项目结构

```
ai-learning-platform/
├── ai-learning-common/          # 公共模块
│   ├── config/                  # 全局配置
│   ├── exception/               # 异常处理
│   ├── result/                  # 统一响应
│   ├── utils/                   # 工具类
│   └── constant/                # 常量定义
│
├── ai-learning-system/          # 系统模块
│   └── ...
│
├── ai-learning-business/         # 业务模块
│   └── ...
│
├── ai-learning-agent/           # AI Agent核心模块
│   ├── client/                  # DeepSeek客户端
│   ├── prompt/                  # 提示词模板
│   └── service/                 # Agent服务
│
└── sql/
    └── init.sql                 # 数据库初始化脚本
```

---

## 🔐 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | (首次登录后修改) |

---

## 📖 API文档

启动服务后访问: http://localhost:8080/swagger-ui.html

### 核心接口

#### 用户接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

#### AI接口
- `POST /api/ai/generate/questions` - AI智能出题
- `POST /api/ai/grade` - AI智能批改
- `POST /api/ai/chat` - AI答疑对话

---

## 🎯 AI Agent类型

1. **学习任务规划Agent** - 制定个性化学习计划
2. **智能出题Agent** - 自动生成高质量题目
3. **智能批改Agent** - 深度批改学生答案
4. **错题诊断Agent** - 分析错误原因和薄弱点
5. **学情分析Agent** - 生成学习报告和建议
6. **智能答疑Agent** - 精准解答学习问题

---

## 📊 数据库设计

完整的数据库设计包括：
- 用户表 (sys_user)
- 学科表 (edu_subject)
- 章节表 (edu_chapter)
- 知识点表 (edu_knowledge_point)
- 题目表 (qms_question)
- 练习记录表 (pra_practice_record)
- 答题记录表 (pra_answer_record)
- 考试表 (exam_exam)
- 错题表 (err_wrong_question)
- 学习报告表 (rpt_learning_report)
- AI会话表 (ai_chat_session)
- 系统配置表 (sys_config)
- ... 等等

---

## 🛠️ 开发指南

### 添加新功能
1. 在对应的module中创建entity
2. 创建mapper接口
3. 实现service层
4. 创建controller
5. 更新数据库表结构

### 扩展AI Agent
1. 在 `PromptTemplate` 中添加提示词
2. 在 `AiAgentService` 中添加Agent方法
3. 配置新的Agent类型

---

## 📝 License

本项目采用 Apache License 2.0 许可证。

---

## 🤝 联系方式

如有问题或建议，请提交Issue或联系开发团队。

---

<div align="center">
  <p>Made with ❤️ by 智学星途团队</p>
  <p>© 2024 智学星途 - 让学习更智能</p>
</div>
