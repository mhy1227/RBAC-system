package com.czj.rbac.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import lombok.Data;

import java.util.concurrent.Executor;

@Data
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "rbac.log")
public class LogConfig {
    
    /**
     * 是否启用异步日志
     */
    private boolean asyncEnabled = true;
    
    /**
     * 异步线程池配置
     */
    private AsyncConfig async = new AsyncConfig();
    
    /**
     * 日志保留天数
     */
    private int retentionDays = 30;
    
    @Data
    public static class AsyncConfig {
        /**
         * 核心线程数
         */
        private int corePoolSize = 2;
        
        /**
         * 最大线程数
         */
        private int maxPoolSize = 5;
        
        /**
         * 队列容量
         */
        private int queueCapacity = 100;
    }
    /**
     * 日志异步任务执行器
     */
    @Bean("logTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(async.getCorePoolSize());
        executor.setMaxPoolSize(async.getMaxPoolSize());
        executor.setQueueCapacity(async.getQueueCapacity());
        executor.setThreadNamePrefix("log-task-");
        executor.initialize();
        return executor;
    }

} 