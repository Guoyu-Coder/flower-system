# 智学星途-AI Agent智能学习平台 - 项目规范文档

## 一、项目概述

**项目名称：** 智学星途（Smart Learning Journey）  
**项目类型：** 企业级前后端分离在线学习平台  
**核心定位：** 基于DeepSeek大模型的自研学习领域AI Agent智能体平台

### 1.1 项目愿景
打造具备独立思考、任务规划、用户感知、长期记忆、自主决策能力的学习专属AI Agent智能体，做成可上线运营的正规在线学习网站。

### 1.2 核心价值
- **AI Agent驱动**：区别于简单API调用，实现真正的智能学习Agent
- **企业级标准**：完整的用户系统、权限管理、业务模块
- **学习闭环**：从出题→练习→批改→错题诊断→学情分析完整闭环

---

## 二、技术架构

### 2.1 整体架构
```
┌─────────────────────────────────────────────────────────────┐
│                      前端 (Vue3 + Vite5)                    │
│  学生端 │ 教师端 │ 管理端 │ AI答疑 │ 学情分析                  │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST API
┌──────────────────────▼──────────────────────────────────────┐
│                    后端 (SpringBoot3)                        │
│  Controller │ Service │ Agent智能调度层 │ Mapper             │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴───────────────┐
        ▼                              ▼
┌──────────────────┐        ┌──────────────────┐
│    MySQL 8.0     │        │     Redis        │
│   业务数据存储    │        │  缓存/会话/令牌   │
└──────────────────┘        └──────────────────┘
        │
        ▼
┌──────────────────────────────────────────┐
│          AI Agent 智能体层                 │
│  DeepSeek大模型 │ 自研Agent策略 │ 提示词工程│
└──────────────────────────────────────────┘
```

### 2.2 后端技术栈
- **核心框架**：SpringBoot 3.2.x + Java 22
- **持久层**：MyBatis-Plus 3.5.x
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.x
- **认证**：JWT + Spring Security
- **文档**：SpringDoc OpenAPI 3 (Swagger)
- **工具**：Hutool、OkHttp3、Lombok、FastJSON2
- **构建**：Maven 3.9+

### 2.3 前端技术栈
- **框架**：Vue3 + Composition API
- **构建**：Vite 5
- **路由**：Vue Router 4
- **状态**：Pinia
- **UI组件**：Element Plus
- **HTTP**：Axios
- **可视化**：ECharts 5
- **实时通信**：WebSocket

### 2.4 AI技术栈
- **大模型**：DeepSeek Chat/Reasoner API
- **Agent框架**：自研学习领域Agent
- **提示词工程**：结构化Prompt模板
- **上下文管理**：Redis会话存储
- **流式输出**：SSE (Server-Sent Events)

---

## 三、项目结构

### 3.1 后端多模块结构
```
ai-learning-platform/
├── ai-learning-common/          # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/ai/learning/common/
│       ├── config/              # 全局配置
│       ├── exception/           # 异常处理
│       ├── result/              # 统一响应
│       ├── utils/               # 工具类
│       └── constant/            # 常量定义
│
├── ai-learning-system/          # 系统模块
│   ├── pom.xml
│   └── src/main/java/com/ai/learning/system/
│       ├── controller/          # 系统控制器
│       ├── service/             # 系统服务
│       ├── mapper/              # 数据访问
│       └── entity/              # 系统实体
│
├── ai-learning-business/         # 业务模块
│   ├── pom.xml
│   └── src/main/java/com/ai/learning/business/
│       ├── controller/          # 业务控制器
│       ├── service/             # 业务服务
│       ├── mapper/              # 数据访问
│       ├── entity/              # 业务实体
│       └── dto/                 # 数据传输对象
│
└── ai-learning-agent/           # AI Agent核心模块
    ├── pom.xml
    └── src/main/java/com/ai/learning/agent/
        ├── core/                # Agent核心
        ├── prompt/              # 提示词模板
        ├── memory/              # 上下文记忆
        ├── strategy/            # Agent策略
        └── client/              # DeepSeek客户端
```

### 3.2 前端项目结构
```
vue-learning-platform/
├── public/                     # 静态资源
├── src/
│   ├── api/                    # API接口
│   ├── assets/                 # 资源文件
│   ├── components/             # 公共组件
│   ├── layouts/                # 布局组件
│   ├── router/                 # 路由配置
│   ├── stores/                  # Pinia状态
│   ├── utils/                   # 工具函数
│   ├── views/                   # 页面视图
│   │   ├── frontend/            # 前台页面
│   │   ├── teacher/              # 教师页面
│   │   └── admin/               # 管理页面
│   ├── App.vue
│   └── main.js
├── package.json
└── vite.config.js
```

---

## 四、数据库设计

### 4.1 核心数据表

#### 4.1.1 用户相关表

**sys_user** - 用户表
```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT '/default-avatar.png' COMMENT '头像',
    role ENUM('tourist', 'student', 'teacher', 'admin') DEFAULT 'student' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    total_score INT DEFAULT 0 COMMENT '总积分',
    total_duration BIGINT DEFAULT 0 COMMENT '累计学习时长(秒)',
    total_questions INT DEFAULT 0 COMMENT '累计做题数',
    total_correct INT DEFAULT 0 COMMENT '累计正确数',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0否 1是',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

**sys_user_token** - 用户Token表
```sql
CREATE TABLE sys_user_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(500) NOT NULL COMMENT 'JWT令牌',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token (token(255)),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户Token表';
```

**sys_login_log** - 登录日志表
```sql
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT '登录地点',
    device VARCHAR(100) COMMENT '设备信息',
    login_status TINYINT COMMENT '登录状态: 0失败 1成功',
    login_msg VARCHAR(255) COMMENT '登录消息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';
```

#### 4.1.2 学科知识表

**edu_subject** - 学科表
```sql
CREATE TABLE edu_subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学科ID',
    name VARCHAR(100) NOT NULL COMMENT '学科名称',
    code VARCHAR(50) NOT NULL COMMENT '学科编码',
    icon VARCHAR(255) COMMENT '学科图标',
    description TEXT COMMENT '学科描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';
```

**edu_chapter** - 章节表
```sql
CREATE TABLE edu_chapter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    name VARCHAR(100) NOT NULL COMMENT '章节名称',
    code VARCHAR(50) COMMENT '章节编码',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_subject_id (subject_id),
    FOREIGN KEY (subject_id) REFERENCES edu_subject(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';
```

**edu_knowledge_point** - 知识点表
```sql
CREATE TABLE edu_knowledge_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    code VARCHAR(50) COMMENT '知识点编码',
    description TEXT COMMENT '知识点描述',
    importance_level TINYINT DEFAULT 2 COMMENT '重要程度: 1简单 2一般 3重要 4核心',
    error_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '历史错误率',
    practice_count INT DEFAULT 0 COMMENT '练习次数',
    master_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '掌握率',
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_error_rate (error_rate),
    FOREIGN KEY (chapter_id) REFERENCES edu_chapter(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';
```

#### 4.1.3 题库相关表

**qms_question** - 题目表
```sql
CREATE TABLE qms_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    chapter_id BIGINT COMMENT '章节ID',
    knowledge_point_ids VARCHAR(500) COMMENT '关联知识点ID列表(逗号分隔)',
    type ENUM('single_choice', 'multi_choice', 'true_false', 'fill_blank', 'short_answer', 'material_analysis') NOT NULL COMMENT '题目类型',
    difficulty TINYINT NOT NULL COMMENT '难度: 1简单 2中等 3困难',
    content TEXT NOT NULL COMMENT '题目内容',
    options JSON COMMENT '选项(JSON格式: [{key, value, isCorrect}])',
    answer TEXT NOT NULL COMMENT '标准答案',
    analysis TEXT COMMENT '答案解析',
    points DECIMAL(5,2) DEFAULT 1.00 COMMENT '分值',
    tags VARCHAR(500) COMMENT '标签(JSON数组)',
    error_tags VARCHAR(500) COMMENT '易错标签',
    exam_point VARCHAR(255) COMMENT '考点',
    source VARCHAR(255) COMMENT '题目来源',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    correct_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '正确率',
    status TINYINT DEFAULT 0 COMMENT '状态: 0待审核 1已上架 2已下架',
    create_user_id BIGINT COMMENT '创建用户ID',
    review_user_id BIGINT COMMENT '审核用户ID',
    review_time DATETIME COMMENT '审核时间',
    review_msg VARCHAR(255) COMMENT '审核意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_subject_id (subject_id),
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_status (status),
    FULLTEXT idx_content (content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';
```

**qms_question_history** - 题目历史版本表
```sql
CREATE TABLE qms_question_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    version INT NOT NULL COMMENT '版本号',
    content TEXT NOT NULL,
    options JSON,
    answer TEXT NOT NULL,
    analysis TEXT,
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目历史版本表';
```

#### 4.1.4 练习考试相关表

**pra_practice_record** - 练习记录表
```sql
CREATE TABLE pra_practice_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    subject_id BIGINT COMMENT '学科ID',
    practice_type ENUM('sequential', 'random', 'wrong', 'special') NOT NULL COMMENT '练习类型',
    question_ids VARCHAR(2000) COMMENT '题目ID列表(JSON数组)',
    current_index INT DEFAULT 0 COMMENT '当前题目索引',
    total_count INT COMMENT '总题数',
    answered_count INT DEFAULT 0 COMMENT '已答数量',
    correct_count INT DEFAULT 0 COMMENT '正确数量',
    score DECIMAL(5,2) COMMENT '得分',
    duration INT COMMENT '练习时长(秒)',
    status ENUM('in_progress', 'completed', 'abandoned') DEFAULT 'in_progress',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    submit_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_practice_type (practice_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习记录表';
```

**pra_answer_record** - 答题记录表
```sql
CREATE TABLE pra_answer_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    practice_id BIGINT COMMENT '练习记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    user_answer TEXT COMMENT '用户答案',
    correct_answer TEXT COMMENT '标准答案',
    is_correct TINYINT COMMENT '是否正确: 0错 1对',
    score DECIMAL(5,2) COMMENT '得分',
    duration INT COMMENT '答题时长(秒)',
    submit_time DATETIME COMMENT '提交时间',
    ai_evaluation TEXT COMMENT 'AI批改评价',
    ai_analysis TEXT COMMENT 'AI详细分析',
    knowledge_point_ids VARCHAR(500) COMMENT '关联知识点ID',
    error_type VARCHAR(50) COMMENT '错误类型: misunderstanding|calculation|misread|盲区',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_id (practice_id),
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_is_correct (is_correct),
    INDEX idx_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';
```

**exam_exam** - 考试表
```sql
CREATE TABLE exam_exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '考试标题',
    subject_id BIGINT COMMENT '学科ID',
    exam_type ENUM('daily', 'weekly', 'monthly', 'final', 'custom') NOT NULL COMMENT '考试类型',
    total_score DECIMAL(5,2) DEFAULT 100.00 COMMENT '总分',
    passing_score DECIMAL(5,2) DEFAULT 60.00 COMMENT '及格分',
    duration INT NOT NULL COMMENT '考试时长(分钟)',
    total_questions INT COMMENT '总题数',
    question_ids VARCHAR(3000) COMMENT '题目ID列表(JSON)',
    generate_type ENUM('manual', 'ai_auto') DEFAULT 'manual' COMMENT '生成方式',
    ai_prompt TEXT COMMENT 'AI生成时的提示词',
    status ENUM('draft', 'published', 'ongoing', 'ended', 'archived') DEFAULT 'draft',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    allow_review TINYINT DEFAULT 1 COMMENT '允许查看成绩',
    allow_review_time DATETIME COMMENT '允许查卷时间',
    create_user_id BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_subject_id (subject_id),
    INDEX idx_exam_type (exam_type),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';
```

**exam_exam_record** - 考试记录表
```sql
CREATE TABLE exam_exam_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    answer_records TEXT COMMENT '答题记录(JSON)',
    total_score DECIMAL(5,2) COMMENT '总分',
    correct_count INT COMMENT '正确题数',
    total_count INT COMMENT '总题数',
    is_passed TINYINT COMMENT '是否及格',
    duration INT COMMENT '考试用时(秒)',
    start_time DATETIME COMMENT '开始时间',
    submit_time DATETIME COMMENT '提交时间',
    is_automatically_submit TINYINT DEFAULT 0 COMMENT '是否自动交卷',
    status ENUM('ongoing', 'submitted', 'reviewed') DEFAULT 'submitted',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_exam_id (exam_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';
```

#### 4.1.5 错题相关表

**err_wrong_question** - 错题表
```sql
CREATE TABLE err_wrong_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    practice_record_id BIGINT COMMENT '练习记录ID',
    exam_record_id BIGINT COMMENT '考试记录ID',
    user_answer TEXT COMMENT '用户错误答案',
    correct_answer TEXT COMMENT '正确答案',
    error_type VARCHAR(50) COMMENT '错误类型',
    knowledge_point_ids VARCHAR(500) COMMENT '关联知识点ID',
    error_times INT DEFAULT 1 COMMENT '错误次数',
    last_error_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后错误时间',
    mastery_level TINYINT DEFAULT 0 COMMENT '掌握程度: 0-100',
    is_key_point TINYINT DEFAULT 0 COMMENT '是否重点标记',
    is_collected TINYINT DEFAULT 0 COMMENT '是否收藏',
    review_count INT DEFAULT 0 COMMENT '复习次数',
    last_review_time DATETIME COMMENT '最后复习时间',
    next_review_time DATETIME COMMENT '下次复习时间',
    ai_diagnosis TEXT COMMENT 'AI诊断分析',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_error_type (error_type),
    INDEX idx_mastery_level (mastery_level),
    INDEX idx_is_key_point (is_key_point),
    INDEX idx_next_review_time (next_review_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题表';
```

#### 4.1.6 学习报告表

**rpt_learning_report** - 学习报告表
```sql
CREATE TABLE rpt_learning_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    report_type ENUM('daily', 'weekly', 'monthly') NOT NULL COMMENT '报告类型',
    report_date DATE NOT NULL COMMENT '报告日期',
    subject_id BIGINT COMMENT '学科ID(可选)',
    total_practice_count INT DEFAULT 0 COMMENT '总练习次数',
    total_question_count INT DEFAULT 0 COMMENT '总做题数',
    correct_count INT DEFAULT 0 COMMENT '正确数',
    correct_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '正确率',
    total_duration INT DEFAULT 0 COMMENT '总学习时长(秒)',
    knowledge_mastery JSON COMMENT '知识点掌握度(JSON)',
    performance_trend JSON COMMENT '表现趋势(JSON)',
    weak_points JSON COMMENT '薄弱点分析(JSON)',
    strong_points JSON COMMENT '强项分析(JSON)',
    improvement_suggestions TEXT COMMENT '改进建议',
    ai_summary TEXT COMMENT 'AI总结评语',
    score_change DECIMAL(5,2) COMMENT '分数变化',
    rank_change INT COMMENT '排名变化',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_report_type (report_type),
    INDEX idx_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习报告表';
```

**rpt_knowledge_mastery** - 知识点掌握度表
```sql
CREATE TABLE rpt_knowledge_mastery (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    knowledge_point_id BIGINT NOT NULL,
    mastery_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '掌握率 0-100',
    practice_count INT DEFAULT 0 COMMENT '练习次数',
    correct_count INT DEFAULT 0 COMMENT '正确次数',
    average_duration INT DEFAULT 0 COMMENT '平均答题时长',
    last_practice_time DATETIME COMMENT '最后练习时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_knowledge_point_id (knowledge_point_id),
    UNIQUE KEY uk_user_knowledge (user_id, knowledge_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点掌握度表';
```

#### 4.1.7 AI相关表

**ai_chat_session** - AI会话表
```sql
CREATE TABLE ai_chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_type ENUM('qa', 'tutoring', 'planning', 'analysis') NOT NULL COMMENT '会话类型',
    title VARCHAR(255) COMMENT '会话标题',
    context_summary TEXT COMMENT '上下文摘要',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    last_message_time DATETIME COMMENT '最后消息时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0结束 1进行中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_session_type (session_type),
    INDEX idx_last_message_time (last_message_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表';
```

**ai_chat_message** - AI消息表
```sql
CREATE TABLE ai_chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role ENUM('user', 'assistant', 'system') NOT NULL COMMENT '消息角色',
    content TEXT NOT NULL COMMENT '消息内容',
    model VARCHAR(50) COMMENT '使用的模型',
    token_count INT COMMENT 'Token消耗',
    metadata JSON COMMENT '元数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';
```

**ai_usage_log** - AI使用日志表
```sql
CREATE TABLE ai_usage_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID',
    agent_type VARCHAR(50) NOT NULL COMMENT 'Agent类型',
    model VARCHAR(50) NOT NULL COMMENT '使用模型',
    prompt_tokens INT COMMENT '输入Token数',
    completion_tokens INT COMMENT '输出Token数',
    total_tokens INT COMMENT '总Token数',
    api_cost DECIMAL(10,4) COMMENT 'API费用',
    request_time INT COMMENT '请求耗时(ms)',
    status TINYINT COMMENT '请求状态: 0失败 1成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_agent_type (agent_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI使用日志表';
```

#### 4.1.8 系统配置表

**sys_config** - 系统配置表
```sql
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_name VARCHAR(100) COMMENT '配置名称',
    config_group VARCHAR(50) COMMENT '配置分组',
    description VARCHAR(255) COMMENT '配置描述',
    data_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string',
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化配置数据
INSERT INTO sys_config (config_key, config_value, config_name, config_group, data_type, description) VALUES
('deepseek.api_key', '', 'DeepSeek API密钥', 'ai', 'string', 'DeepSeek大模型API密钥'),
('deepseek.base_url', 'https://api.deepseek.com', 'DeepSeek API地址', 'ai', 'string', 'DeepSeek API基础地址'),
('deepseek.model', 'deepseek-chat', '默认模型', 'ai', 'string', '默认使用的模型'),
('deepseek.temperature', '0.7', '温度参数', 'ai', 'number', '生成随机性参数'),
('deepseek.max_tokens', '2000', '最大Token数', 'ai', 'number', '最大响应Token数'),
('site.name', '智学星途', '网站名称', 'site', 'string', '平台网站名称'),
('site.logo', '/logo.png', '网站Logo', 'site', 'string', '网站Logo路径'),
('site.icp', '', 'ICP备案号', 'site', 'string', '网站ICP备案号'),
('exam.default_duration', '120', '默认考试时长(分钟)', 'exam', 'number', '默认考试时长'),
('exam.default_total_score', '100', '默认总分', 'exam', 'number', '默认考试总分'),
('exam.default_passing_score', '60', '默认及格分', 'exam', 'number', '默认及格分数'),
('jwt.secret', '${JWT_SECRET}', 'JWT密钥', 'security', 'string', 'JWT签名密钥'),
('jwt.expire_time', '86400', 'Token过期时间(秒)', 'security', 'number', 'JWT Token过期时间，默认7天');
```

**sys_announcement** - 系统公告表
```sql
CREATE TABLE sys_announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    type ENUM('info', 'warning', 'success', 'error') DEFAULT 'info',
    priority TINYINT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 1 COMMENT '状态: 0下架 1上架',
    publish_time DATETIME COMMENT '发布时间',
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
```

**sys_banner** - 轮播图表
```sql
CREATE TABLE sys_banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) COMMENT '标题',
    image_url VARCHAR(500) NOT NULL COMMENT '图片地址',
    link_url VARCHAR(500) COMMENT '跳转链接',
    link_type ENUM('none', 'inner', 'outer') DEFAULT 'none' COMMENT '链接类型',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    start_time DATETIME COMMENT '展示开始时间',
    end_time DATETIME COMMENT '展示结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';
```

---

## 五、功能模块详细设计

### 5.1 用户中心模块

#### 5.1.1 功能清单
- [x] 用户注册（用户名、密码、手机号）
- [x] 登录（用户名/手机号+密码）
- [x] JWT Token认证
- [x] 游客/学生/教师/管理员四角色
- [x] 个人资料修改
- [x] 头像上传
- [x] 密码修改
- [x] 学习数据统计

#### 5.1.2 权限控制
```
游客（tourist）：浏览公开内容
学生（student）：刷题、考试、查看报告
教师（teacher）：题库管理、出题、考试管理
管理员（admin）：全站管理
```

### 5.2 学科知识管理模块

#### 5.2.1 三级知识体系
```
学科(Subject) → 章节(Chapter) → 知识点(KnowledgePoint)
```

#### 5.2.2 功能清单
- [x] 学科CRUD
- [x] 章节CRUD
- [x] 知识点CRUD
- [x] 知识点排序
- [x] 知识点关联题目
- [x] 知识图谱展示

### 5.3 题库管理模块

#### 5.3.1 题目类型
```java
public enum QuestionType {
    SINGLE_CHOICE("单选题"),
    MULTI_CHOICE("多选题"),
    TRUE_FALSE("判断题"),
    FILL_BLANK("填空题"),
    SHORT_ANSWER("简答题"),
    MATERIAL_ANALYSIS("材料分析题")
}
```

#### 5.3.2 功能清单
- [x] 题目CRUD
- [x] 题目审核上架
- [x] 题目导入Excel
- [x] 题目导出Excel
- [x] 题目筛选（学科/章节/知识点/难度/类型）
- [x] Redis缓存热门题目

### 5.4 AI智能出题模块

#### 5.4.1 出题模式
```java
public enum GenerateMode {
    CUSTOM("自定义条件出题"),
    SPECIAL_TRAINING("专项训练出题"),
    FULL_PAPER("模拟试卷自动组卷"),
    WEAK_POINT("薄弱点定向出题")
}
```

#### 5.4.2 AI出题流程
```
1. 接收出题请求（学科、知识点、难度、数量、题型）
2. 调用出题Agent分析需求
3. 构建结构化Prompt
4. 调用DeepSeek生成题目
5. 解析JSON结果
6. 批量插入数据库
7. 返回出题结果
```

### 5.5 在线刷题模块

#### 5.5.1 练习模式
```java
public enum PracticeType {
    SEQUENTIAL("顺序刷题"),
    RANDOM("随机刷题"),
    WRONG("错题重刷"),
    SPECIAL("专项刷题")
}
```

#### 5.5.2 功能清单
- [x] 多种练习模式
- [x] 实时保存进度
- [x] 答题计时
- [x] 实时批改
- [x] 解析查看
- [x] 答题记录存档

### 5.6 在线考试模块

#### 5.6.1 考试流程
```
创建考试 → 发布考试 → 学生参与 → 限时作答 → 自动交卷 → 即时出分
```

#### 5.6.2 功能清单
- [x] 考试创建（手动/AI自动）
- [x] 考试配置（时长、分值、及格线）
- [x] 考试发布
- [x] 学生在线作答
- [x] 倒计时提醒
- [x] 强制交卷
- [x] 成绩查询
- [x] 排名统计

### 5.7 AI错题本模块

#### 5.7.1 错题管理
```java
public enum ErrorType {
    MISUNDERSTANDING("概念混淆"),
    CALCULATION("计算失误"),
    MISREAD("审题错误"),
    BLIND_SPOT("知识点盲区")
}
```

#### 5.7.2 功能清单
- [x] 自动收录错题
- [x] 错题分类
- [x] AI错因诊断
- [x] AI复盘笔记
- [x] 举一反三训练
- [x] 错题打印导出

### 5.8 学情分析模块

#### 5.8.1 分析维度
- 知识点掌握度
- 正确率趋势
- 学习时长统计
- 薄弱点分析
- 学习计划推荐

#### 5.8.2 功能清单
- [x] 学习数据统计
- [x] 知识点雷达图
- [x] 正确率趋势图
- [x] 学习报告生成
- [x] AI学习建议

### 5.9 AI答疑广场模块

#### 5.9.1 功能清单
- [x] 智能问答
- [x] 知识点关联答疑
- [x] 多轮对话记忆
- [x] 历史记录查询
- [x] 热门问题推荐

---

## 六、AI Agent核心设计

### 6.1 Agent类型

```java
public enum AgentType {
    // 学习任务规划Agent
    TASK_PLANNING("学习任务规划Agent"),
    
    // 智能出题Agent
    QUESTION_GENERATOR("智能出题Agent"),
    
    // 智能批改Agent
    ANSWER_GRADER("智能批改Agent"),
    
    // 错题诊断Agent
    ERROR_DIAGNOSIS("错题诊断Agent"),
    
    // 举一反三强化Agent
    VARIATION_GENERATOR("举一反三强化Agent"),
    
    // 学情分析Agent
    LEARNING_ANALYZER("学情分析Agent"),
    
    // 智能答疑Agent
    QNA_ASSISTANT("智能答疑Agent")
}
```

### 6.2 提示词模板

#### 6.2.1 出题模板
```json
{
  "name": "question_generator",
  "template": "你是一个专业的出题专家。请根据以下要求生成练习题：\n\n## 出题要求\n- 学科：{subject_name}\n- 知识点：{knowledge_points}\n- 难度等级：{difficulty} (1=简单, 2=中等, 3=困难)\n- 题目类型：{question_types}\n- 题目数量：{question_count}\n- 分值：{total_score}分\n\n## 输出要求\n请以JSON数组格式返回，示例：\n[\n  {\n    \"type\": \"single_choice\",\n    \"content\": \"题目内容\",\n    \"options\": [\n      {\"key\": \"A\", \"value\": \"选项A\", \"isCorrect\": true},\n      {\"key\": \"B\", \"value\": \"选项B\", \"isCorrect\": false}\n    ],\n    \"answer\": \"A\",\n    \"analysis\": \"答案解析\",\n    \"difficulty\": 2\n  }\n]\n\n## 注意事项\n1. 题目要符合教学大纲要求\n2. 答案必须准确无误\n3. 解析要详细清楚\n4. 难度要符合要求"
}
```

#### 6.2.2 批改模板
```json
{
  "name": "answer_grader",
  "template": "你是一个严格的批改老师。请批改以下学生答案：\n\n## 题目信息\n- 题目类型：{question_type}\n- 题目内容：{question_content}\n- 标准答案：{correct_answer}\n- 分值：{score}分\n\n## 学生答案\n{user_answer}\n\n## 批改要求\n请以JSON格式返回：\n{\n  \"isCorrect\": true/false,\n  \"score\": 分数,\n  \"maxScore\": 满分,\n  \"evaluation\": \"总体评价\",\n  \"errorAnalysis\": \"错误分析\",\n  \"correctGuidance\": \"正确解题思路\",\n  \"improvement\": \"提升建议\"\n}"
}
```

#### 6.2.3 错题诊断模板
```json
{
  "name": "error_diagnosis",
  "template": "请分析以下错题，诊断学生的错误类型和薄弱知识点：\n\n## 错题信息\n- 题目：{question_content}\n- 学生答案：{user_answer}\n- 正确答案：{correct_answer}\n- 知识点：{knowledge_points}\n\n## 诊断要求\n请以JSON格式返回：\n{\n  \"errorType\": \"错误类型(misunderstanding|calculation|misread|盲区)\",\n  \"errorReason\": \"具体错误原因\",\n  \"weakKnowledgePoints\": [\"薄弱知识点列表\"],\n  \"diagnosis\": \"详细诊断分析\",\n  \"suggestions\": [\"改进建议\"]\n}"
}
```

### 6.3 上下文记忆管理

```java
@Service
public class ChatMemoryService {
    
    // Redis存储会话上下文
    // 存储结构：会话ID -> 消息列表
    // 支持多轮对话记忆
    // 自动清理过期会话（默认7天）
}
```

---

## 七、API接口设计

### 7.1 用户接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/user/register | 用户注册 | 公开 |
| POST | /api/user/login | 用户登录 | 公开 |
| POST | /api/user/logout | 退出登录 | 登录 |
| GET | /api/user/info | 获取用户信息 | 登录 |
| PUT | /api/user/info | 修改用户信息 | 登录 |
| PUT | /api/user/password | 修改密码 | 登录 |
| POST | /api/user/avatar | 上传头像 | 登录 |

### 7.2 学科知识接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/subject/list | 学科列表 | 公开 |
| CRUD | /api/subject/** | 学科管理 | 教师/管理员 |
| GET | /api/chapter/list | 章节列表 | 公开 |
| CRUD | /api/chapter/** | 章节管理 | 教师/管理员 |
| GET | /api/knowledge/list | 知识点列表 | 公开 |
| CRUD | /api/knowledge/** | 知识点管理 | 教师/管理员 |

### 7.3 题库接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/question/list | 题目列表 | 登录 |
| GET | /api/question/{id} | 题目详情 | 登录 |
| POST | /api/question | 创建题目 | 教师/管理员 |
| PUT | /api/question | 更新题目 | 教师/管理员 |
| DELETE | /api/question/{id} | 删除题目 | 教师/管理员 |
| POST | /api/question/import | 导入题目 | 教师/管理员 |
| GET | /api/question/export | 导出题目 | 教师/管理员 |
| PUT | /api/question/review | 审核题目 | 教师/管理员 |

### 7.4 AI接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/ai/generate/questions | AI智能出题 | 登录 |
| POST | /api/ai/grade | AI智能批改 | 登录 |
| POST | /api/ai/diagnose | 错题AI诊断 | 登录 |
| POST | /api/ai/chat | AI答疑对话 | 登录 |
| POST | /api/ai/chat/stream | AI流式对话 | 登录 |
| GET | /api/ai/chat/history | 对话历史 | 登录 |
| POST | /api/ai/analyze/learning | 学情AI分析 | 登录 |

### 7.5 练习考试接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | /api/practice/start | 开始练习 | 登录 |
| PUT | /api/practice/{id}/answer | 提交答案 | 登录 |
| PUT | /api/practice/{id}/save | 保存进度 | 登录 |
| POST | /api/practice/{id}/submit | 提交练习 | 登录 |
| GET | /api/practice/list | 练习记录 | 登录 |
| GET | /api/practice/{id} | 练习详情 | 登录 |
| GET | /api/exam/list | 考试列表 | 登录 |
| POST | /api/exam | 创建考试 | 教师/管理员 |
| POST | /api/exam/{id}/publish | 发布考试 | 教师/管理员 |
| POST | /api/exam/{id}/join | 参加考试 | 登录 |
| POST | /api/exam/record/{id}/submit | 提交试卷 | 登录 |
| GET | /api/exam/record/{id} | 考试详情 | 登录 |

---

## 八、前端页面设计

### 8.1 学生端页面

1. **首页** (`/`)
   - 轮播图
   - 快捷功能入口
   - 学习数据概览
   - 热门学科推荐

2. **刷题广场** (`/practice`)
   - 学科筛选
   - 题目类型筛选
   - AI出题入口
   - 练习模式选择

3. **在线考试** (`/exam`)
   - 公开考试列表
   - 我的考试记录
   - 考试详情/成绩

4. **错题本** (`/wrong`)
   - 全部错题
   - 分类筛选
   - 错题复盘
   - 举一反三训练

5. **学情分析** (`/analysis`)
   - 学习概览
   - 知识点掌握度
   - 正确率趋势
   - 学习报告

6. **AI答疑** (`/qa`)
   - 智能问答
   - 历史记录
   - 热门问题

7. **个人中心** (`/profile`)
   - 个人信息
   - 学习记录
   - 账号设置

### 8.2 教师端页面

1. **控制台** (`/teacher`)
2. **题库管理** (`/teacher/questions`)
3. **试卷管理** (`/teacher/exams`)
4. **AI配置** (`/teacher/ai-config`)

### 8.3 管理端页面

1. **数据大盘** (`/admin`)
2. **用户管理** (`/admin/users`)
3. **学科管理** (`/admin/subjects`)
4. **系统配置** (`/admin/config`)
5. **公告管理** (`/admin/announcements`)

---

## 九、项目部署

### 9.1 环境要求
- JDK 22
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis 7.x

### 9.2 启动顺序
1. 启动MySQL和Redis
2. 执行数据库初始化脚本
3. 启动后端SpringBoot应用
4. 启动前端Vue开发服务器

### 9.3 配置文件
- 后端：`application.yml`
- 前端：`.env.development` / `.env.production`

---

## 十、开发规范

### 10.1 代码规范
- 统一使用阿里Java开发规范
- 前端使用ESLint + Prettier
- 所有接口必须文档化

### 10.2 Git规范
- 分支：feature/xxx, bugfix/xxx, release/v1.0
- Commit信息：feat: xxx, fix: xxx, docs: xxx

### 10.3 文档规范
- 所有接口必须编写Swagger注释
- 核心业务逻辑必须添加注释
- README文档必须包含启动说明

---

## 十一、验收标准

### 11.1 功能验收
- [ ] 所有模块基础功能完整
- [ ] AI Agent能够正常工作
- [ ] 前后端联调正常
- [ ] 数据库操作正常

### 11.2 性能验收
- [ ] 接口响应时间 < 500ms
- [ ] 支持100+并发用户
- [ ] Redis缓存正常工作

### 11.3 安全验收
- [ ] JWT认证正常工作
- [ ] 权限控制有效
- [ ] 无SQL注入风险
- [ ] 敏感信息加密存储
