package com.czj.rbac.service.impl;

import com.czj.rbac.mapper.SysRoleMapper;
import com.czj.rbac.mapper.SysUserMapper;
import com.czj.rbac.model.SysUser;
import com.czj.rbac.model.SysRole;
import com.czj.rbac.model.dto.UserDTO;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.model.query.UserQuery;
import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.PageResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SysUserServiceImplTest {
    @InjectMocks
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Test
    public void testFindById_Success() {
        // 准备测试数据
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setNickname("管理员");
        user.setStatus(1);

        when(userMapper.findById(anyLong())).thenReturn(user);

        // 执行测试
        UserVO result = userService.findById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("管理员", result.getNickname());
    }

    @Test
    public void testAdd_Success() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("123456");
        
        when(userMapper.checkUsername(anyString())).thenReturn(0);
        when(userMapper.insert(any())).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.add(userDTO));
    }

    @Test
    public void testAdd_DuplicateUsername() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("123456");

        when(userMapper.checkUsername(anyString())).thenReturn(1);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            userService.add(userDTO);
        });
    }

    @Test
    public void testDelete_Success() {
        // 准备测试数据
        Long userId = 1L;
        SysUser user = new SysUser();
        user.setId(userId);
        
        when(userMapper.findById(userId)).thenReturn(user);
        when(userMapper.deleteById(userId)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.delete(userId));
    }

    @Test
    public void testUpdate_Success() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("test");
        
        when(userMapper.update(any())).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.update(userDTO));
    }

    @Test
    public void testAssignRoles_Success() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> roleIds = Arrays.asList(1L, 2L);

        // 模拟用户存在
        SysUser user = new SysUser();
        user.setId(userId);
        when(userMapper.findById(userId)).thenReturn(user);

        // 模拟角色存在
        when(roleMapper.findById(1L)).thenReturn(new SysRole());
        when(roleMapper.findById(2L)).thenReturn(new SysRole());

        when(userMapper.deleteUserRoles(anyLong())).thenReturn(1);
        when(userMapper.insertUserRoles(anyLong(), anyList())).thenReturn(2);

        // 执行测试
        assertDoesNotThrow(() -> {
            userService.assignRoles(userId, roleIds);
        });
    }

    @Test
    public void testAssignRoles_InvalidRole() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> roleIds = Arrays.asList(999L);

        when(userMapper.findById(userId)).thenReturn(new SysUser());
        when(roleMapper.findById(999L)).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            userService.assignRoles(userId, roleIds);
        });
    }

    @Test
    public void testUpdateStatus_Success() {
        // 准备测试数据
        Long userId = 1L;
        Integer status = 1;

        when(userMapper.updateStatus(userId, status)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.updateStatus(userId, status));
    }

    @Test
    public void testUpdateStatus_InvalidStatus() {
        // 准备测试数据
        Long userId = 1L;
        Integer invalidStatus = 2; // 无效的状态值

        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> {
            userService.updateStatus(userId, invalidStatus);
        });
    }

    @Test
    public void testFindById_NotFound() {
        // 准备测试数据
        when(userMapper.findById(anyLong())).thenReturn(null);

        // 执行测试
        UserVO result = userService.findById(999L);

        // 验证结果
        assertNull(result);
    }

    @Test
    public void testAdd_EmptyUsername() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("123456");

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.add(userDTO);
        });
        assertEquals("用户名和密码不能为空", exception.getMessage());
    }

    @Test
    public void testAssignRoles_EmptyRoleIds() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> roleIds = Collections.emptyList();

        // 模拟用户存在
        when(userMapper.findById(userId)).thenReturn(new SysUser());

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.assignRoles(userId, roleIds);
        });
        assertEquals("角色ID列表不能为空", exception.getMessage());
    }

    @Test
    public void testDelete_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        when(userMapper.findById(userId)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.delete(userId);
        });
        assertEquals("用户不存在", exception.getMessage());
    }
} 