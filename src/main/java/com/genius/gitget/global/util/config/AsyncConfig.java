package com.genius.gitget.global.util.config;

import com.genius.gitget.global.util.exception.handler.AsyncExceptionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
    private int CORE_POOL_SIZE = 10; // 스레드 풀의 코어 스레드 수
    private int MAX_POOL_SIZE = 30; // 스레드 풀의 최대 스레드 수
    private int QUEUE_CAPACITY = 10000; // 작업 큐의 용량

    @Bean(name = "threadExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(CORE_POOL_SIZE); // 코어 스레드 수 설정
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE); // 최대 스레드 수 설정
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY); // 큐 용량 설정
        taskExecutor.setThreadNamePrefix("Executor-"); // 스레드 이름 접두사 설정
        taskExecutor.initialize();

        // 1. 데코레이터 적용
//        taskExecutor.setTaskDecorator(new CustomDecorator());
        // 2. 거부 작업 처리
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // 3. 핸들러 생성해 예외처리
        return new AsyncExceptionHandler();
    }
}
