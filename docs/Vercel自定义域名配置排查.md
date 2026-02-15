# 🔍 Vercel 自定义域名无法访问排查指南

## 🎯 问题说明

**症状**：
- ✅ Vercel 默认域名（`xxx.vercel.app`）可以正常访问
- ❌ 自定义域名无法访问（点击域名时打不开）

**可能原因**：
1. DNS 配置不正确或未生效
2. SSL 证书未配置完成
3. 域名在 Vercel 中未正确添加
4. DNS 传播延迟（通常需要几分钟到几小时）

---

## ✅ 第一步：检查 Vercel 中的域名配置

### 1.1 确认域名已添加

1. **登录 Vercel Dashboard**
   - 访问：https://vercel.com/dashboard
   - 进入你的项目

2. **检查域名设置**
   - 点击 **"Settings"** → **"Domains"**
   - 查看你的自定义域名是否在列表中

3. **检查域名状态**
   - 域名旁边会显示状态：
     - ✅ **Valid Configuration**（有效配置）= DNS 配置正确
     - ⚠️ **Invalid Configuration**（无效配置）= DNS 配置有问题
     - ⏳ **Pending**（等待中）= 正在验证，等待 DNS 传播

### 1.2 查看 DNS 配置要求

在 Vercel 的 **"Domains"** 页面，点击你的域名，会显示需要配置的 DNS 记录：

**通常有两种配置方式**：

#### 方式 A：使用 CNAME 记录（推荐）

- **Type**: `CNAME`
- **Name**: `@` 或 `www` 或你的子域名
- **Value**: `cname.vercel-dns.com`（Vercel 会显示具体的值）

#### 方式 B：使用 A 记录

- **Type**: `A`
- **Name**: `@`
- **Value**: `76.76.21.21`（Vercel 会显示具体的 IP）

---

## ✅ 第二步：检查域名注册商的 DNS 配置

### 2.1 登录域名注册商

根据你的域名注册商（阿里云、腾讯云、GoDaddy、Cloudflare 等），登录控制台。

### 2.2 找到 DNS 管理

不同注册商的名称可能不同：
- **阿里云**：域名控制台 → 域名解析
- **腾讯云**：域名管理 → DNS 解析
- **GoDaddy**：DNS Management
- **Cloudflare**：DNS 设置

### 2.3 检查 DNS 记录

确认是否已添加 Vercel 要求的 DNS 记录：

**示例配置**（假设域名是 `libra.example.com`）：

```
Type    Name    Value                    TTL
CNAME   @       cname.vercel-dns.com     自动
CNAME   www     cname.vercel-dns.com     自动
```

或者：

```
Type    Name    Value          TTL
A       @       76.76.21.21    自动
CNAME   www     cname.vercel-dns.com  自动
```

### 2.4 常见错误

❌ **错误 1**：DNS 记录类型错误
- 错误：添加了 `A` 记录，但 Vercel 要求 `CNAME`
- 解决：删除错误的记录，添加正确的记录类型

❌ **错误 2**：记录值错误
- 错误：`Value` 字段填写了错误的地址
- 解决：确保 `Value` 与 Vercel 显示的值完全一致

❌ **错误 3**：Name 字段错误
- 错误：根域名应该用 `@`，但写成了其他值
- 解决：根域名用 `@`，子域名用具体名称（如 `www`）

❌ **错误 4**：多条冲突记录
- 错误：同时存在 `A` 记录和 `CNAME` 记录指向根域名
- 解决：根域名只能有一种记录类型，删除冲突的记录

---

## ✅ 第三步：验证 DNS 配置

### 3.1 使用命令行工具检查

**Windows PowerShell**：

```powershell
# 检查 CNAME 记录
nslookup -type=CNAME your-domain.com

# 检查 A 记录
nslookup -type=A your-domain.com
```

**在线工具**：
- **DNS Checker**：https://dnschecker.org
- **MXToolbox**：https://mxtoolbox.com/DNSLookup.aspx

### 3.2 检查 DNS 传播状态

1. 访问：https://dnschecker.org
2. 输入你的域名
3. 选择记录类型（`CNAME` 或 `A`）
4. 查看全球 DNS 服务器的解析结果
5. 如果大部分服务器显示正确的值，说明 DNS 已生效

**注意**：DNS 传播通常需要：
- 几分钟到几小时（通常 5-30 分钟）
- 最长可能需要 24-48 小时（罕见）

---

## ✅ 第四步：检查 SSL 证书

### 4.1 在 Vercel 中查看 SSL 状态

1. 在 Vercel Dashboard 中，进入 **"Settings"** → **"Domains"**
2. 点击你的域名
3. 查看 **"SSL Certificate"**（SSL 证书）部分：
   - ✅ **Valid**（有效）= 证书已配置
   - ⏳ **Pending**（等待中）= 正在生成证书
   - ❌ **Invalid**（无效）= 证书配置有问题

### 4.2 SSL 证书生成时间

- Vercel 会自动为你的域名生成 SSL 证书
- 通常需要 **几分钟到几小时**
- 如果 DNS 配置正确，证书会自动生成

### 4.3 如果 SSL 证书一直显示 "Pending"

1. **确认 DNS 配置正确**
   - 回到第二步，重新检查 DNS 配置

2. **等待更长时间**
   - 有时需要等待 1-2 小时

3. **删除并重新添加域名**
   - 在 Vercel 中删除域名
   - 等待 5 分钟
   - 重新添加域名

---

## ✅ 第五步：检查浏览器访问

### 5.1 测试不同协议

尝试访问：
- `http://your-domain.com`（HTTP）
- `https://your-domain.com`（HTTPS）

**如果 HTTP 可以访问，但 HTTPS 不行**：
- SSL 证书可能未配置完成
- 等待 SSL 证书生成（通常几分钟）

**如果两者都无法访问**：
- DNS 配置可能有问题
- 回到第二步检查 DNS 配置

### 5.2 检查浏览器错误信息

打开浏览器开发者工具（F12），查看 **"Console"** 和 **"Network"** 标签页：

**常见错误**：
- `ERR_NAME_NOT_RESOLVED` = DNS 解析失败
- `ERR_CONNECTION_TIMED_OUT` = 无法连接到服务器
- `ERR_SSL_PROTOCOL_ERROR` = SSL 证书问题
- `NET::ERR_CERT_AUTHORITY_INVALID` = SSL 证书无效

### 5.3 清除浏览器缓存

有时浏览器缓存了旧的 DNS 记录：

1. **Chrome/Edge**：
   - 按 `Ctrl + Shift + Delete`
   - 选择 **"缓存的图片和文件"**
   - 点击 **"清除数据"**

2. **或者使用无痕模式**：
   - 按 `Ctrl + Shift + N`（Chrome/Edge）
   - 在无痕窗口中访问你的域名

---

## 🔧 快速修复步骤

### 方案 1：重新配置 DNS（推荐）

1. **在 Vercel 中查看 DNS 要求**
   - 进入 **"Settings"** → **"Domains"**
   - 点击你的域名
   - 复制显示的 DNS 配置要求

2. **在域名注册商中删除旧记录**
   - 删除所有与 Vercel 相关的 DNS 记录

3. **添加新的 DNS 记录**
   - 按照 Vercel 显示的要求，添加新的 DNS 记录
   - 确保类型、名称、值完全一致

4. **等待 DNS 传播**
   - 等待 5-30 分钟
   - 使用 https://dnschecker.org 检查传播状态

5. **在 Vercel 中验证**
   - 回到 Vercel Dashboard
   - 查看域名状态是否变为 **"Valid Configuration"**

### 方案 2：删除并重新添加域名

如果 DNS 配置看起来正确，但域名仍然无法访问：

1. **在 Vercel 中删除域名**
   - 进入 **"Settings"** → **"Domains"**
   - 点击域名旁边的 **"..."** → **"Remove"**

2. **等待 5 分钟**

3. **重新添加域名**
   - 点击 **"Add Domain"**
   - 输入你的域名
   - 按照提示配置 DNS

4. **等待 DNS 传播和 SSL 证书生成**

### 方案 3：使用子域名（临时方案）

如果根域名（`example.com`）配置有问题，可以先用子域名（`www.example.com`）：

1. **在 Vercel 中添加子域名**
   - 添加 `www.your-domain.com`

2. **配置 DNS**
   - 添加 CNAME 记录：
     - **Name**: `www`
     - **Value**: `cname.vercel-dns.com`

3. **等待生效**
   - 子域名的 DNS 传播通常更快

---

## 📋 检查清单

在排查时，请确认以下所有项：

- [ ] 域名已在 Vercel 中添加
- [ ] Vercel 显示需要配置的 DNS 记录类型和值
- [ ] 在域名注册商中已添加正确的 DNS 记录
- [ ] DNS 记录的类型、名称、值与 Vercel 要求完全一致
- [ ] 使用 `nslookup` 或在线工具验证 DNS 已生效
- [ ] 等待了足够的时间（至少 10-30 分钟）
- [ ] SSL 证书状态为 "Valid" 或 "Pending"
- [ ] 尝试了 `http://` 和 `https://` 两种协议
- [ ] 清除了浏览器缓存或使用无痕模式测试

---

## 🆘 如果仍然无法解决

请提供以下信息：

1. **域名**（可以隐藏敏感部分，例如：`libra.xxx.com`）
2. **Vercel 域名状态截图**（Settings → Domains）
3. **域名注册商的 DNS 配置截图**
4. **DNS 检查工具结果**（https://dnschecker.org）
5. **浏览器错误信息**（F12 → Console 和 Network 标签页）
6. **访问的完整 URL**（`http://` 还是 `https://`）

有了这些信息，我可以更准确地帮你定位问题！🚀

---

## 💡 常见问题 FAQ

### Q1: DNS 配置正确，但域名仍然无法访问？

**A**: 可能是 DNS 传播延迟。等待 30 分钟到 1 小时，然后再次检查。

### Q2: SSL 证书一直显示 "Pending"？

**A**: 
1. 确认 DNS 配置正确且已生效
2. 等待更长时间（最长可能需要几小时）
3. 如果超过 24 小时仍然 "Pending"，删除并重新添加域名

### Q3: 根域名无法访问，但子域名可以？

**A**: 根域名（`@`）的 DNS 配置可能有问题。检查根域名的 DNS 记录是否正确。

### Q4: 使用 Cloudflare 作为 DNS 提供商？

**A**: 
1. 在 Cloudflare 中，确保 **"Proxy status"**（代理状态）设置为 **"DNS only"**（灰色云朵），而不是 **"Proxied"**（橙色云朵）
2. 或者，在 Vercel 中添加 Cloudflare 的域名验证记录

### Q5: 域名在其他地方可以访问，但在国内无法访问？

**A**: 这可能是网络问题，不是 DNS 配置问题。参考 `docs/国内网络访问解决方案.md`。

---

祝你配置顺利！🎉
