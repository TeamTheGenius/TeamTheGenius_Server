package com.genius.gitget.global.util.exception.handler;

import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler implements MessageSender {
    private final Environment env;
    private final SlackService slackService;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> globalExceptionHandler(Exception e) {
        log.error("예외처리 되지 않은 Exception 발생 - 처리 필요");
        log.error("[UNHANDLED ERROR] " + e.getMessage(), e);
        sendSlackMessage(e);

        return ResponseEntity.badRequest().body(
                new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @Override
    public void sendSlackMessage(Exception exception) {
        if (!env.matchesProfiles("prod")) {
            return;
        }
        slackService.sendMessage(exception);
    }
}
