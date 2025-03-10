# RBAC系统文件上传管理模块分析

## 1. 模块概述

文件上传管理模块是RBAC系统的文件处理基础设施，主要负责用户头像、附件等文件的上传、存储和访问管理。该模块实现了文件上传的安全控制、存储管理、访问授权等功能，并提供了完善的文件处理机制。

## 2. 核心组件

### 2.1 配置管理
```yaml
rbac:
  upload:
    avatar:
      path: /upload/avatar/  # 头像存储路径
      allowed-types: image/jpeg,image/png,image/gif  # 允许的文件类型
      max-size: 5242880  # 最大文件大小(5MB)
    attachment:
      path: /upload/attachment/  # 附件存储路径
      allowed-types: application/pdf,.doc,.docx  # 允许的文件类型
      max-size: 10485760  # 最大文件大小(10MB)
```

### 2.2 上传服务实现
```java
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    
    @Value("${rbac.upload.avatar.path}")
    private String avatarPath;
    
    @Value("${rbac.upload.avatar.allowed-types}")
    private String allowedTypes;
    
    @Value("${rbac.upload.avatar.max-size}")
    private long maxSize;
    
    @Override
    public String uploadAvatar(MultipartFile file) {
        // 1. 验证文件
        validateFile(file);
        
        // 2. 生成文件名
        String fileName = generateFileName(file);
        
        // 3. 保存文件
        try {
            String filePath = avatarPath + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            
            // 4. 返回访问路径
            return filePath;
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage());
            throw new BusinessException("文件上传失败");
        }
    }
    
    private void validateFile(MultipartFile file) {
        // 验证文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (!allowedTypes.contains(contentType)) {
            throw new BusinessException("不支持的文件类型");
        }
    }
    
    private String generateFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return UUID.randomUUID().toString() + "." + extension;
    }
}
```

## 3. 核心功能

### 3.1 文件上传
1. 文件验证
   - 大小限制
   - 类型限制
   - 内容检查
   - 病毒扫描

2. 文件存储
   - 本地存储
   - 文件命名
   - 目录管理
   - 备份机制

### 3.2 文件访问
1. 访问控制
   - 权限验证
   - 链接生成
   - 防盗链
   - 有效期控制

2. 文件下载
   - 断点续传
   - 限速控制
   - 并发限制
   - 日志记录

## 4. 安全特性

### 4.1 上传安全
1. 文件验证
   - MIME类型检查
   - 文件头检查
   - 内容安全扫描
   - 文件名过滤

2. 存储安全
   - 路径转义
   - 目录限制
   - 权限控制
   - 加密存储

### 4.2 访问安全
1. 权限控制
   - 用户认证
   - 权限验证
   - 访问日志
   - 异常监控

2. 防护措施
   - 防盗链
   - 防爬虫
   - 限流控制
   - CC防护

## 5. 性能优化

### 5.1 上传优化
- 分片上传
- 断点续传
- 压缩处理
- 异步处理

### 5.2 存储优化
- 目录分散
- 文件压缩
- 重复检测
- 定期清理

### 5.3 访问优化
- 图片压缩
- CDN加速
- 缓存策略
- 并发控制

## 6. 应用场景

### 6.1 头像上传
```java
@PostMapping("/avatar")
public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
    // 1. 验证文件
    if (file.isEmpty()) {
        throw new BusinessException("请选择文件");
    }
    
    // 2. 上传文件
    String filePath = fileUploadService.uploadAvatar(file);
    
    // 3. 更新用户头像
    Long userId = SecurityUtils.getLoginUserId();
    userService.updateAvatar(userId, filePath);
    
    return Result.success(filePath);
}
```

### 6.2 文件下载
```java
@GetMapping("/download/{fileId}")
public void downloadFile(@PathVariable String fileId, HttpServletResponse response) {
    // 1. 获取文件信息
    FileInfo fileInfo = fileService.getFileInfo(fileId);
    if (fileInfo == null) {
        throw new BusinessException("文件不存在");
    }
    
    // 2. 验证权限
    if (!fileService.hasPermission(SecurityUtils.getLoginUserId(), fileId)) {
        throw new BusinessException("没有权限访问");
    }
    
    // 3. 设置响应头
    response.setContentType(fileInfo.getContentType());
    response.setHeader("Content-Disposition", "attachment;filename=" + fileInfo.getFileName());
    
    // 4. 输出文件
    try (InputStream inputStream = new FileInputStream(fileInfo.getFilePath());
         OutputStream outputStream = response.getOutputStream()) {
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    } catch (IOException e) {
        log.error("文件下载失败: {}", e.getMessage());
        throw new BusinessException("文件下载失败");
    }
}
```

## 7. 监控管理

### 7.1 上传监控
- 上传成功率
- 文件大小分布
- 上传速度监控
- 错误原因统计

### 7.2 存储监控
- 空间使用率
- 文件数量统计
- 存储分布情况
- 异常文件检测

### 7.3 访问监控
- 访问量统计
- 带宽使用监控
- 热点文件分析
- 异常访问检测

## 8. 待优化点

### 8.1 功能优化
- 支持更多存储方式
- 增强文件处理能力
- 完善防护机制
- 优化用户体验

### 8.2 性能优化
- 上传性能优化
- 存储空间优化
- 访问速度优化
- 资源利用优化

### 8.3 安全优化
- 加强安全检测
- 完善权限控制
- 增强防护能力
- 优化审计功能

## 9. 最佳实践

### 9.1 上传规范
- 严格文件验证
- 合理的大小限制
- 统一的命名规则
- 完整的错误处理

### 9.2 存储规范
- 目录结构规范
- 备份策略制定
- 清理策略制定
- 权限管理规范

### 9.3 访问规范
- 权限控制规范
- 防护措施配置
- 监控告警设置
- 应急处理流程 