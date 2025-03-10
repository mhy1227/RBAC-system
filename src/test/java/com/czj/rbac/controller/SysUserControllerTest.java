package com.czj.rbac.controller;

import com.czj.rbac.model.dto.UserDTO;
import com.czj.rbac.model.vo.UserVO;
import com.czj.rbac.service.SysUserService;
import com.czj.rbac.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyLong;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.PageResult;
import com.czj.rbac.common.ResponseCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@AutoConfigureMockMvc
public class SysUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private String testToken;

    @BeforeEach
    public void setup() {
        // 生成测试用token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("username", "admin");
        claims.put("permissions", Arrays.asList("sys:admin"));
        claims.put("iat", new Date().getTime());
        claims.put("exp", new Date().getTime() + 3600000); // 1小时后过期
        
        testToken = JwtUtil.TOKEN_PREFIX + JwtUtil.generateJwt(claims);
    }

    @Test
    public void testGetById_Success() throws Exception {
        // 准备测试数据
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setUsername("admin");
        userVO.setNickname("管理员");

        when(userService.findById(1L)).thenReturn(userVO);

        // 执行测试
        mockMvc.perform(get("/user/1")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.nickname").value("管理员"));
    }

    @Test
    public void testPage_Success() throws Exception {
        // 准备测试数据
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setUsername("admin");
        
        PageResult<UserVO> pageResult = new PageResult<>(
            Collections.singletonList(userVO), 1L, 1, 10);
        
        when(userService.findPage(any())).thenReturn(pageResult);

        // 执行测试
        mockMvc.perform(get("/user/page")
                .header("Authorization", testToken)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("admin"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    public void testAdd_Success() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("123456");
        userDTO.setNickname("测试用户");

        // 执行测试
        mockMvc.perform(post("/user")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testAssignRoles_Success() throws Exception {
        // 准备测试数据
        List<Long> roleIds = Arrays.asList(1L, 2L);

        // 执行测试
        mockMvc.perform(post("/user/1/role")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testUpdateStatus_Success() throws Exception {
        mockMvc.perform(put("/user/1/status/1")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testUpdateStatus_InvalidStatus() throws Exception {
        // Mock业务异常
        doThrow(new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "无效的状态值"))
            .when(userService).updateStatus(anyLong(), any(Integer.class));

        // 执行测试
        mockMvc.perform(put("/user/1/status/2")
                .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.PARAM_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("无效的状态值"));
    }

    @Test
    public void testAdd_DuplicateUsername() throws Exception {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("123456");

        // Mock业务异常
        doThrow(new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "用户名已存在"))
            .when(userService).add(any(UserDTO.class));

        // 执行测试
        mockMvc.perform(post("/user")
                .header("Authorization", testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.PARAM_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    // ... 其他测试方法
} 