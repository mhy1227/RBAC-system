package com.czj.rbac.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordUtil {
    
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SALT_LENGTH = 16;
    private static final String SPLIT_CHAR = "$";
    
    /**
     * 加密密码
     * 格式: Base64(salt)$Base64(MD5(password+salt))
     */
    public static String encode(String password) {
        try {
            // 生成盐值
            String salt = generateSalt();
            
            // 加密
            String encryptedPassword = md5Encrypt(password + salt);
            
            // 拼接结果
            return Base64.getEncoder().encodeToString(salt.getBytes()) 
                + SPLIT_CHAR 
                + encryptedPassword;
                
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败");
        }
    }
    
    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 分割密文
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            // 获取盐值
            String salt = new String(Base64.getDecoder().decode(parts[0]));
            
            // 加密待验证的密码
            String encryptedPassword = md5Encrypt(rawPassword + salt);
            
            // 比对密文
            return parts[1].equals(encryptedPassword);
            
        } catch (Exception e) {
            log.error("密码验证失败", e);
            return false;
        }
    }
    
    /**
     * 生成随机盐值
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        StringBuilder salt = new StringBuilder(SALT_LENGTH);
        for (int i = 0; i < SALT_LENGTH; i++) {
            salt.append(SALT_CHARS.charAt(random.nextInt(SALT_CHARS.length())));
        }
        return salt.toString();
    }
    
    /**
     * MD5加密
     */
    private static String md5Encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(text.getBytes());
        return Base64.getEncoder().encodeToString(digest);
    }

    public static void main(String[] args) {
        String salt=encode("123456");
        System.out.println(salt);
    }
} 