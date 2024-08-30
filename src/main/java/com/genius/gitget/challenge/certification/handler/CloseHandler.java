package com.genius.gitget.challenge.certification.handler;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CloseHandler implements ApplicationListener<ContextClosedEvent> {
	private final ThreadPoolTaskExecutor executor;

	public CloseHandler(ThreadPoolTaskExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		executor.shutdown();
		log.info("Gracefully Shutdown.");
	}
}
