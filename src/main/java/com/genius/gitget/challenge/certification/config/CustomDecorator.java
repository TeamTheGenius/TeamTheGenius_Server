package com.genius.gitget.challenge.certification.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class CustomDecorator implements TaskDecorator {
	@Override
	public Runnable decorate(Runnable runnable) {
		// 현재 요청의 RequestAttribute를 가져옴
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();

		return () -> {
			try {
				// 작업 실행 전에 RequestAttributes를 설정
				RequestContextHolder.setRequestAttributes(attributes);

				// 작업 실행
				runnable.run();
			} finally {
				// 작업 실행 후에 RequestAttributes를 제거
				RequestContextHolder.resetRequestAttributes();
			}
		};
	}
}

