-- ============================================================
-- TravelAI - AI旅游攻略Agent 数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 引擎: InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS travel_ai
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE travel_ai;

-- ============================================================
-- 1. 基础地理数据
-- ============================================================

-- ----------------------------
-- 省份/州
-- ----------------------------
DROP TABLE IF EXISTS province;
CREATE TABLE province (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(64)     NOT NULL                 COMMENT '省份名称（中文）',
    name_en     VARCHAR(128)    DEFAULT NULL             COMMENT '省份名称（英文）',
    code        VARCHAR(16)     DEFAULT NULL             COMMENT '行政区划代码',
    region      VARCHAR(32)     DEFAULT NULL             COMMENT '所属大区（华东/华南/华北/华中/西南/西北/东北）',
    sort_order  INT             DEFAULT 0                COMMENT '排序权重',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_region (region)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='省份/州表';

-- ----------------------------
-- 城市
-- ----------------------------
DROP TABLE IF EXISTS city;
CREATE TABLE city (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(64)     NOT NULL                 COMMENT '城市名称（中文）',
    name_en     VARCHAR(128)    DEFAULT NULL             COMMENT '城市名称（英文/拼音）',
    province_id BIGINT          NOT NULL                 COMMENT '所属省份ID',
    latitude    DECIMAL(10,7)   DEFAULT NULL             COMMENT '纬度',
    longitude   DECIMAL(10,7)   DEFAULT NULL             COMMENT '经度',
    level       VARCHAR(16)     DEFAULT NULL             COMMENT '城市等级（一线/新一线/二线/三线/其他）',
    is_hot      TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否热门目的地 0=否 1=是',
    is_coastal  TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否沿海城市 0=否 1=是',
    image_url   VARCHAR(512)    DEFAULT NULL             COMMENT '城市封面图URL',
    description TEXT            DEFAULT NULL             COMMENT '城市简介（AI生成用上下文）',
    sort_order  INT             DEFAULT 0                COMMENT '排序权重（热门推荐排序）',
    status      TINYINT         NOT NULL DEFAULT 1       COMMENT '状态 0=禁用 1=启用',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name_province (name, province_id),
    KEY idx_province_id (province_id),
    KEY idx_is_hot (is_hot),
    KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='城市表';

-- ----------------------------
-- 景点标签
-- ----------------------------
DROP TABLE IF EXISTS scenic_tag;
CREATE TABLE scenic_tag (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name        VARCHAR(64)     NOT NULL                 COMMENT '标签名称',
    type        VARCHAR(32)     NOT NULL DEFAULT 'other' COMMENT '标签分类（nature=自然风光 / culture=人文历史 / food=美食 / shopping=购物 / activity=体验活动 / family=亲子 / couple=情侣 / photography=摄影 / other=其他）',
    icon        VARCHAR(255)    DEFAULT NULL             COMMENT '标签图标URL',
    sort_order  INT             DEFAULT 0                COMMENT '排序权重',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点标签表';

-- ----------------------------
-- 景点
-- ----------------------------
DROP TABLE IF EXISTS attraction;
CREATE TABLE attraction (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(128)    NOT NULL                 COMMENT '景点名称',
    name_en         VARCHAR(256)    DEFAULT NULL             COMMENT '景点名称（英文/拼音）',
    city_id         BIGINT          NOT NULL                 COMMENT '所属城市ID',
    category        VARCHAR(32)     NOT NULL DEFAULT 'other' COMMENT '景点分类（同scenic_tag.type，冗余便于查询）',
    description     TEXT            DEFAULT NULL             COMMENT '景点描述（丰富的文本，供AI理解）',
    address         VARCHAR(512)    DEFAULT NULL             COMMENT '详细地址',
    latitude        DECIMAL(10,7)   DEFAULT NULL             COMMENT '纬度',
    longitude       DECIMAL(10,7)   DEFAULT NULL             COMMENT '经度',
    ticket_price    DECIMAL(10,2)   DEFAULT NULL             COMMENT '门票价格（元）',
    ticket_desc     VARCHAR(255)    DEFAULT NULL             COMMENT '门票说明（如"学生半价""周一闭馆"）',
    opening_hours   VARCHAR(256)    DEFAULT NULL             COMMENT '开放时间描述（如"08:30-17:00"）',
    duration        INT             DEFAULT NULL             COMMENT '建议游览时长（分钟）',
    rating          DECIMAL(3,2)    DEFAULT NULL             COMMENT '评分（0.00-5.00）',
    image_url       VARCHAR(512)    DEFAULT NULL             COMMENT '景点封面图URL',
    images          JSON            DEFAULT NULL             COMMENT '景点图片集 JSON: ["url1","url2"]',
    tips            TEXT            DEFAULT NULL             COMMENT '游览贴士（AI生成上下文用）',
    is_must_visit   TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否必游景点 0=否 1=是',
    is_free         TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否免费 0=否 1=是',
    is_indoor       TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否室内景点（雨天备选） 0=否 1=是',
    season_best     VARCHAR(64)     DEFAULT NULL             COMMENT '最佳游览季节（spring/summer/autumn/winter/all）',
    visit_count     BIGINT          NOT NULL DEFAULT 0       COMMENT '虚拟访问量（热度排序用）',
    sort_order      INT             DEFAULT 0                COMMENT '排序权重',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态 0=禁用 1=启用',
    version         INT             NOT NULL DEFAULT 1       COMMENT '乐观锁版本号',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除 0=未删除 1=已删除',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_city_id (city_id),
    KEY idx_category (category),
    KEY idx_rating (rating),
    KEY idx_is_must_visit (is_must_visit),
    KEY idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- ----------------------------
-- 景点-标签关联（多对多）
-- ----------------------------
DROP TABLE IF EXISTS attraction_tag;
CREATE TABLE attraction_tag (
    id              BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    attraction_id   BIGINT      NOT NULL                 COMMENT '景点ID',
    tag_id          BIGINT      NOT NULL                 COMMENT '标签ID',
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_attraction_tag (attraction_id, tag_id),
    KEY idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点-标签关联表';

-- ============================================================
-- 2. 用户系统
-- ============================================================

-- ----------------------------
-- 用户
-- ----------------------------
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    username        VARCHAR(64)     NOT NULL                 COMMENT '用户名（登录用）',
    password_hash   VARCHAR(256)    NOT NULL                 COMMENT '密码哈希（BCrypt）',
    email           VARCHAR(128)    DEFAULT NULL             COMMENT '邮箱',
    phone           VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    avatar_url      VARCHAR(512)    DEFAULT NULL             COMMENT '头像URL',
    role            VARCHAR(32)     NOT NULL DEFAULT 'USER'  COMMENT '角色（USER=普通用户 / ADMIN=管理员）',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态 0=禁用 1=正常 2=未激活',
    last_login_at   DATETIME        DEFAULT NULL             COMMENT '最后登录时间',
    last_login_ip   VARCHAR(64)     DEFAULT NULL             COMMENT '最后登录IP',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除 0=未删除 1=已删除',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '注册时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    KEY idx_phone (phone),
    KEY idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 用户画像（旅行偏好）
-- ----------------------------
DROP TABLE IF EXISTS user_profile;
CREATE TABLE user_profile (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id             BIGINT          NOT NULL                 COMMENT '用户ID（一对一）',
    nickname            VARCHAR(64)     DEFAULT NULL             COMMENT '昵称',
    gender              TINYINT         DEFAULT NULL             COMMENT '性别 0=未知 1=男 2=女',
    birth_year          INT             DEFAULT NULL             COMMENT '出生年份',
    home_city           VARCHAR(64)     DEFAULT NULL             COMMENT '常住城市',
    travel_preference   JSON            DEFAULT NULL             COMMENT '旅行偏好 JSON: {"style":"文化深度","pace":"轻松","interests":["历史","美食"],"transport":["高铁","自驾"],"accommodation":"舒适型","companion":"家庭","budget_level":"中档"}',
    budget_preference   JSON            DEFAULT NULL             COMMENT '预算偏好 JSON: {"total_range":[3000,8000],"per_day_range":[500,1500],"allocate":{"accommodation":0.35,"food":0.25,"transport":0.20,"tickets":0.15,"other":0.05}}',
    food_preference     JSON            DEFAULT NULL             COMMENT '饮食偏好 JSON: {"cuisines":["川菜","日料"],"avoid":["香菜"],"budget_per_meal":[50,200]}',
    tags                JSON            DEFAULT NULL             COMMENT '用户标签 JSON: ["亲子","自驾游","摄影爱好者","美食探索"]',
    total_trips         INT             NOT NULL DEFAULT 0       COMMENT '累计生成攻略次数',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户画像表';

-- ============================================================
-- 3. 攻略系统
-- ============================================================

-- ----------------------------
-- 旅游攻略计划
-- ----------------------------
DROP TABLE IF EXISTS travel_plan;
CREATE TABLE travel_plan (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id             BIGINT          NOT NULL                 COMMENT '用户ID',
    title               VARCHAR(256)    NOT NULL                 COMMENT '攻略标题（AI自动生成或用户自定义）',
    destination_city_id BIGINT          NOT NULL                 COMMENT '目标城市ID',
    origin_city         VARCHAR(128)    DEFAULT NULL             COMMENT '出发城市名（用户自由输入）',
    days                INT             NOT NULL                 COMMENT '出行天数',
    start_date          DATE            DEFAULT NULL             COMMENT '计划出发日期',
    budget_total        DECIMAL(12,2)   NOT NULL                 COMMENT '总预算（元）',
    budget_breakdown    JSON            DEFAULT NULL             COMMENT '预算明细 JSON: {"accommodation":1500,"food":800,"transport":600,"tickets":400,"shopping":500,"other":200}',
    travel_preference   JSON            DEFAULT NULL             COMMENT '本次旅行偏好（本次请求的快照，独立于user_profile）',
    travel_style        VARCHAR(32)     DEFAULT NULL             COMMENT '旅行风格（文化深度/休闲度假/美食探索/户外冒险/亲子同乐/情侣浪漫/穷游背包/奢华享受）',
    season              VARCHAR(16)     DEFAULT NULL             COMMENT '季节（spring/summer/autumn/winter）',
    companion           VARCHAR(32)     DEFAULT NULL             COMMENT '同行人员（solo/couple/family/friends/group）',
    overview            TEXT            DEFAULT NULL             COMMENT '攻略概览（AI生成的综述文案）',
    overall_suggestions TEXT            DEFAULT NULL             COMMENT '总体建议（AI生成的注意事项/打包清单/安全提示）',
    ai_model            VARCHAR(64)     DEFAULT NULL             COMMENT '使用的AI模型（如 deepseek-chat / deepseek-reasoner）',
    prompt_version      VARCHAR(32)     DEFAULT NULL             COMMENT '使用的Prompt模板版本（关联prompt_template.name+version）',
    ai_tokens_used      INT             DEFAULT NULL             COMMENT 'AI消耗Token数',
    ai_cost             DECIMAL(10,4)   DEFAULT NULL             COMMENT 'AI调用费用（元）',
    total_estimated_cost DECIMAL(12,2)  DEFAULT NULL             COMMENT '预估总花费（系统计算）',
    is_public           TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否公开分享 0=仅自己 1=公开',
    view_count          BIGINT          NOT NULL DEFAULT 0       COMMENT '浏览次数',
    favorite_count      BIGINT          NOT NULL DEFAULT 0       COMMENT '被收藏次数（冗余计数）',
    status              VARCHAR(32)     NOT NULL DEFAULT 'draft' COMMENT '状态（draft=草稿 / generating=生成中 / completed=已完成 / failed=生成失败 / archived=已归档）',
    version             INT             NOT NULL DEFAULT 1       COMMENT '乐观锁版本号',
    deleted             TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除 0=未删除 1=已删除',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_destination_city (destination_city_id),
    KEY idx_status (status),
    KEY idx_is_public (is_public),
    KEY idx_created_at (created_at),
    KEY idx_user_status_deleted (user_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游攻略计划表';

-- ----------------------------
-- 每日行程
-- ----------------------------
DROP TABLE IF EXISTS travel_day;
CREATE TABLE travel_day (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    plan_id         BIGINT          NOT NULL                 COMMENT '攻略计划ID',
    day_number      INT             NOT NULL                 COMMENT '第几天（从1开始）',
    date_label      VARCHAR(64)     DEFAULT NULL             COMMENT '日期描述（如"Day 1 / 7月15日 周一"）',
    title           VARCHAR(256)    DEFAULT NULL             COMMENT '当日主题（如"初识北京·历史探索"）',
    description     TEXT            DEFAULT NULL             COMMENT '当日概述（AI生成的引言文案）',
    weather_advice  VARCHAR(256)    DEFAULT NULL             COMMENT '天气建议（预留，可集成天气API）',
    accommodation   VARCHAR(512)    DEFAULT NULL             COMMENT '住宿建议/推荐',
    accommodation_cost DECIMAL(10,2) DEFAULT NULL            COMMENT '预估住宿费用（元）',
    estimated_cost  DECIMAL(10,2)   DEFAULT NULL             COMMENT '当日预估总费用（元）',
    sort_order      INT             NOT NULL DEFAULT 0       COMMENT '排序（=day_number，冗余）',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_plan_day (plan_id, day_number),
    KEY idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日行程表';

-- ----------------------------
-- 行程节点（每日路线中的具体景点/餐饮/活动）
-- ----------------------------
DROP TABLE IF EXISTS travel_route;
CREATE TABLE travel_route (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    plan_id             BIGINT          NOT NULL                 COMMENT '攻略计划ID（冗余，便于跨天查询）',
    day_id              BIGINT          NOT NULL                 COMMENT '所属行程日ID',
    sort_order          INT             NOT NULL DEFAULT 0       COMMENT '当日顺序号（1=第一站...）',
    node_type           VARCHAR(32)     NOT NULL DEFAULT 'attraction' COMMENT '节点类型（attraction=景点 / restaurant=餐饮 / hotel=住宿 / transport=交通 / activity=活动 / custom=自定义）',
    attraction_id       BIGINT          DEFAULT NULL             COMMENT '关联景点ID（node_type=attraction时有值）',
    custom_name         VARCHAR(256)    DEFAULT NULL             COMMENT '自定义节点名称（非景点类或AI自由发挥项）',
    custom_description  TEXT            DEFAULT NULL             COMMENT '自定义节点描述',
    address             VARCHAR(512)    DEFAULT NULL             COMMENT '节点地址',
    latitude            DECIMAL(10,7)   DEFAULT NULL             COMMENT '纬度',
    longitude           DECIMAL(10,7)   DEFAULT NULL             COMMENT '经度',
    start_time          TIME            DEFAULT NULL             COMMENT '建议开始时间（如"09:00"）',
    end_time            TIME            DEFAULT NULL             COMMENT '建议结束时间（如"11:30"）',
    duration_minutes    INT             DEFAULT NULL             COMMENT '建议停留时长（分钟）',
    transport_from_prev VARCHAR(32)     DEFAULT NULL             COMMENT '从上一节点到此节点的交通方式（walking/driving/transit/taxi/subway/bicycle）',
    transport_duration  INT             DEFAULT NULL             COMMENT '从上一节点到此节点的交通耗时（分钟）',
    transport_distance  INT             DEFAULT NULL             COMMENT '从上一节点到此节点的距离（米）',
    estimated_cost      DECIMAL(10,2)   DEFAULT NULL             COMMENT '该节点预估费用（门票+餐饮等）',
    tips                TEXT            DEFAULT NULL             COMMENT 'AI生成的节点小贴士',
    image_url           VARCHAR(512)    DEFAULT NULL             COMMENT '节点配图URL',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_day_id (day_id),
    KEY idx_plan_id (plan_id),
    KEY idx_attraction_id (attraction_id),
    KEY idx_node_type (node_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程节点表';

-- ============================================================
-- 4. 收藏系统
-- ============================================================

-- ----------------------------
-- 收藏
-- ----------------------------
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    user_id     BIGINT          NOT NULL                 COMMENT '用户ID',
    item_type   VARCHAR(32)     NOT NULL                 COMMENT '收藏类型（plan=攻略 / attraction=景点 / route=路线 / knowledge=知识）',
    item_id     BIGINT          NOT NULL                 COMMENT '收藏对象ID',
    note        VARCHAR(512)    DEFAULT NULL             COMMENT '收藏备注',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '收藏时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_item (user_id, item_type, item_id),
    KEY idx_user_id (user_id),
    KEY idx_item_type_id (item_type, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================================
-- 5. AI 相关
-- ============================================================

-- ----------------------------
-- Prompt 模板
-- ----------------------------
DROP TABLE IF EXISTS prompt_template;
CREATE TABLE prompt_template (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(128)    NOT NULL                 COMMENT '模板名称（唯一标识，如"travel_plan_system_v1"）',
    display_name    VARCHAR(256)    DEFAULT NULL             COMMENT '模板显示名称（中文）',
    template_type   VARCHAR(32)     NOT NULL DEFAULT 'system' COMMENT '模板类型（system=系统提示 / user=用户提示 / few_shot=少样本示例）',
    scene           VARCHAR(64)     NOT NULL                 COMMENT '适用场景（travel_plan=攻略生成 / route_plan=路线规划 / budget_analysis=预算分析 / recommend=推荐 / overview=综述 / knowledge_query=知识查询）',
    content         TEXT            NOT NULL                 COMMENT 'Prompt模板内容（支持变量占位符 {{variable_name}}）',
    variables       JSON            DEFAULT NULL             COMMENT '模板变量定义 JSON: [{"name":"destination","type":"string","required":true,"description":"目的地"},...]',
    model           VARCHAR(64)     DEFAULT 'deepseek-chat'  COMMENT '推荐模型',
    temperature     DECIMAL(3,2)    DEFAULT 0.7              COMMENT '推荐温度参数',
    max_tokens      INT             DEFAULT 4096              COMMENT '最大输出Token数',
    version         INT             NOT NULL DEFAULT 1       COMMENT '模板版本号',
    is_active       TINYINT(1)      NOT NULL DEFAULT 1       COMMENT '是否启用 0=禁用 1=启用',
    is_default      TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否为该场景的默认模板',
    remark          VARCHAR(512)    DEFAULT NULL             COMMENT '备注说明',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name_version (name, version),
    KEY idx_scene (scene),
    KEY idx_is_active (is_active),
    KEY idx_scene_default (scene, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Prompt模板表';

-- ----------------------------
-- 旅游知识库（RAG数据源）
-- ----------------------------
DROP TABLE IF EXISTS travel_knowledge;
CREATE TABLE travel_knowledge (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    title           VARCHAR(256)    NOT NULL                 COMMENT '知识标题',
    content         MEDIUMTEXT      NOT NULL                 COMMENT '知识正文（会被向量化或全文检索）',
    summary         VARCHAR(1024)   DEFAULT NULL             COMMENT '知识摘要（简短摘要用于检索预览）',
    category        VARCHAR(64)     NOT NULL                 COMMENT '知识分类（city_guide=城市攻略 / attraction_detail=景点详情 / food_culture=饮食文化 / transport_tips=交通贴士 / travel_tips=旅行贴士 / local_custom=当地风俗 / weather=天气建议 / budget_ref=预算参考 / safety=安全须知 / visa=签证信息）',
    city_id         BIGINT          DEFAULT NULL             COMMENT '关联城市ID（可空=通用知识）',
    attraction_id   BIGINT          DEFAULT NULL             COMMENT '关联景点ID（可空=非景点知识）',
    tags            JSON            DEFAULT NULL             COMMENT '知识标签 JSON: ["亲子友好","网红打卡","省钱技巧"]',
    source_type     VARCHAR(64)     DEFAULT NULL             COMMENT '来源类型（official=官方 / ugc=用户生成 / ai=AI生成 / crawled=爬取 / manual=人工录入）',
    source          VARCHAR(256)    DEFAULT NULL             COMMENT '知识来源名称（如"Wikipedia""马蜂窝""官方旅游局"）',
    source_url      VARCHAR(1024)   DEFAULT NULL             COMMENT '来源URL',
    embedding_id    VARCHAR(128)    DEFAULT NULL             COMMENT '向量ID（外部向量数据库引用，如Milvus/Pinecone）',
    reliability     TINYINT         NOT NULL DEFAULT 3       COMMENT '可靠度（1-5，5=官方认证）',
    use_count       BIGINT          NOT NULL DEFAULT 0       COMMENT '被引用次数（热度排序）',
    effective_start DATE            DEFAULT NULL             COMMENT '生效开始日期（季节性知识用）',
    effective_end   DATE            DEFAULT NULL             COMMENT '生效结束日期（季节性知识用）',
    version         INT             NOT NULL DEFAULT 1       COMMENT '版本号',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态 0=草稿 1=已发布 2=过期',
    deleted         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category),
    KEY idx_city_id (city_id),
    KEY idx_attraction_id (attraction_id),
    KEY idx_status_deleted (status, deleted),
    KEY idx_embedding_id (embedding_id),
    FULLTEXT KEY ft_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游知识库表（RAG数据源）';

-- ----------------------------
-- 攻略模板库（优秀旅游攻略模板，供AI生成时参考）
-- ----------------------------
DROP TABLE IF EXISTS travel_template;
CREATE TABLE travel_template (
    id                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name                VARCHAR(256)    NOT NULL                 COMMENT '模板名称',
    destination_city_id BIGINT          NOT NULL                 COMMENT '目的地城市ID',
    title               VARCHAR(256)    NOT NULL                 COMMENT '攻略标题',
    description         TEXT            DEFAULT NULL             COMMENT '模板描述/亮点说明',
    days                INT             NOT NULL                 COMMENT '天数',
    travel_style        VARCHAR(32)     DEFAULT NULL             COMMENT '旅行风格（文化深度/休闲度假/美食探索/户外冒险/亲子同乐/情侣浪漫/穷游背包/奢华享受）',
    season              VARCHAR(16)     DEFAULT NULL             COMMENT '适用季节（spring/summer/autumn/winter/all）',
    budget_level        VARCHAR(16)     DEFAULT NULL             COMMENT '预算级别（budget=经济 / moderate=适中 / luxury=奢华）',
    budget_reference    DECIMAL(12,2)   DEFAULT NULL             COMMENT '参考预算（人均/元）',
    companion           VARCHAR(32)     DEFAULT NULL             COMMENT '适合同行（solo/couple/family/friends/group/all）',
    itinerary           JSON            NOT NULL                 COMMENT '行程模板 JSON: {"days":[{"day":1,"title":"...","routes":[{"name":"...","type":"attraction",...}]}]}',
    tags                JSON            DEFAULT NULL             COMMENT '模板标签 JSON: ["经典路线","适合首次打卡","网红拍照"]',
    overview            TEXT            DEFAULT NULL             COMMENT '攻略综述文案（参考用）',
    cover_image         VARCHAR(512)    DEFAULT NULL             COMMENT '封面图URL',
    use_count           BIGINT          NOT NULL DEFAULT 0       COMMENT '被引用次数',
    like_count          BIGINT          NOT NULL DEFAULT 0       COMMENT '点赞数',
    is_official         TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否官方推荐 0=否 1=是',
    sort_order          INT             DEFAULT 0                COMMENT '排序权重',
    status              TINYINT         NOT NULL DEFAULT 1       COMMENT '状态 0=下架 1=上架 2=草稿',
    deleted             TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除 0=未删除 1=已删除',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_destination_city (destination_city_id),
    KEY idx_travel_style (travel_style),
    KEY idx_season (season),
    KEY idx_budget_level (budget_level),
    KEY idx_days (days),
    KEY idx_status_deleted (status, deleted),
    KEY idx_is_official (is_official)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='攻略模板库表（优秀攻略范例，供AI参考）';

-- ============================================================
-- 初始化数据：插入默认 Prompt 模板
-- ============================================================
INSERT INTO prompt_template (name, display_name, template_type, scene, content, variables, is_default) VALUES
(
    'travel_plan_system_v1',
    '攻略生成系统提示 v1',
    'system',
    'travel_plan',
    '你是一位资深的旅游规划专家。请根据以下用户需求生成一份详细的旅游攻略。\n\n用户需求：\n- 目的地：{{destination}}\n- 出发地：{{origin}}\n- 出行天数：{{days}}天\n- 预算：{{budget}}元\n- 旅行偏好：{{preferences}}\n- 出行季节：{{season}}\n- 同行人员：{{companion}}\n\n要求：\n1. 生成{{days}}天的详细行程安排\n2. 每天包含3-5个景点/活动\n3. 提供交通方式建议\n4. 提供餐饮推荐\n5. 预算分配合理\n6. 考虑景点开放时间和季节因素\n7. 提供实用的旅行贴士',
    '[{"name":"destination","type":"string","required":true,"description":"目的地城市"},{"name":"origin","type":"string","required":true,"description":"出发地"},{"name":"days","type":"number","required":true,"description":"出行天数"},{"name":"budget","type":"number","required":true,"description":"总预算（元）"},{"name":"preferences","type":"string","required":false,"description":"旅行偏好"},{"name":"season","type":"string","required":false,"description":"出行季节"},{"name":"companion","type":"string","required":false,"description":"同行人员"}]',
    1
),
(
    'route_plan_system_v1',
    '路线规划系统提示 v1',
    'system',
    'route_plan',
    '你是一位专业的路线规划师。请根据以下信息规划{{days}}天的{{destination}}旅行路线。\n\n已知景点列表：\n{{attractions}}\n\n要求：\n1. 每天路线合理，避免来回折返\n2. 考虑景点间距离和交通时间\n3. 上午安排大型景点，下午安排轻松活动\n4. 每天总游览时间控制在6-8小时\n5. 标注每段交通方式和耗时',
    '[{"name":"destination","type":"string","required":true,"description":"目的地"},{"name":"days","type":"number","required":true,"description":"天数"},{"name":"attractions","type":"string","required":true,"description":"景点列表JSON"}]',
    1
),
(
    'budget_analysis_system_v1',
    '预算分析系统提示 v1',
    'system',
    'budget_analysis',
    '你是一位专业的旅行预算分析师。请分析以下旅行计划并生成预算明细。\n\n旅行信息：\n- 目的地：{{destination}}\n- 天数：{{days}}\n- 总预算：{{budget}}元\n- 行程摘要：{{itinerary_summary}}\n\n要求：\n1. 按类别分解预算（住宿/餐饮/交通/门票/购物/其他）\n2. 给出每日预算建议\n3. 指出可以节省的开支项\n4. 给出省钱建议\n5. 输出JSON格式的预算明细',
    '[{"name":"destination","type":"string","required":true,"description":"目的地"},{"name":"days","type":"number","required":true,"description":"天数"},{"name":"budget","type":"number","required":true,"description":"总预算"},{"name":"itinerary_summary","type":"string","required":true,"description":"行程摘要"}]',
    1
),
(
    'recommend_system_v1',
    '推荐系统提示 v1',
    'system',
    'recommend',
    '你是一位旅游推荐专家。根据用户偏好从候选列表中推荐最合适的{{item_type}}。\n\n用户偏好：{{preferences}}\n候选列表：\n{{candidates}}\n\n要求：\n1. 每个推荐附带推荐理由\n2. 按匹配度降序排列\n3. 考虑用户预算约束\n4. 考虑季节适宜性\n5. 输出JSON格式，最多推荐{{top_n}}个',
    '[{"name":"item_type","type":"string","required":true,"description":"推荐类型（attraction/food/hotel/activity）"},{"name":"preferences","type":"string","required":true,"description":"用户偏好JSON"},{"name":"candidates","type":"string","required":true,"description":"候选列表JSON"},{"name":"top_n","type":"number","required":false,"description":"推荐数量上限"}]',
    1
);
