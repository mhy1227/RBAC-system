# RBAC系统安全问题模块分析

## 1. 模块概述

安全问题模块是RBAC系统中重要的安全保障机制，主要用于用户身份验证的补充手段。该模块支持密码找回、重要操作验证等场景，实现了密保问题的设置、验证、管理等功能，并包含完善的安全防护机制。

## 2. 核心组件

### 2.1 控制器层（SecurityQuestionController）
```java
@Slf4j
@RestController
@RequestMapping("/security/question")
@RequiredArgsConstructor
public class SecurityQuestionController {
    private final SecurityQuestionService securityQuestionService;

    @PostMapping("/set")
    public Result<Boolean> setSecurityQuestions(@RequestBody UserSecurity userSecurity) {
        return Result.success(securityQuestionService.setSecurityQuestions(userSecurity));
    }

    @PostMapping("/verify/{userId}")
    public Result<Boolean> verifyAnswers(
            @PathVariable Long userId,
            @RequestBody List<String> answers) {
        return Result.success(securityQuestionService.verifyAnswers(userId, answers));
    }

    @GetMapping("/{userId}")
    public Result<UserSecurity> getUserSecurity(@PathVariable Long userId) {
        return Result.success(securityQuestionService.getUserSecurity(userId));
    }
}
```

### 2.2 服务层（SecurityQuestionServiceImpl）
```java
@Slf4j
@Service    
@RequiredArgsConstructor
public class SecurityQuestionServiceImpl implements SecurityQuestionService {
    private final SecurityQuestionMapper securityQuestionMapper;
    private final SysUserMapper userMapper;
    
    private static final int MAX_ERROR_COUNT = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setSecurityQuestions(UserSecurity userSecurity) {
        // 检查是否已设置密保
        UserSecurity existingSecurity = securityQuestionMapper.findByUserId(userSecurity.getUserId());
        boolean result;
        if (existingSecurity == null) {
            result = securityQuestionMapper.insertUserSecurity(userSecurity) > 0;
        } else {
            result = securityQuestionMapper.updateUserSecurity(userSecurity) > 0;
        }
        
        // 更新用户表密保状态
        if (result) {
            userMapper.updateSecurityStatus(userSecurity.getUserId(), 1);
        }
        return result;
    }
}
```

## 3. 核心功能

### 3.1 密保问题管理
1. 问题模板管理
   - 添加模板
   - 更新模板
   - 启用/禁用模板
   - 模板分类管理

2. 用户密保设置
   - 设置密保问题
   - 更新密保问题
   - 重置密保问题
   - 密保状态管理

### 3.2 答案验证机制
1. 验证流程
   - 获取用户密保信息
   - 验证答案匹配度
   - 记录验证结果
   - 更新错误计数

2. 安全控制
   - 最大错误次数限制
   - 错误次数统计
   - 临时锁定机制
   - 答案加密存储

## 4. 安全特性

### 4.1 防暴力破解
- 错误次数限制
- 账号锁定机制
- 验证频率控制
- IP限制

### 4.2 数据安全
- 答案加密存储
- 传输加密
- 访问控制
- 操作审计

### 4.3 业务安全
- 状态检查
- 权限验证
- 操作记录
- 异常处理

## 5. 实现机制

### 5.1 数据结构
1. 问题模板
   - 模板ID
   - 问题内容
   - 问题类型
   - 状态标识

2. 用户密保
   - 用户ID
   - 问题ID列表
   - 答案列表
   - 设置时间

### 5.2 核心流程
1. 设置密保
   - 验证用户状态
   - 检查已有设置
   - 保存新设置
   - 更新用户状态

2. 验证答案
   - 获取密保信息
   - 比对答案
   - 更新错误计数
   - 返回验证结果

## 6. 待优化点

### 6.1 功能优化
1. 验证方式扩展
   - 支持动态问题
   - 增加问题难度等级
   - 支持自定义问题

2. 安全性增强
   - 答案复杂度检查
   - 智能风险识别
   - 多因素认证支持

### 6.2 用户体验优化
1. 交互优化
   - 问题推荐机制
   - 答案提示功能
   - 快速解锁通道

2. 流程优化
   - 简化设置流程
   - 优化验证流程
   - 提供找回机制

## 7. 特色功能

### 7.1 模板管理
- 灵活的模板配置
- 多类型支持
- 状态管理
- 使用统计

### 7.2 验证策略
- 多级验证
- 动态难度
- 智能防护
- 风险识别

## 8. 最佳实践

### 8.1 设置规范
- 问题设置建议
- 答案复杂度要求
- 更新周期建议
- 验证场景建议

### 8.2 使用建议
- 合理设置阈值
- 定期更新密保
- 及时处理异常
- 做好数据备份

### 8.3 安全建议
- 加强访问控制
- 实施监控告警
- 定期安全审计
- 制定应急预案 