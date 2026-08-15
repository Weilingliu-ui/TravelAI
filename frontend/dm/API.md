# TravelAI 后端 API 文档

> Base URL: `http://localhost:8080`
> Swagger: `http://localhost:8080/swagger-ui/index.html`

---

## 通用规范

### 统一返回格式

```typescript
// 成功
{ "code": 200, "message": "操作成功", "data": T, "timestamp": 1786007309269 }

// 失败
{ "code": 非200, "message": "错误描述", "timestamp": 1786007309269 }
```

### 状态码

| code | 说明 | 前端处理 |
|------|------|----------|
| 200 | 成功 | — |
| 400 | 参数校验失败 | toast message |
| 401 | 未登录 / Token 过期 | 跳转登录页 |
| 403 | 无权操作 | toast "无权操作" |
| 500 | 服务器异常 | toast message |
| 1003 | 资源不存在 | toast message |
| 1004 | 数据重复 | toast message |
| 3001 | 用户不存在 | toast "用户名或密码错误" |
| 3002 | 密码错误 | toast "用户名或密码错误" |

### 认证方式

登录后将 token 存入 localStorage，所有需认证的请求携带 Header：

```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySW...
```

前端拦截器示例：

```typescript
// request interceptor
const token = localStorage.getItem('token')
if (token) {
  config.headers.Authorization = `Bearer ${token}`
}

// response interceptor
if (response.data.code === 401) {
  localStorage.removeItem('token')
  router.push('/login')
}
```

---

## 一、认证模块

### 1.1 注册

```
POST /api/auth/register
```

| 参数 | 类型 | 必填 | 校验 |
|------|------|:--:|------|
| username | string | ✅ | 3-64 位 |
| password | string | ✅ | 6-128 位 |
| email | string | ✅ | 合法邮箱 |

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"123456","email":"user@test.com"}'
```

**响应**：

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "userId": 5,
    "username": "newuser"
  }
}
```

### 1.2 登录

```
POST /api/auth/login

Body:
  username  string  必填
  password  string  必填
```

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'
```

**响应**：

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "userId": 2,
    "username": "zhangsan"
  }
}
```

**错误**：

| 场景 | code | message |
|------|------|---------|
| 用户不存在 | 3001 | 用户名或密码错误 |
| 密码错误 | 3002 | 用户名或密码错误 |
| 用户被禁用 | 3003 | 用户已被禁用 |

---

## 二、用户画像

> 需要登录

### 2.1 获取我的画像

```
GET /api/profile
```

**响应**：

```json
{
  "code": 200,
  "data": {
    "userId": 2,
    "username": "zhangsan",
    "homeCity": "北京"
  }
}
```

> `homeCity` 为 `null` 时表示用户尚未设置出发城市。

### 2.2 更新出发城市

```
PUT /api/profile/home-city

Body:
{
  "homeCity": "北京"
}
```

**响应**：`{ "code": 200, "data": "ok" }`

> AI 生成攻略时，若前端未传 `origin` 参数，后端自动从此画像中读取 `homeCity` 作为出发城市。

---

## 三、首页 & 景点浏览

> 全部无需登录

### 2.1 省份列表

```
GET /api/provinces
```

**响应**：

```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "浙江省", "nameEn": "Zhejiang", "code": "330000", "region": "华东" }
  ]
}
```

### 2.2 城市列表

```
GET /api/cities
```

**响应**：

```json
{
  "code": 200,
  "data": [
    {
      "id": 1, "name": "Hangzhou", "nameEn": "Hangzhou",
      "provinceId": 11, "latitude": 30.2741, "longitude": 120.1551,
      "level": "新一线", "isHot": 1, "isCoastal": 0,
      "imageUrl": null, "description": null, "sortOrder": 0, "status": 1
    }
  ]
}
```

### 2.3 景点分页查询

```
GET /api/attractions?pageNum=1&pageSize=10&cityId=&keyword=&tag=&sortBy=popularity
```

| 参数 | 类型 | 默认 | 说明 |
|------|------|------|------|
| pageNum | int | 1 | 页码 |
| pageSize | int | 10 | 每页数量 |
| cityId | Long | — | 城市筛选 |
| keyword | string | — | 名称+描述模糊搜索 |
| tag | string | — | 分类标签 |
| sortBy | string | popularity | `popularity` / `rating` / `latest` |

```bash
# 杭州热门景点
curl "http://localhost:8080/api/attractions?cityId=1&sortBy=rating&pageSize=5"

# 搜索 "temple"
curl "http://localhost:8080/api/attractions?keyword=temple&pageSize=5"
```

**响应**：

```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "name": "West Lake",
        "nameEn": null,
        "cityId": 1,
        "category": "nature",
        "description": "Famous UNESCO World Heritage site...",
        "address": "Hangzhou, Zhejiang",
        "latitude": null,
        "longitude": null,
        "ticketPrice": 0.00,
        "ticketDesc": null,
        "openingHours": "Open 24 hours",
        "duration": 180,
        "rating": 4.80,
        "imageUrl": null,
        "coverImage": null,
        "tips": null,
        "isMustVisit": 1,
        "isFree": 1,
        "isIndoor": 0,
        "seasonBest": null,
        "visitCount": 10000,
        "createdAt": "2026-08-05T23:08:00"
      }
    ],
    "total": 8,
    "size": 5,
    "current": 1,
    "pages": 2
  }
}
```

### 2.4 热门景点

```
GET /api/attractions/hot?limit=5
```

```bash
curl "http://localhost:8080/api/attractions/hot?limit=6"
```

**响应**：`{ "code": 200, "data": [ Attraction, ... ] }`（数组，按 visitCount 倒序）

### 2.5 搜索建议

```
GET /api/attractions/search?keyword=West
```

**响应**：`{ "code": 200, "data": [ Attraction, ... ] }`（最多 10 条）

### 2.6 景点详情

```
GET /api/attractions/{id}
```

```bash
curl http://localhost:8080/api/attractions/1
```

**响应**：`{ "code": 200, "data": Attraction }`

---

## 四、AI 行程规划

> 需要登录

### 3.1 生成攻略（核心接口）

```
POST /api/ai/generate
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:--:|------|
| destination | string | ✅ | 目的地名称（如 "杭州"） |
| days | int | ✅ | 天数，≥1 |
| budget | number | ✅ | 总预算(元)，≥0 |
| travelers | int | ✅ | 出行人数，≥1 |
| origin | string | — | 出发城市，不传则自动从用户画像读取 |
| interests | string | — | 兴趣偏好，多选后空格分隔，如 "nature culture food" |
| travelMode | string | — | driving(自驾) / transit(公共交通) / walking(步行) / mixed(混合) |
| scene | string | — | travel_system(默认) / budget_trip(穷游) / family_trip(亲子) / couple_trip(情侣) / photography_trip(摄影) / food_trip(美食) |

```bash
curl -X POST http://localhost:8080/api/ai/generate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "destination": "杭州",
    "days": 3,
    "budget": 3000,
    "travelers": 2,
    "interests": "nature culture food",
    "travelMode": "driving",
    "scene": "family_trip"
  }'
```

**响应**：

```json
{ "code": 200, "data": 15 }
```

`data` = **planId**，前端拿到后跳转到攻略详情页 `/plan/15`

**注意**：此接口调用 DeepSeek，耗时约 5-15 秒，前端需显示 loading 状态。

### 3.2 调试接口（开发用）

```
POST /api/ai/travel/debug
```

参数同 3.1。返回候选景点 + Prompt 预览，不调用 AI。

```json
{
  "code": 200,
  "data": {
    "candidates": [ AiAttractionCandidate, ... ],
    "candidateCount": 8,
    "promptPreview": "你是一位资深旅游规划师..."
  }
}
```

---

## 五、我的攻略

> 需要登录

### 4.1 攻略列表

```
GET /api/travel-plans
```

**响应**：`{ "code": 200, "data": [ TravelPlan, ... ] }`

> 仅返回当前登录用户的攻略

### 4.2 攻略详情

```
GET /api/travel-plans/{planId}
```

### 4.3 每日行程

```
GET /api/travel-plans/{planId}/days
```

**响应**：

```json
{
  "code": 200,
  "data": [
    {
      "id": 1, "planId": 13, "dayNumber": 1,
      "dateLabel": null, "title": "西湖环湖经典游",
      "description": "上午从断桥开始漫步白堤...",
      "weatherAdvice": null,
      "accommodation": "西湖附近酒店",
      "accommodationCost": 450.00,
      "estimatedCost": 860.00,
      "sortOrder": 1,
      "createdAt": "..."
    }
  ]
}
```

### 4.4 行程节点（路线）

```
GET /api/travel-plans/{planId}/days/{dayId}/routes
```

**响应**：

```json
{
  "code": 200,
  "data": [
    {
      "id": 1, "planId": 13, "dayId": 1, "sortOrder": 1,
      "nodeType": "attraction",
      "attractionId": 1,
      "customName": "West Lake",
      "customDescription": "Famous UNESCO site...",
      "address": null,
      "latitude": null, "longitude": null,
      "startTime": "09:30", "endTime": "11:30",
      "durationMinutes": 120,
      "transportFromPrev": "walking",
      "transportDuration": 15,
      "transportDistance": null,
      "estimatedCost": 0.00,
      "tips": "Morning light is best",
      "imageUrl": null
    }
  ]
}
```

### 4.5 手动创建攻略（非 AI）

```
POST /api/travel-plans

Body:
{
  "title": "我的杭州游",
  "destinationCityId": 1,
  "originCity": "上海",
  "days": 3,
  "budgetTotal": 3000.00,
  "travelStyle": "culture",
  "season": "spring",
  "companion": "family",
  "status": "draft"
}
```

> userId 自动绑定当前用户，前端无需传

```
PUT    /api/travel-plans          → 更新攻略
DELETE /api/travel-plans/{planId} → 删除（逻辑删除）

POST   /api/travel-plans/{planId}/days             → 添加行程日
PUT    /api/travel-plans/{planId}/days              → 更新
DELETE /api/travel-plans/{planId}/days/{dayId}      → 删除

POST   /api/travel-plans/{planId}/days/{dayId}/routes  → 添加节点
PUT    .../routes                                      → 更新
DELETE .../routes/{routeId}                            → 删除
```

---

## 六、收藏

> 需要登录

### 5.1 我的收藏列表

```
GET /api/favorites
```

**响应**：`{ "code": 200, "data": [ TravelPlan, ... ] }`

### 5.2 收藏 / 取消

```
POST   /api/favorites/{planId}    → 收藏（幂等：重复收藏不报错）
DELETE /api/favorites/{planId}    → 取消收藏
```

### 5.3 收藏状态

```
GET /api/favorites/{planId}/status
```

**响应**：`{ "code": 200, "data": true }`

---

## 七、评论

> 需要登录

### 6.1 景点评论列表

```
GET /api/comments/attraction/{attractionId}?page=1&size=10
```

**响应**：

```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 3, "userId": 2, "nickname": "zhangsan",
        "attractionId": 1,
        "content": "Beautiful lake, great for photos!",
        "rating": 5,
        "createdAt": "2026-08-06T17:08:29"
      }
    ],
    "total": 3,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 6.2 发表评论

```
POST /api/comments

Body:
  attractionId  Long     必填
  content       string   必填, 1-500 字
  rating        int      选填, 1-5, 默认 5
```

### 6.3 删除评论

```
DELETE /api/comments/{id}
```

> 只能删除自己的评论

---

## 八、文件上传

> 无需登录

```
POST /api/files/upload
Content-Type: multipart/form-data
Form Key: file
```

```html
<input type="file" onChange="upload" />

<script>
async function upload(e) {
  const form = new FormData()
  form.append('file', e.target.files[0])
  const res = await fetch('/api/files/upload', { method: 'POST', body: form })
  const { data } = await res.json()
  // data.url = "http://localhost:9000/travel/travel/uuid.jpg"
}
</script>
```

**响应**：

```json
{ "code": 200, "data": { "url": "http://localhost:9000/travel/travel/abc123.jpg" } }
```

---

## 九、数据类型参考

### Attraction（景点）

```typescript
interface Attraction {
  id: number
  name: string
  nameEn?: string
  cityId: number
  category: string           // nature / culture / food / shopping / activity / family
  description: string
  address: string
  latitude: number
  longitude: number
  ticketPrice: number
  openingHours: string       // "Open 24 hours" / "08:00-17:30"
  duration: number           // 建议游览时长（分钟）
  rating: number             // 0.00-5.00
  imageUrl?: string
  coverImage?: string
  tips?: string
  isMustVisit: number        // 0/1
  isFree: number             // 0/1
  isIndoor: number           // 0/1
  visitCount: number         // 热度
}
```

### TravelPlan（攻略）

```typescript
interface TravelPlan {
  id: number
  userId: number
  title: string
  destinationCityId: number
  originCity?: string
  days: number
  budgetTotal: number
  budgetBreakdown?: string    // JSON
  travelStyle?: string
  overview?: string
  overallSuggestions?: string
  status: string              // draft / completed / failed
  viewCount: number
  favoriteCount: number
  createdAt: string
}
```

### TravelDay（行程日）

```typescript
interface TravelDay {
  id: number
  planId: number
  dayNumber: number
  title?: string
  description?: string
  accommodation?: string
  accommodationCost?: number
  estimatedCost?: number
  routes?: TravelRoute[]
}
```

### TravelRoute（行程节点）

```typescript
interface TravelRoute {
  id: number
  planId: number
  dayId: number
  sortOrder: number
  nodeType: string            // attraction / restaurant / hotel / transport / activity / custom
  attractionId?: number
  customName?: string
  customDescription?: string
  startTime?: string          // "09:00"
  endTime?: string            // "11:30"
  durationMinutes?: number
  transportFromPrev?: string  // walking / driving / transit / taxi / subway / bicycle
  transportDuration?: number
  estimatedCost?: number
  tips?: string
  imageUrl?: string
}
```

### CommentVO（评论）

```typescript
interface CommentVO {
  id: number
  userId: number
  nickname: string
  attractionId: number
  content: string
  rating: number             // 1-5
  createdAt: string
}
```

---

## 十、前端页面建议

### 页面路由

```
/                         首页（热门景点 + 搜索）
/login                    登录注册
/plan/:id                 攻略详情（每日行程 + 路线 + 地图）
/plans                    我的攻略列表
/ai-generate              AI 生成攻略（输入表单 + 结果展示）
/favorites                我的收藏
```

### 关键交互流程

```
AI 生成攻略:
  首页 → 输入目的地/天数/预算/偏好 → 点击生成
  → 前端 loading 状态（5-15s）
  → 拿到 planId → 跳转 /plan/:id

攻略详情页:
  planId → GET /travel-plans/:id (攻略基本信息)
  → GET /travel-plans/:id/days (每日行程)
  → GET /travel-plans/:id/days/:dayId/routes (每个day的路线)
  → 渲染时间线 UI

收藏:
  攻略详情页 → 点击收藏按钮
  → POST /favorites/:planId
  → 按钮状态切换
```

### Swagger 地址

```
http://localhost:8080/swagger-ui/index.html
```

全部接口可在线调试。

---

> 有问题随时沟通。
