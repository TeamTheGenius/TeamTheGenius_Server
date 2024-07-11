package com.genius.gitget.global.util.exception.handler;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BusinessExceptionHandler implements MessageSender {
    private final Environment env;
    private final SlackService slackService;

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse> globalBusinessExceptionHandler(BusinessException e) {
        log.error("[ERROR]" + e.getMessage(), e);
        sendSlackMessage(e);

        return ResponseEntity.badRequest().body(
                new CommonResponse(e.getStatus(), e.getMessage())
        );
    }

    @Override
    public void sendSlackMessage(Exception exception) {
        if (!env.matchesProfiles("prod")) {
            return;
        }
        slackService.sendMessage(exception);
    }

}
