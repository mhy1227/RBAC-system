package com.czj.rbac.service.impl;

import com.czj.rbac.mapper.SysPermissionMapper;
import com.czj.rbac.service.SysPermissionService;
import com.czj.rbac.model.SysPermission;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.model.dto.PermissionDTO;
import com.czj.rbac.model.query.PermissionQuery;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.czj.rbac.service.SysLogService;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import com.czj.rbac.service.DistributedLockService;
import com.czj.rbac.common.UserNotLoggedInException;
import com.czj.rbac.context.UserContext;
import com.czj.rbac.util.SecurityUtils;

@Slf4j
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper permissionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysLogService logService;

    @Autowired
    private DistributedLockService lockService;

    @PostConstruct
    public void init() {
        // 系统启动时预热缓存
        preloadCache();
        // 启动定时刷新任务
        startCacheRefreshTask();
    }

    /**
     * 预热缓存
     */
    private void preloadCache() {
        try {
            log.info("开始预热权限缓存...");
            long startTime = System.currentTimeMillis();
            
            // 1. 加载权限树
            findPermissionTree(null);
            
            // 2. 加载启用状态的权限列表
            findList(1, null);
            
            long endTime = System.currentTimeMillis();
            log.info("权限缓存预热完成，耗时：{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("权限缓存预热失败: {}", e.getMessage());
        }
    }

    /**
     * 启动缓存定时刷新任务
     */
    private void startCacheRefreshTask() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                log.info("开始刷新权限缓存...");
                preloadCache();
            } catch (Exception e) {
                log.error("刷新权限缓存失败: {}", e.getMessage());
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    @Override
    public PermissionVO findById(Long id) {
        long startTime = System.currentTimeMillis();
        try {
            String cacheKey = "permission:" + id;
            String permissionJson = (String) redisTemplate.opsForValue().get(cacheKey);
            
            if (permissionJson != null) {
                try {
                    return objectMapper.readValue(permissionJson, PermissionVO.class);
                } catch (Exception e) {
                    log.error("解析权限缓存数据失败: {}", e.getMessage());
                    // 删除损坏的缓存数据
                    redisTemplate.delete(cacheKey);
                }
            }
            
            SysPermission permission = permissionMapper.findById(id);
            if (permission == null) {
                return null;
            }
            
            PermissionVO permissionVO = convert(permission, PermissionVO.class);
            try {
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(permissionVO), 1, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("缓存权限数据失败: {}", e.getMessage());
            }
            
            return permissionVO;
        } finally {
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > 100) {
                log.warn("权限查询耗时较长: {}ms, id: {}", endTime - startTime, id);
            }
        }
    }

    @Override
    public PageResult<PermissionVO> findPage(PermissionQuery query) {
        log.info("分页查询权限列表: {}", query);
        
        // 参数校验
        if (query == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "查询参数不能为空");
        }
        if (query.getPage() == null || query.getPage() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "页码必须大于0");
        }
        if (query.getSize() == null || query.getSize() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "每页大小必须大于0");
        }

        // 获取当前用户ID
        Long userId = SecurityUtils.getLoginUserId();
        if (userId == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "用户未登录");
        }
        
        List<SysPermission> permissions;
        
        // 如果是超级管理员，可以查看所有权限
        if (SecurityUtils.hasAdminPermission()) {
            permissions = permissionMapper.findPage(
                query.getStatus(),
                query.getType()
            );
        } else {
            // 普通用户只能查看自己拥有的权限
            permissions = permissionMapper.findPermissionsByUserId(userId);
            // 根据查询条件过滤
            if (query.getStatus() != null || StringUtils.hasText(query.getType())) {
                permissions = permissions.stream()
                    .filter(p -> (query.getStatus() == null || p.getStatus().equals(query.getStatus()))
                            && (query.getType() == null || query.getType().equals(p.getType())))
                    .collect(Collectors.toList());
            }
        }
        
        if (permissions.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L, query.getPage(), query.getSize());
        }
        
        // 转换为VO对象
        List<PermissionVO> permissionVOs = permissions.stream()
            .map(permission -> convert(permission, PermissionVO.class))
            .collect(Collectors.toList());
        
        // 使用内存分页
        return handlePage(permissionVOs, query.getPage(), query.getSize());
    }

    @Override
    public List<PermissionVO> findList(Integer status, String type) {
        log.info("查询权限列表, status: {}, type: {}", status, type);
        
        // 1. 尝试从缓存获取
        String cacheKey = String.format("permission:list:%s:%s", 
            status == null ? "all" : status,
            type == null ? "all" : type);
            
        String listJson = (String) redisTemplate.opsForValue().get(cacheKey);
        if (listJson != null) {
            try {
                return objectMapper.readValue(listJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PermissionVO.class));
            } catch (Exception e) {
                log.error("解析权限列表缓存数据失败: {}", e.getMessage());
            }
        }
        
        // 2. 缓存未命中，从数据库查询
        List<SysPermission> permissions = permissionMapper.findList(status, type);
        List<PermissionVO> permissionVOs = convertList(permissions, PermissionVO.class);
        
        // 3. 存入缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(permissionVOs), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("缓存权限列表数据失败: {}", e.getMessage());
        }
        
        return permissionVOs;
    }

    @Override
    public List<PermissionVO> findByParentId(Long parentId) {
        log.info("查询子权限列表, parentId: {}", parentId);
        
        // 参数校验
        if (parentId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "父级ID不能为空");
        }
        
        // 1. 尝试从缓存获取
        String cacheKey = "permission:parent:" + parentId;
        String listJson = (String) redisTemplate.opsForValue().get(cacheKey);
        if (listJson != null) {
            try {
                return objectMapper.readValue(listJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PermissionVO.class));
            } catch (Exception e) {
                log.error("解析子权限列表缓存数据失败: {}", e.getMessage());
            }
        }
        
        // 2. 缓存未命中，从数据库查询
        List<SysPermission> permissions = permissionMapper.findByParentId(parentId);
        List<PermissionVO> permissionVOs = convertList(permissions, PermissionVO.class);
        
        // 3. 存入缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(permissionVOs), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("缓存子权限列表数据失败: {}", e.getMessage());
        }
        
        return permissionVOs;
    }

    @Override
    public List<PermissionVO> findPermissionsByRoleId(Long roleId) {
        log.info("查询角色权限列表, roleId: {}", roleId);
        
        // 参数校验
        if (roleId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色ID不能为空");
        }
        
        List<SysPermission> permissions = permissionMapper.findPermissionsByRoleId(roleId);
        return convertList(permissions, PermissionVO.class);
    }

    @Override
    public List<String> findPermissionsByUserId(Long userId) {
        log.info("查询用户权限列表, userId: {}", userId);
        
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户ID不能为空");
        }
        
        List<PermissionVO> permissions = permissionMapper.findByUserId(userId);
        return permissions.stream()
            .map(PermissionVO::getPermissionCode)
            .collect(Collectors.toList());
    }

    @Override
    public List<PermissionVO> findPermissionTree(String type) {
        String cacheKey = "permission:tree:" + (type == null ? "all" : type);
        
        // 1. 尝试从缓存获取
        String treeJson = (String) redisTemplate.opsForValue().get(cacheKey);
        if (treeJson != null) {
            try {
                return objectMapper.readValue(treeJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PermissionVO.class));
            } catch (Exception e) {
                log.error("解析权限树缓存数据失败: {}", e.getMessage());
            }
        }
        
        // 2. 缓存未命中，从数据库查询
        List<SysPermission> permissions = permissionMapper.findPermissionTree(type);
        List<PermissionVO> permissionVOs = convertList(permissions, PermissionVO.class);
        List<PermissionVO> tree = buildTree(permissionVOs);
        
        // 3. 存入缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(tree), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("缓存权限树数据失败: {}", e.getMessage());
        }
        
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PermissionDTO permissionDTO) {
        log.info("新增权限: {}", permissionDTO);
        
        // 1. 参数校验
        if (permissionDTO == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限信息不能为空");
        }
        if (StringUtils.isEmpty(permissionDTO.getPermissionName()) 
            || StringUtils.isEmpty(permissionDTO.getPermissionCode())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限名称和编码不能为空");
        }
        
        // 2. 检查权限编码是否存在
        if (permissionMapper.checkPermissionCode(permissionDTO.getPermissionCode()) > 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限编码已存在");
        }
        
        // 3. 检查父级权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            SysPermission parent = permissionMapper.findById(permissionDTO.getParentId());
            if (parent == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "父级权限不存在");
            }
            if (parent.getStatus() == 0) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "父级权限已禁用");
            }
        }
        
        // 4. 保存权限
        SysPermission permission = convert(permissionDTO, SysPermission.class);
        // 设置默认状态
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        permissionMapper.insert(permission);
        
        // 5. 清理相关缓存
        if (permission.getParentId() != null) {
            clearPermissionCache(permission.getParentId());
        }
        clearPermissionTreeCache();
        
        // 6. 记录操作日志
        logService.saveLog("权限管理", "新增权限", String.format("新增权限[%s(%s)]", 
            permission.getPermissionName(), permission.getPermissionCode()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionDTO permissionDTO) {
        log.info("更新权限: {}", permissionDTO);
        
        // 1. 参数校验
        if (permissionDTO == null || permissionDTO.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限信息不能为空");
        }
        if (StringUtils.isEmpty(permissionDTO.getPermissionName())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限名称不能为空");
        }
        
        // 2. 检查权限是否存在
        SysPermission oldPermission = permissionMapper.findById(permissionDTO.getId());
        if (oldPermission == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限不存在");
        }
        
        // 3. 检查权限编码唯一性
        if (StringUtils.hasText(permissionDTO.getPermissionCode()) 
            && !permissionDTO.getPermissionCode().equals(oldPermission.getPermissionCode())) {
            if (permissionMapper.checkPermissionCode(permissionDTO.getPermissionCode()) > 0) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限编码已存在");
            }
        }
        
        // 4. 更新权限
        SysPermission permission = convert(permissionDTO, SysPermission.class);
        permissionMapper.update(permission);
        
        // 5. 清理缓存
        batchClearCache(permission.getId());
        
        // 6. 记录操作日志
        logService.saveLog("权限管理", "更新权限", String.format("权限[%s]从[%s]更新为[%s]", 
            oldPermission.getPermissionCode(), oldPermission.getPermissionName(), permission.getPermissionName()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除权限: {}", id);
        
        // 参数校验
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限ID不能为空");
        }
        
        // 1. 检查权限是否存在
        SysPermission permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限不存在");
        }
        
        // 2. 检查是否有子权限
        if (permissionMapper.countByParentId(id) > 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "存在子权限，不能删除");
        }
        
        // 3. 检查是否被角色引用
        if (permissionMapper.countByRolePermissions(id) > 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), 
                String.format("权限[%s]已被角色引用，请先解除引用", permission.getPermissionName()));
        }
        
        // 4. 删除权限
        permissionMapper.deleteById(id);
        
        // 5. 清理缓存
        batchClearCache(id);
        
        // 6. 记录操作日志
        logService.saveLog("权限管理", "删除权限", String.format("删除权限[%s(%s)]", 
            permission.getPermissionName(), permission.getPermissionCode()));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        String lockKey = "permission:status:" + id;
        lockService.executeWithLock(lockKey, 10, () -> {
        log.info("更新权限状态: id={}, status={}", id, status);
            
            // 参数校验
            if (id == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限ID不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "无效的状态值");
            }
            
            // 1. 检查权限是否存在
            SysPermission permission = permissionMapper.findById(id);
            if (permission == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限不存在");
            }
            
            // 2. 如果是禁用操作，检查是否有启用状态的子权限
            if (status == 0) {
                List<SysPermission> children = permissionMapper.findByParentId(id);
                if (children.stream().anyMatch(child -> child.getStatus() == 1)) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), 
                        "存在启用状态的子权限，请先禁用子权限");
                }
            }
            
            // 3. 更新状态
        permissionMapper.updateStatus(id, status);
            
            // 4. 清理缓存
            batchClearCache(id);
            
            // 5. 记录操作日志
            logService.saveLog("权限管理", "更新状态", String.format("权限[%s]状态更新为[%s]", 
                permission.getPermissionName(), status == 1 ? "启用" : "禁用"));
            
            return null;
        });
    }

    /**
     * 构建权限树
     */
    private List<PermissionVO> buildTree(List<PermissionVO> permissions) {
        List<PermissionVO> tree = new ArrayList<>();
        Map<Long, PermissionVO> permissionMap = new HashMap<>();
        
        // 1. 构建权限映射
        for (PermissionVO permission : permissions) {
            permissionMap.put(permission.getId(), permission);
        }
        
        // 2. 构建树形结构
        for (PermissionVO permission : permissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                tree.add(permission);
            } else {
                PermissionVO parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }
        
        // 3. 对树进行排序
        sortPermissionTree(tree);
        
        return tree;
    }
    
    /**
     * 对权限树进行排序
     */
    private void sortPermissionTree(List<PermissionVO> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        // 按创建时间排序
        permissions.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        // 递归排序子节点
        for (PermissionVO permission : permissions) {
            if (permission.getChildren() != null) {
                sortPermissionTree(permission.getChildren());
            }
        }
    }

    /**
     * 清理权限缓存
     */
    private void clearPermissionCache(Long permissionId) {
        if (permissionId != null) {
            String cacheKey = "permission:" + permissionId;
            redisTemplate.delete(cacheKey);
        }
    }

    /**
     * 清理权限树缓存
     */
    public void clearPermissionTreeCache() {
        Set<String> keys = redisTemplate.keys("permission:tree:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 清理权限列表缓存
     */
    public void clearPermissionListCache() {
        Set<String> keys = redisTemplate.keys("permission:list:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 清理父级权限缓存
     */
    public void clearParentPermissionCache() {
        Set<String> keys = redisTemplate.keys("permission:parent:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 批量清理权限缓存
     */
    public void batchClearCache(Long permissionId) {
        String lockKey = "permission:cache:clear";
        lockService.executeWithLock(lockKey, 10, () -> {
            try {
                List<String> keys = new ArrayList<>();
                
                // 1. 添加单个权限缓存key
                if (permissionId != null) {
                    keys.add("permission:" + permissionId);
                }
                
                // 2. 添加权限树缓存key
                Set<String> treeKeys = redisTemplate.keys("permission:tree:*");
                if (treeKeys != null) {
                    keys.addAll(treeKeys);
                }
                
                // 3. 添加权限列表缓存key
                Set<String> listKeys = redisTemplate.keys("permission:list:*");
                if (listKeys != null) {
                    keys.addAll(listKeys);
                }
                
                // 4. 添加父级权限缓存key
                Set<String> parentKeys = redisTemplate.keys("permission:parent:*");
                if (parentKeys != null) {
                    keys.addAll(parentKeys);
                }
                
                // 5. 批量删除缓存
                if (!keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                log.error("清理权限缓存失败: {}", e.getMessage());
            }
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        String lockKey = "permission:batch:delete";
        lockService.executeWithLock(lockKey, 30, () -> {
            // 1. 检查是否有子权限
            for (Long id : ids) {
                if (permissionMapper.countByParentId(id) > 0) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), 
                        String.format("权限[%d]存在子权限，不能删除", id));
                }
            }
            
            // 2. 检查是否被角色引用
            for (Long id : ids) {
                if (permissionMapper.countByRolePermissions(id) > 0) {
                    SysPermission permission = permissionMapper.findById(id);
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), 
                        String.format("权限[%s]已被角色引用，请先解除引用", permission.getPermissionName()));
                }
            }
            
            // 3. 批量删除
            permissionMapper.deleteBatchByIds(ids);
            
            // 4. 清理缓存
            batchClearCache(null);
            
            // 5. 记录操作日志
            logService.saveLog("权限管理", "批量删除权限", String.format("批量删除权限: %s", ids));
            
            return null;
        });
    }

    @Override
    public PageResult<PermissionVO> findPageInMemory(PermissionQuery query) {
        log.info("内存分页查询权限列表: {}", query);
        
        // 参数校验
        if (query == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "查询参数不能为空");
        }
        if (query.getPage() == null || query.getPage() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "页码必须大于0");
        }
        if (query.getSize() == null || query.getSize() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "每页大小必须大于0");
        }
        
        // 1. 查询所有符合条件的数据
        List<SysPermission> allPermissions = permissionMapper.findList(query.getStatus(), query.getType());
        
        // 2. 在内存中进行过滤
        List<SysPermission> filteredPermissions = allPermissions.stream()
            .filter(permission -> {
                boolean match = true;
                // 按权限名称过滤
                if (StringUtils.hasText(query.getPermissionName())) {
                    match = match && permission.getPermissionName().contains(query.getPermissionName());
                }
                // 按权限编码过滤
                if (StringUtils.hasText(query.getPermissionCode())) {
                    match = match && permission.getPermissionCode().contains(query.getPermissionCode());
                }
                return match;
            })
            .collect(Collectors.toList());
        
        // 3. 转换为VO对象
        List<PermissionVO> permissionVOs = convertList(filteredPermissions, PermissionVO.class);
        
        // 4. 使用BaseServiceImpl中的分页处理方法
        return handlePage(permissionVOs, query.getPage(), query.getSize());
    }
} 