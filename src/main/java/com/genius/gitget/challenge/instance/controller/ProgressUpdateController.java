package com.genius.gitget.challenge.instance.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.instance.service.ProgressUpdater;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProgressUpdateController {
    private final ProgressUpdater progressUpdater;

    @GetMapping("/challenges/update")
    public ResponseEntity<CommonResponse> updateProgress() {
        LocalDate currentDate = LocalDate.now();
        progressUpdater.updateToActivity(currentDate);
        progressUpdater.updateToDone(currentDate);

        return ResponseEntity.ok().body(
                new CommonResponse(SUCCESS.getStatus(), SUCCESS.getMessage())
        );
    }
}
