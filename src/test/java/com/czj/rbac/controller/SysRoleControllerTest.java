package com.czj.rbac.controller;

import com.czj.rbac.model.vo.RoleVO;
import com.czj.rbac.service.SysRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.model.dto.RoleDTO;
import com.czj.rbac.common.ResponseCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@AutoConfigureMockMvc
public class SysRoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysRoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJwZXJtaXNzaW9ucyI6WyJzeXM6YWRtaW4iXX0.xmhL4LxR2aUyf4CpC1iM1XhER2gLTk2Zl4C4cn4WqM4";

    @Test
    public void testGetById_Success() throws Exception {
        // 准备测试数据
        RoleVO roleVO = new RoleVO();
        roleVO.setId(1L);
        roleVO.setRoleName("测试角色");

        when(roleService.findById(1L)).thenReturn(roleVO);

        // 执行测试
        mockMvc.perform(get("/role/1")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.roleName").value("测试角色"));
    }

    @Test
    public void testPage_Success() throws Exception {
        // 准备测试数据
        PageResult<RoleVO> pageResult = new PageResult<>(
            Collections.singletonList(new RoleVO()), 1L, 1, 10);

        when(roleService.findPage(any())).thenReturn(pageResult);

        // 执行测试
        mockMvc.perform(get("/role/page")
                .header("Authorization", TEST_TOKEN)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    public void testAssignPermissions_Success() throws Exception {
        // 准备测试数据
        List<Long> permissionIds = Arrays.asList(1L, 2L);

        doNothing().when(roleService).assignPermissions(1L, permissionIds);

        // 执行测试
        mockMvc.perform(post("/role/1/permission")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testUpdateStatus_Success() throws Exception {
        doNothing().when(roleService).updateStatus(1L, 1);

        mockMvc.perform(put("/role/1/status/1")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testAdd_Success() throws Exception {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("测试角色");
        roleDTO.setRoleCode("TEST_ROLE");

        doNothing().when(roleService).add(any(RoleDTO.class));

        // 执行测试
        mockMvc.perform(post("/role")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testAdd_NoPermission() throws Exception {
        // 准备测试数据
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("测试角色");
        roleDTO.setRoleCode("TEST_ROLE");

        doThrow(new BusinessException(ResponseCode.FORBIDDEN))
            .when(roleService).add(any(RoleDTO.class));

        // 执行测试
        mockMvc.perform(post("/role")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.msg").value("无权限进行此操作"));
    }

    @Test
    public void testAssignPermissions_InvalidRole() throws Exception {
        // 准备测试数据
        List<Long> permissionIds = Arrays.asList(1L, 2L);

        doThrow(new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "角色不存在"))
                .when(roleService).assignPermissions(999L, permissionIds);

        // 执行测试
        mockMvc.perform(post("/role/999/permission")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("角色不存在"));
    }

    // ... 其他测试方法
} 