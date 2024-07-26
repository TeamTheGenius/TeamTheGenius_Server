package com.genius.gitget.util.instance;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;

public class InstanceFactory {
    public static Instance create() {
        return Instance.builder()
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(10))
                .build();
    }
}
