# Git & GitHub 使用指南

## 1. Git 基础配置

### 1.1 安装Git
1. 访问Git官网：https://git-scm.com/downloads
2. 下载对应系统的Git安装包
3. 运行安装程序，使用默认配置即可
4. 安装完成后，打开命令行，输入 `git --version` 验证安装

### 1.2 初始配置
```bash
# 设置用户名（建议使用GitHub用户名）
git config --global user.name "你的GitHub用户名"

# 设置邮箱（建议使用GitHub注册邮箱）
git config --global user.email "你的GitHub邮箱"

# 查看配置
git config --list
```

## 2. GitHub 配置

### 2.1 创建GitHub账号
1. 访问 https://github.com
2. 点击"Sign up"注册账号
3. 填写用户名、邮箱和密码
4. 验证邮箱

### 2.2 配置SSH密钥（推荐）
```bash
# 生成SSH密钥
ssh-keygen -t ed25519 -C "你的GitHub邮箱"

# 查看公钥内容
cat ~/.ssh/id_ed25519.pub

# 测试SSH连接
ssh -T git@github.com
```

添加SSH密钥到GitHub：
1. 登录GitHub
2. 点击右上角头像 -> Settings
3. 左侧菜单选择"SSH and GPG keys"
4. 点击"New SSH key"
5. 粘贴公钥内容并保存

### 2.3 创建GitHub仓库
1. 登录GitHub
2. 点击右上角"+"号 -> "New repository"
3. 填写仓库信息：
   - Repository name：仓库名称
   - Description：仓库描述（可选）
   - Public/Private：公开/私有
   - README：是否初始化README文件
   - .gitignore：选择项目类型
   - License：选择开源协议

## 3. 本地仓库操作

### 3.1 初始化仓库
```bash
# 进入项目目录
cd your-project

# 初始化Git仓库
git init
```

### 3.2 创建.gitignore文件
```bash
# 在项目根目录创建.gitignore文件
touch .gitignore  # Linux/Mac
# 或
echo.> .gitignore  # Windows
```

.gitignore文件内容示例：
```
# Maven
target/
*.jar
*.war
*.ear
*.class

# IDE
.idea/
*.iml
.vscode/
.project
.classpath

# 日志文件
*.log

# 系统文件
.DS_Store
Thumbs.db
```

## 4. Git基本操作

### 4.1 本地操作
```bash
# 查看仓库状态
git status

# 添加文件到暂存区
git add 文件名    # 添加指定文件
git add .       # 添加所有文件

# 提交更改
git commit -m "提交说明"

# 查看提交历史
git log
```

### 4.2 分支操作
```bash
# 查看分支
git branch

# 创建分支
git branch 分支名

# 切换分支
git checkout 分支名
# 或
git switch 分支名    # Git 2.23版本后的新命令

# 创建并切换分支
git checkout -b 分支名
# 或
git switch -c 分支名

# 合并分支
git merge 分支名
```

## 5. GitHub远程仓库操作

### 5.1 关联远程仓库
```bash
# 添加远程仓库（HTTPS方式）
git remote add origin https://github.com/用户名/仓库名.git

# 添加远程仓库（SSH方式，推荐）
git remote add origin git@github.com:用户名/仓库名.git

# 查看远程仓库
git remote -v
```

### 5.2 推送和拉取
```bash
# 首次推送（设置上游分支）
git push -u origin master

# 后续推送
git push

# 拉取更新
git pull

# 克隆仓库
git clone 仓库地址
```

### 5.3 多人协作
```bash
# 获取远程分支
git fetch

# 查看所有分支（包括远程）
git branch -a

# 基于远程分支创建本地分支
git checkout -b local-branch origin/remote-branch
```

## 6. GitHub工作流

### 6.1 Fork工作流
1. 在GitHub上Fork目标仓库
2. 克隆自己的Fork到本地
3. 创建功能分支
4. 提交修改
5. 推送到自己的Fork
6. 创建Pull Request

### 6.2 Pull Request流程
1. 在GitHub上点击"Pull requests"
2. 点击"New pull request"
3. 选择源分支和目标分支
4. 填写PR描述
5. 等待review和合并

## 7. 最佳实践

### 7.1 提交规范
```bash
# 提交格式
<类型>: <描述>

# 常用类型
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 7.2 分支管理
- main/master: 主分支
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复
- release/*: 发布分支

### 7.3 安全建议
1. 不要提交敏感信息（密码、密钥等）
2. 使用.gitignore过滤敏感文件
3. 定期更新依赖包
4. 及时同步远程代码

## 8. 常见问题处理

### 8.1 解决冲突
```bash
# 1. 拉取最新代码
git pull

# 2. 解决冲突文件中的标记
<<<<<<< HEAD
本地代码
=======
远程代码
>>>>>>> branch-name

# 3. 提交解决结果
git add .
git commit -m "resolve conflicts"
```

### 8.2 撤销操作
```bash
# 撤销工作区修改
git checkout -- 文件名
# 或
git restore 文件名

# 撤销暂存区
git reset HEAD 文件名
# 或
git restore --staged 文件名

# 撤销提交
git reset --soft HEAD^    # 保留修改
git reset --hard HEAD^    # 丢弃修改
```

## 9. 文档更新记录

| 日期 | 版本 | 更新内容 | 更新人 |
|------|------|----------|--------|
| 2024-03-09 | v1.0 | 初始版本 | 开发团队 |
| 2024-03-09 | v1.1 | 添加GitHub相关内容 | 开发团队 | 