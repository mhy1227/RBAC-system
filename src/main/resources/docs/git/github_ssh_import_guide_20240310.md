# GitHub SSH密钥导入指南

## 1. 背景说明

### 1.1 使用场景
- 需要在GitHub上配置SSH密钥
- 需要在新设备上使用该密钥
- 更换设备时需要迁移SSH配置
- 本地SSH配置出现问题需要重置
- 需要切换或更换GitHub账号

### 1.2 前提条件
- 可以登录GitHub账号
- 安装了Git（Windows用户需要安装Git Bash）
- 有管理员权限（可以修改.ssh目录）

### 1.3 特殊情况处理
1. **已有多个SSH密钥**
   ```bash
   # 查看现有的SSH密钥
   ls -al ~/.ssh/
   
   # 可能看到的文件：
   # id_rsa（旧账号的私钥）
   # id_rsa.pub（旧账号的公钥）
   # id_ed25519（新账号的私钥）
   # id_ed25519.pub（新账号的公钥）
   ```

2. **账号切换步骤**
   - 备份现有的SSH配置：
     ```bash
     # 备份整个.ssh目录
     cp -r ~/.ssh ~/.ssh_backup
     
     # 重命名现有密钥（如果要保留的话）
     mv ~/.ssh/id_rsa ~/.ssh/id_rsa_old_account
     mv ~/.ssh/id_rsa.pub ~/.ssh/id_rsa_old_account.pub
     ```
   
   - 创建或修改SSH配置文件：
     ```bash
     # 编辑配置文件
     nano ~/.ssh/config
     
     # 添加以下内容：
     # 旧账号配置（如果需要保留）
     Host github-old
         HostName github.com
         User git
         IdentityFile ~/.ssh/id_rsa_old_account
     
     # 新账号配置
     Host github.com
         HostName github.com
         User git
         IdentityFile ~/.ssh/id_ed25519
     ```

3. **验证配置**
   ```bash
   # 测试新账号连接
   ssh -T git@github.com
   
   # 如果保留了旧账号，测试旧账号连接
   ssh -T git@github-old
   ```

4. **更新远程仓库地址**
   ```bash
   # 查看当前远程仓库
   git remote -v
   
   # 移除旧的远程仓库
   git remote remove origin
   
   # 添加新的远程仓库（使用新账号）
   git remote add origin git@github.com:新用户名/仓库名.git
   ```

## 2. 生成SSH密钥

### 2.1 本地生成SSH密钥
1. 打开终端（Windows用户使用Git Bash）
2. 生成新的SSH密钥：
   ```bash
   # 使用你的GitHub邮箱
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```
3. 提示选择保存位置时，直接按Enter使用默认位置
4. 提示输入密码时，可以直接按Enter（不设置密码）或设置一个密码

### 2.2 查看公钥内容
```bash
# 查看公钥内容
cat ~/.ssh/id_ed25519.pub

# 输出示例：
# ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... your_email@example.com
```

### 2.3 添加到GitHub
1. 复制公钥内容（整行，从ssh-ed25519开始到邮箱结束）
2. 登录GitHub账号
3. 点击右上角头像
4. 选择"Settings"（设置）
5. 在左侧菜单栏找到并点击"SSH and GPG keys"
6. 点击"New SSH key"按钮
7. 填写表单：
   - Title：给密钥起个描述性名称（如："我的工作电脑"）
   - Key Type：选择"Authentication Key"
   - Key：粘贴刚才复制的公钥内容
8. 点击"Add SSH key"保存

### 2.4 测试连接
```bash
# 测试SSH连接
ssh -T git@github.com

# 首次连接会看到警告：
The authenticity of host 'github.com (IP ADDRESS)' can't be established.
# 输入 yes 继续

# 如果成功，会显示：
Hi username! You've successfully authenticated, but GitHub does not provide shell access.
```

## 3. 本地配置步骤

### 3.1 备份现有SSH配置
```bash
# 如果存在旧的SSH配置，先备份
mv ~/.ssh ~/.ssh_backup_$(date +%Y%m%d)

# 创建新的.ssh目录
mkdir ~/.ssh
```

### 3.2 从GitHub获取密钥信息
1. 登录GitHub账号
2. 进入Settings -> SSH and GPG keys
3. 找到需要使用的SSH密钥
4. 记录以下信息：
   - 密钥类型（如：ed25519或rsa）
   - 公钥内容
   - 密钥注释（通常是邮箱地址）

### 3.3 配置本地SSH文件
1. **创建私钥文件**
   ```bash
   # 创建并设置权限
   touch ~/.ssh/id_ed25519  # 如果是RSA密钥，则为id_rsa
   chmod 600 ~/.ssh/id_ed25519
   ```

2. **创建公钥文件**
   ```bash
   # 创建公钥文件
   touch ~/.ssh/id_ed25519.pub  # 如果是RSA密钥，则为id_rsa.pub
   chmod 644 ~/.ssh/id_ed25519.pub
   
   # 将GitHub上的公钥内容复制到此文件
   echo "从GitHub复制的公钥内容" > ~/.ssh/id_ed25519.pub
   ```

3. **创建配置文件**
   ```bash
   # 创建并设置权限
   touch ~/.ssh/config
   chmod 600 ~/.ssh/config
   
   # 添加基本配置
   echo "Host github.com
       HostName github.com
       User git
       IdentityFile ~/.ssh/id_ed25519" > ~/.ssh/config
   ```

### 3.4 测试配置
```bash
# 测试SSH连接
ssh -T git@github.com

# 如果成功，会显示：
# Hi username! You've successfully authenticated, but GitHub does not provide shell access.
```

## 4. 常见问题

### 4.1 权限问题
```bash
# 确保文件权限正确
chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_ed25519
chmod 644 ~/.ssh/id_ed25519.pub
chmod 600 ~/.ssh/config
```

### 4.2 连接测试失败
1. 检查公钥内容是否完整复制
2. 确认GitHub账号状态正常
3. 验证网络连接是否正常

### 4.3 多账号配置
```bash
# ~/.ssh/config 示例
Host github-personal
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_ed25519_personal

Host github-work
    HostName github.com
    User git
    IdentityFile ~/.ssh/id_ed25519_work
```

## 5. 安全建议

### 5.1 私钥保护
- 永远不要分享私钥文件
- 定期更换SSH密钥
- 及时删除不再使用的密钥

### 5.2 备份建议
- 安全保存原始密钥信息
- 记录密钥对应的账号信息
- 保持GitHub账号的安全性

## 6. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-10 | v1.0 | 初始版本 | 开发团队 |
| 2024-03-10 | v1.1 | 添加GitHub生成SSH密钥的步骤 | 开发团队 | 