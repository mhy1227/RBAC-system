# RBAC系统配置说明文档

## 服务器配置
```yaml
server:
  port: 8080  # 服务器端口号
```

## Spring配置
### 数据源配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL驱动类
    url: jdbc:mysql://localhost:3306/rbac_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai  # 数据库连接URL
    username: root  # 数据库用户名
    password: 1234  # 数据库密码
```

### 事务管理配置
```yaml
spring:
  transaction:
    rollback-on-commit-failure: true  # 提交失败时回滚事务
    default-timeout: 30  # 事务超时时间（秒）
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost  # Redis服务器地址
    port: 6379  # Redis服务器端口
    database: 0  # 使用的数据库索引
    timeout: 10000  # 连接超时时间（毫秒）
    lettuce:
      pool:
        max-active: 8  # 连接池最大连接数
        max-wait: -1  # 连接池最大阻塞等待时间（负值表示没有限制）
        min-idle: 0  # 连接池中的最小空闲连接
        max-idle: 8  # 连接池中的最大空闲连接
```

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB  # 单个文件大小限制
      max-request-size: 5MB  # 总请求大小限制
```

## MyBatis配置
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml  # Mapper XML文件位置
  type-aliases-package: com.czj.rbac.model  # 实体类包路径
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰命名转换
```

## API文档配置
```yaml
springdoc:
  api-docs:
    enabled: true  # 启用API文档
  swagger-ui:
    path: /swagger-ui.html  # Swagger UI访问路径
```

## RBAC系统配置
### 文件上传配置
```yaml
rbac:
  upload:
    avatar:
      path: /upload/avatar/  # 头像上传路径
      allowed-types: image/jpeg,image/png,image/gif  # 允许的文件类型
      max-size: 5242880  # 最大文件大小（字节）
```

### JWT配置
```yaml
rbac:
  jwt:
    secret-key: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789  # JWT密钥
    expire-time: 43200000  # Token过期时间（12小时，毫秒）
    refresh-expire-time: 86400000  # 刷新Token过期时间（24小时，毫秒）
    token:
      blacklist-size: 10000  # Token黑名单大小
```

### 缓存配置
```yaml
rbac:
  cache:
    user-permission:
      expire: 3600  # 用户权限缓存过期时间（秒）
    manager-permission:
      expire: 3600  # 管理员权限缓存过期时间（秒）
    user:
      expire: 1800  # 用户信息缓存过期时间（秒）
    role:
      expire: 3600  # 角色信息缓存过期时间（秒）
    permission:
      expire: 3600  # 权限信息缓存过期时间（秒）
    token:
      expire: 7200  # Token缓存过期时间（秒）
```

### 用户配置
```yaml
rbac:
  user:
    default-password: "123456"  # 默认密码
    password:
      min-length: 6  # 密码最小长度
      max-length: 20  # 密码最大长度
      require-number: true  # 是否要求包含数字
      require-letter: true  # 是否要求包含字母
      require-special: false  # 是否要求包含特殊字符
```

### 日志配置
```yaml
rbac:
  log:
    async-enabled: true  # 启用异步日志
    async:
      core-pool-size: 2  # 核心线程数
      max-pool-size: 5  # 最大线程数
      queue-capacity: 100  # 队列容量
    retention-days: 30  # 日志保留天数
```

### 权限配置
```yaml
rbac:
  permission:
    admin: "sys:admin"  # 管理员权限标识
```

## 配置说明

### 数据库配置
- 使用MySQL数据库
- 默认数据库名: rbac_system
- 默认用户名: root
- 默认密码: 123

### Redis配置
- 使用Lettuce连接池
- 默认使用本地Redis
- 连接池最大连接数: 8
- 最大空闲连接: 8

### 文件上传配置
- 支持的图片格式: jpg, png, gif
- 单个文件最大: 5MB
- 上传文件保存路径: /upload/avatar/

### JWT配置
- Token有效期: 12小时
- 刷新Token有效期: 24小时
- 使用自定义密钥

### 缓存配置
- 用户权限缓存: 1小时
- 用户信息缓存: 30分钟
- 角色和权限缓存: 1小时
- Token缓存: 2小时

### 密码策略
- 默认密码: 123456
- 密码长度: 6-20位
- 必须包含: 数字和字母
- 特殊字符: 可选

### 日志配置
- 异步记录日志
- 使用线程池处理
- 日志保留30天 