package com.genius.gitget.global.util.exception.handler;

import static com.genius.gitget.global.util.exception.ErrorCode.FILE_MAX_SIZE_EXCEED;

import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class FileExceptionHandler implements MessageSender {
    private final Environment env;
    private final SlackService slackService;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<CommonResponse> globalExceptionHandler(Exception e) {
        log.error("Multipart 용량이 최대 크기를 초과하여 예외가 발생했습니다.");
        sendSlackMessage(e);

        return ResponseEntity.badRequest().body(
                new CommonResponse(HttpStatus.BAD_REQUEST, FILE_MAX_SIZE_EXCEED.getMessage()));
    }

    @Override
    public void sendSlackMessage(Exception exception) {
        if (!env.matchesProfiles("prod")) {
            return;
        }
        slackService.sendMessage(exception);
    }
}
