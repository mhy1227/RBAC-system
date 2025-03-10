package com.czj.rbac.service.impl;

import com.czj.rbac.mapper.SysRoleMapper;
import com.czj.rbac.mapper.SysPermissionMapper;
import com.czj.rbac.service.SysRoleService;
import com.czj.rbac.service.RoleCacheService;
import com.czj.rbac.service.DistributedLockService;
import com.czj.rbac.service.SysLogService;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.service.SysPermissionService;
import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.model.SysRole;
import com.czj.rbac.model.vo.RoleVO;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.model.dto.RoleDTO;
import com.czj.rbac.model.query.RoleQuery;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysRoleServiceImpl extends BaseServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private RoleCacheService roleCacheService;
    
    @Autowired
    private DistributedLockService lockService;

    @Autowired
    private SysLogService logService;

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysPermissionMapper permissionMapper;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public RoleVO findById(Long id) {
        log.info("查询角色信息, id: {}", id);
        
        // 1. 尝试从缓存获取
        RoleVO roleVO = roleCacheService.getRoleCache(id);
        if (roleVO != null) {
            return roleVO;
        }
        
        // 2. 缓存未命中，从数据库查询
        SysRole role = roleMapper.findById(id);
        if (role == null) {
            roleCacheService.setRoleCache(id, null); // 缓存空值
            return null;
        }
        
        // 3. 转换并缓存结果
        roleVO = convert(role, RoleVO.class);
        roleCacheService.setRoleCache(id, roleVO);
        
        return roleVO;
    }

    @Override
    public PageResult<RoleVO> findPage(RoleQuery query) {
        log.info("分页查询角色列表: {}", query);
        
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
        
        // 1. 查询数据
        List<SysRole> roles = roleMapper.findPageWithFilter(
            (query.getPage() - 1) * query.getSize(),
            query.getSize(),
            query.getStatus(),
            query.getRoleName(),
            query.getRoleCode()
        );
        
        if (roles.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L, query.getPage(), query.getSize());
        }
        
        // 2. 转换为VO对象并缓存
        List<RoleVO> roleVOs = roles.stream().map(role -> {
            RoleVO roleVO = convert(role, RoleVO.class);
            roleCacheService.setRoleCache(role.getId(), roleVO);
            return roleVO;
        }).collect(Collectors.toList());
        
        // 3. 获取总数
        int total = roleMapper.countWithFilter(
            query.getStatus(),
            query.getRoleName(),
            query.getRoleCode()
        );
        
        return new PageResult<>(roleVOs, (long)total, query.getPage(), query.getSize());
    }

    @Override
    public List<RoleVO> findList(Integer status) {
        log.info("查询角色列表, status: {}", status);
        List<SysRole> roles = roleMapper.findList(status);
        return convertList(roles, RoleVO.class);
    }

    @Override
    public PageResult<RoleVO> findPageInMemory(RoleQuery query) {
        log.info("内存分页查询角色列表: {}", query);
        
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
        List<SysRole> allRoles = roleMapper.findList(query.getStatus());
        
        // 2. 在内存中进行过滤
        List<SysRole> filteredRoles = allRoles.stream()
            .filter(role -> {
                boolean match = true;
                // 按角色名称过滤
                if (StringUtils.hasText(query.getRoleName())) {
                    match = match && role.getRoleName().contains(query.getRoleName());
                }
                // 按角色编码过滤
                if (StringUtils.hasText(query.getRoleCode())) {
                    match = match && role.getRoleCode().contains(query.getRoleCode());
                }
                return match;
            })
            .collect(Collectors.toList());
            
        // 3. 转换为VO对象并缓存
        List<RoleVO> roleVOs = filteredRoles.stream().map(role -> {
            RoleVO roleVO = convert(role, RoleVO.class);
            roleCacheService.setRoleCache(role.getId(), roleVO);
            return roleVO;
        }).collect(Collectors.toList());
        
        // 4. 使用基类的分页处理方法
        return handlePage(roleVOs, query.getPage(), query.getSize());
    }

    @Override
    public List<RoleVO> findRolesByUserId(Long userId) {
        log.info("查询用户角色列表, userId: {}", userId);
        List<SysRole> roles = roleMapper.findRolesByUserId(userId);
        return convertList(roles, RoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RoleDTO roleDTO) {
        log.info("新增角色: {}", roleDTO);
        
        // 参数校验
        if (roleDTO == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色信息不能为空");
        }
        
        String lockKey = "role:code:" + roleDTO.getRoleCode();
        lockService.executeWithLock(lockKey, 10, () -> {
            // 1. 参数校验
            if (StringUtils.isEmpty(roleDTO.getRoleName()) || StringUtils.isEmpty(roleDTO.getRoleCode())) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色名称和编码不能为空");
            }
            
            // 2. 检查角色编码是否存在
            if (roleMapper.checkRoleCode(roleDTO.getRoleCode()) > 0) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色编码已存在");
            }
            
            // 3. 保存角色
            SysRole role = convert(roleDTO, SysRole.class);
            // 设置默认状态
            if (role.getStatus() == null) {
                role.setStatus(1);
            }
            roleMapper.insert(role);
            
            // 4. 分配权限
            if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
                roleMapper.insertRolePermissions(role.getId(), roleDTO.getPermissionIds());
            }
            
            // 记录操作日志
            logService.saveLog("角色管理", "新增角色", String.format("角色编码: %s, 角色名称: %s", 
                roleDTO.getRoleCode(), roleDTO.getRoleName()));
            
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO roleDTO) {
        log.info("更新角色: {}", roleDTO);
        
        // 参数校验
        if (roleDTO == null || roleDTO.getId() == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色信息不能为空");
        }
        
        String lockKey = "role:" + roleDTO.getId();
        lockService.executeWithLock(lockKey, 10, () -> {
            // 获取原角色信息，用于日志记录
            SysRole oldRole = roleMapper.findById(roleDTO.getId());
            if (oldRole == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色不存在");
            }
            
            SysRole role = convert(roleDTO, SysRole.class);
            roleMapper.update(role);
            
            // 更新权限
            if (roleDTO.getPermissionIds() != null) {
                roleMapper.deleteRolePermissions(role.getId());
                if (!roleDTO.getPermissionIds().isEmpty()) {
                    roleMapper.insertRolePermissions(role.getId(), roleDTO.getPermissionIds());
                }
            }
            
            // 删除缓存
            roleCacheService.deleteRoleCache(role.getId());
            roleCacheService.deleteRolePermissionCache(role.getId());
            
            // 记录操作日志
            logService.saveLog("角色管理", "更新角色", String.format("角色[%s]从[%s]更新为[%s]", 
                oldRole.getRoleCode(), oldRole.getRoleName(), roleDTO.getRoleName()));
            
            // 清理相关用户的缓存
            List<Long> userIds = userMapper.findUserIdsByRoleId(roleDTO.getId());
            if (!userIds.isEmpty()) {
                userService.clearUserCaches(userIds);
            }
            
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除角色: {}", id);
        
        // 参数校验
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色ID不能为空");
        }
        
        String lockKey = "role:" + id;
        lockService.executeWithLock(lockKey, 10, () -> {
            // 获取角色信息，用于日志记录
            SysRole role = roleMapper.findById(id);
            if (role == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR, "角色不存在");
            }
            
            // 检查是否有关联用户
            List<Long> userIds = userMapper.findUserIdsByRoleId(id);
            if (!userIds.isEmpty()) {
                throw new BusinessException(ResponseCode.PARAM_ERROR, 
                    String.format("角色[%s]下还有关联用户，请先解除关联", role.getRoleName()));
            }
            
            // 删除角色权限关系
            roleMapper.deleteRolePermissions(id);
            // 删除角色
            roleMapper.deleteById(id);
            // 删除缓存
            roleCacheService.deleteRoleCache(id);
            roleCacheService.deleteRolePermissionCache(id);
            
            // 记录操作日志
            logService.saveLog("角色管理", "删除角色", String.format("删除角色[%s(%s)]", 
                role.getRoleName(), role.getRoleCode()));
            
            return null;
        });
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("更新角色状态: id={}, status={}", id, status);
        
        // 参数校验
        if (id == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "无效的状态值");
        }
        
        String lockKey = "role:" + id;
        lockService.executeWithLock(lockKey, 10, () -> {
            // 检查角色是否存在
            SysRole role = roleMapper.findById(id);
            if (role == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色不存在");
            }
            
            roleMapper.updateStatus(id, status);
            
            // 清理角色缓存
            roleCacheService.deleteRoleCache(id);
            
            // 清理相关用户的缓存
            List<Long> userIds = userMapper.findUserIdsByRoleId(id);
            if (!userIds.isEmpty()) {
                userService.clearUserCaches(userIds);
            }
            
            // 记录操作日志
            logService.saveLog("角色管理", "更新状态", String.format("角色[%s]状态更新为[%s]", 
                role.getRoleName(), status == 1 ? "启用" : "禁用"));
            
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("分配角色权限, roleId: {}, permissionIds: {}", roleId, permissionIds);
        
        if (roleId == null || permissionIds == null || permissionIds.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "参数不能为空");
        }
        
        String lockKey = "role:permission:" + roleId;
        lockService.executeWithLock(lockKey, 10, () -> {
            // 1. 检查角色是否存在
                SysRole role = roleMapper.findById(roleId);
                if (role == null) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色不存在");
                }
            
            // 2. 分配权限
                roleMapper.deleteRolePermissions(roleId);
                if (permissionIds != null && !permissionIds.isEmpty()) {
                    roleMapper.insertRolePermissions(roleId, permissionIds);
                }
                
            // 3. 清理缓存
            roleCacheService.deleteRolePermissionCache(roleId);
            
            // 4. 清理相关用户的缓存
                    List<Long> userIds = userMapper.findUserIdsByRoleId(roleId);
                    if (!userIds.isEmpty()) {
                        userService.clearUserCaches(userIds);
                    }
            
            // 5. 记录操作日志
            List<String> permissionNames = permissionIds == null ? Collections.emptyList() :
                permissionMapper.findByIds(permissionIds)
                    .stream()
                    .map(p -> p.getPermissionName())
                    .collect(Collectors.toList());
            logService.saveLog("角色管理", "分配角色权限", 
                String.format("角色[%s]分配权限: %s", role.getRoleName(), String.join(",", permissionNames)));
            
            return null;
        });
    }

    @Override
    public RoleVO findRoleDetail(Long roleId) {
        log.info("查询角色详情, roleId: {}", roleId);
        
        // 1. 尝试从缓存获取
        RoleVO roleVO = roleCacheService.getRoleCache(roleId);
        if (roleVO != null) {
            return roleVO;
        }
        
        // 2. 从数据库查询
        RoleVO role = roleMapper.findRoleDetail(roleId);
        if (role == null) {
            return null;
        }
        
        // 3. 缓存结果
        roleCacheService.setRoleCache(roleId, role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        log.info("批量删除角色: {}", ids);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色ID列表不能为空");
        }
        
        String lockKey = "role:batch:" + String.join(",", ids.stream().map(String::valueOf).collect(Collectors.toList()));
        lockService.executeWithLock(lockKey, 10, () -> {
            // 1. 检查是否有关联用户
            for (Long id : ids) {
                List<Long> userIds = userMapper.findUserIdsByRoleId(id);
                if (!userIds.isEmpty()) {
                    SysRole role = roleMapper.findById(id);
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), 
                        String.format("角色[%s]已关联用户，无法删除", role.getRoleName()));
                }
            }
            
            // 2. 删除角色权限关系
            for (Long id : ids) {
                roleMapper.deleteRolePermissions(id);
            }
            
            // 3. 批量删除角色
            roleMapper.deleteBatchByIds(ids);
            
            // 4. 清理缓存
            for (Long id : ids) {
                roleCacheService.deleteRoleCache(id);
                roleCacheService.deleteRolePermissionCache(id);
            }
            
            // 5. 记录操作日志
            List<SysRole> roles = roleMapper.findByIds(ids);
            String roleNames = roles.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
            logService.saveLog("角色管理", "批量删除角色", String.format("删除角色: %s", roleNames));
                
                return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        log.info("移除角色权限, roleId: {}, permissionIds: {}", roleId, permissionIds);
        
        if (roleId == null || permissionIds == null || permissionIds.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "参数不能为空");
        }
        
        String lockKey = "role:permission:" + roleId;
        lockService.executeWithLock(lockKey, 10, () -> {
            // 1. 检查角色是否存在
            SysRole role = roleMapper.findById(roleId);
            if (role == null) {
                throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色不存在");
            }
            
            // 2. 移除权限
            roleMapper.deleteRolePermissions(roleId);
            
            // 3. 清理缓存
            roleCacheService.deleteRolePermissionCache(roleId);
            
            // 4. 清理相关用户的缓存
            List<Long> userIds = userMapper.findUserIdsByRoleId(roleId);
            if (!userIds.isEmpty()) {
                userService.clearUserCaches(userIds);
            }
            
            // 5. 记录操作日志
            List<String> permissionNames = permissionIds == null ? Collections.emptyList() :
                permissionMapper.findByIds(permissionIds)
                    .stream()
                    .map(p -> p.getPermissionName())
                    .collect(Collectors.toList());
            logService.saveLog("角色管理", "移除角色权限", 
                String.format("角色[%s]移除权限: %s", role.getRoleName(), String.join(",", permissionNames)));
            
            return null;
        });
    }

    @Override
    public boolean checkRoleCodeExists(String roleCode) {
        log.info("检查角色编码是否存在: {}", roleCode);
        return roleMapper.checkRoleCode(roleCode) > 0;
    }

    @Override
    public boolean hasPermission(Long roleId, Long permissionId) {
        log.info("检查角色是否拥有权限, roleId: {}, permissionId: {}", roleId, permissionId);
        return roleMapper.hasPermission(roleId, permissionId) > 0;
    }

    @Override
    public List<Long> findPermissionIdsByRoleId(Long roleId) {
        log.info("查询角色的权限ID列表, roleId: {}", roleId);
        return roleMapper.findPermissionIdsByRoleId(roleId);
    }

    @Override
    public void clearRoleCache(Long roleId) {
        if (roleId != null) {
            try {
                roleCacheService.deleteRoleCache(roleId);
                roleCacheService.deleteRolePermissionCache(roleId);
            } catch (Exception e) {
                log.error("清理角色缓存失败: {}", e.getMessage());
            }
        }
    }

    /*
     * 该方法已废弃，请使用assignPermissions和removePermissions方法
     */
    /*
    @Transactional(rollbackFor = Exception.class)
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        // ... 已注释的代码 ...
    }
    */
} 