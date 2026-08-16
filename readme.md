# 农家乐出行预订平台（FarmStay Booking Platform）

## V3.0 企业级微服务架构设计文档

**版本：V3.0 Enterprise Edition**
**定位：Java 微服务 / Spring Cloud / 高并发交易系统 **

---

# 一、项目定位

## 1.1 项目简介

农家乐出行预订平台是一个面向乡村旅游与农家乐场景的在线预约平台，用户可以浏览农家乐服务、选择日期与套餐、完成下单与支付；商户可以管理店铺、服务资源、库存与订单。

平台采用 **Spring Cloud 微服务架构**，围绕 **高并发下单、防超卖、订单与库存最终一致性、服务治理与可观测性** 进行设计，目标是实现一个真实互联网交易系统的企业级项目。

---

# 二、系统目标

## 2.1 业务目标

* 在线预约农家乐服务
* 支持按日期/场次管理库存
* 支持订单、支付、退款流程
* 支持商户自主运营店铺
* 支持库存防超卖
* 支持高并发下单

## 2.2 技术目标

* 微服务解耦
* 服务可独立部署
* 高可用
* 高并发
* 最终一致性
* 消息可靠
* 可观测
* 易扩展

---

# 三、业务模型（DDD）

## 核心领域

### 用户域

消费者、商户、管理员

### 店铺域

农家乐店铺

### 服务域

住宿、餐饮、采摘、垂钓、亲子体验等

### 库存域

按日期/场次管理库存资源

### 订单域

预约订单

### 支付域

支付单、退款单

### 通知域

短信、消息、订单通知

---

# 四、总体架构

```
                客户端（Web / App / 小程序）
                           |
                           v
                  Spring Cloud Gateway
                           |
         +-----------------+-----------------+
         |                 |                 |
         v                 v                 v
      User Service     Product Service   Merchant Service
                           |
                           |
                           v
                     Inventory Service
                           |
                           |
                           v
                       Order Service
                           |
                           |
                           v
                      Payment Service
                           |
                           |
                           v
                     Notification Service

-----------------------------------------------
Nacos        服务注册/配置中心
Sentinel     限流 / 熔断 / 降级
RabbitMQ     异步事件总线
Redis        缓存 / 热点库存
MySQL        各服务独立数据库
SkyWalking   链路追踪
Prometheus   指标采集
Grafana      可视化监控
```

---

# 五、微服务拆分

## Gateway Service

职责：

* 路由
* JWT 鉴权
* 限流
* 灰度发布入口
* 日志

## User Service

职责：

* 注册
* 登录
* Token
* 用户信息
* 商户账号

## Merchant Service

职责：

* 店铺管理
* 店铺营业状态
* 店铺审核

## Product Service

职责：

* 服务资源管理
* 分类
* 图片
* 详情
* 上下架

## Inventory Service

职责：

* 库存查询
* 库存预占
* 库存确认
* 库存释放
* 库存流水

## Order Service

职责：

* 创建订单
* 状态机
* 超时取消
* 退款
* 对账

## Payment Service

职责：

* 支付单
* 第三方支付
* 回调
* 退款

## Notification Service

职责：

* 短信
* 邮件
* 订单通知

---

# 六、数据库设计

> 说明：数据库设计目标为各服务使用独立数据库（库名与微服务对应），服务间不直接跨库 join，通过 Feign 或消息事件解耦。
> 开发阶段暂时同库分表

## 6.1 用户库（farmhouse-user）

### 用户表 `user`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| username | varchar(50) | 用户名，唯一 |
| password | varchar(100) | BCrypt 加密密码 |
| phone | varchar(20) | 手机号 |
| nickname | varchar(50) | 昵称 |
| role | tinyint | 角色：0消费者 1商户 2管理员 |
| status | tinyint | 账号状态：1正常 0禁用 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_username (username)`

## 6.2 商户库（farmhouse-merchant）

### 商户/店铺表 `merchant`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| user_id | bigint | 关联用户，唯一 |
| shop_name | varchar(100) | 店铺名称 |
| status | tinyint | 营业状态：1营业中 2休息中 3关闭 |
| audit_status | tinyint | 审核状态：0待审核 1通过 2拒绝 |
| description | varchar(500) | 店铺描述 |
| contact_phone | varchar(20) | 联系电话 |
| address | varchar(255) | 店铺地址 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_user_id (user_id)`

## 6.3 服务库（farmhouse-product）

### 分类表 `category`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| name | varchar(50) | 分类名称 |
| parent_id | bigint | 父分类，0 表示顶级 |
| sort | int | 排序值 |
| status | tinyint | 1启用 0禁用 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### 服务表 `product`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| merchant_id | bigint | 商户ID |
| category_id | bigint | 分类ID |
| title | varchar(100) | 服务名称 |
| subtitle | varchar(200) | 副标题 |
| description | text | 服务描述 |
| price | decimal(10,2) | 价格 |
| cover_url | varchar(255) | 封面图 |
| status | tinyint | 状态：0草稿 1已上架 2已下架 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

> 库存不放在服务表，由库存库独立管理。

## 6.4 库存库（farmhouse-inventory）

### 库存表 `inventory`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| product_id | bigint | 服务ID |
| stock_date | date | 库存日期，可空（支持按日期/场次管理） |
| total | int | 总库存 |
| available | int | 可售库存 |
| reserved | int | 已锁定库存 |
| sold | int | 已售库存 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_product_date (product_id, stock_date)`

> 四态库存：`total = available + reserved + sold`。通过条件 UPDATE 原子变更 available/reserved，防止超卖。

### 库存流水表 `inventory_record`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| order_id | bigint | 订单ID |
| product_id | bigint | 服务ID |
| stock_date | date | 库存日期 |
| operation | tinyint | 操作类型：1预占 2确认 3释放 |
| quantity | int | 变动数量 |
| created_at | datetime | 创建时间 |

唯一索引：`uk_order_operation (order_id, operation)`

> 幂等消费：同一订单同一操作的重复消息直接 ACK，不重复变更库存。

## 6.5 订单库（farmhouse-order）

### 订单表 `orders`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| order_no | varchar(64) | 订单号，唯一 |
| request_id | varchar(64) | 幂等请求ID，唯一 |
| user_id | bigint | 用户ID |
| product_id | bigint | 服务ID |
| stock_date | date | 预约日期 |
| quantity | int | 数量 |
| amount | decimal(10,2) | 订单金额 |
| status | tinyint | 0 INIT 1 WAIT_PAY 2 PAY_SUCCESS 3 CANCEL 4 FINISHED 5 REFUND |
| expire_time | datetime | 支付超时时间 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_order_no (order_no)`、`uk_request_id (request_id)`

> request_id 用于创建订单幂等：重复请求直接返回已有订单。

## 6.6 支付库（farmhouse-payment）

### 支付表 `payment`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| payment_no | varchar(64) | 支付流水号，唯一 |
| order_id | bigint | 订单ID |
| amount | decimal(10,2) | 支付金额 |
| status | tinyint | 状态：0待支付 1成功 2失败 3退款 |
| channel | varchar(20) | 支付渠道 |
| paid_at | datetime | 支付时间 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_payment_no (payment_no)`

> payment_no 用于回调幂等：重复回调直接返回 SUCCESS。

### 退款表 `refund`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| refund_no | varchar(64) | 退款号，唯一 |
| payment_id | bigint | 支付单ID |
| order_id | bigint | 订单ID |
| amount | decimal(10,2) | 退款金额 |
| status | tinyint | 状态：0处理中 1成功 2失败 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

唯一索引：`uk_refund_no (refund_no)`

## 6.7 通知库（farmhouse-notification）

### 通知表 `notification`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| user_id | bigint | 接收用户 |
| type | tinyint | 通知类型 |
| content | varchar(500) | 通知内容 |
| status | tinyint | 0未读 1已读 |
| created_at | datetime | 创建时间 |

---

# 七、库存模型（企业级）

## 为什么不用简单库存

传统：

```
stock = 100
```

无法区分：

* 可售
* 已锁定
* 已售

## 企业级库存模型

| 字段        | 说明    |
| --------- | ----- |
| total     | 总库存   |
| available | 可售库存  |
| reserved  | 已锁定库存 |
| sold      | 已售库存  |

### 下单预占

```
available -= n
reserved += n
```

### 支付成功

```
reserved -= n
sold += n
```

### 订单取消

```
reserved -= n
available += n
```

这样可以支持：

* 支付超时
* 库存锁定
* 对账
* 风险控制

---

# 八、库存预占时序

```
用户
 |
 | 创建订单
 v
Order Service
 |
 | 创建 INIT 订单
 |
 +------------------------>
                          Inventory Service
                          |
                          | 条件 UPDATE
                          |
                          | available >= n
                          |
                          | available -= n
                          | reserved += n
                          |
                          +-------------------->
                                               SUCCESS
 |
 | 更新订单状态
 | WAIT_PAY
 |
 | 发送延迟消息
 |
 v
RabbitMQ
```

---

# 九、防超卖方案

## 条件 UPDATE

```sql
UPDATE inventory
SET available = available - #{qty},
    reserved  = reserved  + #{qty}
WHERE product_id = #{productId}
  AND stock_date = #{date}
  AND available >= #{qty};
```

### 优点

* 原子
* 无分布式锁
* 高性能
* 简单可靠

依赖：

**InnoDB 行锁**

---

# 十、为什么不用分布式锁

不采用：

Redis Lock

原因：

* 串行化吞吐低
* Redis 成为热点
* 锁超时复杂
* 主从切换问题

采用：

**数据库条件 UPDATE**

原则：

> 能用数据库原子能力解决的问题，不引入分布式锁。

---

# 十一、订单状态机

```
INIT
 |
 v
WAIT_PAY
 |       \
 |        \
 |         \
PAY_SUCCESS CANCEL
 |
 v
FINISHED
 |
 v
REFUND
```

状态转换必须：

* 单向
* 幂等
* 可追踪

---

# 十二、支付架构

## 支付流程

```
Order
 |
 | create payment
 |
 v
Payment
 |
 | 第三方支付
 |
 v
Callback
 |
 | 幂等校验
 |
 v
Order
```

## 幂等

payment_no UNIQUE

重复回调：

直接返回 SUCCESS。

---

# 十三、RabbitMQ 架构

## 事件类型

OrderCreated

OrderTimeout

PaymentSuccess

PaymentRefund

InventoryReserved

InventoryReleased

## Exchange

order.event

payment.event

inventory.event

## Queue

order.delay.queue

payment.callback.queue

inventory.release.queue

---

# 十四、可靠消息

## Producer Confirm

确认发送成功。

## 消息持久化

* Durable Queue
* Persistent Message

## Consumer Ack

Manual Ack。

## 重试

失败：

* Retry
* Dead Letter Queue

## 幂等消费

inventory_record

唯一键：

```
(order_id, operation)
```

重复消费：

直接 ACK。

---

# 十五、最终一致性

## 场景

库存扣减成功

订单创建失败

## 方案

本地事务

*

补偿

流程：

```
库存预占成功

↓

订单创建失败

↓

发送释放库存消息

↓

Inventory Release

↓

available += n
reserved -= n
```

保证：

**最终一致**

---

# 十六、缓存架构

## Cache Aside

查询：

Redis

↓

Miss

↓

MySQL

↓

Redis

更新：

MySQL

↓

Delete Redis

## 热点库存

TTL：

30~60 秒

*

随机过期

防止：

* 雪崩
* 击穿
* 穿透

---

# 十七、幂等设计

## 创建订单

request_id UNIQUE

重复请求：

返回已有订单。

## 支付回调

payment_no UNIQUE

## 库存释放

(order_id, operation)

UNIQUE。

---

# 十八、服务治理

## Sentinel

### 限流

下单接口

库存接口

详情接口

### 熔断

库存服务异常

支付服务异常

### 线程隔离

防止：

库存服务阻塞

拖垮订单服务。

---

# 十九、高可用

## 多实例

每个服务：

> =2 实例

## 注册中心

Nacos

自动摘除故障节点。

## Gateway

负载均衡：

Round Robin。

---

# 二十、可观测性

## SkyWalking

链路：

Gateway

↓

Order

↓

Inventory

↓

Payment

## Prometheus

采集：

* QPS
* RT
* JVM
* CPU
* 内存

## Grafana

展示：

实时监控面板。

---

# 二十一、数据库索引

## 21.1 索引清单

| 表 | 索引名 | 索引字段 | 类型 | 覆盖场景 |
|----|--------|---------|------|---------|
| user | uk_username | username | 唯一 | 登录、注册唯一校验 |
| merchant | uk_user_id | user_id | 唯一 | 用户-店铺一对一 |
| product | idx_merchant_id | merchant_id | 普通 | 商户查询自己的服务 |
| product | idx_category_id | category_id | 普通 | 按分类筛选 |
| product | idx_status_category | status, category_id | 联合 | 列表查询（已上架 + 分类） |
| inventory | uk_product_date | product_id, stock_date | 唯一 | 条件 UPDATE 定位库存行 |
| inventory_record | uk_order_operation | order_id, operation | 唯一 | 幂等消费 |
| orders | uk_order_no | order_no | 唯一 | 订单号查询 |
| orders | uk_request_id | request_id | 唯一 | 创建订单幂等 |
| orders | idx_user_id | user_id | 普通 | 用户订单列表 |
| orders | idx_status_expire | status, expire_time | 联合 | 超时未支付扫描 |
| payment | uk_payment_no | payment_no | 唯一 | 支付回调幂等 |
| payment | idx_order_id | order_id | 普通 | 按订单查支付 |
| refund | uk_refund_no | refund_no | 唯一 | 退款单唯一 |
| refund | idx_order_id | order_id | 普通 | 按订单查退款 |

## 21.2 设计原则

1. **唯一索引承担幂等**：request_id、payment_no、order_no、username 均用唯一索引，由数据库层保证幂等与唯一性，应用层无需额外加锁。
2. **库存行定位靠联合唯一索引**：`uk_product_date` 让条件 UPDATE 精确命中单行，InnoDB 行锁串行化并发扣减，是防超卖的根基。
3. **超时扫描走联合索引**：`idx_status_expire (status, expire_time)` 避免全表扫描，定时任务只扫 `status = WAIT_PAY AND expire_time < now` 的行。
4. **列表查询覆盖索引**：`idx_status_category` 联合索引覆盖「已上架 + 分类」的常见查询，减少回表。
5. **避免冗余索引**：单列索引能被联合索引前缀覆盖时不再单独建。
6. **流水表只增不改**：inventory_record 无 updated_at，纯追加，联合唯一键保证幂等即可。

---

# 二十二、性能指标

| 指标    | 目标          |
| ----- | ----------- |
| 下单 RT | P95 < 800ms |
| 库存查询  | P95 < 100ms |
| 热点详情  | P95 < 200ms |
| 并发下单  | 1000 QPS    |
| 库存查询  | 5000 QPS    |
| 系统可用性 | >=99.9%     |

---

# 二十三、压测方案

工具：

JMeter

模拟：

1000 并发下单

验证：

* 不超卖
* RT
* CPU
* MySQL 行锁等待
* Redis 命中率

---

# 二十四、演进路线

V1.0

单体

↓

V2.0

Spring Cloud

↓

V3.0

企业级微服务

↓

V4.0

Redis Lua

*

MQ 削峰

*

库存分片

↓

V5.0

Kafka

*

CQRS

*

DDD 深化

*

ElasticSearch

---

# 二十五、项目亮点

1. **库存预占模型**：available / reserved / sold 四态库存模型，支持预约业务与库存锁定。
2. **无分布式锁防超卖**：数据库条件 UPDATE + InnoDB 行锁，实现高并发库存扣减。
3. **最终一致性架构**：本地事务 + RabbitMQ 补偿，替代 Seata，降低系统复杂度。
4. **可靠消息体系**：Confirm、持久化、Ack、DLQ、幂等消费完整闭环。
5. **企业级微服务治理**：Gateway、Nacos、Sentinel、Redis、RabbitMQ、SkyWalking、Prometheus、Grafana 全链路覆盖。
6. **高并发可扩展**：支持多实例部署、限流熔断、缓存热点、异步解耦，为秒级高并发预约场景预留演进空间。

---

# 二十六、总结

本项目不是传统 CRUD 电商，而是围绕 **预约型交易系统** 构建的企业级微服务架构实践。通过库存预占、防超卖、可靠消息、最终一致性、服务治理、可观测性等设计，完整覆盖 Java 后端面试中的核心能力点，包括：

* Spring Cloud
* 微服务拆分
* 分布式事务
* RabbitMQ
* Redis
* MySQL 并发控制
* Sentinel
* 高可用
* 高并发
* DDD 领域建模
* 系统架构设计

该项目可继续演进为 **秒杀、酒店、门票、预约等高并发交易系统**。