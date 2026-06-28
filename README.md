# 🍔 You-Hungry（饿了吧外卖）

> 学校食堂智能外卖配送系统 —— Spring Boot + Vue + 微信小程序

## 📖 项目简介

You-Hungry 是一套专为**学校食堂**设计的智能外卖配送平台，支持学生通过**微信小程序**在线浏览菜单、下单支付，食堂管理员通过 **Web 管理后台**进行菜品管理、订单处理和数据分析。

### ✨ 核心功能

| 端 | 功能 |
|---|---|
| 🛒 **微信小程序（用户端）** | 浏览菜品/套餐、购物车、收货地址管理、微信支付下单、订单跟踪、再来一单 |
| 🖥️ **Web 管理后台（商家端）** | 员工管理、分类管理、菜品管理（含口味）、套餐管理、订单处理（接单/拒单/派送/完成）、店铺启停、数据报表、营业额统计 |
| ⚡ **自动化任务** | 超时未付款自动取消（15分钟）、配送超时自动完成（每日凌晨） |
| 🔔 **实时通知** | WebSocket 推送新订单提醒 |

---

## 🛠 技术栈

### 后端

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 2.7.3 | 核心框架 |
| MyBatis + PageHelper | 2.2.0 / 1.3.0 | ORM + 分页 |
| MySQL + Druid | — / 1.2.1 | 数据库 + 连接池 |
| Redis | — | 缓存（菜品列表、店铺状态） |
| JWT (jjwt) | 0.9.1 | 双端认证（admin/user 独立密钥） |
| Knife4j (Swagger) | 3.0.2 | API 文档 |
| 阿里云 OSS | 3.10.2 | 图片/文件上传 |
| 微信支付 SDK | 0.4.8 | 微信 JSAPI 支付 |
| WebSocket | — | 实时消息推送 |
| Apache POI | 3.16 | Excel 报表导出 |
| AspectJ | 1.9.4 | AOP 自动填充字段 |
| Lombok | 1.18.30 | 简化代码 |

### 前端

| 端 | 技术 | 说明 |
|---|---|---|
| 管理后台 | Vue.js + Element UI | SPA 单页应用，暗色主题 |
| 用户端 | uni-app → 微信小程序 | 编译输出至 `mp-weixin/` |

---

## 📁 项目结构

```
you-hungry/
├── yh-common/                    # 公共模块
│   └── src/main/java/com/yh/
│       ├── constant/             # 常量（消息、状态、JWT）
│       ├── context/              # ThreadLocal 用户上下文
│       ├── enumeration/          # 枚举（INSERT/UPDATE）
│       ├── exception/            # 业务异常类
│       ├── json/                 # Jackson 配置
│       ├── properties/           # 配置属性类
│       ├── result/               # 统一响应 Result<T>
│       └── utils/                # 工具类（JWT、OSS、HttpClient、微信支付）
├── yh-pojo/                      # 数据模型模块
│   └── src/main/java/com/yh/
│       ├── entity/               # 实体类（11 张表）
│       ├── dto/                  # 数据传输对象
│       └── vo/                   # 视图对象
├── yh-server/                    # Spring Boot 主应用
│   └── src/main/java/com/yh/
│       ├── controller/
│       │   ├── admin/            # 管理端 API（员工、分类、菜品、套餐、订单、报表）
│       │   ├── user/             # 用户端 API（购物车、地址、订单）
│       │   └── notify/           # 微信支付回调
│       ├── service/              # 业务接口
│       │   └── impl/             # 业务实现
│       ├── mapper/               # MyBatis 映射接口
│       ├── config/               # Spring 配置
│       ├── interceptor/          # JWT 拦截器
│       ├── handler/              # 全局异常处理
│       ├── aspect/               # AOP 切面（自动填充）
│       ├── annotation/           # 自定义注解 @AutoFill
│       ├── webSocket/            # WebSocket 服务
│       └── task/                 # 定时任务
│   └── src/main/resources/
│       ├── application.yml       # 主配置
│       ├── application-dev.yml   # 开发环境配置
│       └── mapper/               # MyBatis XML 映射文件（12 个）
├── html/                         # Vue 管理后台（构建产物）
│   └── sky/
│       ├── index.html            # SPA 入口
│       ├── css/                  # 样式文件
│       ├── js/                   # JS 文件（app + chunk-vendors）
│       └── img/                  # 图片资源
├── mp-weixin/                    # 微信小程序（uni-app 编译输出）
│   ├── app.json                  # 小程序配置
│   ├── pages/                    # 页面（12 个）
│   ├── components/               # 通用组件
│   ├── common/                   # 公共 JS/CSS
│   └── static/                   # 静态资源
└── pom.xml                       # Maven 父 POM
```

---

## 🚀 快速开始

### 环境要求

| 依赖 | 版本要求 | 说明 |
|---|---|---|
| JDK | 17+ | 开发使用 JDK 21 |
| Maven | 3.6+ | 构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存服务 |
| Node.js | 16+ | 前端开发（可选） |
| 微信开发者工具 | 最新版 | 小程序调试 |

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd you-hungry
```

### 2. 数据库准备

创建数据库并导入数据：

```sql
CREATE DATABASE IF NOT EXISTS sky_take_out DEFAULT CHARACTER SET utf8mb4;
```

> 数据库初始化 SQL 脚本请自行准备（根据实体类建表）。

### 3. 修改配置

编辑 `yh-server/src/main/resources/application-dev.yml`：

```yaml
sky:
  datasource:
    host: localhost
    port: 3306
    database: sky_take_out
    username: root
    password: your_password    # 修改为你的数据库密码
  redis:
    host: localhost
    port: 6379
    database: 0
  alioss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: your_key    # 修改为你的 OSS Key
    access-key-secret: your_secret
    bucket-name: your_bucket
  wechat:
    appid: your_appid          # 修改为你的微信小程序 AppID
    secret: your_secret        # 修改为你的小程序 Secret
    notifyUrl: https://your-domain/notify/paySuccess
```

### 4. 构建与启动后端

```bash
# 编译打包
mvn clean package -DskipTests

# 启动应用
java -jar yh-server/target/sky-server-1.0-SNAPSHOT.jar
```

启动成功后访问：
- **API 服务**：http://localhost:8080
- **API 文档**：http://localhost:8080/doc.html

### 5. 启动管理后台前端

```bash
# 方式一：使用 Nginx（推荐）
# 将 html/sky/ 目录配置为 Nginx 静态文件目录
# 参考 nginx 配置见下方

# 方式二：直接在 IDE 中打开 html/sky/index.html
```

**Nginx 配置示例**（监听 8085 端口，代理 API 到后端）：

```nginx
server {
    listen       8085;
    server_name  localhost;

    location / {
        root   html/sky;
        index  index.html index.htm;
    }

    location /api/ {
        proxy_pass   http://localhost:8080/admin/;
    }

    location /user/ {
        proxy_pass   http://localhost:8080/user/;
    }

    location /ws/ {
        proxy_pass   http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

### 6. 启动微信小程序

1. 打开**微信开发者工具**
2. 导入项目，选择 `mp-weixin/` 目录
3. AppID 使用 `wx761c9d3d65a6f6c3`（或修改为你自己的）
4. 确保后端 `localhost:8080` 可访问

---

## 📡 API 接口概览

API 文档可通过 Swagger UI 查看：http://localhost:8080/doc.html

### 管理端接口（`/admin/**`）—— 需 admin JWT 认证

| 模块 | 主要接口 | 说明 |
|---|---|---|
| 🔑 员工管理 | `POST /admin/employee/login` | 登录（公开） |
| | `GET /admin/employee/page` | 员工分页查询 |
| | `POST/PUT /admin/employee` | 新增/编辑员工 |
| | `POST /admin/employee/status/{status}` | 启用/禁用员工 |
| 📂 分类管理 | `GET /admin/category/page` | 分类分页查询 |
| | `POST/PUT/DELETE /admin/category` | 分类增删改 |
| | `POST /admin/category/status/{status}` | 启用/禁用分类 |
| 🍜 菜品管理 | `GET /admin/dish/page` | 菜品分页查询 |
| | `POST/PUT/DELETE /admin/dish` | 菜品增删改（含口味） |
| | `POST /admin/dish/status/{status}` | 起售/停售菜品 |
| 📦 套餐管理 | `GET /admin/setmeal/page` | 套餐分页查询 |
| | `POST/PUT/DELETE /admin/setmeal` | 套餐增删改 |
| | `POST /admin/setmeal/status/{status}` | 启售/停售套餐 |
| 📋 订单管理 | `GET /admin/order/conditionSearch` | 订单搜索/筛选 |
| | `PUT /admin/order/confirm` | 接单 |
| | `PUT /admin/order/rejection` | 拒单（需填写原因） |
| | `PUT /admin/order/delivery/{id}` | 开始配送 |
| | `PUT /admin/order/complete/{id}` | 完成订单 |
| 🏪 店铺管理 | `PUT /admin/shop/{status}` | 设置营业状态 |
| | `GET /admin/shop/status` | 查询营业状态 |
| 📊 数据报表 | `GET /admin/report/turnoverStatistics` | 营业额统计 |
| | `GET /admin/report/userStatistics` | 用户统计 |
| | `GET /admin/report/ordersStatistics` | 订单统计 |
| | `GET /admin/report/top10` | 销量 Top10 |
| | `GET /admin/report/export` | 导出 Excel 报表 |
| 🖼️ 文件上传 | `POST /admin/common/upload` | 上传图片到 OSS |

### 用户端接口（`/user/**`）—— 需 user JWT 认证

| 模块 | 主要接口 | 说明 |
|---|---|---|
| 🔐 登录 | `POST /user/user/login` | 微信登录（公开） |
| 📋 分类 | `GET /user/category/list` | 分类列表 |
| 🍜 菜品 | `GET /user/dish/list` | 菜品列表（Redis 缓存） |
| 📦 套餐 | `GET /user/setmeal/list` | 套餐列表（Spring Cache） |
| | `GET /user/setmeal/dish/{id}` | 套餐详情 |
| 🛒 购物车 | `POST /user/shoppingCart/add` | 添加到购物车 |
| | `GET /user/shoppingCart/list` | 查看购物车 |
| | `POST /user/shoppingCart/sub` | 减少商品数量 |
| | `DELETE /user/shoppingCart/clean` | 清空购物车 |
| 📍 地址 | `GET /user/addressBook/list` | 地址列表 |
| | `POST/PUT/DELETE /user/addressBook` | 地址增删改 |
| | `PUT /user/addressBook/default` | 设为默认地址 |
| 📦 订单 | `POST /user/order/submit` | 提交订单 |
| | `PUT /user/order/payment` | 发起支付 |
| | `GET /user/order/historyOrders` | 历史订单 |
| | `PUT /user/order/cancel/{id}` | 取消订单 |
| | `POST /user/order/repetition/{id}` | 再来一单 |
| | `GET /user/order/reminder/{id}` | 催单 |

---

## 🗄️ 数据库表结构

| 表名 | 实体 | 说明 |
|---|---|---|
| `employee` | Employee | 员工/管理员（用户名、密码、状态） |
| `user` | User | 微信小程序用户（openid、头像） |
| `category` | Category | 菜品/套餐分类（type=1 菜品, 2 套餐） |
| `dish` | Dish | 菜品（名称、价格、图片、状态） |
| `dish_flavor` | DishFlavor | 菜品口味（辣度、温度等） |
| `setmeal` | Setmeal | 套餐（名称、价格、图片） |
| `setmeal_dish` | SetmealDish | 套餐-菜品关联（含份数） |
| `orders` | Orders | 订单（6 种状态、支付、配送信息） |
| `order_detail` | OrderDetail | 订单明细 |
| `shopping_cart` | ShoppingCart | 购物车 |
| `address_book` | AddressBook | 收货地址（省市区、标签） |

### 订单状态流转

```
待付款(1) ──支付──▶ 待接单(2) ──接单──▶ 已接单(3) ──派送──▶ 派送中(4) ──送达──▶ 已完成(5)
    │                                        │
    └──超时15分钟/手动取消──▶ 已取消(6)        └──拒单──▶ 已取消(6)
```

---

## 🏗️ 架构设计

```
                        ┌──────────────────────┐
                        │    微信小程序用户端     │
                        │   (uni-app / mp-      │
                        │    weixin)            │
                        └──────────┬───────────┘
                                   │ HTTPS
                                   ▼
┌──────────────┐     ┌──────────────────────┐     ┌──────────────────┐
│  管理后台     │────▶│       Nginx          │────▶│   Spring Boot    │
│  (Vue.js)    │     │   (反向代理 + 静态文件) │     │   (端口 8080)     │
└──────────────┘     └──────────────────────┘     └────────┬─────────┘
                                                          │
                              ┌───────────────────────────┼───────────┐
                              │                           │           │
                              ▼                           ▼           ▼
                       ┌──────────┐               ┌──────────┐ ┌──────────┐
                       │  MySQL   │               │  Redis   │ │ 阿里云OSS │
                       │ (数据库)  │               │  (缓存)   │ │ (图片存储) │
                       └──────────┘               └──────────┘ └──────────┘
```

### 关键技术点

- **双 JWT 认证**：管理端（admin）和用户端（user）使用不同的密钥和 Token 名称，各自独立的拦截器
- **AOP 自动填充**：`@AutoFill` 注解自动填充 `createTime/createUser/updateTime/updateUser`
- **统一响应格式**：`Result<T>`（code=1 成功，code=0 失败）
- **全局异常处理**：`@RestControllerAdvice` 统一捕获业务异常和 SQL 异常
- **Redis 缓存策略**：菜品列表按分类缓存（key: `dish_{categoryId}`）、店铺状态、套餐列表
- **定时任务**：每分钟检查超时未支付订单，每日凌晨完成超时配送订单
- **WebSocket 推送**：支付成功后实时推送新订单通知到管理端

---

## ⚙️ 配置说明

### JWT 配置

| 配置项 | 管理端 | 用户端 |
|---|---|---|
| 签名密钥 | `itcast` | `itheima` |
| Token 有效期 | 2 小时 | 2 小时 |
| 请求头名称 | `token` | `authentication` |

### 微信支付配置

| 配置项 | 说明 |
|---|---|
| `sky.wechat.appid` | 小程序 AppID |
| `sky.wechat.secret` | 小程序 Secret |
| `sky.wechat.mchid` | 商户号 |
| `sky.wechat.notifyUrl` | 支付回调地址（需公网可达） |

> ⚠️ **注意**：微信支付回调需要公网可达的 URL，开发环境可使用 [cpolar](https://www.cpolar.com/) 等内网穿透工具。

---

## 📝 开发说明

### 代码风格

- Controller → Service → Mapper 三层架构
- 日志级别配置：mapper=DEBUG, service=INFO, controller=INFO
- MyBatis 开启驼峰命名自动映射
- 实体类时间字段使用 `LocalDateTime`

### 浏览器兼容

管理后台支持现代浏览器（Chrome、Edge、Firefox 最新版本）。

---

## 🐛 常见问题

1. **数据库连接失败**：检查 `application-dev.yml` 中数据库连接信息是否正确，MySQL 服务是否启动
2. **Redis 连接失败**：确认 Redis 服务是否在 `localhost:6379` 运行
3. **图片上传失败**：检查阿里云 OSS 配置是否正确，Bucket 是否存在
4. **微信小程序无法访问后端**：检查小程序 request 合法域名是否配置，开发工具中可勾选「不校验合法域名」
5. **支付回调收不到**：确保 `notifyUrl` 公网可达，检查 cpolar 隧道是否运行

---

## 📄 License

本项目仅供学习参考。
