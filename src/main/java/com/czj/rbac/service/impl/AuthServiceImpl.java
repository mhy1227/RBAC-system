package com.czj.rbac.service.impl;

import com.czj.rbac.service.AuthService;
import com.czj.rbac.service.SysPermissionService;
import com.czj.rbac.service.TokenService;
import com.czj.rbac.service.SysLogService;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.model.SysUser;
import com.czj.rbac.model.TokenPair;
import com.czj.rbac.model.dto.LoginDTO;
import com.czj.rbac.model.vo.LoginVO;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.event.UserLoginEvent;
import com.czj.rbac.util.JwtUtil;
import com.czj.rbac.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl extends BaseServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private SysLogService logService;

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public LoginVO login(String username, String password) {
        try {
            // 1. 验证用户名密码
            SysUser user = validateUser(username, password);
            
            // 2. 生成token
            String loginId = UUID.randomUUID().toString();
            TokenPair tokenPair = tokenService.generateTokenPair(user.getId());
            
            // 3. 组装登录返回信息
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(tokenPair.getToken());
            loginVO.setRefreshToken(tokenPair.getRefreshToken());
            loginVO.setUser(convertToUserVO(user));
            
            // 4. 发布登录成功事件
            eventPublisher.publishEvent(new UserLoginEvent(this, user.getId(), loginId, true, null));
            
            log.info("用户登录成功 - username: {}", username);
            return loginVO;
            
        } catch (Exception e) {
            // 发布登录失败事件
            log.error("用户登录失败 - username: {}", username, e);
            eventPublisher.publishEvent(new UserLoginEvent(this, null, null, false, e.getMessage()));
            throw e;
        }
    }

    @Override
    public void logout() {
        // 获取当前用户ID
        Long userId = JwtUtil.getCurrentUserId();
        if (userId != null) {
            try {
                // 获取用户信息用于日志记录
                SysUser user = userMapper.findById(userId);
                
                // 移除token
                tokenService.removeToken(userId);
                
                // 记录登出日志
                logService.saveLog("用户登出", "登出成功", 
                    String.format("用户[%s]登出成功", user != null ? user.getUsername() : userId));
                    
                log.info("用户登出成功 - userId: {}", userId);
            } catch (Exception e) {
                log.error("用户登出失败 - userId: {}", userId, e);
                throw new BusinessException(ResponseCode.SYSTEM_ERROR, "登出失败");
            }
        }
    }

    @Override
    public LoginVO getCurrentUser() {
        // 1. 获取当前用户ID
        Long userId = JwtUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        // 2. 查询用户信息
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        // 3. 查询用户权限
        List<String> permissions = permissionService.findPermissionsByUserId(userId);

        // 4. 组装返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setPermissions(permissions);

        return loginVO;
    }
    
    @Override
    public LoginVO refreshToken(String refreshToken) {
        // 参数校验
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "刷新令牌不能为空");
        }
        
        // 1. 获取当前token
        String currentToken = JwtUtil.getTokenFromRequest();
        if (currentToken == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "访问令牌不能为空");
        }
        
        // 2. 刷新token
        try {
            TokenPair tokenPair = tokenService.refreshToken(refreshToken);
            
            // 3. 获取用户信息
            Long userId = JwtUtil.getCurrentUserId();
            SysUser user = userMapper.findById(userId);
            if (user == null) {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND);
            }
            
            List<String> permissions = permissionService.findPermissionsByUserId(userId);
            
            // 4. 记录token刷新日志
            logService.saveLog("Token刷新", "刷新成功", 
                String.format("用户[%s]刷新Token成功", user.getUsername()));
            
            // 5. 返回新的登录信息
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(tokenPair.getToken());
            loginVO.setRefreshToken(tokenPair.getRefreshToken());
            loginVO.setUserId(user.getId());
            loginVO.setUsername(user.getUsername());
            loginVO.setPermissions(permissions);
            
            log.info("Token刷新成功 - username: {}", user.getUsername());
            return loginVO;
        } catch (BusinessException e) {
            log.warn("Token刷新失败 - error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "Token刷新失败");
        }
    }

    /**
     * 验证用户名和密码
     */
    private SysUser validateUser(String username, String password) {
        // 参数校验
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new BusinessException(ResponseCode.PARAM_ERROR, "用户名和密码不能为空");
        }

        // 查询用户
        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("登录失败: 用户不存在 - username: {}", username);
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败: 用户已禁用 - username: {}", username);
            throw new BusinessException(ResponseCode.USER_DISABLED);
        }

        // 检查账号是否被锁定
        if (user.isLocked()) {
            log.warn("登录失败: 账号已锁定 - username: {}", username);
            throw new BusinessException(ResponseCode.USER_LOCKED, 
                String.format("账号已被锁定,请%d分钟后再试", SysUser.LOCK_TIME_MINUTES));
        }

        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            handleLoginFailure(user);
        }

        // 登录成功，重置失败次数和更新登录时间
        handleLoginSuccess(user);
        
        return user;
    }
    
    /**
     * 处理登录失败
     */
    private void handleLoginFailure(SysUser user) {
        int currentFailCount = user.getLoginFailCount() == null ? 0 : user.getLoginFailCount();
        currentFailCount++;
        
        user.setLoginFailCount(currentFailCount);
        if (currentFailCount >= SysUser.MAX_LOGIN_FAIL_COUNT) {
            user.setLockTime(LocalDateTime.now().plusMinutes(SysUser.LOCK_TIME_MINUTES));
        }
        
        userMapper.updateLoginFail(user.getId(), currentFailCount, user.getLockTime());
        
        if (user.isLocked()) {
            throw new BusinessException(ResponseCode.USER_LOCKED, 
                String.format("密码错误次数过多,账号已被锁定%d分钟", SysUser.LOCK_TIME_MINUTES));
        } else {
            throw new BusinessException(ResponseCode.PASSWORD_ERROR, 
                String.format("密码错误,还剩%d次机会", SysUser.MAX_LOGIN_FAIL_COUNT - currentFailCount));
        }
    }
    
    /**
     * 处理登录成功
     */
    private void handleLoginSuccess(SysUser user) {
        user.setLoginFailCount(0);
        user.setLockTime(null);
        user.setLastLoginTime(LocalDateTime.now());
        
        userMapper.resetLoginFail(user.getId());
        userMapper.updateLastLoginTime(user.getId(), user.getLastLoginTime());
    }
    
    /**
     * 转换为UserVO对象
     */
    private UserVO convertToUserVO(SysUser user) {
        if (user == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        vo.setLastLoginTime(user.getLastLoginTime());
        
        // 设置用户权限
        List<String> permissions = userService.findUserPermissions(user.getId())
            .stream()
            .map(PermissionVO::getPermissionCode)
            .collect(Collectors.toList());
        vo.setPermissions(permissions);
        
        return vo;
    }
} 