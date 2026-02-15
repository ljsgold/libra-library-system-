# 🔧 Vercel 环境变量配置指南

## 🎯 问题说明

如果前端部署成功但出现 `ERR_CONNECTION_TIMED_OUT` 错误，通常是因为**没有配置后端 API 地址**。

前端代码使用 `VITE_APP_BASE_API` 环境变量来连接后端，如果未设置，会默认使用 `/api`，这在生产环境中无法工作。

---

## ✅ 配置步骤（3 分钟完成）

### 第一步：获取后端地址

1. 登录 Railway：https://railway.app
2. 进入你的后端项目
3. 点击后端服务（不是 MySQL 服务）
4. 点击 **"Settings"** 标签页
5. 在 **"Domains"** 部分，找到你的后端域名
   - 格式类似：`https://xxx.railway.app`
   - 或者：`https://xxx.up.railway.app`
6. **复制完整的后端地址**（包括 `https://`）

**示例**：
```
https://libra-backend-production.up.railway.app
```

---

### 第二步：在 Vercel 中配置环境变量

1. **登录 Vercel Dashboard**
   - 访问：https://vercel.com/dashboard
   - 进入你的项目：`libra-library-system-mga2`

2. **打开环境变量设置**
   - 点击左侧菜单的 **"Settings"**（设置）
   - 点击 **"Environment Variables"**（环境变量）

3. **添加新环境变量**
   - 点击 **"Add New"**（添加新变量）按钮
   - 填写以下信息：
     - **Name（名称）**：`VITE_APP_BASE_API`
     - **Value（值）**：粘贴你从 Railway 复制的后端地址
       - ✅ 正确格式：`https://xxx.railway.app`
       - ❌ 错误格式：`https://xxx.railway.app/`（不要有末尾斜杠）
       - ❌ 错误格式：`http://xxx.railway.app`（必须是 https）
     - **Environment（环境）**：**全选**（Production、Preview、Development）

4. **保存**
   - 点击 **"Save"**（保存）按钮

---

### 第三步：重新部署前端

**⚠️ 重要：修改环境变量后，必须重新部署才能生效！**

1. 在 Vercel Dashboard 中，点击 **"Deployments"**（部署）标签页
2. 找到最新的部署（通常是第一个）
3. 点击部署右侧的 **"..."**（三个点）菜单
4. 选择 **"Redeploy"**（重新部署）
5. 确认重新部署
6. 等待部署完成（约 1-2 分钟）

---

## ✅ 验证配置

### 方法 1：检查环境变量

1. 在 Vercel Dashboard 中，进入 **"Settings"** → **"Environment Variables"**
2. 确认存在变量：
   - **Name**: `VITE_APP_BASE_API`
   - **Value**: 你的后端地址（例如：`https://xxx.railway.app`）
   - **Environment**: Production, Preview, Development（全部选中）

### 方法 2：测试后端连接

1. 在浏览器中直接访问你的后端地址：
   ```
   https://你的后端地址/swagger-ui.html
   ```
2. **如果能看到 Swagger 文档** = 后端正常运行 ✅
3. **如果无法访问或超时** = 后端有问题，需要检查 Railway 日志

### 方法 3：检查前端网络请求

1. 访问你的前端地址
2. 打开浏览器开发者工具（按 `F12`）
3. 打开 **"Network"**（网络）标签页
4. 尝试执行一个操作（例如：登录、查看图书列表）
5. 查看网络请求：
   - **请求 URL** 应该是：`https://你的后端地址/xxx`
   - **状态码**：
     - `200` = 成功 ✅
     - `ERR_CONNECTION_TIMED_OUT` = 无法连接到后端 ❌

---

## 🔍 常见问题

### Q1: 环境变量应该填什么值？

**A:** 填写你的 Railway 后端地址，格式：
```
https://你的后端域名
```

**示例**：
- ✅ `https://libra-backend-production.up.railway.app`
- ❌ `https://libra-backend-production.up.railway.app/`（不要有末尾斜杠）
- ❌ `http://libra-backend-production.up.railway.app`（必须是 https）
- ❌ `libra-backend-production.up.railway.app`（必须包含 https://）

### Q2: 为什么修改环境变量后还是不行？

**A:** 环境变量修改后，**必须重新部署前端才能生效**。Vercel 在构建时会读取环境变量，如果只是修改了环境变量但没有重新部署，前端代码中仍然使用的是旧的值。

### Q3: 环境变量的 Environment 应该选哪个？

**A:** **建议全选**（Production、Preview、Development），这样在所有环境中都能正常工作。

### Q4: 如何确认后端地址是否正确？

**A:** 在浏览器中直接访问：
```
https://你的后端地址/swagger-ui.html
```
- 如果能看到 Swagger 文档 = 地址正确 ✅
- 如果无法访问 = 地址错误或后端未运行 ❌

### Q5: 前端部署成功，但后端连接失败？

**A:** 检查以下几点：
1. ✅ 后端服务在 Railway 中是否显示 "Running"（绿色）
2. ✅ 后端地址是否可以访问（直接访问 Swagger 文档）
3. ✅ Vercel 环境变量是否已正确配置
4. ✅ 是否已重新部署前端

---

## 📋 快速检查清单

完成配置后，请确认：

- [ ] Railway 后端服务状态为 "Running"（绿色）
- [ ] 可以直接访问后端 Swagger 文档（`https://你的后端地址/swagger-ui.html`）
- [ ] Vercel 环境变量 `VITE_APP_BASE_API` 已设置
- [ ] 环境变量值是正确的后端地址（`https://xxx.railway.app`，无末尾斜杠）
- [ ] 环境变量已应用到所有环境（Production, Preview, Development）
- [ ] 修改环境变量后已重新部署前端
- [ ] 已清除浏览器缓存（`Ctrl + Shift + Delete`）

---

## 🎯 下一步

完成以上配置后：
1. 等待前端重新部署完成（约 1-2 分钟）
2. 清除浏览器缓存
3. 重新访问前端地址
4. 测试登录功能

如果仍然无法连接，请查看 `docs/线上部署问题排查.md` 获取更详细的排查步骤。

---

## 💡 提示

- **环境变量修改后必须重新部署**，这是最常见的错误！
- **后端地址必须是 HTTPS**，不能使用 HTTP
- **不要有末尾斜杠**，例如：`https://xxx.railway.app` ✅，不是 `https://xxx.railway.app/` ❌
- **清除浏览器缓存**，避免使用旧的 JavaScript 代码

祝你配置顺利！🚀
