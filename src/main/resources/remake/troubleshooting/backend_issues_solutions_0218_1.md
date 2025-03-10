 # 后端问题解决方案文档

## 1. MyBatis Mapper XML解析错误

### 问题描述
启动应用时出现以下错误：
```
Caused by: java.io.IOException: Failed to parse mapping resource: 'file [D:\Code\workspace_idea\workspace20250120\cursor-rbac\target\classes\mapper\SecurityQuestionMapper.xml]'

Caused by: org.apache.ibatis.builder.BuilderException: Error creating document instance.  
Cause: org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 2; 文件提前结束。
```

### 错误分析
1. 错误链路：
   - Spring Boot 启动时初始化 Bean
   - 创建 SqlSessionFactory 时解析 Mapper XML 文件
   - 解析 SecurityQuestionMapper.xml 时发生 XML 格式错误
   - 文件提前结束（可能是文件不完整或格式错误）

2. 可能的原因：
   - SecurityQuestionMapper.xml 文件内容为空
   - XML 文件格式不正确（缺少必要的XML声明或根元素）
   - XML 文件编码问题
   - 文件损坏或不完整

### 解决方案

解决了，最终是发现xml里的sql语句不完全，也没写多少
#### 1. 检查文件完整性
1. 确认 SecurityQuestionMapper.xml 文件存在且内容完整
2. 检查文件是否包含基本的 MyBatis Mapper XML 结构：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.czj.rbac.mapper.SecurityQuestionMapper">
    <!-- mapper内容 -->
</mapper>
```

#### 2. 验证文件位置
1. 确认文件位于正确的目录：`src/main/resources/mapper/`
2. 确认 application.yml 中的 MyBatis 配置正确：
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.czj.rbac.model
```

#### 3. 编码处理
1. 确保文件使用 UTF-8 编码
2. 检查是否有特殊字符或BOM标记

#### 4. 构建清理
如果以上都正确，可以尝试：
1. 清理项目：`mvn clean`
2. 重新编译：`mvn compile`
3. 重新构建：`mvn clean install`

### 预防措施
1. 使用 IDE 的 XML 验证功能
2. 添加 MyBatis Generator 插件自动生成规范的 Mapper XML
3. 在提交代码前进行 XML 格式检查
4. 使用版本控制工具追踪文件变更

### 相关配置检查清单
1. pom.xml 依赖
   - mybatis-spring-boot-starter
   - mysql-connector-java

2. application.yml 配置
   - 数据库连接信息
   - MyBatis 配置
   - 日志级别（可选设置为 DEBUG 以查看更多信息）

3. 项目结构
   - Mapper 接口位置
   - XML 文件位置
   - 实体类位置