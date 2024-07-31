package com.genius.gitget.util.instance;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;

public class InstanceFactory {
    /**
     * LocalDate.now()를 기준으로 PREACTIVITY(시작 전) 인스턴스 생성 후 반환
     */
    public static Instance createPreActivity(int duration) {
        return Instance.builder()
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now().plusDays(1))
                .completedDate(LocalDateTime.now().plusDays(duration + 1))
                .build();
    }

    /**
     * LocalDate.now()를 기준으로 진행 중인 인스턴스 생성 후 반환
     */
    public static Instance createActivity(int duration) {
        return Instance.builder()
                .progress(Progress.ACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(duration))
                .build();
    }

    /**
     * LocalDate.now()를 기준으로 완료된 인스턴스 생성 후 반환
     */
    public static Instance createDone(int duration) {
        return Instance.builder()
                .progress(Progress.ACTIVITY)
                .startedDate(LocalDateTime.now().minusDays(duration - 1))
                .completedDate(LocalDateTime.now().minusDays(1))
                .build();
    }
}
