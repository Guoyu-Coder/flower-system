-- 智学星途-AI智能学习平台 数据库初始化脚本
-- 数据库: ai_learning_platform
-- 版本: 1.0.0

CREATE DATABASE IF NOT EXISTS ai_learning_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_learning_platform;

-- ==================== 用户相关表 ====================

CREATE TABLE IF NOT EXISTS sys_user (
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
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_user_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(500) NOT NULL COMMENT 'JWT令牌',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token (token(255)),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户Token表';

CREATE TABLE IF NOT EXISTS sys_login_log (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ==================== 学科知识表 ====================

CREATE TABLE IF NOT EXISTS edu_subject (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学科表';

CREATE TABLE IF NOT EXISTS edu_chapter (
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
    FOREIGN KEY (subject_id) REFERENCES edu_subject(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

CREATE TABLE IF NOT EXISTS edu_knowledge_point (
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
    FOREIGN KEY (chapter_id) REFERENCES edu_chapter(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点表';

-- ==================== 题库相关表 ====================

CREATE TABLE IF NOT EXISTS qms_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    chapter_id BIGINT COMMENT '章节ID',
    knowledge_point_ids VARCHAR(500) COMMENT '关联知识点ID列表(逗号分隔)',
    type ENUM('single_choice', 'multi_choice', 'true_false', 'fill_blank', 'short_answer', 'material_analysis') NOT NULL COMMENT '题目类型',
    difficulty TINYINT NOT NULL COMMENT '难度: 1简单 2中等 3困难',
    content TEXT NOT NULL COMMENT '题目内容',
    options JSON COMMENT '选项(JSON格式)',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

CREATE TABLE IF NOT EXISTS qms_question_history (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目历史版本表';

-- ==================== 练习考试相关表 ====================

CREATE TABLE IF NOT EXISTS pra_practice_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    subject_id BIGINT COMMENT '学科ID',
    practice_type ENUM('sequential', 'random', 'wrong', 'special') NOT NULL COMMENT '练习类型',
    question_ids TEXT COMMENT '题目ID列表(JSON数组)',
    current_index INT DEFAULT 0 COMMENT '当前题目索引',
    total_count INT COMMENT '总题数',
    answered_count INT DEFAULT 0 COMMENT '已答数量',
    correct_count INT DEFAULT 0 COMMENT '正确数量',
    score DECIMAL(5,2) COMMENT '得分',
    duration INT COMMENT '练习时长(秒)',
    status ENUM('in_progress', 'completed', 'abandoned') DEFAULT 'in_progress',
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    submit_time DATETIME COMMENT '提交时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_practice_type (practice_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习记录表';

CREATE TABLE IF NOT EXISTS pra_answer_record (
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
    error_type VARCHAR(50) COMMENT '错误类型',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_id (practice_id),
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_is_correct (is_correct),
    INDEX idx_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题记录表';

CREATE TABLE IF NOT EXISTS exam_exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '考试标题',
    subject_id BIGINT COMMENT '学科ID',
    exam_type ENUM('daily', 'weekly', 'monthly', 'final', 'custom') NOT NULL COMMENT '考试类型',
    total_score DECIMAL(5,2) DEFAULT 100.00 COMMENT '总分',
    passing_score DECIMAL(5,2) DEFAULT 60.00 COMMENT '及格分',
    duration INT NOT NULL COMMENT '考试时长(分钟)',
    total_questions INT COMMENT '总题数',
    question_ids TEXT COMMENT '题目ID列表(JSON)',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

CREATE TABLE IF NOT EXISTS exam_exam_record (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- ==================== 错题相关表 ====================

CREATE TABLE IF NOT EXISTS err_wrong_question (
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
    mastery_level INT DEFAULT 0 COMMENT '掌握程度: 0-100',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题表';

-- ==================== 学习报告表 ====================

CREATE TABLE IF NOT EXISTS rpt_learning_report (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习报告表';

CREATE TABLE IF NOT EXISTS rpt_knowledge_mastery (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点掌握度表';

-- ==================== AI相关表 ====================

CREATE TABLE IF NOT EXISTS ai_chat_session (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会话表';

CREATE TABLE IF NOT EXISTS ai_chat_message (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI消息表';

CREATE TABLE IF NOT EXISTS ai_usage_log (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI使用日志表';

-- ==================== 系统配置表 ====================

CREATE TABLE IF NOT EXISTS sys_config (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS sys_announcement (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

CREATE TABLE IF NOT EXISTS sys_banner (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- ==================== 初始化数据 ====================

-- 插入管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin', 1);

-- 插入系统配置
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
('jwt.secret', 'REMOVED_JWT_SECRET', 'JWT密钥', 'security', 'string', 'JWT签名密钥'),
('jwt.expire_time', '604800', 'Token过期时间(秒)', 'security', 'number', 'JWT Token过期时间，默认7天');

-- 插入示例学科
INSERT INTO edu_subject (name, code, description, sort) VALUES
('高等数学', 'math', '大学高等数学课程', 1),
('大学英语', 'english', '大学英语课程', 2),
('大学物理', 'physics', '大学物理课程', 3);

-- 插入示例章节
INSERT INTO edu_chapter (subject_id, name, sort) VALUES
(1, '函数与极限', 1),
(1, '导数与微分', 2),
(1, '积分', 3),
(2, '阅读理解', 1),
(2, '写作', 2),
(3, '力学', 1);

-- 插入示例知识点
INSERT INTO edu_knowledge_point (chapter_id, name, description, importance_level) VALUES
(1, '函数的基本性质', '掌握函数的单调性、奇偶性、周期性', 2),
(1, '极限的概念', '理解数列极限和函数极限', 3),
(1, '极限的计算', '掌握极限的计算方法', 3),
(2, '导数的定义', '理解导数的几何意义和物理意义', 3),
(2, '求导法则', '掌握各种函数的求导方法', 3),
(3, '不定积分', '掌握不定积分的计算', 2),
(3, '定积分', '理解定积分的概念和应用', 3);

-- 插入示例题目
INSERT INTO qms_question (subject_id, chapter_id, type, difficulty, content, options, answer, analysis, points, status, create_user_id) VALUES
(1, 1, 'single_choice', 1, '下列函数中，是偶函数的是？', '{"options":[{"key":"A","value":"y=x","isCorrect":false},{"key":"B","value":"y=x^2","isCorrect":true},{"key":"C","value":"y=x^3","isCorrect":false},{"key":"D","value":"y=1/x","isCorrect":false}]}', 'B', 'x^2是偶函数，因为f(-x)=(-x)^2=x^2=f(x)', 5.00, 1, 1),
(1, 2, 'single_choice', 2, '函数f(x)=x^3的导数是？', '{"options":[{"key":"A","value":"3x^2","isCorrect":true},{"key":"B","value":"x^2","isCorrect":false},{"key":"C","value":"3x","isCorrect":false},{"key":"D","value":"x^3","isCorrect":false}]}', 'A', '根据幂函数求导法则，(x^n)''=nx^(n-1)', 5.00, 1, 1),
(1, 3, 'true_false', 1, '定积分的值可以是负数。', NULL, '正确', '定积分的值可以是正数、负数或零，取决于函数在区间上的正负', 5.00, 1, 1);

-- 插入示例轮播图
INSERT INTO sys_banner (title, image_url, link_url, link_type, sort, status) VALUES
('欢迎使用智学星途', 'https://picsum.photos/1200/400?random=1', '/', 'inner', 1, 1),
('AI智能出题', 'https://picsum.photos/1200/400?random=2', '/practice', 'inner', 2, 1),
('学习数据分析', 'https://picsum.photos/1200/400?random=3', '/analysis', 'inner', 3, 1);

-- 插入示例公告
INSERT INTO sys_announcement (title, content, type, status, publish_time) VALUES
('系统上线公告', '智学星途AI智能学习平台正式上线，欢迎使用！', 'success', 1, NOW()),
('AI功能升级', '平台AI功能全面升级，支持智能出题和个性化学习规划', 'info', 1, NOW());
