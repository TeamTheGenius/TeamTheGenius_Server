package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProgressUpdater {
    private final InstanceProvider instanceProvider;

    @Transactional
    public void updateToActivity(LocalDate currentDate) {
        List<Instance> preActivities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        for (Instance preActivity : preActivities) {
            LocalDate startedDate = preActivity.getStartedDate().toLocalDate();
            LocalDate completedDate = preActivity.getCompletedDate().toLocalDate();

            if (currentDate.isAfter(startedDate) && currentDate.isBefore(completedDate)) {
                preActivity.updateProgress(Progress.ACTIVITY);
            }
        }
    }

}
