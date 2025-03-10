package com.czj.rbac.service.impl;

import com.czj.rbac.event.UserTokenInvalidationEvent;
import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.mapper.SysRoleMapper;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.service.DataPermissionService;
import com.czj.rbac.service.UserCacheService;
import com.czj.rbac.service.SysLogService;
import com.czj.rbac.service.DistributedLockService;
import com.czj.rbac.model.SysUser;
import com.czj.rbac.model.SysRole;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.model.vo.RoleVO;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.model.dto.UserDTO;
import com.czj.rbac.model.query.UserQuery;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.util.PasswordUtil;
import com.czj.rbac.util.PasswordValidator;
import com.czj.rbac.util.SensitiveInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.czj.rbac.service.impl.BaseServiceImpl;
@Slf4j
@Service
public class SysUserServiceImpl extends BaseServiceImpl implements SysUserService {
    
    @Value("${rbac.user.default-password:123456}")
    private String defaultPassword;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private DataPermissionService dataPermissionService;
    
    @Autowired
    private PasswordValidator passwordValidator;
    
    @Autowired
    private UserCacheService userCacheService;
    
    @Autowired
    private SysLogService logService;
    
    @Autowired
    private DistributedLockService lockService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public UserVO findById(Long id) {
        // 1. 尝试从缓存获取
        UserVO userVO = userCacheService.getUserCache(id);
        if (userVO != null) {
            return userVO;
        }
        
        // 2. 检查数据权限
        if (!dataPermissionService.checkUserDataPermission(id)) {
            log.warn("当前用户无权访问用户数据, targetUserId: {}", id);
            return null;
        }
        
        // 3. 查询数据库
        SysUser user = userMapper.findById(id);
        if (user == null) {
            return null;
        }
        userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        // 4. 敏感信息脱敏
        if (StringUtils.hasText(userVO.getPhone())) {
            userVO.setPhone(SensitiveInfoUtil.maskPhone(userVO.getPhone()));
        }
        if (StringUtils.hasText(userVO.getEmail())) {
            userVO.setEmail(SensitiveInfoUtil.maskEmail(userVO.getEmail()));
        }
        
        // 设置用户权限列表
        List<PermissionVO> permissions = findUserPermissions(id);
        userVO.setPermissions(permissions.stream()
            .map(PermissionVO::getPermissionCode)
            .collect(Collectors.toList()));
        
        // 5. 存入缓存
        userCacheService.setUserCache(id, userVO);
        
        return userVO;
    }

    @Override
    public PageResult<UserVO> findPage(UserQuery query) {
        log.info("分页查询用户列表: {}", query);
        
        // 1. 查询数据
        int total = userMapper.count(query);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, query.getPage(), query.getSize());
        }
        
        List<SysUser> users = userMapper.selectPage(query);
        
        // 2. 过滤无权访问的数据
        List<SysUser> filteredUsers = users.stream()
            .filter(user -> dataPermissionService.checkUserDataPermission(user.getId()))
            .collect(Collectors.toList());
        
        // 3. 转换并返回结果
        List<UserVO> userVOList = filteredUsers.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            
            // 敏感信息脱敏
            if (StringUtils.hasText(userVO.getPhone())) {
                userVO.setPhone(SensitiveInfoUtil.maskPhone(userVO.getPhone()));
            }
            if (StringUtils.hasText(userVO.getEmail())) {
                userVO.setEmail(SensitiveInfoUtil.maskEmail(userVO.getEmail()));
            }
            
            return userVO;
        }).collect(Collectors.toList());
        
        return new PageResult<>(userVOList, (long)filteredUsers.size(), query.getPage(), query.getSize());
    }

    @Override
    public PageResult<UserVO> findPageInMemory(UserQuery query) {
        log.info("内存分页查询用户列表: {}", query);
        
        // 参数校验
        if (query == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "查询参数不能为空");
        }
        if (query.getPage() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "页码必须大于0");
        }
        if (query.getSize() < 1) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "每页大小必须大于0");
        }
        
        // 1. 查询所有符合条件的数据
        List<SysUser> allUsers = userMapper.findList(query.getStatus());
        
        // 2. 在内存中进行过滤
        List<SysUser> filteredUsers = allUsers.stream()
            .filter(user -> {
                boolean match = true;
                // 按用户名过滤
                if (StringUtils.hasText(query.getUsername())) {
                    match = match && user.getUsername().contains(query.getUsername());
                }
                // 按昵称过滤
                if (StringUtils.hasText(query.getNickname())) {
                    match = match && user.getNickname().contains(query.getNickname());
                }
                return match;
            })
            .collect(Collectors.toList());
        
        // 3. 转换为VO对象并缓存
        List<UserVO> userVOs = filteredUsers.stream()
            .map(user -> {
                UserVO userVO = convert(user, UserVO.class);
                userCacheService.setUserCache(user.getId(), userVO);
                return userVO;
            })
            .collect(Collectors.toList());
        
        // 4. 使用BaseServiceImpl中的分页处理方法
        return handlePage(userVOs, query.getPage(), query.getSize());
    }

    // 添加 DTO 转实体的方法
    private SysUser convert(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(UserDTO userDTO) {
        log.info("新增用户: {}", userDTO);
        
        // 1. 参数校验
        if (StringUtils.isEmpty(userDTO.getUsername()) || StringUtils.isEmpty(userDTO.getPassword())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户名和密码不能为空");
        }
        
        // 2. 检查用户名是否存在
        if (userMapper.checkUsername(userDTO.getUsername()) > 0) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户名已存在");
        }
        
        // 3. 密码加密
        SysUser user = convert(userDTO);
        user.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        
        // 4. 保存用户
        userMapper.insert(user);
        
        // 5. 分配角色
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            userMapper.insertUserRoles(user.getId(), userDTO.getRoleIds());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO userDTO) {
        log.info("更新用户: {}", userDTO);
        SysUser user = convert(userDTO);
        
        // 如果密码不为空,则更新密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(PasswordUtil.encode(userDTO.getPassword()));
        }
        
        userMapper.update(user);
        
        // 更新角色
        if (userDTO.getRoleIds() != null) {
            userMapper.deleteUserRoles(user.getId());
            if (!userDTO.getRoleIds().isEmpty()) {
                userMapper.insertUserRoles(user.getId(), userDTO.getRoleIds());
            }
        }
        
        // 删除缓存
        userCacheService.deleteUserCache(user.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除用户: {}", id);
        // 检查用户是否存在
        SysUser user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户不存在");
        }
        // 删除用户角色关系
        userMapper.deleteUserRoles(id);
        // 删除用户
        userMapper.deleteById(id);
        // 删除缓存
        userCacheService.deleteUserCache(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        String lockKey = "user:status:" + id;
        lockService.executeWithLock(lockKey, 30, () -> {
            try {
                // 1. 参数校验
                if (id == null) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户ID不能为空");
                }
                if (status == null || (status != 0 && status != 1)) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "无效的状态值");
                }
                
                // 2. 检查用户是否存在
                SysUser user = userMapper.findById(id);
                if (user == null) {
                    throw new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户不存在");
                }
                
                // 3. 更新状态
                userMapper.updateStatus(id, status);
                
                // 4. 如果是禁用操作,清理用户token和缓存
                if (status == 0) {
                    try {
                        // 发布token失效事件
                        eventPublisher.publishEvent(new UserTokenInvalidationEvent(this, id, "用户被禁用"));
                        // 清理用户缓存
                        userCacheService.deleteUserCache(id);
                        // 记录用户下线
                        logService.saveLog("用户管理", "用户下线", 
                            String.format("用户[%s]被禁用并强制下线", user.getUsername()));
                    } catch (Exception e) {
                        log.error("清理用户token和缓存失败,但不影响业务: {}", e.getMessage());
                    }
                }
                
                // 5. 记录操作日志
                logService.saveLog("用户管理", "更新状态", 
                    String.format("用户[%s]状态更新为[%s]", user.getUsername(), status == 1 ? "启用" : "禁用"));
                
                return null;
            } catch (Exception e) {
                log.error("更新用户状态失败: {}", e.getMessage());
                throw e;
            }
        });
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 验证用户是否存在
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        
        // 验证角色是否存在
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysRole> roles = roleMapper.findByIds(roleIds);
            if (roles.size() != roleIds.size()) {
                throw new BusinessException(ResponseCode.ROLE_NOT_FOUND);
            }
        }
        
        // 删除原有角色关系
        userMapper.deleteUserRoles(userId);
        
        // 添加新的角色关系
        if (roleIds != null && !roleIds.isEmpty()) {
            userMapper.insertUserRoles(userId, roleIds);
        }
        
        // 删除缓存
        userCacheService.deleteUserCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 验证新密码格式
        List<String> errors = passwordValidator.validate(newPassword);
        if (!errors.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, String.join(",", errors));
        }
        
        // 验证用户是否存在
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }
        
        // 新密码不能与旧密码相同
        if (oldPassword.equals(newPassword)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "新密码不能与旧密码相同");
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        userMapper.update(user);
        
        // 重置登录失败次数
        user.resetLoginFail();
        userMapper.resetLoginFail(userId);
        
        // 删除缓存
        userCacheService.deleteUserCache(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        // 验证用户是否存在
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        
        // 重置为默认密码
        user.setPassword(PasswordUtil.encode(defaultPassword));
        userMapper.update(user);
        
        // 重置登录失败次数
        user.resetLoginFail();
        userMapper.resetLoginFail(userId);
        
        // 删除缓存并发布token失效事件
        userCacheService.deleteUserCache(userId);
        eventPublisher.publishEvent(new UserTokenInvalidationEvent(this, userId, "密码重置"));
        
        // 记录操作日志
        logService.saveLog("用户管理", "重置密码", 
            String.format("用户[%s]密码已重置", user.getUsername()));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UserDTO userDTO) {
        // 验证用户是否存在
        SysUser user = userMapper.findById(userDTO.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
        
        // 验证邮箱格式
        if (StringUtils.hasText(userDTO.getEmail()) && !SensitiveInfoUtil.isValidEmail(userDTO.getEmail())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "邮箱格式不正确");
        }
        
        // 验证手机号格式
        if (StringUtils.hasText(userDTO.getPhone()) && !SensitiveInfoUtil.isValidPhone(userDTO.getPhone())) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "手机号格式不正确");
        }
        
        // 只允许修改部分字段
        user.setNickname(userDTO.getNickname());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        
        // 更新用户信息
        userMapper.update(user);
        
        // 删除缓存
        userCacheService.deleteUserCache(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void clearUserCaches(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        try {
            userCacheService.batchDeleteUserCache(userIds.toArray(new Long[0]));
            // 记录操作日志
            logService.saveLog("用户管理", "清理缓存", 
                String.format("批量清理用户缓存: %s", userIds));
        } catch (Exception e) {
            log.error("批量清理用户缓存失败: {}", e.getMessage());
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "清理缓存失败");
        }
    }

    @Override
    public List<PermissionVO> findUserPermissions(Long userId) {
        log.info("查询用户权限列表, userId: {}", userId);
        
        // 1. 检查数据权限
        if (!dataPermissionService.checkUserDataPermission(userId)) {
            log.warn("当前用户无权访问用户权限数据, targetUserId: {}", userId);
            throw new BusinessException(ResponseCode.FORBIDDEN, "无权访问该用户权限数据");
        }
        
        // 2. 从数据库查询权限列表
        List<PermissionVO> permissions = userMapper.findUserPermissions(userId);
        
        // 3. 构建权限树结构
        Map<Long, PermissionVO> permissionMap = new HashMap<>();
        List<PermissionVO> rootPermissions = new ArrayList<>();
        
        // 先将所有权限放入Map中
        for (PermissionVO permission : permissions) {
            permissionMap.put(permission.getId(), permission);
        }
        
        // 构建树形结构
        for (PermissionVO permission : permissions) {
            Long parentId = permission.getParentId();
            if (parentId == null || parentId == 0) {
                rootPermissions.add(permission);
            } else {
                PermissionVO parent = permissionMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }
        
        // 4. 对每一层级的权限进行排序
        sortPermissions(rootPermissions);
        
        return rootPermissions;
    }

    /**
     * 递归对权限列表进行排序
     */
    private void sortPermissions(List<PermissionVO> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        
        // 按照创建时间降序排序
        permissions.sort((p1, p2) -> {
            if (p1.getCreateTime() != null && p2.getCreateTime() != null) {
                return p2.getCreateTime().compareTo(p1.getCreateTime());
            }
            return 0;
        });
        
        // 递归对子权限进行排序
        for (PermissionVO permission : permissions) {
            if (permission.getChildren() != null) {
                sortPermissions(permission.getChildren());
            }
        }
    }
} 