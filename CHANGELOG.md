# 更新日志

## v2.0.0 (2026-02-06)

### 🚀 新功能 Features
- **完整的前后端分离架构** - 基于Vue 3 + Spring Boot的现代化图书管理系统
- **双角色权限系统** - 管理员和普通用户的完整权限控制
- **图书管理功能** - 图书增删改查、库存管理和分类统计
- **借阅管理系统** - 完整的借书、还书、续借流程
- **用户认证体系** - JWT Token认证，支持邮箱验证码登录
- **数据统计面板** - 实时展示图书馆运营数据和图表分析
- **响应式前端界面** - 基于Element Plus的现代化UI组件库

### 🔧 技术栈 Tech Stack
- **后端**: Spring Boot 3.1.5, MyBatis-Plus, Redis, MySQL
- **前端**: Vue 3, TypeScript, Vite, Element Plus, Axios
- **安全**: Spring Security, JWT, BCrypt密码加密
- **部署**: Docker容器化, Nginx反向代理

### 📁 项目结构 Project Structure
```
图书管理系统/
├── libra-admin/          # 后端主应用模块
├── libra-common/         # 公共工具模块
├── libra-framework/      # 框架核心模块
├── libra-frontend/       # 前端应用
├── db/                   # 数据库脚本
├── docs/                 # 文档资料
├── design-system/        # 设计系统规范
└── docker-compose.yml    # 容器编排配置
```

### 🐛 修复的Bug Fixes
- 修复OSS上下文注入稳定性问题
- 解决MyBatis-Plus批量查询方法废弃警告
- 优化日志切面注入问题
- 清理Redis配置中的警告信息
- 完善实体类equals/hashCode方法

### ⚡ 性能优化 Performance
- 添加接口限流保护机制
- 优化数据库查询性能
- 实现Redis缓存策略
- 前端路由懒加载优化

### 📝 文档完善 Documentation
- 完整的API接口文档(Swagger UI)
- 详细的架构设计文档
- 部署指南和环境配置说明
- 开发规范和最佳实践

### 🔒 安全增强 Security
- JWT Token认证机制
- 密码BCrypt加密存储
- 接口访问权限控制
- SQL注入防护
- XSS攻击防范

## v1.0.0 (初始版本)
- 基础的图书管理功能
- 简单的用户管理系统
- 基本的借阅流程实现