package com.czj.rbac.config;

import com.czj.rbac.service.CacheWarmUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private CacheWarmUpService cacheWarmUpService;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("应用启动完成，开始执行缓存预热");
        try {
            cacheWarmUpService.warmUpCaches();
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }
}