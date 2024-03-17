package com.genius.gitget.schedule.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {
    private final ProgressService scheduleService;

    @Transactional
    @Scheduled(cron = "${schedule.cron}")
    public void run() {
        LocalDate now = LocalDate.now();

        log.info(now + ": Schedule 설정에 따라 instance의 Progress 업데이트 진행");

        scheduleService.updateToActivity(now);
        scheduleService.updateToDone(now);
    }
}
