package com.czj.rbac.controller;

import com.czj.rbac.model.vo.PermissionVO;
import com.czj.rbac.service.SysPermissionService;
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
import com.czj.rbac.model.dto.PermissionDTO;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.common.PageResult;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@AutoConfigureMockMvc
public class SysPermissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysPermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJwZXJtaXNzaW9ucyI6WyJzeXM6YWRtaW4iXX0.xmhL4LxR2aUyf4CpC1iM1XhER2gLTk2Zl4C4cn4WqM4";

    @Test
    public void testGetById_Success() throws Exception {
        // 准备测试数据
        PermissionVO permissionVO = new PermissionVO();
        permissionVO.setId(1L);
        permissionVO.setPermissionName("测试权限");

        when(permissionService.findById(1L)).thenReturn(permissionVO);

        // 执行测试
        mockMvc.perform(get("/permission/1")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.permissionName").value("测试权限"));
    }

    @Test
    public void testPage_Success() throws Exception {
        // 准备测试数据
        PermissionVO permissionVO = new PermissionVO();
        permissionVO.setId(1L);
        permissionVO.setPermissionName("用户管理");
        permissionVO.setPermissionCode("sys:user:manage");
        
        PageResult<PermissionVO> pageResult = new PageResult<>(
            Collections.singletonList(permissionVO), 1L, 1, 10);
        
        when(permissionService.findPage(any())).thenReturn(pageResult);

        // 执行测试
        mockMvc.perform(get("/permission/page")
                .header("Authorization", TEST_TOKEN)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].permissionName").value("用户管理"))
                .andExpect(jsonPath("$.data.list[0].permissionCode").value("sys:user:manage"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    public void testGetPermissionTree_Success() throws Exception {
        // 准备测试数据
        PermissionVO parent = new PermissionVO();
        parent.setId(1L);
        parent.setPermissionName("系统管理");
        parent.setPermissionCode("sys:manage");
        
        PermissionVO child = new PermissionVO();
        child.setId(2L);
        child.setPermissionName("用户管理");
        child.setPermissionCode("sys:user:manage");
        child.setParentId(1L);
        
        parent.setChildren(Collections.singletonList(child));
        
        when(permissionService.findPermissionTree("menu")).thenReturn(Collections.singletonList(parent));

        // 执行测试
        mockMvc.perform(get("/permission/tree")
                .header("Authorization", TEST_TOKEN)
                .param("type", "menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].children[0].id").value(2));
    }

    @Test
    public void testAdd_DuplicateCode() throws Exception {
        // 准备测试数据
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPermissionName("测试权限");
        permissionDTO.setPermissionCode("test:permission");

        // Mock业务异常
        doThrow(new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "权限编码已存在"))
            .when(permissionService).add(any(PermissionDTO.class));

        // 执行测试
        mockMvc.perform(post("/permission")
                .header("Authorization", TEST_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.PARAM_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("权限编码已存在"));
    }

    @Test
    public void testDelete_HasChildren() throws Exception {
        // Mock业务异常
        doThrow(new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "存在子权限，不能删除"))
            .when(permissionService).delete(any(Long.class));

        // 执行测试
        mockMvc.perform(delete("/permission/1")
                .header("Authorization", TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.PARAM_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("存在子权限，不能删除"));
    }

    // ... 其他测试方法
} 