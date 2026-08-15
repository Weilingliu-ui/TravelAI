# TravelAI — AI 智能旅游攻略平台

> 🌍 输入目的地、天数和预算，AI 智能生成你的专属旅行计划。短途行程数十秒即可完成，长途行程支持分段生成，实测 8 天行程约 221 秒。

TravelAI 是一个基于 **DeepSeek 大模型** 的智能旅游规划系统，采用前后端分离架构，支持用户注册登录、AI 行程生成、旅游攻略管理、景点浏览与收藏等核心功能。

---

## 📁 项目结构

本项目采用前后端分离的架构，代码分为两个独立仓库：

```
TravelAI/                     # 后端 (Java Spring Boot)
├── src/main/java/...         # 核心业务代码
├── src/main/resources/       # 配置文件 & 数据库脚本
└── pom.xml                   # Maven 构建配置

travelai-web/                 # 前端 (Vue 3)
├── src/
│   ├── api/                  # API 接口封装
│   ├── views/                # 页面组件
│   ├── layouts/              # 布局组件
│   ├── router/               # 路由配置
│   └── stores/               # Pinia 状态管理
├── index.html
└── vite.config.js
```

---

## 🚀 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.5.7 | 核心框架 |
| Spring Security | — | JWT 无状态认证 |
| Spring AI (OpenAI) | 1.0.0-M6 | 对接 DeepSeek 大模型 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 主数据库 |
| Redis | — | 缓存 & 会话 |
| MinIO | 8.5.7 | 对象存储（图片/文件） |
| JWT | 0.12.6 | Token 认证 |
| Hutool | 5.8.34 | 工具库 |
| Lombok | — | 代码简化 |
| SpringDoc | 2.8.6 | Swagger API 文档 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.41 | 渐进式框架 |
| Vite | 5.4.21 | 构建工具 |
| Vue Router | 4.6.4 | 路由管理 |
| Pinia | 4.0.2 | 状态管理 |
| Element Plus | 2.14.3 | UI 组件库 |
| Axios | 1.19.0 | HTTP 客户端 |

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                      用户浏览器                           │
│                  (Vue 3 + Element Plus)                  │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP / Axios
                      ▼
┌─────────────────────────────────────────────────────────┐
│                   Nginx (反向代理)                        │
│           静态资源托管  +  API 请求转发                    │
└─────────────────────┬───────────────────────────────────┘
                      │ /api/*
                      ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot (Java 17)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────────┐    │
│  │ Security │ │   JWT    │ │   AI Service          │    │
│  │  Filter  │ │  Auth    │ │  (DeepSeek API)       │    │
│  └──────────┘ └──────────┘ └──────────────────────┘    │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────────┐    │
│  │MyBatis-  │ │  Redis   │ │   MinIO              │    │
│  │  Plus    │ │  Cache   │ │  (Object Storage)    │    │
│  └──────────┘ └──────────┘ └──────────────────────┘    │
└───────┬──────────────┬────────────────┬─────────────────┘
        │              │                │
        ▼              ▼                ▼
   ┌─────────┐   ┌─────────┐    ┌──────────┐
   │  MySQL  │   │  Redis  │    │  MinIO   │
   │  8.0+   │   │  6.0+   │    │  对象存储  │
   └─────────┘   └─────────┘    └──────────┘
```

---

## ✨ 核心功能

### 1. AI 智能行程规划
- 用户输入目的地、天数、预算、偏好，AI 自动生成详细的旅游攻略
- 基于景点数据库进行候选推荐，结合 DeepSeek 大模型生成个性化行程
- 支持多种出行场景：穷游省钱、亲子出游、情侣浪漫、摄影打卡、美食之旅
- 支持长途分段生成：8 天以上行程自动拆分，逐段稳定生成，避免大模型长文本输出截断
- 生成结果包含每日行程、时间节点、费用预估、旅行贴士、结构化 JSON 输出（完整的时间线、活动安排与预算明细，可直接用于前端渲染）

### 2. 用户认证与画像
- JWT 无状态认证，支持注册 / 登录 / 登出
- 用户画像系统记录旅行偏好（风格、预算、饮食、标签等）
- 预填出发城市，提升体验

### 3. 旅游攻略管理
- 我的攻略列表：查看所有 AI 生成的行程
- 攻略详情：按日查看完整行程安排
- 支持对攻略计划、每日行程、行程节点的增删改查

### 4. 基础数据浏览
- 省份 / 城市 / 景点层级数据
- 热门景点推荐（首页展示）
- 景点详情页（含评分、分类、访问量等）

### 5. 收藏与评论
- 收藏喜欢的攻略或景点
- 对景点发表评论

### 6. Prompt 模板管理
- 可配置的 AI Prompt 模板（系统提示 / 用户提示 / 少样本示例）
- 支持攻略生成、路线规划、预算分析、推荐等多场景模板
- 版本化管理，支持热切换

---

## 📋 数据库设计

核心表结构：

| 表名 | 说明 |
|------|------|
| `province` | 省份/州基础数据 |
| `city` | 城市基础数据（含热门标记、经纬度） |
| `attraction` | 景点数据（含门票、开放时间、评分、标签） |
| `scenic_tag` / `attraction_tag` | 景点标签体系 |
| `user` | 用户表（JWT + BCrypt 密码） |
| `user_profile` | 用户画像（JSON 存储偏好） |
| `travel_plan` | 旅游攻略计划（AI 生成结果） |
| `travel_day` | 每日行程 |
| `travel_route` | 行程节点（景点/餐饮/住宿/交通） |
| `favorite` | 收藏记录 |
| `comment` | 评论 |
| `prompt_template` | AI Prompt 模板 |
| `travel_knowledge` | 旅游知识库（RAG 数据源，预留） |
| `travel_template` | 攻略模板库（优秀范例，预留） |

> 完整建表脚本见 `TravelAI/src/main/resources/db/schema.sql`

---

## 🔧 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- MinIO（可选，用于文件上传）

### 1. 初始化数据库

```bash
mysql -u root -p < TravelAI/src/main/resources/db/schema.sql
```

### 2. 启动后端

```bash
cd TravelAI

# 方式一：直接运行
./mvnw spring-boot:run

# 方式二：打包后运行
./mvnw clean package
java -jar target/TravelAI-0.0.1-SNAPSHOT.jar
```

后端默认运行在 `http://localhost:8080`

API 文档：`http://localhost:8080/swagger-ui.html`

#### 后端环境变量（可选）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | localhost:3306/travel_ai | MySQL 连接 |
| `DB_USERNAME` / `DB_PASSWORD` | root / 101208 | 数据库账号 |
| `REDIS_HOST` / `REDIS_PORT` | localhost:6379 | Redis 连接 |
| `DEEPSEEK_API_KEY` | sk-... | DeepSeek API Key |
| `DEEPSEEK_MODEL` | deepseek-v4-pro | 模型名称 |
| `MINIO_ENDPOINT` | http://localhost:9000 | MinIO 地址 |
| `SERVER_PORT` | 8080 | 服务端口 |
| `JWT_SECRET` | (内置) | JWT 签名密钥 |

### 3. 启动前端

```bash
cd travelai-web
npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`，已配置代理将 `/api` 转发到后端 `8080` 端口。

---

## 🔐 权限设计

| 接口路径 | 权限要求 |
|----------|----------|
| `/api/auth/**` | 匿名访问 |
| `/api/provinces/**`, `/api/cities/**`, `/api/attractions/**`, `/api/files/**` | 匿名访问 |
| `/api/users/**`, `/api/travel-plans/**`, `/api/favorites/**`, `/api/comments/**` | 需 JWT 认证 |
| `/api/**` | 默认需认证 |
| Swagger 文档 | 匿名访问 |

---

## 🌐 API 概览

### 认证接口
- `POST /api/auth/register` — 注册
- `POST /api/auth/login` — 登录

### AI 规划
- `POST /api/ai/generate` — 生成攻略并保存（AI 调用，耗时 30~90s）
- `POST /api/ai/travel/debug` — 调试：查看候选景点和 Prompt 预览

### 基础数据
- `GET /api/provinces` — 省份列表
- `GET /api/cities` — 城市列表
- `GET /api/attractions` — 景点列表
- `GET /api/attractions/hot` — 热门景点
- `GET /api/attractions/{id}` — 景点详情

### 攻略管理
- `GET /api/travel-plans` — 我的攻略列表
- `GET /api/travel-plans/{id}` — 攻略详情
- `POST /api/travel-plans` — 创建攻略
- `GET /api/travel-plans/{planId}/days` — 每日行程
- `GET /api/travel-plans/{planId}/days/{dayId}/routes` — 行程节点

### 其他
- `GET /api/favorites` — 收藏列表
- `POST /api/comments` — 发表评论
- `POST /api/files/upload` — 文件上传

> 完整 API 文档启动后端后访问 `http://localhost:8080/swagger-ui.html`

---

## 📂 前端路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 热门景点展示、Hero 引导 |
| `/login` | 登录/注册 | 用户认证 |
| `/ai-planner` | AI 行程规划 | 填写偏好，AI 生成行程 |
| `/my-plans` | 我的攻略 | 查看已生成的行程列表 |
| `/attraction/:id` | 景点详情 | 景点信息展示 |

---

## 🛠️ 开发说明

### 后端关键配置

- `application.yml` 中已配置 Spring AI 对接 DeepSeek（OpenAI 兼容格式）
- AI 生成接口超时设置为 **300 秒**，以应对大模型响应耗时
- MyBatis-Plus 启用逻辑删除（`deleted` 字段）和驼峰映射
- JWT 过滤器已集成到 Spring Security 过滤链

### 前端代理配置

`vite.config.js` 中已配置开发代理：

```js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

### AI 生成重试机制

前端 `AiPlanner.vue` 中对 AI 生成接口实现了自动重试（最多 3 次），仅在遇到 5xx 或网络错误时重试，避免重复提交。

---

## 🗺️ 未来规划

- [ ] 集成地图服务，可视化行程路线
- [ ] 向量数据库 + RAG，提升 AI 回答准确度
- [ ] 攻略分享社区，支持公开攻略浏览与点赞
- [ ] 天气 API 集成，动态调整行程建议
- [ ] 多模型支持（GPT-4、Claude、通义千问等）

---

## 🌟 项目亮点

- **结构化 AI 输出**：行程以结构化 JSON 形式输出，包含完整的时间线、活动安排与预算明细，前端可直接渲染，区别于市面上纯文本输出的方案
- **长途分段生成**：8 天以上行程自动拆分、逐段生成，有效避免大模型长文本输出截断问题，实测 8 天行程 221 秒稳定完成
- **Prompt 模板引擎**：可配置、可热切换的 AI Prompt 模板体系，支持攻略生成、路线规划、预算分析等多场景，版本化管理便于迭代优化
- **前后端分离 + 生产级配置**：Vue 3 + Spring Boot 标准分离架构，JWT 无状态认证，AI 接口 300 秒超时 + Nginx 层同步配置，可应对生产级大模型调用延迟
- **完整数据体系**：省份 → 城市 → 景点三级基础数据，搭配标签体系与用户画像，为 AI 生成提供结构化知识支撑

---

## 📄 License

MIT

---

> 本项目为学习与实践项目，AI 行程生成基于 DeepSeek 大语言模型，生成结果仅供参考，实际出行请结合当地实时信息。
