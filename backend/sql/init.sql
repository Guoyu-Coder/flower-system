-- ============================================================
-- 鲜花购物商城系统 - 数据库初始化脚本
-- 数据库: flower_shop
-- ============================================================

CREATE DATABASE IF NOT EXISTS flower_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE flower_shop;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 管理员表
-- ============================================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '管理员账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `role` VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色: SUPER_ADMIN, ADMIN',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- 3. 商品分类表
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(500) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0隐藏 1显示',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 4. 商品表
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '副标题',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '原价',
    `discount_price` DECIMAL(10,2) DEFAULT NULL COMMENT '折扣价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `images` TEXT COMMENT '轮播图(JSON数组)',
    `specifications` TEXT COMMENT '规格(JSON)',
    `is_new` TINYINT DEFAULT 0 COMMENT '是否新品: 0否 1是',
    `is_hot` TINYINT DEFAULT 0 COMMENT '是否热门: 0否 1是',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐: 0否 1是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(逗号分隔: 节日,送女友,生日)',
    `flower_language` VARCHAR(500) DEFAULT NULL COMMENT '花语',
    `occasion` VARCHAR(200) DEFAULT NULL COMMENT '适用场景',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_price` (`price`),
    KEY `idx_sales` (`sales`),
    KEY `idx_status` (`status`),
    KEY `idx_tags` (`tags`(100))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 5. 购物车表
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `selected` TINYINT DEFAULT 1 COMMENT '是否选中: 0否 1是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 6. 订单表
-- ============================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `address_id` BIGINT DEFAULT NULL COMMENT '收货地址ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `pay_method` TINYINT DEFAULT 1 COMMENT '支付方式: 1余额 2支付宝 3微信',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0待付款 1待发货 2已发货 3已完成 4已取消 5退款中 6已退款',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `received_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 7. 订单商品明细表
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order` (`order_id`),
    KEY `idx_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- ============================================================
-- 8. 收货地址表
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
    `detail_address` VARCHAR(500) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0否 1是',
    `label` VARCHAR(20) DEFAULT NULL COMMENT '标签: 家,公司,学校',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================================
-- 9. 花语知识库表
-- ============================================================
DROP TABLE IF EXISTS `flower_language`;
CREATE TABLE `flower_language` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `flower_name` VARCHAR(100) NOT NULL COMMENT '花名',
    `alias_name` VARCHAR(200) DEFAULT NULL COMMENT '别名',
    `flower_language` VARCHAR(500) NOT NULL COMMENT '花语',
    `meaning` TEXT COMMENT '寓意详细描述',
    `color_meaning` TEXT COMMENT '不同颜色含义(JSON)',
    `send_scene` TEXT COMMENT '送礼场景(JSON数组)',
    `taboo` TEXT COMMENT '送礼禁忌',
    `match_flowers` VARCHAR(500) DEFAULT NULL COMMENT '搭配花材',
    `maintenance` TEXT COMMENT '养护知识',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '图片',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_flower_name` (`flower_name`),
    FULLTEXT KEY `ft_search` (`flower_name`, `flower_language`, `meaning`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='花语知识库表';

-- ============================================================
-- 10. AI聊天记录表
-- ============================================================
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID(游客为NULL)',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID(游客用UUID)',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: user/assistant',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_session` (`session_id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_created` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天记录表';

-- ============================================================
-- 11. 用户收藏表
-- ============================================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================================
-- 插入基础测试数据
-- ============================================================

-- 管理员
INSERT INTO `admin` (`username`, `password`, `nickname`, `role`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', 'SUPER_ADMIN');

-- 测试用户 (密码均为123456, MD5加密)
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `gender`) VALUES
('testuser', 'e10adc3949ba59abbe56e057f20f883e', '小花', '13800138000', 'test@flower.com', 2),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13900139000', 'zhangsan@test.com', 1),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13700137000', 'lisi@test.com', 1);

-- 商品分类
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('玫瑰', '/icons/rose.png', 1),
('百合', '/icons/lily.png', 2),
('康乃馨', '/icons/carnation.png', 3),
('向日葵', '/icons/sunflower.png', 4),
('郁金香', '/icons/tulip.png', 5),
('混搭花束', '/icons/mixed.png', 6),
('节日花礼', '/icons/festival.png', 7),
('绿植盆栽', '/icons/plant.png', 8);

-- 商品数据 (33条)
INSERT INTO `product` (`category_id`, `name`, `subtitle`, `description`, `price`, `discount_price`, `stock`, `sales`, `cover_image`, `images`, `is_new`, `is_hot`, `is_recommend`, `status`, `tags`, `flower_language`, `occasion`) VALUES
(1, '红玫瑰花束 "心动"', '经典99朵红玫瑰', '精选厄瓜多尔进口红玫瑰99朵，丝绒质感，层层包裹，传递最炽热的爱意。', 599.00, 499.00, 100, 256, '/images/products/red_rose_99.jpg', '["/images/products/red_rose_99_1.jpg","/images/products/red_rose_99_2.jpg"]', 0, 1, 1, 1, '送女友,生日,情人节,求婚', '热情的爱，我爱你，每一天', '表白,求婚,情人节,纪念日'),
(1, '粉色玫瑰花束 "初恋"', '33朵粉玫瑰浪漫之选', '33朵粉玫瑰搭配满天星，浅粉色的温柔如同初恋般美好。', 369.00, 299.00, 150, 189, '/images/products/pink_rose_33.jpg', '["/images/products/pink_rose_33_1.jpg","/images/products/pink_rose_33_2.jpg"]', 1, 1, 1, 1, '送女友,生日,初恋', '初恋，感动，爱的宣言', '表白,约会,生日'),
(1, '白玫瑰花束 "纯真"', '19朵白玫瑰纯洁之爱', '19朵白玫瑰搭配尤加利叶，简约高雅，诉说纯洁的爱恋。', 259.00, 229.00, 120, 132, '/images/products/white_rose_19.jpg', '["/images/products/white_rose_19_1.jpg"]', 0, 0, 0, 1, '送女友,简约', '纯洁，高贵，天真', '求婚,婚礼,道歉'),
(1, '香槟玫瑰花束 "优雅"', '52朵香槟玫瑰轻奢之选', '52朵香槟玫瑰，淡淡的香槟色透着优雅与高贵，诉说＂我愿与你共度一生＂。', 459.00, 399.00, 80, 178, '/images/products/champagne_rose_52.jpg', '["/images/products/champagne_rose_52_1.jpg"]', 0, 1, 1, 1, '送女友,求婚,轻奢', '爱上你是我今生最大的幸福', '求婚,纪念日,表白'),
(2, '白百合花束 "圣洁"', '6头精品白百合', '6头精品白百合，馥郁芬芳，花姿优雅，适合送给敬重的女性。', 189.00, 159.00, 200, 98, '/images/products/white_lily_6.jpg', '["/images/products/white_lily_6_1.jpg"]', 0, 0, 0, 1, '送母亲,送老师,探望', '纯洁，庄严，心心相印', '母亲节,教师节,探望病人'),
(2, '粉色百合花束 "温馨"', '多头粉百合温馨之选', '粉百合花姿绰约，色彩温柔，为生活增添一抹温馨色彩。', 169.00, 149.00, 180, 87, '/images/products/pink_lily.jpg', '["/images/products/pink_lily_1.jpg"]', 1, 0, 0, 1, '送母亲,送朋友', '富贵，吉祥，百年好合', '乔迁,祝福,探病'),
(3, '康乃馨花束 "感恩母亲"', '经典康乃馨感恩花束', '精选粉色康乃馨搭配勿忘我，表达对妈妈最深的爱与感恩。', 159.00, 139.00, 200, 212, '/images/products/carnation_mom.jpg', '["/images/products/carnation_mom_1.jpg"]', 0, 1, 1, 1, '送母亲,母亲节', '母爱，健康，感恩', '母亲节,生日,探望'),
(3, '彩色康乃馨花篮 "祝福"', '多彩康乃馨祝福花篮', '各色康乃馨编织的花篮，色彩缤纷，送给长辈表达祝福。', 199.00, 179.00, 150, 145, '/images/products/carnation_basket.jpg', '["/images/products/carnation_basket_1.jpg"]', 0, 0, 0, 1, '送长辈,送母亲', '热情，魅力，祝福', '生日,节日,乔迁'),
(4, '向日葵花束 "向阳而生"', '5支向日葵阳光花束', '5支向日葵搭配黄莺草，如阳光般灿烂，送给朋友温暖与希望。', 129.00, 109.00, 250, 167, '/images/products/sunflower_5.jpg', '["/images/products/sunflower_5_1.jpg"]', 0, 1, 1, 1, '送朋友,送同学,毕业', '信念，光辉，忠诚，沉默的爱', '毕业,考试成功,开业,生日'),
(4, '向日葵混搭花束 "阳光"', '向日葵搭配雏菊', '向日葵搭配白色雏菊和尤加利叶，清新自然，充满活力。', 149.00, 129.00, 180, 92, '/images/products/sunflower_mix.jpg', '["/images/products/sunflower_mix_1.jpg"]', 0, 0, 0, 1, '送朋友,送同事', '阳光，活力，快乐', '开业,庆祝,加油鼓劲'),
(5, '郁金香花束 "春日浪漫"', '荷兰进口郁金香', '荷兰进口郁金香，色彩斑斓，感受春日浪漫气息。', 229.00, 199.00, 100, 78, '/images/products/tulip_dutch.jpg', '["/images/products/tulip_dutch_1.jpg"]', 1, 0, 0, 1, '送女友,春游', '完美的爱情，永恒的爱', '约会,春游,婚礼'),
(5, '紫色郁金香花束 "神秘"', '紫色郁金香优雅之选', '紫色郁金香搭配银叶菊，神秘而高贵，适合独特的她。', 189.00, 169.00, 120, 56, '/images/products/tulip_purple.jpg', '["/images/products/tulip_purple_1.jpg"]', 0, 0, 0, 1, '送女友,情人节', '永恒的爱，无尽的爱', '情人节,生日,纪念日'),
(6, '混搭花束 "缤纷花园"', '玫瑰雏菊混搭花园风', '粉色玫瑰、白色雏菊、紫色桔梗混搭，如同秘密花园般美好。', 269.00, 239.00, 80, 134, '/images/products/mix_garden.jpg', '["/images/products/mix_garden_1.jpg"]', 0, 1, 0, 1, '送女友,送闺蜜', '多彩生活，美好祝福', '生日,聚会,感谢'),
(6, '混搭花束 "莫奈花园"', '法式复古混搭花束', '复古色调花材混搭，仿佛莫奈笔下的花园，文艺气息十足。', 329.00, 289.00, 60, 89, '/images/products/mix_monet.jpg', '["/images/products/mix_monet_1.jpg"]', 1, 0, 1, 1, '送女友,文艺,轻奢', '艺术，浪漫，格调', '生日,纪念日,节日'),
(7, '情人节礼盒 "一生一世"', '永生花礼盒+巧克力', '进口永生玫瑰礼盒搭配手工巧克力，永不凋谢的爱情。', 399.00, 359.00, 50, 198, '/images/products/valentine_box.jpg', '["/images/products/valentine_box_1.jpg"]', 0, 1, 1, 1, '情人节,送女友,永生花', '永不凋谢的爱', '情人节,七夕,纪念日'),
(7, '母亲节礼盒 "感恩的心"', '康乃馨花束+保温杯', '粉色康乃馨搭配精美保温杯，温暖妈妈的心。', 259.00, 229.00, 100, 156, '/images/products/mom_gift_box.jpg', '["/images/products/mom_gift_box_1.jpg"]', 0, 0, 0, 1, '母亲节,送母亲,礼盒', '感恩，祝福，温暖', '母亲节,妈妈生日'),
(7, '圣诞节花礼 "圣诞快乐"', '圣诞红玫瑰礼盒', '圣诞主题红色花礼，搭配松枝和圣诞装饰，节日氛围满满。', 299.00, 269.00, 70, 67, '/images/products/christmas_box.jpg', '["/images/products/christmas_box_1.jpg"]', 0, 0, 0, 1, '圣诞节,节日', '祝福，欢乐', '圣诞节,新年'),
(1, '蓝色玫瑰花束 "海之恋"', '9朵蓝玫瑰梦幻之选', '9朵蓝色妖姬玫瑰，神秘梦幻，送给心中最特别的人。', 299.00, 269.00, 90, 145, '/images/products/blue_rose_9.jpg', '["/images/products/blue_rose_9_1.jpg"]', 0, 0, 0, 1, '送女友,特别', '奇迹，珍贵，特别', '表白,纪念日,道歉'),
(2, '香水百合花束 "馥郁"', '多头香水百合浓香四溢', '多头香水百合，满室芬芳，高贵典雅。', 179.00, 159.00, 160, 112, '/images/products/perfume_lily.jpg', '["/images/products/perfume_lily_1.jpg"]', 0, 0, 0, 1, '送长辈,送母亲', '纯洁，高雅，幸福', '婚礼,乔迁,看望朋友'),
(4, '向日葵单支装 "小确幸"', '单支向日葵精致包装', '单支精选向日葵，简约却不简单，送给TA一份小惊喜。', 29.00, 19.90, 500, 321, '/images/products/sunflower_single.jpg', '["/images/products/sunflower_single_1.jpg"]', 0, 0, 0, 1, '小礼物,随便送送', '你是我的阳光', '日常,小惊喜,探班'),
(1, '彩虹玫瑰花束 "多彩"', '7色玫瑰彩虹花束', '7种颜色玫瑰组成彩虹花束，色彩斑斓，让爱多彩多姿。', 429.00, 379.00, 60, 98, '/images/products/rainbow_rose.jpg', '["/images/products/rainbow_rose_1.jpg"]', 0, 0, 0, 1, '送女友,惊喜', '多彩的爱，丰富的情感', '生日,纪念日,惊喜'),
(6, '混搭小花束 "清新"', '小清新日常花束', '小巧精致的花束，日常点缀生活，给自己一份好心情。', 69.00, 59.00, 300, 234, '/images/products/mix_small.jpg', '["/images/products/mix_small_1.jpg"]', 0, 0, 1, 1, '日常,自用', '美好的每一天', '日常,办公桌,家居装饰'),
(8, '绿萝盆栽', '大叶绿萝净化空气', '大叶绿萝，净化空气好帮手，室内绿植首选，好养易活。', 39.00, 29.90, 300, 187, '/images/products/green_pothos.jpg', '["/images/products/green_pothos_1.jpg"]', 0, 0, 0, 1, '绿植,家居,办公', '坚韧，善良', '新居,办公室,礼物'),
(8, '多肉植物拼盘', '8种多肉精致组合', '8种萌趣多肉组合盆栽，可爱又好养，送给多肉爱好者。', 59.00, 49.00, 200, 123, '/images/products/succulent.jpg', '["/images/products/succulent_1.jpg"]', 1, 0, 0, 1, '绿植,可爱,礼物', '可爱，顽强', '生日,乔迁,小礼物'),
(8, '富贵竹', '转运竹富贵吉祥', '造型优美的富贵竹，寓意富贵吉祥、节节高升。', 49.00, 39.90, 250, 98, '/images/products/lucky_bamboo.jpg', '["/images/products/lucky_bamboo_1.jpg"]', 0, 0, 0, 1, '送礼,乔迁,开业', '富贵，吉祥，节节高升', '开业,乔迁,春节'),
(1, '红玫瑰花束 "热恋"', '11朵红玫瑰浓情蜜意', '11朵红玫瑰代表一心一意，经典表白花束。', 199.00, 169.00, 200, 267, '/images/products/red_rose_11.jpg', '["/images/products/red_rose_11_1.jpg"]', 0, 0, 1, 1, '送女友,表白,情人节', '一心一意，我爱你', '表白,情人节,约会'),
(1, '粉雪山玫瑰花束 "温柔"', '20朵粉雪山玫瑰', '粉雪山玫瑰，淡淡粉色渐变，温柔如水，适合温柔的她。', 289.00, 259.00, 120, 143, '/images/products/pink_snow.jpg', '["/images/products/pink_snow_1.jpg"]', 0, 0, 0, 1, '送女友,生日', '温柔的爱，甜蜜', '生日,约会,纪念日'),
(7, '七夕礼盒 "鹊桥相会"', '七夕限定玫瑰礼盒', '七夕限定款红玫瑰礼盒，搭配定制卡片，牛郎织女的爱情传说。', 359.00, 329.00, 60, 176, '/images/products/qixi_box.jpg', '["/images/products/qixi_box_1.jpg"]', 0, 0, 1, 1, '七夕,情人节,送女友', '不离不弃，永恒之爱', '七夕,情人节'),
(7, '教师节花束 "桃李芬芳"', '向日葵+康乃馨教师节花束', '向日葵搭配康乃馨，表达对老师的感恩与祝福。', 139.00, 119.00, 200, 88, '/images/products/teacher_day.jpg', '["/images/products/teacher_day_1.jpg"]', 0, 0, 0, 1, '教师节,送老师', '感恩，尊敬，祝福', '教师节'),
(3, '红色康乃馨花束 "敬爱"', '红色康乃馨火热祝福', '红色康乃馨代表热烈的爱，送给敬爱的长辈。', 129.00, 109.00, 180, 76, '/images/products/carnation_red.jpg', '["/images/products/carnation_red_1.jpg"]', 0, 0, 0, 1, '送长辈,送母亲', '健康，长寿，敬爱', '生日,探望,节日'),
(5, '彩色郁金香混搭 "彩虹"', '各色郁金香缤纷组合', '多种颜色郁金香混搭，色彩缤纷，如同彩虹般美丽。', 259.00, 229.00, 80, 65, '/images/products/tulip_mix.jpg', '["/images/products/tulip_mix_1.jpg"]', 0, 0, 0, 1, '送朋友,送女友', '多彩人生，美丽心情', '生日,派对,庆典'),
(6, '绣球花束 "团圆"', '蓝色绣球花簇拥', '蓝色绣球花团锦簇，寓意团圆美满，送给亲朋好友。', 179.00, 159.00, 90, 77, '/images/products/hydrangea.jpg', '["/images/products/hydrangea_1.jpg"]', 0, 0, 0, 1, '送朋友,乔迁', '团圆，美满，感恩', '乔迁,聚会,感谢'),
(2, '多头百合花束 "清香"', '多头百合清雅宜人', '多头百合花束，清香四溢，花形优美，提升居家格调。', 159.00, 139.00, 140, 94, '/images/products/multi_lily.jpg', '["/images/products/multi_lily_1.jpg"]', 0, 0, 0, 1, '送长辈,送母亲', '百年好合，幸福美满', '婚礼,乔迁,看望');

-- 花语知识库数据
INSERT INTO `flower_language` (`flower_name`, `alias_name`, `flower_language`, `meaning`, `color_meaning`, `send_scene`, `taboo`, `match_flowers`, `maintenance`) VALUES
('红玫瑰', '玫瑰', '热情的爱，我爱你，每一天', '红玫瑰是爱情的象征，代表热烈而深厚的爱意。古罗马神话中，红玫瑰与爱神维纳斯紧密相连。', '{"红色":"热情的爱","粉色":"初恋与感动","白色":"纯洁与高贵","黄色":"友谊与祝福","蓝色":"奇迹与珍贵","紫色":"优雅与神秘","香槟色":"一生只爱一人"}', '["表白适合送红玫瑰或粉玫瑰","求婚送99朵红玫瑰代表长久","结婚纪念日送香槟玫瑰","道歉送白玫瑰请求原谅","生日送粉色玫瑰温馨甜美"]', '不要送黄色玫瑰给恋人（代表分手）;不要送倒刺的玫瑰;送玫瑰前记得去掉大部分刺', '玫瑰喜温暖，花瓶水位1/3，每天换水剪根，避免阳光直射，可加鲜花保鲜剂延长花期', '["百合","满天星","尤加利叶","勿忘我"]'),
('百合', '白百合、香水百合', '纯洁，庄严，百年好合', '百合花象征纯洁与高雅，在中国文化中代表＂百年好合＂，多用于婚礼和祝福。', '{"白色":"纯洁与庄严","粉色":"富贵与吉祥","黄色":"感激与快乐","橙色":"激情与能量"}', '["婚礼送白色百合象征纯洁","母亲节送粉色百合表达温馨","探望病人送百合祈愿康复","乔迁送百合祝福新家美好"]', '百合花粉易染色，送人前最好去掉花蕊; 卧室不要放太多百合，香气浓郁影响睡眠', '百合花瓶水位2/3，去花蕊可延长花期，喜阴凉环境，定期换水', '["玫瑰","康乃馨","满天星"]'),
('康乃馨', '香石竹', '母爱，健康，感恩', '康乃馨是母爱的象征，送给母亲最合适。不同颜色代表不同的情感。', '{"粉色":"母爱的温暖","红色":"热烈的爱","白色":"纯洁的爱","黄色":"感激与道歉","紫色":"善变与任性"}', '["母亲节送粉色康乃馨最经典","妈妈生日送红色康乃馨","探望长辈送康乃馨表达祝福","教师节送康乃馨感谢师恩"]', '不要送白色康乃馨给病人（在中国白色多用于丧事）;黄康乃馨代表失望，慎送', '康乃馨喜凉爽，花瓶水位1/2，勤换水剪根，避免阳光直射', '["百合","满天星","勿忘我"]'),
('向日葵', '太阳花', '信念，光辉，忠诚，沉默的爱', '向日葵象征着阳光与希望，永远追随太阳的精神令人感动。', '{"黄色":"阳光与活力","橙色":"热情与能量","红色":"忠诚与热爱"}', '["毕业送向日葵祝福前程似锦","开业送向日葵象征蒸蒸日上","考试成功送向日葵庆祝","朋友心情不好送向日葵带来阳光"]', '向日葵花粉较多，对花粉过敏者慎送; 不宜与香味过浓的花搭配', '向日葵喜水，花瓶水位2/3，每天换水剪根，花期约7-10天', '["雏菊","黄莺","尤加利叶"]'),
('郁金香', '洋荷花', '完美的爱情，永恒的爱', '郁金香是荷兰国花，代表高贵与优雅，不同颜色有不同的含义。', '{"红色":"爱的告白","粉色":"幸福与爱恋","紫色":"永恒的爱","白色":"纯洁与纯情","黄色":"阳光与开朗","彩色":"多彩人生"}', '["约会送粉色郁金香表达爱恋","结婚纪念日送红色郁金香","生日送彩色郁金香寓意多彩人生","春游送郁金香感受春日浪漫"]', '郁金香部分品种有毒，不可食用; 不宜放在卧室', '郁金香喜凉，花瓶水位1/3，勿暴晒，花期约5-7天', '["玫瑰","洋桔梗","银叶菊"]'),
('满天星', '霞草', '配角，纯洁的心灵，思念', '满天星小巧玲珑，常作为配花使用，寓意甘愿做配角守护你。', '{"白色":"纯洁与思念","粉色":"浪漫与甜蜜","蓝色":"梦幻与忧郁"}', '["搭配玫瑰送女友增加浪漫感","单独送表示暗恋与守护","搭配百合送给长辈"]', '满天星做成干花后也很有美感，但鲜切花需要勤换水', '满天星喜水，花瓶水位1/2，花期较长约10-14天', '["玫瑰","百合","康乃馨","桔梗"]'),
('绣球花', '八仙花', '团圆，美满，感恩', '绣球花花团锦簇，圆圆满满，是送给亲朋好友的佳选。', '{"蓝色":"冷静与美满","粉色":"浪漫与甜蜜","紫色":"优雅与神秘","白色":"纯洁与希望"}', '["乔迁送绣球花寓意团圆美满","毕业送绣球花祝福前程","感恩朋友送绣球表达感谢"]', '绣球花对水质要求高，需要经常换水; 部分品种有毒需注意', '绣球花喜水，花瓶水位满，可整枝浸泡，花期约5-8天', '["玫瑰","桔梗","尤加利叶"]'),
('洋桔梗', '草原龙胆', '不变的爱，真诚，感动', '洋桔梗花形优美，色彩丰富，代表永恒不变的爱。', '{"紫色":"永恒的爱","粉色":"真诚与感动","白色":"纯洁与美好","绿色":"希望与自由"}', '["生日送洋桔梗表达真诚祝福","纪念日送紫色洋桔梗","搭配玫瑰花束增加层次感"]', '洋桔梗枝干较脆，运输时易折断', '洋桔梗喜水，花瓶水位1/2，勤换水，花期约7-10天', '["玫瑰","绣球","尤加利叶"]'),
('勿忘我', '星辰花', '永不变心，永恒的记忆', '勿忘我是爱情的见证，寓意请别忘记我，真诚的爱。', '{"紫色":"永恒的爱","粉色":"浪漫与甜蜜","蓝色":"深刻的记忆"}', '["毕业送勿忘我让友谊长存","异地恋送勿忘我表达思念","搭配康乃馨送给母亲"]', '勿忘我通常做干花保存，鲜切花水养也很持久', '勿忘我做干花需倒挂晾干; 水养花瓶水位1/3，花期可达2周以上', '["玫瑰","康乃馨","满天星"]'),
('雏菊', '马兰花', '快乐，幸福，天真', '雏菊代表天真快乐，是祝福朋友的好选择。', '{"白色":"天真与快乐","粉色":"幸福与甜蜜","黄色":"阳光与活力"}', '["送朋友表达快乐祝福","送同学寓意友谊长存","搭配向日葵做阳光花束"]', '雏菊花期较短，需勤换水', '雏菊喜光但怕暴晒，花瓶水位1/2，每天换水', '["向日葵","满天星","勿忘我"]');

-- 测试收货地址
INSERT INTO `address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`, `label`) VALUES
(1000, '小花', '13800138000', '广东省', '深圳市', '南山区', '科技园南区A栋1201室', 1, '家'),
(1000, '小花', '13800138000', '广东省', '深圳市', '福田区', '华强北电子世界3楼', 0, '公司'),
(1001, '张三', '13900139000', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城', 1, '家'),
(1002, '李四', '13700137000', '上海市', '上海市', '浦东新区', '陆家嘴金融中心写字楼A座', 1, '公司');

-- 测试订单
INSERT INTO `orders` (`order_no`, `user_id`, `address_id`, `total_amount`, `discount_amount`, `pay_amount`, `status`, `receiver_name`, `receiver_phone`, `receiver_address`, `pay_time`, `delivery_time`, `created_at`) VALUES
('20250101001', 1000, 1, 499.00, 50.00, 449.00, 3, '小花', '13800138000', '广东省深圳市南山区科技园南区A栋1201室', '2025-01-01 10:30:00', '2025-01-02 14:00:00', '2025-01-01 10:00:00'),
('20250115002', 1000, 1, 299.00, 0.00, 299.00, 1, '小花', '13800138000', '广东省深圳市南山区科技园南区A栋1201室', '2025-01-15 14:20:00', NULL, '2025-01-15 14:00:00'),
('20250120003', 1000, 2, 139.00, 10.00, 129.00, 0, '小花', '13800138000', '广东省深圳市福田区华强北电子世界3楼', NULL, NULL, '2025-01-20 16:00:00'),
('20250201004', 1001, 3, 599.00, 100.00, 499.00, 4, '张三', '13900139000', '北京市朝阳区建国路88号SOHO现代城', NULL, NULL, '2025-02-01 09:00:00'),
('20250301005', 1002, 4, 229.00, 0.00, 229.00, 2, '李四', '13700137000', '上海市浦东新区陆家嘴金融中心写字楼A座', '2025-03-01 11:00:00', '2025-03-02 10:00:00', '2025-03-01 10:30:00');

-- 测试订单商品
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, '红玫瑰花束 "心动"', '/images/products/red_rose_99.jpg', 499.00, 1, 499.00),
(2, 2, '粉色玫瑰花束 "初恋"', '/images/products/pink_rose_33.jpg', 299.00, 1, 299.00),
(3, 26, '红玫瑰花束 "热恋"', '/images/products/red_rose_11.jpg', 139.00, 1, 139.00),
(4, 1, '红玫瑰花束 "心动"', '/images/products/red_rose_99.jpg', 599.00, 1, 599.00),
(5, 11, '郁金香花束 "春日浪漫"', '/images/products/tulip_dutch.jpg', 229.00, 1, 229.00);

-- 测试购物车
INSERT INTO `cart` (`user_id`, `product_id`, `quantity`, `selected`) VALUES
(1000, 3, 2, 1),
(1000, 5, 1, 1),
(1000, 9, 3, 0),
(1001, 1, 1, 1),
(1002, 11, 2, 1);

-- 测试收藏
INSERT INTO `favorite` (`user_id`, `product_id`) VALUES
(1000, 1),
(1000, 4),
(1000, 9),
(1001, 2),
(1002, 6);