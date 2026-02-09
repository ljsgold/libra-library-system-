# 📚 Libra 图书管理系统 (第三版)

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)

**现代化的图书管理系统 | Modern Library Management System**

🚀 完整的前后端分离架构 | 🔐 双角色权限控制 | 📊 数据可视化分析 | 🎨 官网级UI设计

</div>

## 🌟 项目特色

- ✅ **官网级UI设计** - 全新升级的"Simple & Atmospheric"设计风格，极简大气
- ✅ **现代化技术栈** - 基于Spring Boot 3 + Vue 3的最新技术组合
- ✅ **双角色权限** - 管理员和普通用户的完整权限管理体系
- ✅ **响应式设计** - 支持PC、平板、手机等多设备访问
- ✅ **数据可视化** - 丰富的图表展示图书馆运营数据
- ✅ **安全可靠** - JWT认证 + BCrypt加密 + 权限控制
- ✅ **易于部署** - Docker容器化一键部署

## 🏗️ 技术架构

### 后端技术栈
- **核心框架**: Spring Boot 3.1.5
- **持久层**: MyBatis-Plus + MySQL 8.0
- **缓存**: Redis 6.x
- **安全**: Spring Security + JWT
- **API文档**: Swagger UI 3.0
- **构建工具**: Maven 3.9+

### 前端技术栈
- **核心框架**: Vue 3 + TypeScript
- **构建工具**: Vite 4.x
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **状态管理**: Pinia
- **路由**: Vue Router 4

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.x+
- Maven 3.6+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/ljsgold/libra-library-system-.git
cd libra-library-system-
```

2. **初始化数据库**
```sql
-- 创建数据库
CREATE DATABASE libra_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
source db/init.sql
source db/seed.sql
```

3. **启动后端服务**
```bash
cd libra-admin
mvn spring-boot:run
# 访问: http://localhost:8080
# API文档: http://localhost:8080/swagger-ui.html
```

4. **启动前端开发服务器**
```bash
cd libra-frontend
npm install
npm run dev
# 访问: http://localhost:5173
```

### Docker部署

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看运行状态
docker-compose ps

# 停止服务
docker-compose down
```

## 📖 功能特性

### 🎯 核心功能模块

#### 👤 用户管理
- 用户注册/登录（支持邮箱验证码）
- 角色权限分配（管理员/普通用户）
- 个人信息管理
- 密码重置功能

#### 📚 图书管理（管理员）
- 图书信息增删改查
- 库存数量管理
- 图书分类统计
- 批量导入导出

#### 📖 借阅管理
- 图书借阅申请
- 在线续借操作
- 借阅记录查询
- 到期提醒功能

#### 📊 数据统计（管理员）
- 图书借阅统计
- 用户活跃度分析
- 库存状态监控
- 实时数据图表

### 🔐 权限控制

| 功能模块 | 管理员 | 普通用户 |
|---------|--------|----------|
| 图书管理 | ✅ | ❌ |
| 用户管理 | ✅ | ❌ |
| 借阅审批 | ✅ | ❌ |
| 数据统计 | ✅ | ❌ |
| 图书浏览 | ✅ | ✅ |
| 个人借阅 | ✅ | ✅ |
| 个人信息 | ✅ | ✅ |

## 📁 项目结构

```
libra-library-system-/
├── libra-admin/           # 后端主应用
│   ├── src/main/java/
│   │   └── com/libra/admin/
│   │       ├── controller/    # 控制器层
│   │       ├── service/       # 业务逻辑层
│   │       ├── mapper/        # 数据访问层
│   │       ├── entity/        # 实体类
│   │       └── config/        # 配置类
│   └── pom.xml
├── libra-frontend/        # 前端应用
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 公共组件
│   │   ├── api/           # API接口
│   │   ├── router/        # 路由配置
│   │   └── store/         # 状态管理
│   └── package.json
├── libra-common/          # 公共模块
├── libra-framework/       # 框架核心
├── db/                    # 数据库脚本
├── docs/                  # 文档资料
├── design-system/         # 设计规范
├── docker-compose.yml     # Docker编排
└── CHANGELOG.md           # 更新日志
```

## 🔧 开发指南

### 代码规范
- 后端遵循阿里巴巴Java开发手册
- 前端采用ESLint + Prettier代码格式化
- Git提交信息遵循Angular规范

### API设计规范
- RESTful API风格
- 统一响应格式
- 详细的状态码定义
- 完整的Swagger文档

### 数据库设计
- 使用InnoDB存储引擎
- UTF8MB4字符集
- 合理的索引设计
- 外键约束保证数据一致性

## 📊 系统截图

<div align="center">
  <img src="docs/screenshots/dashboard.png" alt="仪表板" width="45%">
  <img src="docs/screenshots/book-list.png" alt="图书列表" width="45%">
  <br/>
  <img src="docs/screenshots/borrow-manage.png" alt="借阅管理" width="45%">
  <img src="docs/screenshots/user-center.png" alt="用户中心" width="45%">
</div>

## 🤝 贡献指南

欢迎任何形式的贡献！

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- **作者**: ljsgold
- **邮箱**: [your-email@example.com]
- **GitHub**: [https://github.com/ljsgold](https://github.com/ljsgold)

## 🙏 致谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)
- [Redis](https://redis.io/)

---

<div align="center">

**🌟 如果你觉得这个项目有用，请给个Star支持一下！**

</div>