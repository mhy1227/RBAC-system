# Git SSH配置教程

## 1. 基础知识

### 1.1 什么是SSH？
SSH（Secure Shell）是一种加密的网络传输协议，可以在不安全的网络中为网络服务提供安全的传输环境。GitHub使用SSH来安全地连接和验证。

### 1.2 为什么要配置SSH？
- 更安全：使用加密通信
- 更方便：不用每次都输入用户名密码
- 更专业：这是开发人员的标准配置

## 2. 详细配置步骤

### 2.1 检查现有SSH密钥
1. 打开Git Bash（在Windows中右键点击桌面或文件夹，选择"Git Bash Here"）
2. 输入命令检查是否已有SSH密钥：
   ```bash
   ls -al ~/.ssh
   ```
3. 如果显示以下文件，说明已经有SSH密钥：
   - id_rsa.pub
   - id_ecdsa.pub
   - id_ed25519.pub

### 2.2 生成新的SSH密钥
1. 打开Git Bash
2. 输入以下命令（替换为你的GitHub邮箱）：
   ```bash
   ssh-keygen -t ed25519 -C "你的邮箱@example.com"
   ```
3. 提示选择文件保存位置时，直接按Enter使用默认位置
4. 提示输入密码时，可以直接按Enter（不设置密码）
   ```
   > Enter passphrase (empty for no passphrase): [按Enter]
   > Enter same passphrase again: [按Enter]
   ```

### 2.3 查看生成的SSH公钥
1. 在Git Bash中输入：
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```
2. 你会看到类似这样的内容：
   ```
   ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... your.email@example.com
   ```
3. 复制整个内容（从ssh-ed25519开始到邮箱结束）

### 2.4 添加SSH密钥到GitHub
1. 登录GitHub网站：https://github.com
2. 点击右上角头像
3. 选择Settings（设置）
4. 在左侧菜单找到"SSH and GPG keys"
5. 点击"New SSH key"（新建SSH密钥）
6. 填写表单：
   - Title：给密钥起个名字（如："我的笔记本"）
   - Key：粘贴之前复制的公钥内容
7. 点击"Add SSH key"保存

### 2.5 测试SSH连接
1. 在Git Bash中输入：
   ```bash
   ssh -T git@github.com
   ```
2. 可能会看到警告：
   ```
   > The authenticity of host 'github.com (IP ADDRESS)' can't be established.
   > RSA key fingerprint is SHA256:nThbg6kXUpJWGl7E1IGOCspRomTxdCARLviKw6E5SY8.
   > Are you sure you want to continue connecting (yes/no)?
   ```
3. 输入"yes"并按Enter
4. 如果看到以下消息，说明配置成功：
   ```
   > Hi username! You've successfully authenticated, but GitHub does not provide shell access.
   ```

## 3. 常见问题解决

### 3.1 权限问题
如果遇到"Permission denied"错误：
1. 确认已经将公钥添加到GitHub
2. 确认使用的是正确的SSH密钥
3. 尝试：
   ```bash
   ssh-add ~/.ssh/id_ed25519
   ```

### 3.2 多个GitHub账号
如果需要配置多个GitHub账号：
1. 为每个账号生成不同的SSH密钥
2. 在~/.ssh/config文件中配置不同的Host

### 3.3 密钥文件丢失
如果密钥文件丢失：
1. 删除GitHub上的旧密钥
2. 重新生成新的密钥对
3. 将新的公钥添加到GitHub

## 4. 使用建议

### 4.1 安全建议
1. 私钥文件（id_ed25519）不要分享给任何人
2. 建议为密钥设置密码（passphrase）
3. 定期更换SSH密钥

### 4.2 备份建议
1. 备份整个.ssh文件夹
2. 记录已添加密钥的GitHub账号
3. 保存密钥密码（如果设置了）

## 5. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-09 | v1.0 | 初始版本 | 开发团队 |

## 6. 实际操作示例

### 6.1 检查现有SSH密钥
```bash
$ ls -al ~/.ssh
total 51
drwxr-xr-x 1 xxykl 197121    0 Jan 29 12:33 ./
drwxr-xr-x 1 xxykl 197121    0 Mar 10 11:12 ../
-rw-r--r-- 1 xxykl 197121 2590 Nov 18 15:21 id_rsa          # 私钥文件
-rw-r--r-- 1 xxykl 197121  562 Nov 18 15:21 id_rsa.pub      # 公钥文件
-rw-r--r-- 1 xxykl 197121  454 Jan 29 12:33 known_hosts
-rw-r--r-- 1 xxykl 197121  279 Jan 29 12:33 known_hosts.old
```
从输出可以看到已经存在SSH密钥对：
- id_rsa：私钥文件
- id_rsa.pub：公钥文件

### 6.2 查看公钥内容
```bash
$ cat ~/.ssh/id_rsa.pub
ssh-rsa AAAAB3NzaC1yc2EAAAADA... [中间内容省略] ...ns9eH33wo2g/+8sM= hmym0604
```
注意：
- 实际公钥内容很长，这里省略了中间部分
- 公钥通常以"ssh-rsa"开头，以邮箱或用户名结尾
- 需要完整复制整个内容，不要漏掉或多复制任何字符

### 6.3 添加到GitHub的步骤
1. 访问：https://github.com/settings/keys
2. 点击"New SSH key"
3. 填写表单：
   - Title：填写"我的Windows笔记本"
   - Key：粘贴上一步复制的完整公钥内容
4. 点击"Add SSH key"保存

### 6.4 测试连接
```bash
$ ssh -T git@github.com
Hi mhy1227! You've successfully authenticated, but GitHub does not provide shell access.
```
看到这个消息就说明SSH配置成功了。

### 6.5 后续操作
现在可以使用SSH方式克隆或操作仓库：
```bash
# 克隆仓库（使用SSH地址）
git clone git@github.com:用户名/仓库名.git

# 或者将现有仓库改为SSH方式
git remote set-url origin git@github.com:用户名/仓库名.git
```

## 7. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-09 | v1.0 | 初始版本 | 开发团队 |
| 2024-03-10 | v1.1 | 添加实际操作示例 | 开发团队 | 

## 8. SSH安全说明

### 8.1 SSH的安全机制
1. **非对称加密原理**
   - 私钥：存储在本地，相当于您的钥匙，要严格保管
   - 公钥：上传到GitHub，相当于锁，可以公开分享
   - 工作原理：只有对应的私钥才能解开公钥加密的内容

### 8.2 安全风险防范
1. **私钥保护**
   - 永远不要分享私钥文件（如：id_ed25519、id_rsa）
   - 不要将私钥上传到公共仓库或网络
   - 私钥文件建议设置600权限（仅所有者可读写）

2. **账号安全**
   - 建议开启GitHub二次验证（2FA）
   - 定期检查GitHub访问日志
   - 定期更换SSH密钥

3. **应急措施**
   - 如果私钥泄露，立即在GitHub删除对应公钥
   - 如果设备丢失，及时删除该设备的SSH密钥
   - 保持本地Git和系统及时更新

### 8.3 多密钥管理最佳实践
1. **密钥命名规范**
   ```bash
   ~/.ssh/
   ├── id_ed25519_github_personal    # 个人GitHub账号
   ├── id_ed25519_github_work        # 工作GitHub账号
   ├── id_ed25519_gitlab_company     # 公司GitLab
   └── config                        # SSH配置文件
   ```

2. **配置文件示例**
   ```bash
   # 个人GitHub
   Host github.com-personal
       HostName github.com
       User git
       IdentityFile ~/.ssh/id_ed25519_github_personal

   # 工作GitHub
   Host github.com-work
       HostName github.com
       User git
       IdentityFile ~/.ssh/id_ed25519_github_work
   ```

### 8.4 日常维护建议
1. **定期检查**
   - 检查SSH密钥列表是否都是自己的设备
   - 删除不再使用的设备的密钥
   - 确认密钥文件权限正确

2. **备份建议**
   - 备份重要的私钥文件
   - 记录各个密钥的用途
   - 保存密钥的密码（如果设置了）

3. **安全习惯**
   - 不同账号使用不同的SSH密钥
   - 重要账号的密钥建议设置密码
   - 定期更新系统和Git客户端

## 9. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-09 | v1.0 | 初始版本 | 开发团队 |
| 2024-03-10 | v1.1 | 添加实际操作示例 | 开发团队 |
| 2024-03-10 | v1.2 | 补充SSH安全说明和最佳实践 | 开发团队 | 

## 10. SSH配置后的实际应用

### 10.1 创建GitHub仓库
1. **登录GitHub**
2. **创建新仓库**
   - 点击右上角"+"号 → "New repository"
   - 填写仓库信息：
     - Repository name：仓库名称（如：cursor-rbac）
     - Description：项目描述
     - Public/Private：选择仓库可见性
     - 不要勾选"Initialize this repository with a README"（如果已有本地代码）

### 10.2 本地仓库操作
1. **初始化本地仓库**
   ```bash
   # 进入项目目录
   cd 项目目录

   # 初始化Git仓库
   git init

   # 添加.gitignore文件（如果需要）
   # 可以从之前的模板复制

   # 添加所有文件到暂存区
   git add .

   # 首次提交
   git commit -m "初始化项目"
   ```

2. **关联远程仓库**
   ```bash
   # 添加远程仓库（使用SSH地址）
   git remote add origin git@github.com:用户名/仓库名.git

   # 推送到GitHub（-u 设置上游分支，之后可以直接用git push）
   git push -u origin master  # 或 main，取决于默认分支
   ```

### 10.3 验证配置
1. **检查远程仓库配置**
   ```bash
   # 查看远程仓库信息
   git remote -v
   ```
   应该看到类似输出：
   ```
   origin  git@github.com:用户名/仓库名.git (fetch)
   origin  git@github.com:用户名/仓库名.git (push)
   ```

2. **测试推送**
   ```bash
   # 修改一些文件后
   git add .
   git commit -m "更新说明"
   git push
   ```

### 10.4 常见问题
1. **推送被拒绝**
   - 可能是远程仓库有其他更改
   - 先执行 `git pull` 同步远程更改
   - 解决冲突后再推送

2. **分支名不匹配**
   - 查看当前分支：`git branch`
   - 创建新分支：`git checkout -b main`
   - 重新推送：`git push -u origin main`

## 11. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-09 | v1.0 | 初始版本 | 开发团队 |
| 2024-03-10 | v1.1 | 添加实际操作示例 | 开发团队 |
| 2024-03-10 | v1.2 | 补充SSH安全说明和最佳实践 | 开发团队 |
| 2024-03-10 | v1.3 | 添加SSH配置后的实际应用指南 | 开发团队 | 