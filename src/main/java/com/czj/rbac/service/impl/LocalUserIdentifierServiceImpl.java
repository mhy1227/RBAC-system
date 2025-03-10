package com.czj.rbac.service.impl;

import com.czj.rbac.service.UserIdentifierService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地实现的用户标识生成服务
 */
@Slf4j
@Service
public class LocalUserIdentifierServiceImpl implements UserIdentifierService {
    
    private static final String SEQUENCE_FILE = "sequence.txt";
    private static final int POOL_SIZE = 1000;
    private static final int REFILL_THRESHOLD = 200;
    
    private final ConcurrentLinkedQueue<String> identifierPool;
    private final AtomicLong maxSequence;
    private final ReentrantLock refillLock;
    
    public LocalUserIdentifierServiceImpl() {
        this.identifierPool = new ConcurrentLinkedQueue<>();
        this.maxSequence = new AtomicLong(0);
        this.refillLock = new ReentrantLock();
    }
    
    @PostConstruct
    public void init() {
        maxSequence.set(readMaxSequence());
        refillPool(false);
        log.info("本地标识符服务初始化完成，当前最大序号：{}", maxSequence.get());
    }
    
    @Override
    public String generateUserIdentifier() {
        String identifier = identifierPool.poll();
        if (identifier == null) {
            refillPool(true);
            identifier = identifierPool.poll();
        }
        
        if (identifierPool.size() < REFILL_THRESHOLD) {
            asyncRefillPool();
        }
        return identifier;
    }
    
    @Override
    public long getCurrentMaxSequence() {
        return maxSequence.get();
    }
    
    @Override
    public int getPoolSize() {
        return identifierPool.size();
    }
    
    @Async
    public void asyncRefillPool() {
        refillPool(false);
    }
    
    private synchronized void refillPool(boolean urgent) {
        if (refillLock.tryLock()) {
            try {
                int batchSize = urgent ? 100 : POOL_SIZE - REFILL_THRESHOLD;
                long currentMax = maxSequence.get();
                
                for (long i = currentMax + 1; i <= currentMax + batchSize; i++) {
                    String identifier = String.format("XH%04d", i);
                    identifierPool.offer(identifier);
                }
                
                maxSequence.addAndGet(batchSize);
                saveMaxSequence(maxSequence.get());
                log.info("补充本地标识符池完成，当前最大序号：{}，补充数量：{}", maxSequence.get(), batchSize);
            } finally {
                refillLock.unlock();
            }
        }
    }
    
    private long readMaxSequence() {
        File file = new File(SEQUENCE_FILE);
        if (!file.exists()) {
            return 0;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return line == null ? 0 : Long.parseLong(line.trim());
        } catch (IOException e) {
            log.error("读取序号文件失败", e);
            return 0;
        }
    }
    
    private void saveMaxSequence(long sequence) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SEQUENCE_FILE))) {
            writer.write(String.valueOf(sequence));
        } catch (IOException e) {
            log.error("保存序号文件失败", e);
        }
    }
}
 