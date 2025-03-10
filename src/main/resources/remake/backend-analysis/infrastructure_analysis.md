# RBAC系统基础设施模块分析

## 1. 模块概述

基础设施模块是RBAC系统的核心支撑组件，包含拦截器机制、上下文管理、全局异常处理等基础功能。该模块为系统提供了统一的认证授权、上下文管理、异常处理等基础服务。

## 2. 核心组件

### 2.1 拦截器机制

#### 认证拦截器（AuthInterceptor）
```java
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/logout",
        "/auth/refresh",
        "/error"
        // ... 其他白名单URL
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 白名单检查
        if (isWhiteListUrl(requestURI)) {
            return true;
        }

        // 2. Token验证
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        // 3. Token有效性验证
        Claims claims = JwtUtil.parseJwt(token);
        request.setAttribute("userId", claims.get("userId"));
        
        return true;
    }
}
```

#### 用户上下文拦截器（UserContextInterceptor）
```java
@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            Long userId = JwtUtil.getCurrentUserId();
            if (userId != null) {
                UserVO user = userService.findById(userId);
                UserContext.setUser(user);
            }
        } catch (Exception e) {
            log.warn("设置用户上下文失败: {}", e.getMessage());
        }
        return true;
    }

    @Override
    public void afterCompletion(...) {
        UserContext.clear();
    }
}
```

### 2.2 上下文管理

#### 用户上下文（UserContext）
```java
public class UserContext {
    private static final ThreadLocal<UserVO> userHolder = new ThreadLocal<>();

    public static void setUser(UserVO user) {
        userHolder.set(user);
    }

    public static UserVO getCurrentUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }
}
```

### 2.3 JWT令牌管理

#### JWT工具类
```java
public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    public static String generateToken(UserVO user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static Claims parseJwt(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
```

## 3. 安全机制

### 3.1 认证流程
1. 请求到达系统
2. AuthInterceptor拦截请求
3. 检查是否为白名单URL
4. 验证Token有效性
5. 解析用户信息
6. 设置用户上下文

### 3.2 会话管理
1. 基于JWT的无状态会话
2. Token过期机制
3. Token刷新机制
4. 用户上下文管理

### 3.3 安全防护
1. 白名单机制
2. Token有效性验证
3. 用户信息隔离
4. 线程安全保证

## 4. 性能优化

### 4.1 拦截器优化
- 白名单匹配优化
- Token验证缓存
- 用户信息缓存

### 4.2 上下文优化
- ThreadLocal资源管理
- 及时清理上下文
- 防止内存泄漏

### 4.3 JWT优化
- Token压缩
- 签名算法选择
- 过期时间策略

## 5. 特色功能

### 5.1 灵活的拦截器链
- 可配置的拦截器顺序
- 动态的白名单管理
- 细粒度的权限控制

### 5.2 安全的上下文管理
- 线程隔离
- 自动清理
- 异常处理

### 5.3 高效的JWT处理
- 自动续期
- 黑名单支持
- 并发控制

## 6. 应用场景

### 6.1 用户认证
```java
@PostMapping("/login")
public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
    // 1. 验证用户名密码
    UserVO user = userService.login(loginDTO);
    
    // 2. 生成Token
    String token = JwtUtil.generateToken(user);
    
    // 3. 返回登录信息
    return Result.success(new LoginVO(token, user));
}
```

### 6.2 权限校验
```java
@GetMapping("/user/info")
@RequirePermission("sys:user:query")
public Result<UserVO> getUserInfo() {
    // 从上下文获取当前用户
    UserVO currentUser = UserContext.getCurrentUser();
    return Result.success(currentUser);
}
```

## 7. 待优化点

### 7.1 功能优化
- Token刷新机制优化
- 权限缓存优化
- 拦截器性能优化

### 7.2 安全优化
- Token加密增强
- 防重放攻击
- 并发控制优化

### 7.3 可用性优化
- 异常处理完善
- 日志记录优化
- 监控指标完善

## 8. 最佳实践

### 8.1 拦截器使用
- 合理配置顺序
- 优化处理逻辑
- 做好异常处理

### 8.2 上下文使用
- 及时清理资源
- 避免上下文泄露
- 异常时清理

### 8.3 JWT使用
- 合理设置过期时间
- 定期轮换密钥
- 做好续期处理