# RBAC系统缓存管理模块分析

## 1. 模块概述

缓存管理模块是RBAC系统的性能优化基础设施，基于Redis实现，采用多级缓存策略管理用户、角色权限等高频访问数据。该模块实现了缓存的自动加载、更新同步、失效控制等功能，并提供了完善的缓存监控和管理机制。

## 2. 核心组件

### 2.1 配置管理
```yaml
rbac:
  cache:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 3000
    permission:
      expire: 3600  # 权限缓存过期时间(秒)
      prefix: "perm:"  # 权限缓存key前缀
    user:
      expire: 7200  # 用户信息缓存过期时间(秒)
      prefix: "user:"  # 用户缓存key前缀
```

### 2.2 缓存服务实现
```java
@Service
@Slf4j
public class RedisCacheServiceImpl implements CacheService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Value("${rbac.cache.permission.expire}")
    private long permissionExpire;
    
    @Value("${rbac.cache.permission.prefix}")
    private String permissionPrefix;
    
    @Override
    public void setPermissions(Long userId, Set<String> permissions) {
        String key = permissionPrefix + userId;
        try {
            String value = JsonUtils.toJson(permissions);
            redisTemplate.opsForValue().set(key, value, permissionExpire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("缓存权限信息失败: {}", e.getMessage());
            throw new CacheException("缓存操作失败");
        }
    }
    
    @Override
    public Set<String> getPermissions(Long userId) {
        String key = permissionPrefix + userId;
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return JsonUtils.fromJson(value, new TypeReference<Set<String>>() {});
        } catch (Exception e) {
            log.error("获取权限缓存失败: {}", e.getMessage());
            throw new CacheException("缓存读取失败");
        }
    }
    
    @Override
    public void removePermissions(Long userId) {
        String key = permissionPrefix + userId;
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除权限缓存失败: {}", e.getMessage());
            throw new CacheException("缓存删除失败");
        }
    }
}
```

## 3. 缓存策略

### 3.1 多级缓存
1. 本地缓存(Caffeine)
   - 最热数据
   - 快速访问
   - 内存占用控制
   - 自动失效

2. 分布式缓存(Redis)
   - 共享数据
   - 一致性保证
   - 持久化支持
   - 原子操作

### 3.2 更新机制
1. 更新策略
   - 同步更新
   - 异步更新
   - 定时更新
   - 按需更新

2. 一致性保证
   - 双删策略
   - 延迟双删
   - 版本号控制
   - 更新队列

### 3.3 预热机制
1. 系统启动预热
   - 核心数据预加载
   - 分批加载
   - 异步加载
   - 优先级控制

2. 动态预热
   - 访问预测
   - 智能预加载
   - 负载感知
   - 容量控制

## 4. 性能优化

### 4.1 缓存命中率优化
- 合理的过期时间
- 智能的淘汰策略
- 预热机制优化
- 热点数据分析

### 4.2 内存优化
- 数据压缩
- 序列化优化
- 淘汰策略调整
- 内存监控

### 4.3 并发处理
- 并发更新控制
- 缓存击穿防护
- 缓存雪崩防护
- 热点数据防护

## 5. 应用场景

### 5.1 权限缓存
```java
@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public Set<String> getUserPermissions(Long userId) {
        // 1. 查询缓存
        Set<String> permissions = cacheService.getPermissions(userId);
        if (permissions != null) {
            return permissions;
        }
        
        // 2. 查询数据库
        permissions = permissionMapper.selectByUserId(userId);
        
        // 3. 更新缓存
        cacheService.setPermissions(userId, permissions);
        
        return permissions;
    }
    
    @Override
    @Transactional
    public void updateUserPermissions(Long userId, Set<String> permissions) {
        // 1. 更新数据库
        permissionMapper.updateUserPermissions(userId, permissions);
        
        // 2. 删除旧缓存
        cacheService.removePermissions(userId);
        
        // 3. 设置新缓存
        cacheService.setPermissions(userId, permissions);
    }
}
```

### 5.2 用户信息缓存
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private CacheService cacheService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserInfo getUserInfo(Long userId) {
        // 1. 查询缓存
        UserInfo userInfo = cacheService.getUserInfo(userId);
        if (userInfo != null) {
            return userInfo;
        }
        
        // 2. 查询数据库
        userInfo = userMapper.selectById(userId);
        
        // 3. 更新缓存
        cacheService.setUserInfo(userId, userInfo);
        
        return userInfo;
    }
}
```

## 6. 监控管理

### 6.1 性能监控
- 命中率监控
- 延迟监控
- QPS监控
- 内存使用监控

### 6.2 异常监控
- 缓存击穿监控
- 缓存雪崩监控
- 更新失败监控
- 连接异常监控

### 6.3 运维管理
- 缓存清理接口
- 缓存统计查看
- 预热控制接口
- 配置动态调整

## 7. 待优化点

### 7.1 功能优化
- 支持更多数据类型
- 增强预热机制
- 完善监控功能
- 优化更新策略

### 7.2 性能优化
- 提升命中率
- 优化内存使用
- 提高并发能力
- 降低延迟

### 7.3 可用性优化
- 提升容错能力
- 增强监控告警
- 优化运维功能
- 完善文档

## 8. 最佳实践

### 8.1 开发规范
- 统一的key设计
- 合理的过期时间
- 规范的更新流程
- 完整的异常处理

### 8.2 性能规范
- 控制缓存粒度
- 优化序列化方式
- 合理使用批量操作
- 注意内存控制

### 8.3 运维规范
- 监控指标定义
- 告警阈值设置
- 容量规划方法
- 应急处理流程 