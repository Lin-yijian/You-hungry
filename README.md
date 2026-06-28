# 🍔 You-Hungry（饿了吧外卖）

> 学校食堂智能外卖配送系统 —— Spring Boot + Vue + 微信小程序

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-2.x-4FC08D)](https://vuejs.org/)
[![JDK](https://img.shields.io/badge/JDK-17%2B-orange)](https://adoptium.net/)

## 📖 项目简介

You-Hungry 是一套专为**学校食堂**设计的智能外卖配送平台。学生通过**微信小程序**浏览菜单、下单支付；食堂管理员通过 **Web 管理后台**管理菜品、处理订单、查看数据报表；同时内置 **AI 智能助理**，支持自然语言查询经营数据。

### ✨ 核心功能

| 端 | 模块 | 功能 |
|---|---|---|
| 🛒 **微信小程序** | 菜品浏览 | 按分类浏览菜品/套餐，选择口味加入购物车 |
| | 下单支付 | 选择收货地址，微信支付下单 |
| | 订单管理 | 查看订单状态，催单，再来一单 |
| | 个人信息 | 管理收货地址，查看历史订单 |
| 🖥️ **Web 管理后台** | 工作台 | 今日经营概览（营业额、订单数、新增用户） |
| | 数据统计 | 营业额/用户/订单趋势图，销量Top10，Excel导出 |
| | 订单管理 | 搜索订单，接单/拒单/派送/完成 |
| | 菜品管理 | 菜品CRUD，含口味配置（辣度、温度等），批量操作 |
| | 套餐管理 | 套餐CRUD，配置套餐内菜品及份数 |
| | 分类管理 | 菜品/套餐分类管理 |
| | 员工管理 | 管理员账号CRUD |
| | 店铺启停 | 一键开启/关闭营业状态 |
| 🤖 **AI 智能助理** | 经营查询 | 自然语言查询营业额、订单、用户等数据 |
| | 订单分析 | 搜索订单，查看订单详情 |
| | 智能建议 | 基于数据的经营分析和异常检测 |
| ⚡ **自动化** | 超时取消 | 未支付订单15分钟后自动取消 |
| | 自动完成 | 配送中订单凌晨自动完成 |
| | 实时推送 | WebSocket 推送新订单通知 |

---

## 🛠 技术栈

### 后端

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 2.7.3 | 核心框架 |
| MyBatis | 2.2.0 | ORM 框架 |
| MyBatis PageHelper | 1.3.0 | 分页插件 |
| MySQL | 8.0+ | 关系数据库 |
| Druid | 1.2.1 | 数据库连接池 |
| Redis | 6.0+ | 缓存（菜品列表、店铺状态） |
| Spring Cache | — | 注解缓存（套餐数据） |
| JWT (jjwt) | 0.9.1 | 双端认证（admin/user 独立密钥） |
| Knife4j (Swagger) | 3.0.2 | API 文档生成 |
| 阿里云 OSS SDK | 3.10.2 | 图片/文件云存储 |
| 微信支付 SDK | 0.4.8 | 微信 JSAPI 支付 |
| WebSocket | — | 新订单实时通知 |
| Apache POI | 3.16 | Excel 报表导出 |
| AspectJ | 1.9.4 | AOP（自动填充时间/操作人） |
| FastJSON | 1.2.76 | JSON 序列化 |
| Lombok | 1.18.30 | 简化 POJO 代码 |
| DeepSeek API | — | AI 大模型（tool-use 模式） |

### 前端

| 端 | 技术栈 | 说明 |
|---|---|---|
| 管理后台 | Vue 2 + Element UI + Webpack | SPA 单页应用，暖食主题 |
| AI 助理 | Vue 2 + Element UI（CDN） | SSE 流式聊天，嵌入管理后台 |
| 用户端 | uni-app → 微信小程序 | 编译输出至 `mp-weixin/` |

---

## 📁 项目结构

```
you-hungry/
│
├── yh-common/                          # 公共模块
│   └── src/main/java/com/yh/
│       ├── constant/                   # 常量（消息文本、状态码、JWT字段名）
│       ├── context/BaseContext.java    # ThreadLocal 用户上下文
│       ├── enumeration/                # 枚举（INSERT/UPDATE）
│       ├── exception/                  # 12个业务异常类
│       ├── json/                       # Jackson 序列化配置
│       ├── properties/                 # 配置属性类（JWT、OSS、微信、AI）
│       ├── result/                     # 统一响应 Result<T> / PageResult
│       └── utils/                      # 工具类（JWT、OSS、HttpClient、微信支付）
│
├── yh-pojo/                            # 数据模型模块
│   └── src/main/java/com/yh/
│       ├── entity/                     # 实体类（13个，含AI对话/消息）
│       ├── dto/                        # 请求 DTO（含AI聊天请求）
│       └── vo/                         # 响应 VO（含AI对话/消息视图）
│
├── yh-server/                          # Spring Boot 主应用
│   ├── src/main/java/com/yh/
│   │   ├── SkyApplication.java        # 启动类
│   │   ├── ai/                         # 🤖 AI 助理模块
│   │   │   ├── controller/            # AiAssistantController
│   │   │   ├── service/               # AiAssistantService + Impl
│   │   │   └── tool/                  # @AiTool 注解 + 注册中心 + 8个工具
│   │   ├── annotation/                # @AutoFill 注解
│   │   ├── aspect/                    # AOP 切面
│   │   ├── config/                    # Spring 配置（MVC、OSS、Redis、WebSocket）
│   │   ├── controller/
│   │   │   ├── admin/                 # 管理端 API（9个控制器）
│   │   │   ├── user/                  # 用户端 API（8个控制器）
│   │   │   └── notify/               # 微信支付回调
│   │   ├── handler/                   # 全局异常处理
│   │   ├── interceptor/               # JWT 拦截器（admin/user 双拦截器）
│   │   ├── mapper/                    # MyBatis 接口（14个，含AI）
│   │   ├── service/                   # 业务接口 + impl
│   │   ├── task/                      # 定时任务（超时取消、自动完成）
│   │   └── webSocket/                 # WebSocket 服务端
│   └── src/main/resources/
│       ├── application.yml            # 主配置
│       ├── application-dev.yml        # 开发环境配置
│       └── mapper/                    # MyBatis XML（14个）
│
├── html/                               # 管理后台前端
│   └── sky/
│       ├── index.html                 # SPA 入口（含主题覆盖 + 侧边栏注入）
│       ├── ai-assistant.html          # AI 助理聊天页面
│       ├── css/                       # 编译样式（已覆盖为暖食主题）
│       ├── js/                        # 编译 JS（app + chunk-vendors）
│       ├── fonts/                     # Element UI 图标字体
│       └── img/                       # 图片资源（Logo、图标）
│
├── mp-weixin/                          # 微信小程序
│   ├── app.json                       # 小程序配置
│   ├── app.js / app.wxss              # 应用入口
│   ├── pages/                         # 12个页面
│   │   ├── index/                     # 首页（菜品浏览）
│   │   ├── order/                     # 下单确认
│   │   ├── details/                   # 订单详情
│   │   ├── pay/                       # 支付页
│   │   ├── success/                   # 支付成功
│   │   ├── address/                   # 地址列表
│   │   ├── addOrEditAddress/          # 新增/编辑地址
│   │   ├── historyOrder/              # 历史订单
│   │   ├── my/                        # 我的
│   │   ├── remark/                    # 订单备注
│   │   └── nonet/                     # 网络异常
│   ├── components/                    # 通用组件（uni-ui）
│   ├── common/                        # 公共 JS/CSS
│   └── static/                        # 静态图片资源
│
├── sql/                                # 数据库脚本
│   └── ai_tables.sql                  # AI 对话/消息建表 SQL
│
└── pom.xml                             # Maven 父 POM（多模块）
```

---

## 🚀 快速开始

### 环境要求

| 依赖 | 版本 | 说明 |
|---|---|---|
| JDK | 17+（推荐21） | Java运行环境 |
| Maven | 3.6+ | 项目构建 |
| MySQL | 8.0+ | 数据库，端口 3306 |
| Redis | 6.0+ | 缓存，端口 6379 |
| Nginx | 1.20+ | 反向代理 + 静态文件 |
| 微信开发者工具 | 最新版 | 小程序调试 |

### 1. 克隆与导入

```bash
git clone <your-repo-url>
cd you-hungry
```

### 2. 数据库准备

创建数据库并执行建表脚本：

```sql
CREATE DATABASE IF NOT EXISTS sky_take_out DEFAULT CHARACTER SET utf8mb4;
```

```bash
# 执行 AI 功能表
mysql -u root -p sky_take_out < sql/ai_tables.sql
```

> 业务表（employee、dish、orders等）需从现有数据库导出或根据实体类自行建表。

### 3. 修改配置文件

编辑 `yh-server/src/main/resources/application-dev.yml`：

```yaml
sky:
  datasource:
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: 你的数据库密码          # ← 修改
  
  redis:
    host: localhost
    port: 6379
  
  ai:
    api-key: sk-你的DeepSeek密钥        # ← 修改，从 platform.deepseek.com 获取
  
  # 以下为可选配置（OSS、微信支付）
  alioss:
    access-key-id: 你的OSS Key
    access-key-secret: 你的OSS Secret
  wechat:
    appid: 你的小程序AppID
    secret: 你的小程序Secret
```

### 4. 构建并启动后端

```bash
# 编译打包（首次需 install 安装本地模块）
mvn clean install -DskipTests

# 启动
java -jar yh-server/target/sky-server-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

启动成功标志：
```
Tomcat started on port(s): 8080 (http)
AI 工具扫描完成，共 10 个工具
server started
```

### 5. 配置并启动 Nginx

Nginx 配置文件位于 `D:\APP\nginx\nginx-1.20.2\conf\nginx.conf`：

```nginx
server {
    listen 8085;
    
    location / {
        root html/sky;          # 管理后台静态文件
        index index.html;
    }
    
    location /api/ai/chat {     # AI SSE 流式端点
        proxy_pass http://127.0.0.1:8080/admin/ai/chat;
        proxy_buffering off;    # 必须关闭，否则 SSE 不工作
        proxy_read_timeout 3600s;
    }
    
    location /api/ {            # 管理端 API
        proxy_pass http://127.0.0.1:8080/admin/;
    }
    
    location /user/ {           # 用户端 API
        proxy_pass http://127.0.0.1:8080/user/;
    }
    
    location /ws/ {             # WebSocket
        proxy_pass http://127.0.0.1:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

```bash
# 启动 Nginx
cd D:/APP/nginx/nginx-1.20.2
start nginx.exe
```

### 6. 访问

| 入口 | 地址 | 说明 |
|---|---|---|
| 🖥️ 管理后台 | http://localhost:8085 | 管理员登录 |
| 🤖 AI 助理 | http://localhost:8085/ai-assistant.html | 独立访问 |
| 📘 API 文档 | http://localhost:8080/doc.html | Swagger/Knife4j |
| 📱 小程序 | 微信开发者工具打开 `mp-weixin/` | 需勾选不校验域名 |

### 7. 运行微信小程序

1. 打开**微信开发者工具**
2. 导入项目 → 选择 `mp-weixin/` 目录（AppID: `wx761c9d3d65a6f6c3`）
3. 右上角 **详情** → **本地设置** → 勾选：
   - ✅ **不校验合法域名、web-view、TLS版本以及HTTPS证书**
4. 模拟器中即可看到小程序界面

---

## 📡 API 接口一览

完整文档：http://localhost:8080/doc.html（启动后端后访问）

### 管理端 `/admin/**`（JWT: Header `token`）

| 控制器 | 路径 | 方法 | 说明 |
|---|---|---|---|
| EmployeeController | `/admin/employee/login` | POST | 登录（公开） |
| | `/admin/employee` | POST/PUT/GET | 员工CRUD |
| | `/admin/employee/page` | GET | 分页查询 |
| | `/admin/employee/status/{status}` | POST | 启用/禁用 |
| CategoryController | `/admin/category` | POST/PUT/DELETE | 分类CRUD |
| | `/admin/category/page` | GET | 分页查询 |
| | `/admin/category/list` | GET | 按类型列表 |
| DishController | `/admin/dish` | POST/PUT/DELETE | 菜品CRUD（含口味） |
| | `/admin/dish/page` | GET | 分页查询 |
| | `/admin/dish/status/{status}` | POST | 起售/停售 |
| SetmealController | `/admin/setmeal` | POST/PUT/DELETE | 套餐CRUD |
| | `/admin/setmeal/page` | GET | 分页查询 |
| | `/admin/setmeal/status/{status}` | POST | 启售/停售 |
| OrderController | `/admin/order/conditionSearch` | GET/POST | 订单搜索 |
| | `/admin/order/confirm` | PUT | 接单 |
| | `/admin/order/rejection` | PUT | 拒单 |
| | `/admin/order/delivery/{id}` | PUT | 派送 |
| | `/admin/order/complete/{id}` | PUT | 完成 |
| | `/admin/order/statistics` | GET | 状态统计 |
| ReportController | `/admin/report/turnoverStatistics` | GET | 营业额趋势 |
| | `/admin/report/userStatistics` | GET | 用户增长 |
| | `/admin/report/ordersStatistics` | GET | 订单趋势 |
| | `/admin/report/top10` | GET | 销量Top10 |
| | `/admin/report/export` | GET | Excel导出 |
| WorkSpaceController | `/admin/workspace/businessData` | GET | 今日概览 |
| | `/admin/workspace/overviewOrders` | GET | 订单分布 |
| ShopController | `/admin/shop/{status}` | PUT | 营业状态 |
| | `/admin/shop/status` | GET | 查看状态 |
| CommonController | `/admin/common/upload` | POST | 图片上传OSS |
| 🤖 AiAssistantController | `/admin/ai/chat` | POST | **SSE流式聊天** |
| | `/admin/ai/conversations` | GET | 对话列表 |
| | `/admin/ai/conversations/{id}` | GET/DELETE | 对话详情/删除 |
| | `/admin/ai/conversations/{id}/title` | PUT | 重命名 |

### 用户端 `/user/**`（JWT: Header `authentication`）

| 控制器 | 路径 | 说明 |
|---|---|---|
| UserController | `/user/user/login` | 微信登录（公开） |
| CategoryController | `/user/category/list` | 分类列表 |
| DishController | `/user/dish/list` | 菜品列表（Redis缓存） |
| SetmealController | `/user/setmeal/list` | 套餐列表 |
| ShoppingCartController | `/user/shoppingCart/add` | 添加购物车 |
| | `/user/shoppingCart/list` | 查看购物车 |
| | `/user/shoppingCart/clean` | 清空购物车 |
| AddressBookController | `/user/addressBook` | 地址CRUD |
| OrderController | `/user/order/submit` | 提交订单 |
| | `/user/order/payment` | 发起支付 |
| | `/user/order/historyOrders` | 历史订单 |
| | `/user/order/cancel/{id}` | 取消订单 |
| | `/user/order/reminder/{id}` | 催单 |
| ShopController | `/user/shop/status` | 营业状态（公开） |

### 支付回调 `/notify/**`（公开）

| 路径 | 说明 |
|---|---|
| `/notify/paySuccess` | 微信支付成功回调 |

---

## 🗄️ 数据库表结构

| 表名 | 实体 | 说明 |
|---|---|---|
| `employee` | Employee | 管理员账号 |
| `user` | User | 微信小程序用户 |
| `category` | Category | 菜品/套餐分类 |
| `dish` | Dish | 菜品（名称、价格、图片、状态） |
| `dish_flavor` | DishFlavor | 菜品口味（辣度、温度等） |
| `setmeal` | Setmeal | 套餐 |
| `setmeal_dish` | SetmealDish | 套餐-菜品关联 |
| `orders` | Orders | 订单（6种状态） |
| `order_detail` | OrderDetail | 订单明细 |
| `shopping_cart` | ShoppingCart | 购物车 |
| `address_book` | AddressBook | 收货地址 |
| `ai_conversation` | AiConversation | 🤖 AI 对话记录 |
| `ai_message` | AiMessage | 🤖 AI 消息记录 |

### 订单状态机

```
待付款(1) ──支付──▶ 待接单(2) ──接单──▶ 已接单(3) ──派送──▶ 派送中(4) ──送达──▶ 已完成(5)
    │                                    │
    ├──超时15分钟自动取消──▶ 已取消(6)      └──拒单──▶ 已取消(6)
    └──手动取消──▶ 已取消(6)
```

---

## 🏗️ 系统架构

```
                       ┌──────────────────────┐
                       │   微信小程序 (uni-app)  │
                       │   端口: 微信开发者工具   │
                       └──────────┬───────────┘
                                  │ HTTP
                                  ▼
┌──────────────┐     ┌──────────────────────┐     ┌──────────────────┐
│  管理后台     │────▶│  Nginx (:8085)        │────▶│  Spring Boot     │
│  (Vue.js)    │     │  ├ 静态文件 html/sky/  │     │  (:8080)         │
│              │     │  ├ /api/ → /admin/    │     │  ├ Controller    │
│  AI 助理     │     │  ├ /user/ 转发        │     │  ├ Service      │
│  (iframe)    │     │  └ /ws/ WebSocket    │     │  ├ Mapper       │
└──────────────┘     └──────────────────────┘     │  └ AI Tool系统   │
                                                  └────────┬─────────┘
                                          ┌────────────────┼────────┐
                                          ▼                ▼        ▼
                                    ┌──────────┐  ┌──────────┐  ┌──────────┐
                                    │  MySQL   │  │  Redis   │  │ DeepSeek │
                                    │  数据库   │  │  缓存     │  │  AI API  │
                                    └──────────┘  └──────────┘  └──────────┘
```

### 关键设计

| 设计点 | 说明 |
|---|---|
| **双JWT认证** | admin（密钥 `itcast`，Header `token`）和 user（密钥 `itheima`，Header `authentication`）各自独立的拦截器 |
| **AOP自动填充** | `@AutoFill(INSERT/UPDATE)` 注解自动填充 createTime/updateTime/createUser/updateUser |
| **统一响应** | `Result<T>` 包装（code=1成功，code=0失败），`PageResult` 包装分页数据 |
| **全局异常** | `@RestControllerAdvice` 统一捕获 BaseException 和 SQL 异常 |
| **Redis缓存** | 菜品列表按分类缓存（key: `dish_{categoryId}`），套餐用 `@CacheEvict` |
| **WebSocket** | `/ws/{sid}` 推送新订单通知（type=1新订单，type=2催单） |
| **定时任务** | 每分钟检查超时未付订单（15分钟自动取消），每日凌晨完成超时配送订单 |
| **AI Tool-Use** | `@AiTool` 注解标记方法 → AiToolRegistry 扫描注册 → DeepSeek 调用工具获取真实数据 |

---

## 🤖 AI 助理架构

### 核心流程

```
用户: "今天营业额多少？"
    │
    ▼
前端 AiAssistantController → AiAssistantService
    │
    ├─ 1. 加载对话历史（最近40条）
    ├─ 2. 构建 System Prompt + 10个工具定义
    ├─ 3. POST DeepSeek API (stream=true, tools=...)
    │
    ▼
DeepSeek 响应：finish_reason="tool_calls"
    │
    ├─ tool: query_turnover (参数: begin, end)
    │
    ▼
AiToolRegistry → QueryTurnoverTool → ReportService.getTurnoverStatistics()
    │
    ├─ 返回真实数据库数据
    │
    ▼
再次调用 DeepSeek（带 tool_result）
    │
    ▼
SSE 流式推送：event:text → 前端逐字显示
              event:done → 保存消息，更新对话
```

### AI 工具列表（10个）

| 工具名 | 服务 | 功能 |
|---|---|---|
| `query_turnover` | ReportService | 营业额统计 |
| `query_user_stats` | ReportService | 用户增长统计 |
| `query_order_stats` | ReportService | 订单统计 |
| `query_top10` | ReportService | 销量排行Top10 |
| `query_business_overview` | WorkspaceService | 今日经营概览 |
| `query_order_overview` | WorkspaceService | 订单状态分布 |
| `query_dish_overview` | WorkspaceService | 菜品总览 |
| `query_setmeal_overview` | WorkspaceService | 套餐总览 |
| `search_orders` | OrderService | 订单搜索 |
| `get_order_detail` | OrderService | 订单详情 |

### SSE 事件流

```
event:text         → 前端追加显示文本
event:tool_call    → 显示"正在查询xxx..."
event:tool_result  → 显示"数据就绪"
event:done         → 保存消息，结束本次对话
event:error        → 显示错误信息
```

---

## 🎨 主题配色

采用 **Warm Kitchen（暖食）** 主题：

| 角色 | 色值 | 说明 |
|---|---|---|
| 主色调 | `#E85D3A` | 暖橘红，温暖开胃 |
| 强调色 | `#F0A500` | 暖金色，精致点缀 |
| 侧边栏 | `#1B1F24` | 深炭灰，沉稳专业 |
| 导航栏 | `#FFFFFF` | 纯白 |
| 页面背景 | `#F2F0EC` | 暖米白 |
| 文字 | `#2D3436` | 深灰 |
| 成功 | `#27AE60` | 翠绿 |
| 警告 | `#F39C12` | 琥珀 |

---

## 🔑 默认登录信息

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `123456` |

---

## 🐛 常见问题

| 问题 | 解决方案 |
|---|---|
| 数据库连接失败 | 检查 `application-dev.yml` 中 `password` 是否正确，MySQL 是否启动 |
| Redis 连接失败 | 确认 Redis 在 `localhost:6379` 运行 |
| AI 回复乱码 | 确认已执行 `mvn clean install` 重新编译（UTF-8编码修复） |
| AI 连接失败 | 检查 DeepSeek API Key 是否有效，网络是否可达 |
| SSE 流不显示 | 确认 Nginx 配置了 `proxy_buffering off` |
| 图片上传失败 | 检查阿里云 OSS 配置，Bucket 是否存在 |
| 小程序报错 | 微信开发者工具 → 详情 → 勾选不校验域名 |
| 支付回调失败 | 确保 `notifyUrl` 公网可达（可用 cpolar 内网穿透） |
| 侧边栏无AI助理 | 清除浏览器缓存，无痕窗口重试 |
| 管理后台还是旧样式 | 按 F12 → Application → Clear site data |

---

## 📝 开发说明

- **日志级别**：mapper=DEBUG，service=INFO，controller=INFO
- **MyBatis**：开启驼峰命名自动映射，XML 在 `resources/mapper/`
- **AI 工具扩展**：在 `com.yh.ai.tool.tools` 包下新建类，方法上加 `@AiTool` 注解即可自动注册
- **前端主题**：修改 `html/sky/index.html` 中 `:root` CSS 变量

---

## 📄 License

本项目仅供学习参考。
