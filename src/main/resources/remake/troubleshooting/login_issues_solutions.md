# 登录功能问题分析与解决方案

## 1. 后端事件处理问题

### 1.1 问题描述

在用户登录流程中，出现了以下问题：
```log
2025-02-18T21:58:18.226+08:00  INFO  --- 用户登录：admin1
2025-02-18T21:58:18.602+08:00  WARN  --- 当前用户未登录
2025-02-18T21:58:18.602+08:00  WARN  --- 当前用户无权访问用户数据, targetUserId: 1
2025-02-18T21:58:18.890+08:00  INFO  --- Token生成成功 - userId: 1
```

问题原因：事件处理时序不当，在用户完成登录（token生成）之前就触发了需要权限验证的操作。

### 1.2 解决方案

#### 方案一：调整事件发布时机

将登录事件的发布时机从token生成时调整到整个登录流程完成后。

```java
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public LoginVO login(String username, String password) {
        // 1. 验证用户名密码
        // 2. 生成token
        TokenPair tokenPair = tokenService.generateTokenPair(userId);
        // 3. 组装登录返回信息
        LoginVO loginVO = new LoginVO();
        // 4. 登录成功后发布事件
        eventPublisher.publishEvent(new UserLoginEvent(this, userId, loginId, true, null));
        return loginVO;
    }
}
```

优点：
- 符合业务逻辑顺序
- 避免权限检查问题
- 保持事件处理的完整性
- 易于维护和扩展

#### 方案二：修改事件监听器逻辑

修改事件监听器，移除权限检查逻辑。

```java
@Component
public class UserEventListener {
    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        try {
            // 直接记录登录信息，不进行权限检查
            loginInfoService.recordLoginInfo(
                event.getUserId(),
                event.getUsername(),
                event.getLoginId(),
                event.isSuccess(),
                event.getFailureReason()
            );
        } catch (Exception e) {
            log.error("处理用户登录事件失败", e);
        }
    }
}
```

优点：
- 实现简单
- 改动较小

缺点：
- 破坏了统一的权限控制机制
- 可能引入安全隐患

## 2. 前端MIME类型错误

### 2.1 问题描述

前端启动时出现MIME类型错误：
```
Failed to load module script: Expected a JavaScript module script but the server responded with a MIME type of "video/mp2t". Strict MIME type checking is enforced for module scripts per HTML spec.
```

### 2.2 可能原因

1. Vite版本与依赖不匹配
2. TypeScript配置问题
3. 构建配置不正确

### 2.3 解决方案

1. 更新依赖版本：
```json
{
  "dependencies": {
    "vue": "^3.3.8",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^4.5.0",
    "vite": "^5.0.0",
    "typescript": "^5.2.2"
  }
}
```

2. 检查并更新Vite配置：
```typescript
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    host: true
  }
})
```

3. 清理和重新安装步骤：
```bash
# 1. 清理依赖和缓存
rm -rf node_modules
rm package-lock.json
npm cache clean --force

# 2. 重新安装依赖
npm install

# 3. 重启开发服务器
npm run dev
```

### 2.4 预防措施

1. 确保使用兼容的Node.js版本（推荐v16+）
2. 保持依赖版本的一致性
3. 使用稳定版本的依赖包
4. 定期更新依赖包版本

## 3. 后续建议

1. 完善错误处理机制
2. 添加详细的日志记录
3. 实现登录状态监控
4. 优化前端构建配置
5. 添加自动化测试用例 