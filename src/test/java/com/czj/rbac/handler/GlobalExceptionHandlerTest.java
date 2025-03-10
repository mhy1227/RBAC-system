package com.czj.rbac.handler;

import com.czj.rbac.common.BusinessException;
import com.czj.rbac.common.ResponseCode;
import com.czj.rbac.common.Result;
import com.czj.rbac.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException validException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void testHandleBusinessException() {
        // 准备测试数据
        BusinessException exception = new BusinessException(ResponseCode.PARAM_ERROR.getCode(), "参数错误");

        // 执行测试
        Result<String> result = exceptionHandler.handleBusinessException(exception);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResponseCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals("参数错误", result.getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // 准备测试数据
        IllegalArgumentException exception = new IllegalArgumentException("参数不合法");

        // 执行测试
        Result<String> result = exceptionHandler.handleIllegalArgumentException(exception);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResponseCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals("参数不合法", result.getMessage());
    }

    @Test
    void testHandleValidException() {
        // 准备测试数据
        FieldError error1 = new FieldError("user", "username", "用户名不能为空");
        FieldError error2 = new FieldError("user", "password", "密码不能为空");

        when(validException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));

        // 执行测试
        Result<String> result = exceptionHandler.handleValidException(validException);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResponseCode.PARAM_ERROR.getCode(), result.getCode());
        assertEquals("用户名不能为空, 密码不能为空", result.getMessage());
    }

    @Test
    void testHandleException() {
        // 准备测试数据
        Exception exception = new RuntimeException("系统错误");

        // 执行测试
        Result<String> result = exceptionHandler.handleException(exception);

        // 验证结果
        assertNotNull(result);
        assertEquals(ResponseCode.ERROR.getCode(), result.getCode());
        assertEquals("系统错误，请联系管理员", result.getMessage());
    }
} 