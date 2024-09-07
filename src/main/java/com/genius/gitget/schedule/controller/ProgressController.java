package com.genius.gitget.schedule.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.schedule.service.ProgressService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProgressController {
    private final ProgressService scheduleService;

    @GetMapping("/challenges/update")
    public ResponseEntity<CommonResponse> updateProgress() {
        LocalDate currentDate = DateUtil.convertToKST(LocalDateTime.now());
        scheduleService.updateToActivity(currentDate);
        scheduleService.updateToDone(currentDate);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
