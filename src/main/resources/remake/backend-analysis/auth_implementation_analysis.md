# RBAC系统鉴权实现分析

## 1. 鉴权实现概述

鉴权（Authorization）是RBAC系统的核心功能，主要解决"用户是否有权限执行某个操作"的问题。我们的实现采用了Spring Security + 自定义注解的方式。

### 1.1 整体架构
```
请求 -> 认证过滤器 -> 鉴权拦截器 -> 权限注解 -> 业务处理
```

### 1.2 核心组件
1. 权限注解
2. 权限拦截器
3. 权限验证服务
4. 权限缓存管理

## 2. 具体实现

### 2.1 自定义权限注解
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    // 权限标识
    String value();
    
    // 权限验证逻辑
    Logical logical() default Logical.AND;
    
    // 是否启用缓存
    boolean useCache() default true;
}
```

### 2.2 权限拦截器实现
```java
@Aspect
@Component
@Order(2)
public class PermissionAspect {
    @Autowired
    private PermissionService permissionService;
    
    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint point, 
            RequiresPermission requiresPermission) throws Throwable {
        // 1. 获取当前用户
        Long userId = SecurityContextHolder.getCurrentUserId();
        
        // 2. 获取所需权限
        String permission = requiresPermission.value();
        
        // 3. 验证权限
        if (!permissionService.hasPermission(userId, permission)) {
            throw new NoPermissionException("没有操作权限");
        }
        
        // 4. 执行原方法
        return point.proceed();
    }
}
```

### 2.3 权限验证服务
```java
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Cacheable(value = "permission", key = "#userId + ':' + #permission")
    @Override
    public boolean hasPermission(Long userId, String permission) {
        // 1. 获取用户角色
        List<Long> roleIds = userRoleMapper.findRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        
        // 2. 检查角色权限
        return rolePermissionMapper.hasPermission(roleIds, permission) > 0;
    }
}
```

## 3. 使用示例

### 3.1 接口层使用
```java
@RestController
@RequestMapping("/user")
public class UserController {
    @RequiresPermission("user:create")
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        // 业务逻辑
        return Result.success(userService.createUser(user));
    }
    
    @RequiresPermission("user:update")
    @PutMapping
    public Result<Boolean> updateUser(@RequestBody User user) {
        // 业务逻辑
        return Result.success(userService.updateUser(user));
    }
}
```

### 3.2 服务层使用
```java
@Service
public class UserServiceImpl implements UserService {
    @RequiresPermission("user:sensitive:view")
    public UserSensitiveInfo getUserSensitiveInfo(Long userId) {
        // 业务逻辑
        return userMapper.findSensitiveInfo(userId);
    }
}
```

## 4. 性能优化

### 4.1 缓存策略
1. 权限结果缓存
```java
@Cacheable(value = "permission", key = "#userId + ':' + #permission")
public boolean hasPermission(Long userId, String permission) {
    // 验证逻辑
}
```

2. 角色权限缓存
```java
@Cacheable(value = "role:permissions", key = "#roleId")
public List<String> getRolePermissions(Long roleId) {
    // 查询逻辑
}
```

### 4.2 批量验证优化
```java
public boolean hasPermissions(Long userId, List<String> permissions) {
    // 1. 批量查询缓存
    List<String> uncachedPermissions = filterUncached(permissions);
    
    // 2. 一次性查询数据库
    if (!CollectionUtils.isEmpty(uncachedPermissions)) {
        batchCheckAndCache(userId, uncachedPermissions);
    }
    
    // 3. 返回结果
    return allPermissionsGranted(userId, permissions);
}
```

## 5. 特色功能

### 5.1 动态权限
```java
public boolean hasPermission(Long userId, String permission, Map<String, Object> context) {
    // 1. 基础权限检查
    if (!hasBasicPermission(userId, permission)) {
        return false;
    }
    
    // 2. 动态条件检查
    return evaluateCondition(userId, permission, context);
}
```

### 5.2 权限继承
```java
public Set<String> getAllPermissions(String permission) {
    Set<String> result = new HashSet<>();
    result.add(permission);
    
    // 添加父级权限
    String parentPermission = getParentPermission(permission);
    while (parentPermission != null) {
        result.add(parentPermission);
        parentPermission = getParentPermission(parentPermission);
    }
    
    return result;
}
```

## 6. 最佳实践

### 6.1 权限设计
1. 权限命名规范
   - 模块名:操作:对象
   - 例如：user:create、role:update:basic

2. 权限粒度控制
   - 接口级权限
   - 字段级权限
   - 数据级权限

### 6.2 使用建议
1. 合理使用缓存
2. 注意权限粒度
3. 做好权限继承
4. 异常处理完善

### 6.3 注意事项
1. 权限验证要全面
2. 防止权限泄露
3. 定期清理缓存
4. 做好日志记录

## 7. 总结

RBAC系统的鉴权实现通过：
1. 注解 + AOP实现权限拦截
2. 缓存机制保证性能
3. 灵活的权限模型
4. 完善的异常处理

为系统提供了强大而灵活的权限控制能力。 