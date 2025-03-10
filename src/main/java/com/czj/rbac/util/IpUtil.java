package com.czj.rbac.util;

import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 */
public class IpUtil {
    
    private static final String UNKNOWN = "unknown";
    
    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件时，不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        try {
            // X-Forwarded-For：Squid 服务代理
            String ipAddresses = request.getHeader("X-Forwarded-For");
            if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                // Proxy-Client-IP：apache 服务代理
                ipAddresses = request.getHeader("Proxy-Client-IP");
            }
            if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                // WL-Proxy-Client-IP：weblogic 服务代理
                ipAddresses = request.getHeader("WL-Proxy-Client-IP");
            }
            if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                // HTTP_CLIENT_IP：有些代理服务器
                ipAddresses = request.getHeader("HTTP_CLIENT_IP");
            }
            if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                // X-Real-IP：nginx服务代理
                ipAddresses = request.getHeader("X-Real-IP");
            }
            
            // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
            if (StringUtils.hasText(ipAddresses)) {
                ip = ipAddresses.split(",")[0];
            }
            
            // 还是不能获取到，最后再通过request.getRemoteAddr()获取
            if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }
} 