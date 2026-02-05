# 企业级 SaaS 图书管理系统 (Libra BMS) 交付文档

## 1. 总体架构设计
系统采用 **微内核 + 插件化策略** 设计，基于 Spring Boot 3.x 与 Vue 3 构建。

### 1.1 技术栈 (Commercial Grade)
- **后端**: Java 21, Spring Boot 3.2, Spring Security, MyBatis Plus, MySQL 8, Redis.
- **前端**: Vue 3.3, TypeScript, Element Plus, Pinia, Axios, ECharts.
- **基础设施**: Docker, Snowflake ID, JWT + Refresh Token.

### 1.2 核心设计模式
- **多租户 (SaaS)**: 共享数据库方案，通过 `TenantLineInnerInterceptor` 在 SQL 层面实现强制物理隔离。
- **状态机**: 图书与借阅状态由受控状态机流转，防止非法状态跳变。
- **并发控制**: 关键业务（如扣减库存）采用 **数据库行锁 (Select For Update) + 原子更新**，确保高并发下数据一致性。

## 2. 数据库设计 (ER 概要)
| 表名 | 核心索引 | 说明 |
| :--- | :--- | :--- |
| `sys_tenant` | `PRIMARY(id)` | 租户主表，控制 SaaS 接入 |
| `sys_user` | `UNIQUE(username, tenant_id)` | 用户表，租户内唯一 |
| `lib_book` | `UNIQUE(isbn, tenant_id)` | 图书主数据 |
| `lib_inventory` | `INDEX(book_id)` | 馆藏单册表，用于副本流转 |
| `lib_borrow_record` | `INDEX(user_id, status)` | 借阅记录，支持逾期检索 |

## 3. 核心 API 示例 (部分)
### 3.1 借书操作
- **URL**: `POST /borrow/do`
- **Header**: `Authorization: Bearer <Access_Token>`
- **Body**: `{ "bookId": 123, "userId": 456 }`
- **Response**: `200 OK`

### 3.2 获取统计面板
- **URL**: `GET /stats/dashboard`
- **Response**: 包含实时库存、逾期数及 7 日借阅趋势图表数据。

## 4. 部署与运维指南
### 4.1 快速启动 (Docker Compose)
1. 编译项目: `mvn clean package`
2. 启动容器: `docker-compose up -d`
3. 访问地址: `http://localhost:8080`

### 4.2 环境配置
- `dev`: 本地开发环境
- `prod`: 生产环境，需开启 Redis 密码、MySQL 读写分离及 SSL。

---
*本系统符合商用安全标准，所有操作均记录在 `sys_log` 审计表中。*
