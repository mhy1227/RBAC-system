package com.czj.rbac.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    
    @Value("${rbac.user.password.min-length:6}")
    private int minLength;
    
    @Value("${rbac.user.password.max-length:20}")
    private int maxLength;
    
    @Value("${rbac.user.password.require-number:true}")
    private boolean requireNumber;
    
    @Value("${rbac.user.password.require-letter:true}")
    private boolean requireLetter;
    
    @Value("${rbac.user.password.require-special:false}")
    private boolean requireSpecial;
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d+.*");
    private static final Pattern LETTER_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile(".*[^a-zA-Z0-9]+.*");
    
    /**
     * 验证密码
     * @param password 密码
     * @return 错误信息列表，如果为空则表示验证通过
     */
    public List<String> validate(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.length() < minLength) {
            errors.add("密码长度不能小于" + minLength + "位");
        }
        
        if (password != null && password.length() > maxLength) {
            errors.add("密码长度不能大于" + maxLength + "位");
        }
        
        if (requireNumber && !NUMBER_PATTERN.matcher(password).matches()) {
            errors.add("密码必须包含数字");
        }
        
        if (requireLetter && !LETTER_PATTERN.matcher(password).matches()) {
            errors.add("密码必须包含字母");
        }
        
        if (requireSpecial && !SPECIAL_PATTERN.matcher(password).matches()) {
            errors.add("密码必须包含特殊字符");
        }
        
        return errors;
    }
} 