package com.czj.rbac.util;

import org.springframework.util.StringUtils;

public class SensitiveInfoUtil {
    
    /**
     * 对用户名进行脱敏处理
     * 规则：
     * 1. 长度小于3，显示第一个字符，其余用*代替
     * 2. 长度大于等于3，显示第一个和最后一个字符，中间用*代替
     *
     * @param username 用户名
     * @return 脱敏后的用户名
     */
    public static String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return "";
        }
        
        int length = username.length();
        if (length < 3) {
            return username.substring(0, 1) + "*".repeat(length - 1);
        }
        
        return username.charAt(0) + 
               "*".repeat(length - 2) + 
               username.charAt(length - 1);
    }
    
    /**
     * 手机号脱敏
     * 保留前3位和后4位，中间用*代替
     */
    public static String maskPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return phone;
        }
        if (phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 邮箱脱敏
     * 邮箱前缀仅显示第一个字符和最后一个字符，中间用*代替
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        
        if (prefix.length() <= 2) {
            return email;
        }
        
        return prefix.charAt(0) + 
               "*".repeat(prefix.length() - 2) + 
               prefix.charAt(prefix.length() - 1) + 
               suffix;
    }
    
    /**
     * 身份证号脱敏
     * 保留前6位和后4位，中间用*代替
     */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        if (idCard.length() < 10) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
} 